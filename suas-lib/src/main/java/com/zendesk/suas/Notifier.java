package com.zendesk.suas;

public interface Notifier<E> {
    void update(E newState, E oldState, Listener<E> listener);
}
