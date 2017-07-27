package com.zendesk.suas;


import java.util.logging.Level;
import java.util.logging.Logger;

class Listeners {

    private static final Logger L = Logger.getLogger("Suas");

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

    static <E, F> Listener<E> create(Component<E, F> component) {
        return new ComponentListener<>(component);
    }

    interface StateListener extends Listener<State> {
        String getKey();
    }

    private static class ComponentListener<E, F> implements Listener<E> {

        private final Component<E, F> component;

        private ComponentListener(Component<E, F> component) {
            this.component = component;
        }

        @Override
        public void update(E oldState, E newState) {
            component.update(component.getSelector().selectData(newState));
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
                listener.update(oldState, newState);
            }
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
                @SuppressWarnings("unchecked") final E newStateTyped = (E) oldState.getState(key);
                if(filter.filter(oldStateTyped, newStateTyped)) {
                    listener.update(oldStateTyped, newStateTyped);
                }
            } catch (ClassCastException ignored) {
                L.log(Level.WARNING, "Either new value or old value cannot be converted to type expected type.");
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

            ClassKeyedListener<?> that = (ClassKeyedListener<?>) o;

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
            E oldStateTyped = oldState.getState(clazz);
            E newStateTyped = newState.getState(clazz);

            if(oldStateTyped != null && newStateTyped != null) {
                if(filter.filter(oldStateTyped, newStateTyped)) {
                    listener.update(oldStateTyped, newStateTyped);
                }
            } else {
                L.log(Level.WARNING, "Either new value or old value cannot be converted to type expected type.");
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
            E oldStateTyped = oldState.getState(key, clazz);
            E newStateTyped = newState.getState(key, clazz);

            if(oldStateTyped != null && newStateTyped != null) {
                if(filter.filter(oldStateTyped, newStateTyped)) {
                    listener.update(oldStateTyped, newStateTyped);
                }
            } else {
                L.log(Level.WARNING, "Either new value or old value cannot be converted to type expected type.");
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

            ClassKeyedListener<?> that = (ClassKeyedListener<?>) o;

            return listener != null ? listener.equals(that.listener) : that.listener == null;
        }

        @Override
        public int hashCode() {
            return listener != null ? listener.hashCode() : 0;
        }

    }

}
