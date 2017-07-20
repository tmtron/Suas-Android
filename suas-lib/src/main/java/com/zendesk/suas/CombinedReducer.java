package com.zendesk.suas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CombinedReducer extends Reducer<State> {

    final List<Reducer> reducers;

    CombinedReducer(List<Reducer> reducers) {
        this.reducers = reducers;
    }

    @Override
    public State reduce(State oldState, Action<?> action) {
        final Map<String, Object> stateMap = new HashMap<>(reducers.size());

        for(Reducer reducer : reducers) {
            final Object oldStateForKey = oldState.getState(reducer.getKey());
            final Object newStateForKey = reducer.reduce(oldStateForKey, action);
            stateMap.put(reducer.getKey(), newStateForKey);
        }

        return new State(stateMap);
    }

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
