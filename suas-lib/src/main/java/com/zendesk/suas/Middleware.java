package com.zendesk.suas;

public interface Middleware {
    void onAction(Action<?> action, GetState state, Dispatcher dispatcher, Continuation continuation);
}
