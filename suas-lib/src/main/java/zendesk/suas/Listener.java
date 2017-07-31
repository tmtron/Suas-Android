package zendesk.suas;

import android.support.annotation.NonNull;

/**
 * Callback used to register for update on the {@link Store}
 */
public interface Listener<E> {
    /**
     * Called if there's an update to the {@link State}
     *
     */
    void update(@NonNull E state);
}
