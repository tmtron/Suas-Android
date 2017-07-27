package com.zendesk.suas;

import android.support.annotation.NonNull;

public interface Listener<E> {
    void update(@NonNull E oldState, @NonNull E newState);
}
