package zendesk.suas;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Definition of a Reducer. The implementation should be able to
 * mutate state on a provided {@link Action}.
 * <br>
 * Each {@link Reducer} implementation is responsible for a certain type of state.
 *
 * @param <E> type of the state
 */
public abstract class Reducer<E> {

    /**
     * Reduce state. Apply the provided action on the oldState.
     *
     * @return the new state, or {@code null} if nothing has changed
     */
    @Nullable
    public abstract E reduce(@NonNull E state, @NonNull Action<?> action);

    /**
     * Returns an empty or default instance of {@code E}.
     */
    @NonNull
    public abstract E getInitialState();

    /**
     * Gets a state key that's unique among all {@link Reducer}.
     */
    @NonNull
    public String getStateKey() {
        return State.keyForClass(getInitialState().getClass());
    }

}
