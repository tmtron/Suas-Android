package com.zendesk.suas;

import android.util.Log;

public class LoggerMiddleWare implements Middleware {

    private final String tag;

    public LoggerMiddleWare(String tag) {
        this.tag = tag;
    }

    @Override
    public void onAction(Action<?> action, GetState state, Dispatcher dispatcher, Continuation continuation) {
        final State oldState = state.getState();
        continuation.next(action);
        final State newState = state.getState();

        Log.d(tag, "===================");
        Log.d(tag, "Action: " + action.getActionType() + " Data: " + action.getData());
        Log.d(tag, "Old state: " + oldState);
        Log.d(tag, "New state: " + newState);
        Log.d(tag, "===================");
    }
}
