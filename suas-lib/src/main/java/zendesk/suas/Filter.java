package zendesk.suas;

import android.support.annotation.NonNull;

/**
 * Interface that gets called before notifying a {@link Listener}.
 * It can decide if the update will be passed along or not.
 */
public interface Filter<E> {

    /**
     * Decide whether to pass the update to the associated {@link Listener}
     *
     * @param oldState old state
     * @param newState new state
     * @return {@code true} if the component or listener should be notified, {@code false} otherwise
     */
    boolean filter(@NonNull E oldState, @NonNull E newState);
}
