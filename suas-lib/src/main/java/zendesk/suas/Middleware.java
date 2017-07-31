package zendesk.suas;

import android.support.annotation.NonNull;

/**
 * Middleware definition.
 * <br>
 * A middleware can be used to implement
 * <ul>
 *     <li>Logging that is called before and after the dispatcher</li>
 *     <li>Asynchronous operations by consuming an {@link Action}</li>
 *     <li>And probably a million more use-cases I can't think about right now</li>
 * </ul>
 *
 */
public interface Middleware {

    /**
     * Called before an {@link Action} gets passed to the {@link Reducer}s.
     *
     * <p>
     *     Examples:
     *
     *     Log state changes:
     *<pre>
     *void onAction(...) {
     *    State oldState = state.getState();
     *    continuation.next(action);
     *    State newState = state.getState();
     *
     *    System.out.println(stateDiff(oldState, newState));
     *}
     *</pre>
     *
     *     Consume an {@link Action} and dispatch it on a background thread:
     *<pre>
     *void onAction(...) {
     *    if(action.getData() instanceof AsyncAction) {
     *        // consume action
     *        backgroundTask.start(action.getData, dispatcher);
     *    } else {
     *        continuation.next(action);
     *    }
     *}
     *</pre>
     * </p>
     *
     * @param action a dispatched action
     * @param store access to the current state and a dispatcher
     * @param continuation callback for passing the action to the next middleware
     */
    void onAction(@NonNull Action<?> action, @NonNull StoreApi store, @NonNull Continuation continuation);
}
