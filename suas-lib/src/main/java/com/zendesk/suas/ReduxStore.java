package com.zendesk.suas;


import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ReduxStore implements Store {

    private State state;
    private final CombinedReducer reducer;
    private final CombinedMiddleware middleware;
    private final Filter defaultFilter;

    private final Collection<Listeners.StateListener> listeners;
    private final Map<Component, Listener> componentListenerMap;

    private ReduxStore(State state, CombinedReducer reducer, CombinedMiddleware combinedMiddleware, Filter<Object> defaultFilter) {
        this.state = state;
        this.reducer = reducer;
        this.middleware = combinedMiddleware;
        this.defaultFilter = defaultFilter;
        this.listeners = new HashSet<>();
        this.componentListenerMap = new HashMap<>();
    }

    @NonNull
    @Override
    public State getState() {
        return state.copy();
    }

    @Override
    public synchronized void dispatchAction(@NonNull Action action) {
        middleware.onAction(action, this, this, new Continuation() {
            @Override
            public void next(@NonNull Action<?> action) {
                final State oldState = getState();
                final CombinedReducer.ReduceResult result = reducer.reduce(getState(), action);
                ReduxStore.this.state = result.getNewState();
                notifyListener(oldState, getState(), result.getUpdatedKeys());
            }
        });
    }

    private void notifyListener(State oldState, State newState, Collection<String> updatedKeys) {
        for(Listeners.StateListener listener : listeners) {
            if(listener.getKey() == null || updatedKeys.contains(listener.getKey())) {
                listener.update(oldState, newState);
            }
        }
    }

    @Override
    public void resetFullState(@NonNull State state) {
        final State oldState = getState();
        this.state = state.copy();
        notifyListener(oldState, this.state, reducer.getAllKeys());
    }

    @Override
    public <E> void addListener(@NonNull String key, @NonNull Listener<E> listener) {
        listeners.add(Listeners.create(key, defaultFilter, listener));
    }

    @Override
    public <E> void addListener(@NonNull String key, @NonNull Filter<E> filter, @NonNull Listener<E> listener) {
        listeners.add(Listeners.create(key, filter, listener));
    }

    @Override
    public <E> void addListener(@NonNull Class<E> clazz, @NonNull Listener<E> listener) {
        listeners.add(Listeners.create(clazz, defaultFilter, listener));
    }

    @Override
    public <E> void addListener(@NonNull Class<E> clazz, @NonNull Filter<E> filter, @NonNull Listener<E> listener) {
        listeners.add(Listeners.create(clazz, filter, listener));
    }

    @Override
    public <E> void addListener(@NonNull String key, @NonNull Class<E> clazz, @NonNull Listener<E> listener) {
        listeners.add(Listeners.create(key, clazz, defaultFilter, listener));
    }

    @Override
    public <E> void addListener(@NonNull String key, @NonNull Class<E> clazz, @NonNull Filter<E> filter, @NonNull Listener<E> listener) {
        listeners.add(Listeners.create(key, clazz, filter, listener));
    }

    @Override
    public void addListener(@NonNull Listener<State> listener) {
        listeners.add(Listeners.create(defaultFilter, listener));
    }

    @Override
    public void addListener(@NonNull Filter<State> filter, @NonNull Listener<State> listener) {
        listeners.add(Listeners.create(filter, listener));
    }

    @Override
    public void removeListener(@NonNull Listener<?> listener) {
        listeners.remove(listener);
    }

    @Override
    public <E> void connect(@NonNull Component<State, E> component) {
        Listener<State> listener = Listeners.create(component);
        Listeners.StateListener stateListener = Listeners.create(defaultFilter, listener);

        registerComponent(stateListener, component);
    }

    @Override
    public <E> void connect(@NonNull Component<State, E> component, @NonNull Filter<State> filter) {
        Listener<State> listener = Listeners.create(component);
        Listeners.StateListener stateListener = Listeners.create(filter, listener);

        registerComponent(stateListener, component);
    }


    @Override
    public <E, F> void connect(@NonNull Component<E, F> component, @NonNull String key) {
        Listener<E> listener = Listeners.create(component);
        Listeners.StateListener stateListener = Listeners.create(key, defaultFilter, listener);

        registerComponent(stateListener, component);
    }

    @Override
    public <E, F> void connect(@NonNull Component<E, F> component, @NonNull String key, @NonNull Filter<E> filter) {
        Listener<E> listener = Listeners.create(component);
        Listeners.StateListener stateListener = Listeners.create(key, filter, listener);

        registerComponent(stateListener, component);
    }

    @Override
    public <E, F> void connect(@NonNull final Component<E, F> component, @NonNull Class<E> clazz) {
        Listener<E> listener = Listeners.create(component);
        Listeners.StateListener stateListener = Listeners.create(clazz, defaultFilter, listener);

        registerComponent(stateListener, component);
    }

    @Override
    public <E, F> void connect(@NonNull Component<E, F> component, @NonNull Class<E> clazz, @NonNull Filter<E> filter) {
        Listener<E> listener = Listeners.create(component);
        Listeners.StateListener stateListener = Listeners.create(clazz, filter, listener);

        registerComponent(stateListener, component);
    }

    @Override
    public <E, F> void connect(@NonNull Component<E, F> component, @NonNull String key, @NonNull Class<E> clazz) {
        Listener<E> listener = Listeners.create(component);
        Listeners.StateListener stateListener = Listeners.create(key, clazz, defaultFilter, listener);

        registerComponent(stateListener, component);
    }

    @Override
    public <E, F> void connect(@NonNull Component<E, F> component, @NonNull String key, @NonNull Class<E> clazz, @NonNull Filter<E> filter) {
        Listener<E> listener = Listeners.create(component);
        Listeners.StateListener stateListener = Listeners.create(key, clazz, filter, listener);

        registerComponent(stateListener, component);
    }


    private void registerComponent(Listeners.StateListener listener, Component component) {
        componentListenerMap.put(component, listener);
        listeners.add(listener);
        listener.update(reducer.getEmptyState(), getState());
    }

    @Override
    public void disconnect(@NonNull Component component) {
        if(componentListenerMap.containsKey(component)) {
            final Listener listener = componentListenerMap.remove(component);
            removeListener(listener);
        }
    }

    public static class Builder {

        private final List<Reducer> reducers;
        private State state;
        private List<Middleware> middleware = new ArrayList<>();
        private Filter<Object> notifier = Filters.DEFAULT;

        public Builder(@NonNull List<Reducer> reducers) {
            assertArgumentsNotNull(reducers, "Reducer must not be null");
            this.reducers = reducers;
        }

        public Builder(@NonNull Reducer... reducers) {
            assertArgumentsNotNull(reducers, "Reducer must not be null");
            this.reducers = Arrays.asList(reducers);
        }

        public Builder withInitialState(@NonNull State state) {
            assertArgumentsNotNull(state, "Initial state must not be null");
            this.state = state;
            return this;
        }

        public Builder withMiddleware(@NonNull List<Middleware> middleware) {
            assertArgumentsNotNull(middleware, "Middleware must not be null");
            this.middleware = middleware;
            return this;
        }

        public Builder withMiddleware(@NonNull Middleware... middleware) {
            assertArgumentsNotNull(middleware, "Middleware must not be null");
            this.middleware = Arrays.asList(middleware);
            return this;
        }

        public Builder withDefaultNotifier(Filter<Object> filter) {
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
