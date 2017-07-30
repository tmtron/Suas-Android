package zendesk.suas;


import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class DefaultStore implements Store {

    private State state;
    private final CombinedReducer reducer;
    private final CombinedMiddleware middleware;
    private final Filter defaultFilter;

    private final Map<Listener, Listeners.StateListener> listenerStateListenerMap;
    private final Map<Component, Listeners.StateListener> componentListenerMap;

    DefaultStore(State state, CombinedReducer reducer, CombinedMiddleware combinedMiddleware, Filter<Object> defaultFilter) {
        this.state = state;
        this.reducer = reducer;
        this.middleware = combinedMiddleware;
        this.defaultFilter = defaultFilter;
        this.listenerStateListenerMap = new HashMap<>();
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
                DefaultStore.this.state = result.getNewState();
                notifyListener(oldState, getState(), result.getUpdatedKeys());
            }
        });
    }

    private void notifyListener(State oldState, State newState, Collection<String> updatedKeys) {
        final Collection<Listeners.StateListener> listeners = new ArrayList<>();
        listeners.addAll(componentListenerMap.values());
        listeners.addAll(listenerStateListenerMap.values());

        for(Listeners.StateListener listener : listeners) {
            if(listener.getKey() == null || updatedKeys.contains(listener.getKey())) {
                listener.update(oldState, newState);
            }
        }
    }

    @Override
    public void reset(@NonNull State state) {
        final State oldState = getState();
        this.state = state.copy();
        notifyListener(oldState, this.state, reducer.getAllKeys());
    }

    @Override
    public <E> void addListener(@NonNull String key, @NonNull Listener<E> listener) {
        registerListener(listener, Listeners.create(key, defaultFilter, listener));
    }

    @Override
    public <E> void addListener(@NonNull String key, @NonNull Filter<E> filter, @NonNull Listener<E> listener) {
        registerListener(listener, Listeners.create(key, filter, listener));
    }

    @Override
    public <E> void addListener(@NonNull Class<E> clazz, @NonNull Listener<E> listener) {
        registerListener(listener, Listeners.create(clazz, defaultFilter, listener));
    }

    @Override
    public <E> void addListener(@NonNull Class<E> clazz, @NonNull Filter<E> filter, @NonNull Listener<E> listener) {
        registerListener(listener, Listeners.create(clazz, filter, listener));
    }

    @Override
    public <E> void addListener(@NonNull String key, @NonNull Class<E> clazz, @NonNull Listener<E> listener) {
        registerListener(listener, Listeners.create(key, clazz, defaultFilter, listener));
    }

    @Override
    public <E> void addListener(@NonNull String key, @NonNull Class<E> clazz, @NonNull Filter<E> filter, @NonNull Listener<E> listener) {
        registerListener(listener, Listeners.create(key, clazz, filter, listener));
    }

    @Override
    public void addListener(@NonNull Listener<State> listener) {
        registerListener(listener, Listeners.create(defaultFilter, listener));
    }

    @Override
    public void addListener(@NonNull Filter<State> filter, @NonNull Listener<State> listener) {
        registerListener(listener, Listeners.create(filter, listener));
    }

    @Override
    public void removeListener(@NonNull Listener listener) {
        listenerStateListenerMap.remove(listener);
    }

    private void registerListener(Listener listener, Listeners.StateListener stateListener) {
        listenerStateListenerMap.put(listener, stateListener);
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
        listener.update(reducer.getEmptyState(), getState());
    }

    @Override
    public void disconnect(@NonNull Component component) {
        componentListenerMap.remove(component);
        System.out.println();
    }

}
