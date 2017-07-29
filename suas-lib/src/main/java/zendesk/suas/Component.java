package zendesk.suas;

import android.support.annotation.NonNull;

/**
 * Component definition. Component represents a main UI view
 *
 * @param <E> type of the state passed into {@link Selector#selectData(Object)}
 * @param <F> type of the view model, passed into {@link #update(Object)}
 */
public interface Component<E, F> {

    /**
     * Called if there's an update to the view model
     */
    void update(@NonNull F e);

    /**
     * Implementation of a {@link Selector} used to transform {@code <E>} to {@code <F>}
     */
    @NonNull
    Selector<E, F> getSelector();

}
