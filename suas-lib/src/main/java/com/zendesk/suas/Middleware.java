package com.zendesk.suas;

import android.support.annotation.NonNull;


public interface Middleware {
    void onAction(@NonNull Action<?> action, @NonNull GetState state,
                  @NonNull Dispatcher dispatcher, @NonNull Continuation continuation);
}
