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
    private final Notifier defaultNotifier;

    private final Collection<Listeners.StateListener> listeners;
    private final Map<Component, Listener> componentListenerMap;

    private ReduxStore(State state, CombinedReducer reducer, CombinedMiddleware combinedMiddleware, Notifier<Object> defaultNotifier) {
        this.state = state;
        this.reducer = reducer;
        this.middleware = combinedMiddleware;
        this.defaultNotifier = defaultNotifier;
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
            public void next(Action<?> action) {
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
    public void resetFullState(State state) {
        final State oldState = getState();
        this.state = state.copy();
        notifyListener(oldState, this.state, reducer.getAllKeys());
    }

    @Override
    public <E> void addListener(String key, Listener<E> listener) {
        listeners.add(Listeners.create(key, defaultNotifier, listener));
    }

    @Override
    public <E> void addListener(String key, Notifier<E> notifier, Listener<E> listener) {
        listeners.add(Listeners.create(key, notifier, listener));
    }

    @Override
    public <E> void addListener(Class<E> clazz, Listener<E> listener) {
        listeners.add(Listeners.create(clazz, defaultNotifier, listener));
    }

    @Override
    public <E> void addListener(Class<E> clazz, Notifier<E> notifier, Listener<E> listener) {
        listeners.add(Listeners.create(clazz, notifier, listener));
    }

    @Override
    public <E> void addListener(String key, Class<E> clazz, Listener<E> listener) {
        listeners.add(Listeners.create(key, clazz, defaultNotifier, listener));
    }

    @Override
    public <E> void addListener(String key, Class<E> clazz, Notifier<E> notifier, Listener<E> listener) {
        listeners.add(Listeners.create(key, clazz, notifier, listener));
    }

    @Override
    public void addListener(Listener<State> listener) {
        listeners.add(Listeners.create(defaultNotifier, listener));
    }

    @Override
    public void addListener(Notifier<State> notifier, Listener<State> listener) {
        listeners.add(Listeners.create(notifier, listener));
    }

    @Override
    public void removeListener(Listener<?> listener) {
        listeners.remove(listener);
    }

    @Override
    public <E> void connect(Component<State, E> component) {
        Listener<State> listener = Listeners.create(component);
        Listeners.StateListener stateListener = Listeners.create(defaultNotifier, listener);

        registerComponent(stateListener, component);
    }

    @Override
    public <E> void connect(Component<State, E> component, Notifier<State> notifier) {
        Listener<State> listener = Listeners.create(component);
        Listeners.StateListener stateListener = Listeners.create(notifier, listener);

        registerComponent(stateListener, component);
    }


    @Override
    public <E, F> void connect(Component<E, F> component, String key) {
        Listener<E> listener = Listeners.create(component);
        Listeners.StateListener stateListener = Listeners.create(key, defaultNotifier, listener);

        registerComponent(stateListener, component);
    }

    @Override
    public <E, F> void connect(Component<E, F> component, String key, Notifier<E> notifier) {
        Listener<E> listener = Listeners.create(component);
        Listeners.StateListener stateListener = Listeners.create(key, notifier, listener);

        registerComponent(stateListener, component);
    }

    @Override
    public <E, F> void connect(final Component<E, F> component, Class<E> clazz) {
        Listener<E> listener = Listeners.create(component);
        Listeners.StateListener stateListener = Listeners.create(clazz, defaultNotifier, listener);

        registerComponent(stateListener, component);
    }

    @Override
    public <E, F> void connect(Component<E, F> component, Class<E> clazz, Notifier<E> notifier) {
        Listener<E> listener = Listeners.create(component);
        Listeners.StateListener stateListener = Listeners.create(clazz, notifier, listener);

        registerComponent(stateListener, component);
    }

    @Override
    public <E, F> void connect(Component<E, F> component, String key, Class<E> clazz) {
        Listener<E> listener = Listeners.create(component);
        Listeners.StateListener stateListener = Listeners.create(key, clazz, defaultNotifier, listener);

        registerComponent(stateListener, component);
    }

    @Override
    public <E, F> void connect(Component<E, F> component, String key, Class<E> clazz, Notifier<E> notifier) {
        Listener<E> listener = Listeners.create(component);
        Listeners.StateListener stateListener = Listeners.create(key, clazz, notifier, listener);

        registerComponent(stateListener, component);
    }


    private void registerComponent(Listeners.StateListener listener, Component component) {
        componentListenerMap.put(component, listener);
        listeners.add(listener);
        listener.update(reducer.getEmptyState(), getState());
    }

    @Override
    public void disconnect(Component component) {
        if(componentListenerMap.containsKey(component)) {
            final Listener listener = componentListenerMap.remove(component);
            removeListener(listener);
        }
    }

    public static class Builder {

        private final List<Reducer> reducers;
        private State state;
        private List<Middleware> middleware = new ArrayList<>();
        private Notifier<Object> notifier = Notifiers.DEFAULT;

        public Builder(@NonNull List<Reducer> reducers) {
            if(reducers == null) throw new IllegalArgumentException("Reducer must not be null");
            this.reducers = reducers;
        }

        public Builder(@NonNull Reducer... reducers) {
            if(reducers == null) throw new IllegalArgumentException("Reducer must not be null");
            this.reducers = Arrays.asList(reducers);
        }

        public Builder withInitialState(@NonNull State state) {
            if(state == null) throw new IllegalArgumentException("Initial state must not be null");
            this.state = state;
            return this;
        }

        public Builder withMiddleware(@NonNull List<Middleware> middleware) {
            if(middleware == null) throw new IllegalArgumentException("Middleware must not be null");
            this.middleware = middleware;
            return this;
        }

        public Builder withMiddleware(@NonNull Middleware... middleware) {
            if(middleware == null) throw new IllegalArgumentException("Middleware must not be null");
            this.middleware = Arrays.asList(middleware);
            return this;
        }

        public Builder withDefaultNotifier(Notifier<Object> notifier) {
            if(notifier == null) throw new IllegalArgumentException("Notifier must not be null");
            this.notifier = notifier;
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
    }

}
