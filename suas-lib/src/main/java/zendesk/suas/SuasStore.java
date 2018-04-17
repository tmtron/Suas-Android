package zendesk.suas;


import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Default implementation of {@link Store}
 */
class SuasStore implements Store {

    private State state;
    private final CombinedReducer reducer;
    private final CombinedMiddleware middleware;
    private final Filter defaultFilter;
    private final Executor executor;
    private final Map<Listener, Listeners.StateListener> listenerStateListenerMap;
    private final Set<Listener<Action<?>>> actionListeners;
    private final AtomicBoolean isReducing = new AtomicBoolean(false);

    SuasStore(State state, CombinedReducer reducer, CombinedMiddleware combinedMiddleware,
              Filter<Object> defaultFilter, Executor executor) {
        this.state = state;
        this.reducer = reducer;
        this.middleware = combinedMiddleware;
        this.defaultFilter = defaultFilter;
        this.executor = executor;
        this.actionListeners = Collections.synchronizedSet(new HashSet<Listener<Action<?>>>());
        this.listenerStateListenerMap = new ConcurrentHashMap<>();
    }

    @NonNull
    @Override
    public State getState() {
        return state.copy();
    }

    @Override
    public synchronized void dispatch(@NonNull final Action action) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                notifyActionListener(action);
                middleware.onAction(action, SuasStore.this, SuasStore.this, new Continuation() {
                    @Override
                    public void next(@NonNull Action<?> action) {
                        if(isReducing.compareAndSet(false,true)) {
                            final State oldState = getState();
                            final CombinedReducer.ReduceResult result = reducer.reduce(getState(), action);
                            SuasStore.this.state = result.getNewState();
                            isReducing.set(false);
                            notifyListener(oldState, getState(), result.getUpdatedKeys());
                        } else {
                            throw new RuntimeException("You must not dispatch actions in your reducer. Seriously. (╯°□°）╯︵ ┻━┻");
                        }
                    }
                });
            }
        });
    }

    private void notifyListener(State oldState, State newState, Collection<String> updatedKeys) {
        for(Listeners.StateListener listener : listenerStateListenerMap.values()) {
            if(listener.getStateKey() == null || updatedKeys.contains(listener.getStateKey())) {
                listener.update(oldState, newState, false);
            }
        }
    }

    private void notifyActionListener(Action<?> action) {
        for (Listener<Action<?>> listener : actionListeners) {
            listener.update(action);
        }
    }

    @Override
    public void reset(@NonNull State state) {
        final State oldState = getState();
        this.state = State.mergeStates(reducer.getEmptyState(), state);
        notifyListener(oldState, this.state, reducer.getAllKeys());
    }

    @Override
    public <E> Subscription addListener(@NonNull String stateKey, @NonNull Listener<E> listener) {
        return registerListener(listener, Listeners.create(stateKey, defaultFilter, listener));
    }

    @Override
    public <E> Subscription addListener(@NonNull String stateKey, @NonNull Filter<E> filter, @NonNull Listener<E> listener) {
        return registerListener(listener, Listeners.create(stateKey, filter, listener));
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
    public <E> Subscription addListener(@NonNull String stateKey, @NonNull Class<E> clazz, @NonNull Listener<E> listener) {
        return registerListener(listener, Listeners.create(stateKey, clazz, defaultFilter, listener));
    }

    @Override
    public <E> Subscription addListener(@NonNull String stateKey, @NonNull Class<E> clazz, @NonNull Filter<E> filter, @NonNull Listener<E> listener) {
        return registerListener(listener, Listeners.create(stateKey, clazz, filter, listener));
    }

    @Override
    public Subscription addActionListener(Listener<Action<?>> actionListener) {
        final Subscription subscription = new ActionListenerSubscription(actionListener);
        subscription.addListener();
        return subscription;
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
       actionListeners.remove(listener);
    }

    private Subscription registerListener(Listener listener, Listeners.StateListener stateListener) {
        final Subscription suasSubscription = new DefaultSubscription(stateListener, listener);
        suasSubscription.addListener();
        return suasSubscription;
    }

    private class ActionListenerSubscription implements Subscription {

        private final Listener<Action<?>> listener;

        private ActionListenerSubscription(Listener<Action<?>> listener) {
            this.listener = listener;
        }

        @Override
        public void removeListener() {
            SuasStore.this.removeListener(listener);
        }

        @Override
        public void addListener() {
            actionListeners.add(listener);
        }

        @Override
        public void informWithCurrentState() {
            // no implemented
        }

    }

    private class DefaultSubscription implements Subscription {

        private final Listeners.StateListener stateListener;
        private final Listener listener;

        DefaultSubscription(Listeners.StateListener stateListener, Listener listener) {
            this.stateListener = stateListener;
            this.listener = listener;
        }

        @Override
        public void removeListener() {
            SuasStore.this.removeListener(listener);
        }

        @Override
        public void addListener() {
            listenerStateListenerMap.put(listener, stateListener);
        }

        @Override
        public void informWithCurrentState() {
            stateListener.update(null, getState(), true);
        }
    }
}
