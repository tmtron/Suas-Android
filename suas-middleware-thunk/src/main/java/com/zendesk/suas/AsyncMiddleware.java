package com.zendesk.suas;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

public class AsyncMiddleware implements Middleware {

    private static final String ACTION_TYPE = "SUAS_ASYNC_ACTION";

    public static Action create(AsyncAction asyncAction) {
        return new Action<>(ACTION_TYPE, asyncAction);
    }

    public static Action forBlockingAction(AsyncAction asyncAction) {
        return create(new AsyncMiddleware.AsyncTaskAction(asyncAction));
    }

    @Override
    public void onAction(@NonNull Action<?> action, @NonNull GetState state, @NonNull Dispatcher dispatcher, @NonNull Continuation continuation) {
        if(ACTION_TYPE.equals(action.getActionType()) && action.getData() instanceof AsyncAction) {
            final AsyncAction asyncAction = action.getData();
            if(asyncAction != null) {
                asyncAction.execute(dispatcher, state);
            }
        } else {
            continuation.next(action);
        }
    }

    private static class AsyncTaskAction implements AsyncAction {

        private final AsyncAction asyncAction;

        private AsyncTaskAction(AsyncAction asyncAction) {
            this.asyncAction = asyncAction;
        }

        @Override
        public void execute(Dispatcher dispatcher, GetState getState) {
            new AsyncActionTask(dispatcher, getState, asyncAction).execute();
        }
    }

    private static class AsyncActionTask extends AsyncTask<Void, Void, Void> {

        private final Dispatcher dispatcher;
        private final GetState getState;
        private final AsyncAction asyncAction;

        private AsyncActionTask(Dispatcher dispatcher, GetState getState, AsyncAction asyncAction) {
            this.dispatcher = dispatcher;
            this.getState = getState;
            this.asyncAction = asyncAction;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            asyncAction.execute(dispatcher, getState);
            return null;
        }
    }
}
