package com.zendesk.suas;

public interface Continuation {
    void next(Action<?> action);
}
