package com.zendesk.suas;

import com.zendesk.suas.Action;
import com.zendesk.suas.Continuation;
import com.zendesk.suas.Dispatcher;
import com.zendesk.suas.GetState;
import com.zendesk.suas.Middleware;

public class MonitorMiddleware implements Middleware {
    @Override
    public void onAction(Action<?> action, GetState state, Dispatcher dispatcher, Continuation continuation) {

    }
}
