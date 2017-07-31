package zendesk.suas;


import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class DefaultStore implements Store {

    private State state;
    private final CombinedReducer reducer;
    private final CombinedMiddleware middleware;
    private final Filter defaultFilter;

    private final Map<Listener, Listeners.StateListener> listenerStateListenerMap;

    DefaultStore(State state, CombinedReducer reducer, CombinedMiddleware combinedMiddleware,
                 Filter<Object> defaultFilter) {
        this.state = state;
        this.reducer = reducer;
        this.middleware = combinedMiddleware;
        this.defaultFilter = defaultFilter;
        this.listenerStateListenerMap = new HashMap<>();
    }

    @NonNull
    @Override
    public State getState() {
        return state.copy();
    }

    @Override
    public synchronized void dispatchAction(@NonNull Action action) {
        middleware.onAction(action, this, new Continuation() {
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
        for(Listeners.StateListener listener : listenerStateListenerMap.values()) {
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
    public <E> Subscription addListener(@NonNull String key, @NonNull Listener<E> listener) {
        return registerListener(listener, Listeners.create(key, defaultFilter, listener));
    }

    @Override
    public <E> Subscription addListener(@NonNull String key, @NonNull Filter<E> filter, @NonNull Listener<E> listener) {
        return registerListener(listener, Listeners.create(key, filter, listener));
    }

    @Override
    public <E> Subscription addListener(@NonNull StateSelector<E> stateSelector, @NonNull Listener<E> listener) {
        return registerListener(listener, Listeners.create(stateSelector, defaultFilter, listener));
    }

    @Override
    public <E> Subscription addListener(@NonNull Filter<State> filter, @NonNull StateSelector<E> stateSelector, @NonNull Listener<E> listener) {
        return registerListener(listener, Listeners.create(stateSelector, filter, listener));
    }

    @Override
    public <E> Subscription addListener(@NonNull Class<E> clazz, @NonNull Listener<E> listener) {
        return registerListener(listener, Listeners.create(clazz, defaultFilter, listener));
    }

    @Override
    public <E> Subscription addListener(@NonNull Class<E> clazz, @NonNull Filter<E> filter, @NonNull Listener<E> listener) {
        return registerListener(listener, Listeners.create(clazz, filter, listener));
    }

    @Override
    public <E> Subscription addListener(@NonNull String key, @NonNull Class<E> clazz, @NonNull Listener<E> listener) {
        return registerListener(listener, Listeners.create(key, clazz, defaultFilter, listener));
    }

    @Override
    public <E> Subscription addListener(@NonNull String key, @NonNull Class<E> clazz, @NonNull Filter<E> filter, @NonNull Listener<E> listener) {
        return registerListener(listener, Listeners.create(key, clazz, filter, listener));
    }

    @Override
    public Subscription addListener(@NonNull Listener<State> listener) {
        return registerListener(listener, Listeners.create(defaultFilter, listener));
    }

    @Override
    public Subscription addListener(@NonNull Filter<State> filter, @NonNull Listener<State> listener) {
        return registerListener(listener, Listeners.create(filter, listener));
    }

    @Override
    public void removeListener(@NonNull Listener listener) {
        listenerStateListenerMap.remove(listener);
    }

    private Subscription registerListener(Listener listener, Listeners.StateListener stateListener) {
        final Subscription suasSubscription = new DefaultSuasSubscription(stateListener, listener);
        suasSubscription.subscribe();
        return suasSubscription;
    }

    private class DefaultSuasSubscription implements Subscription {

        private final Listeners.StateListener stateListener;
        private final Listener listener;

        DefaultSuasSubscription(Listeners.StateListener stateListener, Listener listener) {
            this.stateListener = stateListener;
            this.listener = listener;
        }

        @Override
        public void unsubscribe() {
            removeListener(listener);
        }

        @Override
        public void subscribe() {
            listenerStateListenerMap.put(listener, stateListener);
        }

        @Override
        public void update() {
            stateListener.update(reducer.getEmptyState(), getState());
        }
    }
}
