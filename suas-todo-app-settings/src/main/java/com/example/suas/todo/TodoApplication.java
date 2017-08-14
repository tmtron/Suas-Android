package com.example.suas.todo;

import android.app.Application;
import zendesk.suas.Filters;
import zendesk.suas.LoggerMiddleware;
import zendesk.suas.Middleware;
import zendesk.suas.MonitorMiddleware;
import zendesk.suas.Store;
import zendesk.suas.Suas;

public class TodoApplication extends Application {

    private Store store;

    @Override
    public void onCreate() {
        super.onCreate();

        final Middleware monitorMiddleware = new MonitorMiddleware.Builder(this)
                .withEnableAdb(true)
                .build();

        final Middleware loggerMiddleware = new LoggerMiddleware.Builder()
                .withSerialization(LoggerMiddleware.Serialization.TO_STRING)
                .withLineLength(120)
                .build();

        store = Suas.createStore(new TodoReducer(), new SettingsReducer())
                .withMiddleware(monitorMiddleware, loggerMiddleware)
                .withDefaultFilter(Filters.EQUALS)
                .build();
    }

    public Store getStore() {
        return store;
    }
}
