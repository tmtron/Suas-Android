package com.zendesk.suas;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
    public State(@NonNull Map<String, Object> state) {
        this.state = Collections.unmodifiableMap(state);
    }

    @Nullable
    public Object getState(@NonNull String key) {
        return state.get(key);
    }

    @Nullable
    public <E> E getState(@NonNull Class<E> clazz) {
        final Object data = state.get(clazz.getSimpleName());
        if(clazz.isInstance(data)) {
            //noinspection unchecked
            return (E) data;
        } else {
            return null;
        }
    }

    @Nullable
    public <E> E getState(@NonNull String key, @NonNull Class<E> clazz) {
        final Object data = state.get(key);
        if(clazz.isInstance(data)) {
            //noinspection unchecked
            return (E) data;
        } else {
            return null;
        }
    }

    @NonNull
    public State copy() {
        return new State(new HashMap<>(state));
    }

    /**
     * Add or update the provided scope.
     */
    void updateKey(String key, Object newState) {
        state.put(key, newState);
    }

    void updateKey(Class key, Object newState) {
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

