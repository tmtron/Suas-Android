package zendesk.suas;

import android.support.annotation.NonNull;


public interface StoreApi {

    /**
     * Gets a copy of the current {@link State} from {@link Store}.
     *
     * @return copy of the current state
     */
    @NonNull
    State getState();

    /**
     * Dispatches an {@link Action} to the {@link Store}.
     */
    void dispatchAction(@NonNull Action action);

}
