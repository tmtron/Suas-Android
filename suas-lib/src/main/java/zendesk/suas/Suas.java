package zendesk.suas;


import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class Suas {

    public static Builder createStore(@NonNull Collection<Reducer> reducers) {
        if(reducers == null || reducers.isEmpty()) {
            throw new IllegalArgumentException("Reducer must not be null or empty");
        }
        return new Builder(reducers);
    }

    public static Builder createStore(@NonNull Reducer... reducers) {
        if(reducers == null || reducers.length == 0) {
            throw new IllegalArgumentException("Reducer must not be null or empty");
        }
        return new Builder(Arrays.asList(reducers));
    }

    public static class Builder {

        private final Collection<Reducer> reducers;
        private State state;
        private Collection<Middleware> middleware = new ArrayList<>();
        private Filter<Object> notifier = Filters.DEFAULT;

        Builder(@NonNull Collection<Reducer> reducers) {

            this.reducers = reducers;
        }

        public Builder withInitialState(@NonNull State state) {
            assertArgumentsNotNull(state, "Initial state must not be null");
            this.state = state;
            return this;
        }

        public Builder withMiddleware(@NonNull Collection<Middleware> middleware) {
            assertArgumentsNotNull(middleware, "Middleware must not be null");
            this.middleware = middleware;
            return this;
        }

        public Builder withMiddleware(@NonNull Middleware... middleware) {
            assertArgumentsNotNull(middleware, "Middleware must not be null");
            this.middleware = Arrays.asList(middleware);
            return this;
        }

        public Builder withDefaultFilter(Filter<Object> filter) {
            assertArgumentsNotNull(filter, "Notifier must not be null");
            this.notifier = filter;
            return this;
        }

        public Store build() {
            final CombinedReducer combinedReducer = new CombinedReducer(reducers);
            final CombinedMiddleware combinedMiddleware = new CombinedMiddleware(middleware);

            final State initialState;
            if(state != null) {
                initialState = state;
            } else {
                initialState = combinedReducer.getEmptyState();
            }

            return new ReduxStore(initialState, combinedReducer, combinedMiddleware, notifier);
        }

        private void assertArgumentsNotNull(Object input, String msg) {
            if(input == null) {
                throw new IllegalArgumentException(msg);
            }
        }
    }
}
