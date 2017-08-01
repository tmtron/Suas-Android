package zendesk.suas;

/**
 * Definition of an asynchronous action. Used by {@link AsyncMiddleware}
 */
public interface AsyncAction {

    /**
     * Method executed when it hits {@link AsyncMiddleware}
     *
     * <p>
     *     When wrapped with {@link AsyncMiddleware#create(AsyncAction)} this method <i>will not</i>
     *     be executed on a background thread.
     * </p>
     *
     * @param dispatcher dispatcher
     * @param getState state
     */
    void execute(Dispatcher dispatcher, GetState getState);

}
