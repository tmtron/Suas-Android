package com.zendesk.suas;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class CombinedReducer extends Reducer<State> {

    private final Collection<Reducer> reducers;

    CombinedReducer(Collection<Reducer> reducers) {
        assertReducers(reducers);
        this.reducers = reducers;
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

    @NonNull
    @Override
    public State reduce(@NonNull State oldState, @NonNull Action<?> action) {
        final State state = new State();

        for(Reducer reducer : reducers) {
            final Object oldStateForKey = oldState.getState(reducer.getKey());
            @SuppressWarnings("unchecked") final Object newStateForKey = reducer.reduce(oldStateForKey, action);
            state.updateKey(reducer.getKey(), newStateForKey);
        }

        return state;
    }

    @NonNull
    @Override
    public State getEmptyState() {
        final Map<String, Object> stateMap = new HashMap<>(reducers.size());

        for(Reducer r : reducers) {
            final Object emptyState = r.getEmptyState();
            stateMap.put(r.getKey(), emptyState);
        }

        return new State(stateMap);
    }
}
