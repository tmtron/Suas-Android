package com.zendesk.suas;

public interface Listener<E> {
    void update(E oldState, E newState);
}
