package com.zendesk.suas;

public interface Filter<E> {
    boolean filter(E oldState, E newState);
}
