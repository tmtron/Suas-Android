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
        void update(State oldState, State newState);
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
        public void update(State oldState, State newState) {
            if(filter.filter(oldState, newState)) {
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
        public void update(State oldState, State newState) {
            if(filter.filter(oldState, newState)) {
                listener.update(newState);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Default that = (Default) o;

            return listener != null ? listener.equals(that.listener) : that.listener == null;
        }

        @Override
        public int hashCode() {
            return listener != null ? listener.hashCode() : 0;
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
        public void update(State oldState, State newState) {
            try {
                @SuppressWarnings("unchecked") final E oldStateTyped = (E) oldState.getState(key);
                @SuppressWarnings("unchecked") final E newStateTyped = (E) newState.getState(key);
                if(newStateTyped != null && oldStateTyped != null&& filter.filter(oldStateTyped, newStateTyped)) {
                    listener.update(newStateTyped);
                } else {
                    L.log(Level.WARNING, KEY_NOT_FOUND);
                }
            } catch (ClassCastException ignored) {
                L.log(Level.WARNING, WRONG_TYPE);
            }
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            StringKeyedListener<?> that = (StringKeyedListener<?>) o;

            return listener != null ? listener.equals(that.listener) : that.listener == null;
        }

        @Override
        public int hashCode() {
            return listener != null ? listener.hashCode() : 0;
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
        public void update(State oldState, State newState) {
            final E oldStateTyped = oldState.getState(clazz);
            final E newStateTyped = newState.getState(clazz);

            if(oldStateTyped != null && newStateTyped != null) {
                if(filter.filter(oldStateTyped, newStateTyped)) {
                    listener.update(newStateTyped);
                }
            } else {
                L.log(Level.WARNING, WRONG_TYPE + " or " + KEY_NOT_FOUND);
            }
        }

        @Override
        public String getKey() {
            return State.keyForClass(clazz);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ClassKeyedListener<?> that = (ClassKeyedListener<?>) o;

            return listener != null ? listener.equals(that.listener) : that.listener == null;
        }

        @Override
        public int hashCode() {
            return listener != null ? listener.hashCode() : 0;
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
        public void update(State oldState, State newState) {
            final E oldStateTyped = oldState.getState(key, clazz);
            final E newStateTyped = newState.getState(key, clazz);

            if(oldStateTyped != null && newStateTyped != null && filter.filter(oldStateTyped, newStateTyped)) {
                listener.update(newStateTyped);
            } else {
                L.log(Level.WARNING, WRONG_TYPE + " or " + KEY_NOT_FOUND);
            }
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ClassStringKeyedListener<?> that = (ClassStringKeyedListener<?>) o;

            return listener != null ? listener.equals(that.listener) : that.listener == null;
        }

        @Override
        public int hashCode() {
            return listener != null ? listener.hashCode() : 0;
        }

    }

}
