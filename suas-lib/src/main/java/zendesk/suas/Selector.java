package zendesk.suas;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Definition to transform {@link State} into a view model
 * @param <E> type of the view model
 */
public interface Selector<E, F> {

    @Nullable
    F selectData(@NonNull E data);

}
