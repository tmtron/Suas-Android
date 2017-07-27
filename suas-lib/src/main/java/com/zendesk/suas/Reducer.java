package com.zendesk.suas;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Definition of a Reducer. The implementation should be able to
 * mutate state on a provided {@link Action}.
 * <br />
 * Each {@link Reducer} implementation is responsible for a certain type of state.
 *
 * @param <E> type of the state
 */
public abstract class Reducer<E> {

    /**
     * Reduce state. Apply the provided action on the oldState.
     */
    @Nullable
    public abstract E reduce(@NonNull E oldState, @NonNull Action<?> action);

    /**
     * Returns an empty or default instance of {@code E}.
     */
    @NonNull
    public abstract E getEmptyState();

    /**
     * Gets a key that's unique among all {@link Reducer}.
     */
    @NonNull
    public String getKey() {
        return State.keyForClass(getEmptyState().getClass());
    }

}
