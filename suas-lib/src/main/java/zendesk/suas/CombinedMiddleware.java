package zendesk.suas;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Iterator;

/**
 * Helper class for combing all provided {@link Middleware}
 */
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
    public void onAction(@NonNull Action<?> action, @NonNull StoreApi store, @NonNull Continuation continuation) {
        if(middleware != null) {
            loopThroughMiddleware(action, store, continuation, middleware.iterator());
        } else {
            continuation.next(action);
        }
    }

    private void loopThroughMiddleware(final Action<?> action, final StoreApi store, final Continuation continuation,
                                       final Iterator<Middleware> middleware) {
        if (middleware.hasNext()) {
            final Middleware next = middleware.next();
            next.onAction(action, store, new Continuation() {
                @Override
                public void next(@NonNull Action<?> action) {
                    loopThroughMiddleware(action, store, continuation, middleware);
                }
            });
        } else {
            continuation.next(action);
        }
    }
}
