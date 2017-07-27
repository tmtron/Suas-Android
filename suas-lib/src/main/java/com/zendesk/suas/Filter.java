package com.zendesk.suas;

import android.support.annotation.NonNull;

public interface Filter<E> {
    boolean filter(@NonNull E oldState, @NonNull E newState);
}
