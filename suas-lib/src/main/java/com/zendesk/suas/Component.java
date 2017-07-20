package com.zendesk.suas;

/**
 * Component definition. Usually implemented by UI elements.
 *
 * @param <E> type of the view model
 */
public interface Component<E, F> {

    /**
     * Called if there's an update to the view model
     */
    void update(F e);

    /**
     * Implementation of a {@link Selector} used to transform {@link State} to {@code E}
     */
    Selector<E, F> getSelector();

}
