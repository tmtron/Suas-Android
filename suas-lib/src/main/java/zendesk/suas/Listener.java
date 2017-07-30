package zendesk.suas;

import android.support.annotation.NonNull;

/**
 * Callback used to register for update on the {@link Store}
 */
public interface Listener<E> {
    /**
     * Called if there's an update to the {@link State}
     *
     * @param oldState previous state
     * @param newState new state
     */
    void update(@NonNull E oldState, @NonNull E newState);
}
