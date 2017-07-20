package com.zendesk.suas;

/**
 * Interface for dispatching {@link Action}.
 */
public interface Dispatcher {

    /**
     * Dispatch a synchronous {@link Action}.
     */
    void dispatchAction(Action action);

}
