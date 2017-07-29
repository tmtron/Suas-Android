package com.zendesk.suas;

import android.support.annotation.NonNull;

public class Filters {

    public static final Filter DEFAULT = new DefaultFilter();
    public static final Filter EQUALS = new EqualsFilter();

    private Filters() {
        // intentionally empty
    }

    private static class DefaultFilter implements Filter {

        @Override
        public boolean filter(@NonNull Object oldState, @NonNull Object newState) {
            return true;
        }

    }

    private static class EqualsFilter implements Filter {

        @Override
        public boolean filter(@NonNull Object oldState, @NonNull Object newState) {
            return !oldState.equals(newState);
        }

    }

}
