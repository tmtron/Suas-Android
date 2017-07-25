package com.zendesk.suas;


class Listeners {

    static <E> Listener<State> create(String key, Notifier notifier, Listener<E> listener) {
        return new StringKeyedListener<>(key, listener, notifier);
    }

    static <E> Listener<State> create(Class<E> clazz, Notifier notifier, Listener<E> listener) {
        return new ClassKeyedListener<>(clazz, listener, notifier);
    }

    static <E> Listener<State> create(String key, Class<E> clazz, Notifier notifier, Listener<E> listener) {
        return new ClassStringKeyedListener<>(key, clazz, listener, notifier);
    }

    static Listener<State> create(Notifier notifier, Listener<State> listener) {
        return new Default(listener, notifier);
    }

    static <E, F> Listener<E> create(Component<E, F> component) {
        return new ComponentListener<>(component);
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

    private static class Default implements Listener<State> {

        private final Listener<State> listener;
        private final Notifier<State> notifier;

        private Default(Listener<State> listener, Notifier<State> notifier) {
            this.listener = listener;
            this.notifier = notifier;
        }

        @Override
        public void update(State oldState, State newState) {
            notifier.update(oldState, newState, listener);
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

    private static class StringKeyedListener<E> implements Listener<State> {

        private final String key;
        private final Listener<E> listener;
        private final Notifier<E> notifier;

        private StringKeyedListener(String key, Listener<E> listener, Notifier<E> notifier) {
            this.key = key;
            this.listener = listener;
            this.notifier = notifier;
        }

        @Override
        public void update(State oldState, State newState) {
            notifier.update(
                    (E)oldState.getState(key), // type check
                    (E)newState.getState(key), // type check
                    listener);
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

    private static class ClassKeyedListener<E> implements Listener<State> {

        private final Class<E> clazz;
        private final Listener<E> listener;
        private final Notifier<E> notifier;

        private ClassKeyedListener(Class<E> clazz, Listener<E> listener, Notifier<E> notifier) {
            this.clazz = clazz;
            this.listener = listener;
            this.notifier = notifier;
        }

        @Override
        public void update(State oldState, State newState) {
            notifier.update(
                    (E)oldState.getState(clazz), // FIXME null check
                    (E)newState.getState(clazz), // FIXME null check
                    listener
            );
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

    private static class ClassStringKeyedListener<E> implements Listener<State> {

        private final Class<E> clazz;
        private final String key;
        private final Listener<E> listener;
        private final Notifier<E> notifier;


        private ClassStringKeyedListener(String key, Class<E> clazz, Listener<E> listener, Notifier<E> notifier) {
            this.clazz = clazz;
            this.listener = listener;
            this.key = key;
            this.notifier = notifier;
        }

        @Override
        public void update(State oldState, State newState) {
            notifier.update(
                    (E)oldState.getState(key, clazz), // FIXME
                    (E)newState.getState(key, clazz), // FIXME null check
                    listener
            );
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
