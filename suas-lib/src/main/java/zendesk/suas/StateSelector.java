package zendesk.suas;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Definition for transforming a {@link State} or part of the {@link State} into a view model

 * @param <E> type of the view model
 */
public interface StateSelector<E> {

    /**
     * Convert a {@link State} or subset of the {@link State} of type {@code <E>} into a view model
     * of type {@code <F>}
     *
     * @return the view model or {@code null}
     */
    @Nullable
    E selectData(@NonNull State state);

}
