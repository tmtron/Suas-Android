package com.zendesk.suas;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of state that's used in a {@link ReduxStore}.
 */
public class State implements Serializable {

    private final Map<String, Object> state;

    /**
     * Used for testing and creating a copy.
     */
    public State(Map<String, Object> state) {
        this.state = Collections.unmodifiableMap(state);
    }

    public Object getState(String key) {
        return state.get(key);
    }

    public State copy() {
        return new State(new HashMap<>(state));
    }

    /**
     * Add or update the provided scope.
     */
    public void updateKey(String key, Object newState) {
        state.put(key, newState);
    }

    public void updateKey(Class key, Object newState) {
        state.put(key.getSimpleName(), newState);
    }

    @Override
    public String toString() {
        return "State{" +
                "state=" + state +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State state1 = (State) o;

        return state.equals(state1.state);
    }

    @Override
    public int hashCode() {
        return state.hashCode();
    }
}

