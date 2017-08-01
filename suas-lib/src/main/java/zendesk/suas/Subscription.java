package zendesk.suas;

/**
 * A {@link Subscription} gets returned after registering a {@link Listener} to the {@link Store}.
 */
public interface Subscription {

    /**
     * Unsubscribe the associated listener from the {@link Store}
     */
    void unsubscribe();

    /**
     * Subscribe the associated listener to the {@link Store}
     *
     * <p>
     *      Can be called after {@link #unsubscribe()}
     * </p>
     */
    void subscribe();

    /**
     * Trigger an update of the associated {@link Listener} with the recent {@link State}.
     */
    void update();
}
