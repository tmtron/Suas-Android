package com.zendesk.suas;

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
    public Action(String actionType, E data) {
        this.actionType = actionType;
        this.data = data;
    }

    /**
     * Create an {@link Action} without a payload.
     *
     * @param actionType type of the action
     */
    public Action(String actionType) {
        this.actionType = actionType;
        this.data = null;
    }

    /**
     * Gets the action type
     */
    public String getActionType() {
        return actionType;
    }

    /**
     * Gets the payload.
     */
    @SuppressWarnings("TypeParameterHidesVisibleType")
    public <E> E getData() {
        //noinspection unchecked
        return (E) data;
    }

    @Override
    public String toString() {
        return "Action{" +
                "actionType='" + actionType + '\'' +
                ", data=" + data +
                '}';
    }
}
