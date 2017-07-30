package zendesk.suas;


import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Suas - This is the entry point.
 */
public class Suas {

    private Suas() {
        // intentionally empty
    }

    /**
     * Creates a {@link Store}.
     *
     * <p>
     *     A {@link Store} must at least have one {@link Reducer}.
     *     <br>
     *     It's not allowed to have two {@link Reducer} registered to the same key.
     * </p>
     *
     * @param reducers a collection of {@link Reducer}
     * @return a instance of {@link Builder} for further configuration
     */
    public static Builder createStore(@NonNull Collection<Reducer> reducers) {
        if(reducers == null || reducers.isEmpty()) {
            throw new IllegalArgumentException("Reducer must not be null or empty");
        }
        return new Builder(reducers);
    }

    /**
     * Creates a {@link Store}.
     *
     * <p>
     *     A {@link Store} must at least have one {@link Reducer}.
     *     <br>
     *     It's not allowed to have two {@link Reducer} registered to the same key.
     * </p>
     *
     * @param reducers a collection of {@link Reducer}
     * @return a instance of {@link Builder} for further configuration
     */
    public static Builder createStore(@NonNull Reducer... reducers) {
        if(reducers == null || reducers.length == 0) {
            throw new IllegalArgumentException("Reducer must not be null or empty");
        }
        return new Builder(Arrays.asList(reducers));
    }

    /**
     * Fluent API for creating a {@link Store}.
     */
    public static class Builder {

        private final Collection<Reducer> reducers;
        private State state;
        private Collection<Middleware> middleware = new ArrayList<>();
        private Filter<Object> notifier = Filters.DEFAULT;

        Builder(@NonNull Collection<Reducer> reducers) {
            this.reducers = reducers;
        }

        /**
         * Configure the {@link Store} with a non empty {@link State}
         *
         * @param state an initial state
         */
        public Builder withInitialState(@NonNull State state) {
            assertArgumentsNotNull(state, "Initial state must not be null");
            this.state = state;
            return this;
        }

        /**
         * Configure the {@link Store} with one or many {@link Middleware}
         *
         * @param middleware a list of {@link Middleware}
         */
        public Builder withMiddleware(@NonNull Collection<Middleware> middleware) {
            assertArgumentsNotNull(middleware, "Middleware must not be null");
            this.middleware = middleware;
            return this;
        }

        /**
         * Configure the {@link Store} with one or many {@link Middleware}
         *
         * @param middleware a list of {@link Middleware}
         */
        public Builder withMiddleware(@NonNull Middleware... middleware) {
            assertArgumentsNotNull(middleware, "Middleware must not be null");
            this.middleware = Arrays.asList(middleware);
            return this;
        }

        /**
         * Configure the {@link Store} with a default {@link Filter}.
         *
         * <p>
         *     Default: {@link Filters#DEFAULT}
         * </p>
         *
         * @param filter a custom default filter
         */
        public Builder withDefaultFilter(Filter<Object> filter) {
            assertArgumentsNotNull(filter, "Notifier must not be null");
            this.notifier = filter;
            return this;
        }

        /**
         * Creates an instance {@link Store} with the provided options.
         *
         * @return a new store
         */
        public Store build() {
            final CombinedReducer combinedReducer = new CombinedReducer(reducers);
            final CombinedMiddleware combinedMiddleware = new CombinedMiddleware(middleware);
            final State initialState = buildState(combinedReducer, state);

            return new DefaultStore(initialState, combinedReducer, combinedMiddleware, notifier);
        }

        private State buildState(CombinedReducer combinedReducer, State state) {
            final State emptyState = combinedReducer.getEmptyState();

            final State initialState;
            if(state != null) {
                final State passedInState = state.copy();
                for(String key : emptyState.getKeys()) {
                    if(passedInState.getState(key) == null) {
                        passedInState.updateKey(key, emptyState.getState(key));
                    }
                }
                initialState = passedInState;
            } else {
                initialState = emptyState;
            }

            return initialState;
        }

        private void assertArgumentsNotNull(Object input, String msg) {
            if(input == null) {
                throw new IllegalArgumentException(msg);
            }
        }
    }
}
