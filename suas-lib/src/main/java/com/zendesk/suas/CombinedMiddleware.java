package com.zendesk.suas;

import java.util.ArrayList;
import java.util.List;

class CombinedMiddleware implements Middleware {

    private final LinkedMiddleware linkedMiddleware;

    CombinedMiddleware(List<Middleware> middleware) {
        if(middleware == null || middleware.size() == 0) {
            this.linkedMiddleware = null;
        } else {
            this.linkedMiddleware = LinkedMiddleware.fromList(middleware);
        }
    }

    @Override
    public void onAction(Action<?> action, GetState state, Dispatcher dispatcher, Continuation continuation) {
        if(linkedMiddleware != null) {
            loopThroughIt(action, state, dispatcher, continuation, linkedMiddleware);
        } else {
            continuation.next(action);
        }
    }

    private void loopThroughIt(Action<?> action, final GetState state, final Dispatcher dispatcher,
                               final Continuation continuation, final LinkedMiddleware decoratedMiddleware) {
        decoratedMiddleware.onAction(action, state, dispatcher, new Continuation() {
            @Override
            public void next(Action<?> action) {
                if(decoratedMiddleware.getNext() != null) {
                    loopThroughIt(action, state, dispatcher, continuation, decoratedMiddleware.getNext());
                } else {
                    continuation.next(action);
                }
            }
        });
    }

    private static class LinkedMiddleware implements Middleware {

        static LinkedMiddleware fromList(List<Middleware> middleware) {
            final List<LinkedMiddleware> result = new ArrayList<>();

            LinkedMiddleware prevItem = null;
            for(int i = middleware.size() - 1; i >= 0; i--) {
                final LinkedMiddleware item = new LinkedMiddleware(middleware.get(i), prevItem);
                result.add(0, item);
                prevItem = item;
            }

            return result.get(0);
        }

        private final Middleware middleware;
        private final LinkedMiddleware next;

        LinkedMiddleware(Middleware middleware, LinkedMiddleware next) {
            this.middleware = middleware;
            this.next = next;
        }

        @Override
        public void onAction(Action<?> action, GetState state, Dispatcher dispatcher, Continuation continuation) {
            middleware.onAction(action, state, dispatcher, continuation);
        }

        LinkedMiddleware getNext() {
            return next;
        }
    }

}
