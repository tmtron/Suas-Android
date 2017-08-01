package zendesk.suas;

import android.support.annotation.NonNull;

/**
 * Function for dispatching an {@link Action} to the {@link Store}.
 */
public interface Dispatcher {

    /**
     * Dispatches an {@link Action} to the {@link Store}.
     */
    void dispatchAction(@NonNull Action action);

}
