package com.zendesk.suas;

import android.support.annotation.NonNull;

/**
 * Interface for dispatching {@link Action}.
 */
public interface Dispatcher {

    /**
     * Dispatch a synchronous {@link Action}.
     */
    void dispatchAction(@NonNull Action action);

}
