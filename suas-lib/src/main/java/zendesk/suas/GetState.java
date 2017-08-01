package zendesk.suas;


import android.support.annotation.NonNull;

/**
 * Interface for getting the current {@link State}
 */
public interface GetState {

    /**
     * Gets a copy of the current {@link State} from {@link Store}.
     *
     * @return copy of the current state
     */
    @NonNull
    State getState();
}
