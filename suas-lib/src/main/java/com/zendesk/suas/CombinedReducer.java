package com.zendesk.suas;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class CombinedReducer {

    private final Collection<Reducer> reducers;
    private final Collection<String> keys;

    CombinedReducer(Collection<Reducer> reducers) {
        assertReducers(reducers);
        this.reducers = reducers;
        this.keys = getKeys(reducers);
    }

    private void assertReducers(Collection<Reducer> reducers) {
        if(reducers == null || reducers.size() == 0) {
            throw new IllegalArgumentException("No reducers provided");
        }

        final Set<String> keys = new HashSet<>();
        for(Reducer r : reducers) {
            keys.add(r.getKey());
        }
        if(keys.size() != reducers.size()) {
            throw new IllegalArgumentException("Reducers must not have the same key");
        }
    }

    private Collection<String> getKeys(Collection<Reducer> reducers) {
        final Collection<String> keys = new HashSet<>();
        for(Reducer reducer : reducers) {
            keys.add(reducer.getKey());
        }
        return keys;
    }

    @NonNull
    public ReduceResult reduce(@NonNull State oldState, @NonNull Action<?> action) {
        final State state = new State();
        final Collection<String> updatedKeys = new HashSet<>();

        for(Reducer reducer : reducers) {
            final Object oldStateForKey = oldState.getState(reducer.getKey());
            @SuppressWarnings("unchecked") final Object newStateForKey = reducer.reduce(oldStateForKey, action);
            if(newStateForKey != null) {
                state.updateKey(reducer.getKey(), newStateForKey);
                updatedKeys.add(reducer.getKey());
            } else {
                state.updateKey(reducer.getKey(), oldStateForKey);
            }
        }

        return new ReduceResult(updatedKeys, state);
    }

    @NonNull
    public State getEmptyState() {
        final Map<String, Object> stateMap = new HashMap<>(reducers.size());

        for(Reducer r : reducers) {
            final Object emptyState = r.getEmptyState();
            stateMap.put(r.getKey(), emptyState);
        }

        return new State(stateMap);
    }

    Collection<String> getAllKeys() {
        return keys;
    }

    static class ReduceResult {
        private final Collection<String> updatedKeys;
        private final State newState;

        ReduceResult(Collection<String> updatedKeys, State newState) {
            this.updatedKeys = updatedKeys;
            this.newState = newState;
        }

        Collection<String> getUpdatedKeys() {
            return updatedKeys;
        }

        State getNewState() {
            return newState;
        }
    }

}
