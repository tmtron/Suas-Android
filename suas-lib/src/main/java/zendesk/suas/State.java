package zendesk.suas;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of state that's used in a {@link SuasStore}.
 */
public class State implements Serializable {

    static String keyForClass(Class clazz) {
        return clazz.getSimpleName();
    }

    static State mergeStates(State emptyState, State state) {
        final State initialState;

        if(state != null) {
            final State passedInState = state.copy();
            for(String stateKey : emptyState.getStateKeys()) {
                if(passedInState.getState(stateKey) == null) {
                    passedInState.updateKey(stateKey, emptyState.getState(stateKey));
                }
            }
            initialState = passedInState;
        } else {
            initialState = emptyState;
        }

        return initialState;
    }

    private final Map<String, Object> state;

    State(@NonNull Map<String, Object> state) {
        this.state = new HashMap<>(state);
    }

    /**
     * Create a new and <i>empty</i> state.
     */
    public State() {
        this.state = new HashMap<>();
    }

    /**
     * Get a state for a state key
     *
     * @param stateKey state key
     * @return not typed state
     */
    @Nullable
    public Object getState(@NonNull String stateKey) {
        return state.get(stateKey);
    }

    /**
     * Get a state for a {@link Class}
     *
     * <p>
     *     If available in the state returns a the state
     *     with type {@code <E>}
     * </p>
     *
     * @param clazz type of the state
     * @return the state with the correct type or {@code null}
     */
    @Nullable
    public <E> E getState(@NonNull Class<E> clazz) {
        final Object data = state.get(keyForClass(clazz));
        if(clazz.isInstance(data)) {
            //noinspection unchecked
            return (E) data;
        } else {
            return null;
        }
    }

    /**
     * Get a state for the a state key of the type {@code <E>}
     *
     * <p>
     *     If available in the state returns a the state
     *     with type {@code <E>}
     * </p>
     *
     * @param stateKey key for the state
     * @param clazz type of the state
     * @return the state with the correct type or {@code null}
     */
    @Nullable
    public <E> E getState(@NonNull String stateKey, @NonNull Class<E> clazz) {
        final Object data = state.get(stateKey);
        if(clazz.isInstance(data)) {
            //noinspection unchecked
            return (E) data;
        } else {
            return null;
        }
    }

    @NonNull
    State copy() {
        return new State(new HashMap<>(state));
    }

    /**
     * Add or update the provided scope.
     */
    void updateKey(String key, Object newState) {
        state.put(key, newState);
    }

    <E> void updateKey(Class<E> stateKey, E newState) {
        state.put(keyForClass(stateKey), newState);
    }

    private Collection<String> getStateKeys() {
        return state.keySet();
    }

    Map<String, Object> getState() {
        return state;
    }

    @Override
    public String toString() {
        return state.toString();
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

