package zendesk.suas;

import android.support.annotation.NonNull;

/**
 * Set of basic implementation of {@link Filter}
 */
public class Filters {

    /**
     * Default implementation of {@link Filter}, that always returns {@code true}.
     */
    public static final Filter DEFAULT = new DefaultFilter();

    /**
     * Equals backed implementation of {@link Filter}, returns {@code true} or {@code false} based
     * on {@link Object#equals(Object)}
     */
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
