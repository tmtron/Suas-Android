package zendesk.suas;

import android.support.annotation.NonNull;

import zendesk.suas.Selector;

/**
 * Component definition. Usually implemented by UI elements.
 *
 * @param <E> type of the view model
 */
public interface Component<E, F> {

    /**
     * Called if there's an update to the view model
     */
    void update(@NonNull F e);

    /**
     * Implementation of a {@link Selector} used to transform {@link State} to {@code E}
     */
    @NonNull
    Selector<E, F> getSelector();

}
