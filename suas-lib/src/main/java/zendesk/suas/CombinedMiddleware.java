package zendesk.suas;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Iterator;

class CombinedMiddleware implements Middleware {

    private final Collection<Middleware> middleware;

    CombinedMiddleware(Collection<Middleware> middleware) {
        if(middleware == null || middleware.size() == 0) {
            this.middleware = null;
        } else {
            this.middleware = middleware;
        }
    }

    @Override
    public void onAction(@NonNull Action<?> action, @NonNull GetState state,
                         @NonNull Dispatcher dispatcher, @NonNull Continuation continuation) {
        if(middleware != null) {
            loopThroughMiddleware(action, state, dispatcher, continuation, middleware.iterator());
        } else {
            continuation.next(action);
        }
    }

    private void loopThroughMiddleware(final Action<?> action, final GetState state, final Dispatcher dispatcher,
                                       final Continuation continuation, final Iterator<Middleware> middleware) {
        if (middleware.hasNext()) {
            final Middleware next = middleware.next();
            next.onAction(action, state, dispatcher, new Continuation() {
                @Override
                public void next(@NonNull Action<?> action) {
                    loopThroughMiddleware(action, state, dispatcher, continuation, middleware);
                }
            });
        } else {
            continuation.next(action);
        }
    }

}
