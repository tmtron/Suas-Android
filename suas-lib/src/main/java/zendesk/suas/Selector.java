package zendesk.suas;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Definition for transforming a {@link State} or part of the {@link State} into a view model

 * @param <E> type of the state
 * @param <F> type of the view model
 */
public interface Selector<E, F> {

    /**
     * Convert a {@link State} or subset of the {@link State} of type {@code <E>} into a view model
     * of type {@code <F>}
     *
     * @return the view model or {@code null} if the nothing should be passed to {@link Component#update(Object)}
     */
    @Nullable
    F selectData(@NonNull E data);

}
