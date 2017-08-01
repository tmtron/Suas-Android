package zendesk.suas;


import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class for registering {@link Listener} or {@link Component} to a
 * certain part of the {@link State}
 */
class Listeners {

    private static final Logger L = Logger.getLogger("Suas");
    private static final String WRONG_TYPE = "Either new value or old value cannot be converted to type expected type.";
    private static final String KEY_NOT_FOUND = "Requested key not found in store";

    private Listeners() {
        // intentionally empty
    }

    static <E> StateListener create(String key, Filter<E> notifier, Listener<E> listener) {
        return new StringKeyedListener<>(key, listener, notifier);
    }

    static <E> StateListener create(Class<E> clazz, Filter<E> notifier, Listener<E> listener) {
        return new ClassKeyedListener<>(clazz, listener, notifier);
    }

    static <E> StateListener create(String key, Class<E> clazz, Filter<E> notifier, Listener<E> listener) {
        return new ClassStringKeyedListener<>(key, clazz, listener, notifier);
    }

    static StateListener create(Filter<State> notifier, Listener<State> listener) {
        return new Default(listener, notifier);
    }

    static <E> StateListener create(StateSelector<E> stateSelector, Filter<State> filter, Listener<E> listener) {
        return new StateSelectorListener<>(listener, stateSelector, filter);
    }

    interface StateListener {
        String getKey();
        void update(State oldState, State newState, boolean skipFilter);
    }

    private static class StateSelectorListener<E> implements StateListener {

        private final Listener<E> listener;
        private final StateSelector<E> stateSelector;
        private final Filter<State> filter;

        private StateSelectorListener(Listener<E> listener, StateSelector<E> stateSelector, Filter<State> filter) {
            this.listener = listener;
            this.stateSelector = stateSelector;
            this.filter = filter;
        }

        @Override
        public String getKey() {
            return null;
        }

        @Override
        public void update(State oldState, State newState, boolean skipFilter) {
            if(skipFilter || filter.filter(oldState, newState)) {
                final E data = stateSelector.selectData(newState);
                if(data != null) {
                    listener.update(data);
                }
            }
        }
    }

    private static class Default implements StateListener {

        private final Listener<State> listener;
        private final Filter<State> filter;

        private Default(Listener<State> listener, Filter<State> filter) {
            this.listener = listener;
            this.filter = filter;
        }

        @Override
        public void update(State oldState, State newState, boolean skipFilter) {
            if(filter.filter(oldState, newState) || skipFilter) {
                listener.update(newState);
            }
        }

        @Override
        public String getKey() {
            return null;
        }

    }

    private static class StringKeyedListener<E> implements StateListener {

        private final String key;
        private final Listener<E> listener;
        private final Filter<E> filter;

        private StringKeyedListener(String key, Listener<E> listener, Filter<E> filter) {
            this.key = key;
            this.listener = listener;
            this.filter = filter;
        }

        @Override
        public void update(State oldState, State newState, boolean skipFilter) {
            try {
                E newStateTyped = null;
                E oldStateTyped = null;

                if(oldState != null) {
                    //noinspection unchecked
                    oldStateTyped = (E) oldState.getState(key);
                }
                if(newState != null) {
                    //noinspection unchecked
                    newStateTyped = (E) newState.getState(key);
                }

                Listeners.update(newStateTyped, oldStateTyped, filter, listener, skipFilter);

            } catch (ClassCastException ignored) {
                L.log(Level.WARNING, WRONG_TYPE);
            }
        }

        @Override
        public String getKey() {
            return key;
        }

    }

    private static class ClassKeyedListener<E> implements StateListener {

        private final Class<E> clazz;
        private final Listener<E> listener;
        private final Filter<E> filter;

        private ClassKeyedListener(Class<E> clazz, Listener<E> listener, Filter<E> filter) {
            this.clazz = clazz;
            this.listener = listener;
            this.filter = filter;
        }

        @Override
        public void update(State oldState, State newState, boolean skipFilter) {

            E newStateTyped = null;
            E oldStateTyped = null;

            if(oldState != null) {
                oldStateTyped = oldState.getState(clazz);
            }
            if(newState != null) {
                newStateTyped = newState.getState(clazz);
            }

            Listeners.update(newStateTyped, oldStateTyped, filter, listener, skipFilter);
        }

        @Override
        public String getKey() {
            return State.keyForClass(clazz);
        }

    }

    private static class ClassStringKeyedListener<E> implements StateListener {

        private final Class<E> clazz;
        private final String key;
        private final Listener<E> listener;
        private final Filter<E> filter;

        private ClassStringKeyedListener(String key, Class<E> clazz, Listener<E> listener, Filter<E> filter) {
            this.clazz = clazz;
            this.listener = listener;
            this.key = key;
            this.filter = filter;
        }

        @Override
        public void update(State oldState, State newState, boolean skipFilter) {

            E newStateTyped = null;
            E oldStateTyped = null;

            if(oldState != null) {
                oldStateTyped = oldState.getState(key, clazz);
            }
            if(newState != null) {
                newStateTyped = newState.getState(key, clazz);
            }

            Listeners.update(newStateTyped, oldStateTyped, filter, listener, skipFilter);
        }

        @Override
        public String getKey() {
            return key;
        }

    }


    private static <E> void update(E newState, E oldState, Filter<E> filter, Listener<E> listener, boolean skipFilter) {
        if(newState != null && skipFilter) {
            listener.update(newState);

        } else if(newState != null && oldState != null) {
            if(filter.filter(oldState, newState)) {
                listener.update(newState);
            }

        } else {
            L.log(Level.WARNING, KEY_NOT_FOUND);
        }
    }

}
