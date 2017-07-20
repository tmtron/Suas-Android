package com.zendesk.suas;

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
     * This method ALWAYS returns new a state. Even if it's doing nothing.
     */
    public abstract E reduce(E oldState, Action<?> action);

    /**
     * Returns an empty or default instance of {@code E}.
     */
    public abstract E getEmptyState();

    /**
     * Gets a key that's unique among all {@link Reducer}.
     */
    public String getKey() {
        return getEmptyState().getClass().getSimpleName();
    }

}
