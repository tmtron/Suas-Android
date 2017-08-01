package zendesk.suas;

import android.support.annotation.NonNull;

/**
 * Interface used in a {@link Middleware} for passing an {@link Action} down the
 * chain of {@link Middleware}.
 */
public interface Continuation {

    /**
     * Pass an {@link Action} to the next {@link Middleware}
     *
     * <p>
     *     This function must be called in {@link Middleware#onAction(Action, GetState, Dispatcher, Continuation)}
     *     to pass an {@link Action} down the chain of {@link Middleware}.
     *     <br>
     *     A {@link Middleware} can choose to consume an {@link Action} by
     *     not calling {@link Continuation#next(Action)}.
     * </p>
     *
     * @param action the action
     */
    void next(@NonNull Action<?> action);
}
