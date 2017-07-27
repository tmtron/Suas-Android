package com.zendesk.suas;

public class Filters {

    public static final Filter DEFAULT = new DefaultFilter();
    public static final Filter EQUALS = new EqualsFilter();

    private static class DefaultFilter implements Filter {

        @Override
        public boolean filter(Object oldState, Object newState) {
            return true;
        }

    }

    private static class EqualsFilter implements Filter {

        @Override
        public boolean filter(Object oldState, Object newState) {
            return !oldState.equals(newState);
        }
    }

}
