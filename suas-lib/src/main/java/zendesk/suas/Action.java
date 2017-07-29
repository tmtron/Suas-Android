package zendesk.suas;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Synchronous action that can be dispatched in a {@link ReduxStore} to
 * kick of state changes.
 */
public class Action<E> {

    private final String actionType;
    private final E data;

    /**
     * Create an {@link Action}
     *
     * @param actionType type of the action
     * @param data payload of the action
     */
    public Action(@NonNull String actionType, @Nullable E data) {
        this.actionType = actionType;
        this.data = data;
    }

    /**
     * Create an {@link Action} without a payload.
     *
     * @param actionType type of the action
     */
    public Action(@NonNull String actionType) {
        this.actionType = actionType;
        this.data = null;
    }

    /**
     * Gets the action type
     */
    @NonNull
    public String getActionType() {
        return actionType;
    }

    /**
     * Gets the payload.
     */
    @Nullable
    public E getRawData() {
        return data;
    }

    @Nullable
    public <F> F getData(@NonNull Class<F> clazz) {
        if(clazz.isInstance(data)) {
            return clazz.cast(data);
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <F> F getData() {
        return (F)data;
    }


    @Override
    public String toString() {
        return "Action{" +
                "actionType='" + actionType + '\'' +
                ", data=" + data +
                '}';
    }
}
