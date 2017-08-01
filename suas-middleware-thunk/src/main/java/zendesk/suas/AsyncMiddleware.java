package zendesk.suas;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

/**
 * Async Middleware - {@link Action}s carrying an {@link AsyncAction} get intercepted
 * by this {@link Middleware} and not dispatched to the reducer.
 * Instead the {@link AsyncMiddleware} executes {@link AsyncAction#execute(Dispatcher, GetState)}
 * <br>
 * <p>
 * How to use this middleware:
 * <ol>
 *     <li>Create an {@link AsyncAction}</li>
 *     <li>In {@link AsyncAction#execute(Dispatcher, GetState)} perform any operation. {@link AsyncAction#execute(Dispatcher, GetState)} <i>isn't</i> executed on a background thread.</li>
 *     <li>When the result is ready use the {@link Dispatcher} to dispatch a new {@link Action}</li>
 *     <li>Create {@link Action} by calling {@link AsyncMiddleware#create(AsyncAction)}</li>
 * </ol>
 * <br>
 * <pre>
 * AsyncAction asyncAction = new AsyncAction(){
 *   public void execute(final Dispatcher dispatcher, GetState getState) {
 *     Network network = new Network();
 *     network.load(new Callback() {
 *       public void onResult(Object data) {
 *         dispatcher.dispatchAction(new Action<>("network_success", data));
 *       }
 *     });
 *   }
 * };
 *
 * Action action = AsyncMiddleware.create(asyncAction);
 * store.dispatchAction(action);
 * </pre>
 * For convenience there's {@link AsyncMiddleware#forBlockingAction(AsyncAction)}. An {@link Action}
 * created with that function will be be executed on a background thread. For doing so it uses
 * Android's {@link AsyncTask}.
 */
public class AsyncMiddleware implements Middleware {

    private static final String ACTION_TYPE = "SUAS_ASYNC_ACTION";

    /**
     * Create an {@link Action}. The action created through this method
     * will never reach a {@link Reducer}. Instead the passed in {@link AsyncAction}
     * will be executed.
     *
     * <p>
     *      <b>Important:</b> {@link AsyncAction#execute(Dispatcher, GetState)} will be run on the
     *      main thread.
     * </p>
     *
     * @param asyncAction an async action
     * @return an action
     */
    public static Action create(AsyncAction asyncAction) {
        return new Action<>(ACTION_TYPE, asyncAction);
    }

    /**
     * Create an {@link Action}. The action created through this method
     * will never reach a {@link Reducer}. Instead the passed in {@link AsyncAction}
     * will be executed.
     *
     * <p>
     *     <b>Important:</b> {@link AsyncAction#execute(Dispatcher, GetState)} will be run on a background
     *     thread. For doing so {@link AsyncMiddleware} uses Android's {@link AsyncTask}.
     *     <br>
     *     If you wish more control over the threading behavior consider using {@link AsyncMiddleware#create(AsyncAction)}
     *     with your own threading strategy.
     * </p>
     *
     * @param asyncAction an async action
     * @return an action
     */
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
