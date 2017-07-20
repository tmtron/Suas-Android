package com.zendesk.suas;

public class Notifiers {

    public static final Notifier DEFAULT = new DefaultNotifier();
    public static final Notifier EQUALS = new EqualsNotifier();

    private static class DefaultNotifier implements Notifier<Object> {

        @Override
        public void update(Object newState, Object oldState, Listener listener) {
            listener.update(newState, oldState);
        }

    }

    private static class EqualsNotifier implements Notifier<Object> {

        @Override
        public void update(Object newState, Object oldState, Listener listener) {
            if(!oldState.equals(newState)) {
                listener.update(newState, oldState);
            }
        }

    }

}
