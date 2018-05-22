package zendesk.suas;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Helper class for handling all passed in {@link Reducer}.
 */
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
            keys.add(r.getStateKey());
        }
        if(keys.size() != reducers.size()) {
            throw new IllegalArgumentException("Two or more reducers are tied to the same key");
        }
    }

    private Collection<String> getKeys(Collection<Reducer> reducers) {
        final Collection<String> keys = new HashSet<>();
        for(Reducer reducer : reducers) {
            keys.add(reducer.getStateKey());
        }
        return keys;
    }

    @NonNull
    public ReduceResult reduce(@NonNull State oldState, @NonNull Action<?> action) {
        final State state = new State();
        final Collection<String> updatedKeys = new HashSet<>();

        for(Reducer reducer : reducers) {
            final Object oldStateForKey = oldState.getState(reducer.getStateKey());
            @SuppressWarnings("unchecked") final Object newStateForKey = reducer.reduce(oldStateForKey, action);
            if(newStateForKey != null) {
                state.updateKey(reducer.getStateKey(), newStateForKey);
                if (newStateForKey != oldStateForKey) {
                    updatedKeys.add(reducer.getStateKey());
                }
            } else {
                state.updateKey(reducer.getStateKey(), oldStateForKey);
            }
        }

        return new ReduceResult(updatedKeys, state);
    }

    public State getEmptyState() {
        final Map<String, Object> stateMap = new HashMap<>(reducers.size());

        for(Reducer r : reducers) {
            final Object emptyState = r.getInitialState();
            stateMap.put(r.getStateKey(), emptyState);
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
