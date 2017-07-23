package com.zendesk.suas;

public interface Notifier<E> {
    void update(E oldState, E newState, Listener<E> listener);
}
