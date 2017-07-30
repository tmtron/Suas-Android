package com.exaple.suas.counter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import zendesk.suas.Action;
import zendesk.suas.LoggerMiddleware;
import zendesk.suas.Reducer;
import zendesk.suas.Store;
import zendesk.suas.Suas;

public class SuasCounter {

    private static final String INCREMENT_ACTION = "increment";
    private static final String DECREMENT_ACTION = "decrement";

    public static void main(String [] args) {

        Store store = createStore();

        store.addListener(Counter.class, (oldState, newState) -> {
            System.out.println("Java - State changed to " + newState.count);
        });

        store.dispatchAction(getIncrementAction(10));
        store.dispatchAction(getIncrementAction(1));
        store.dispatchAction(getDecrementAction(5));

    }

    private static Action getDecrementAction(int value) {
        return new Action<>(DECREMENT_ACTION, value);
    }

    private static Action getIncrementAction(int value) {
        return new Action<>(INCREMENT_ACTION, value);
    }

    private static Store createStore() {
        // Create a store with a CounterReducer
        // LoggerMiddleware for advanced logging
        return Suas.createStore(new CounterReducer())
                .withMiddleware(new LoggerMiddleware())
                .build();
    }

    private static class CounterReducer extends Reducer<Counter> {

        @Nullable
        @Override
        public Counter reduce(@NonNull Counter oldState, @NonNull Action<?> action) {
            switch (action.getActionType()) {
                case INCREMENT_ACTION: {
                    // Handle increment action
                    int incrementValue = action.getData();
                    return new Counter(oldState.count + incrementValue);
                }
                case DECREMENT_ACTION: {
                    // Handle decrement action
                    int decrementValue = action.getData();
                    return new Counter(oldState.count - decrementValue);
                }
                default: {
                    // Important: If action does not affect, return null
                    return null;
                }
            }
        }

        @NonNull
        @Override
        public Counter getEmptyState() {
            // Provide a default value
            return new Counter(0);
        }
    }


    static class Counter {

        final int count;

        Counter(int count) {
            this.count = count;
        }
    }
}
