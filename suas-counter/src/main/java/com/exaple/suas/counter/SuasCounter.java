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

        store.addListener(Counter.class, (state) -> System.out.println("Java - State changed to " + state.count));

        store.dispatch(getIncrementAction(10));
        store.dispatch(getIncrementAction(1));
        store.dispatch(getDecrementAction(5));

    }

    private static IntAction getDecrementAction(int value) {
        return new IntAction(DECREMENT_ACTION, value);
    }

    private static IntAction getIncrementAction(int value) {
        return new IntAction(INCREMENT_ACTION, value);
    }

    private static Store createStore() {
        // Create a store with a CounterReducer
        // LoggerMiddleware for advanced logging
        return Suas.createStore(new CounterReducer())
                .withMiddleware(new LoggerMiddleware())
                .build();
    }

    private static class IntAction extends Action<Integer> {
        final private int data;

        public IntAction(@NonNull String actionType, int data) {
            super(actionType, data);
            this.data = data;
        }

        @SuppressWarnings("unchecked")
        public @NonNull Integer getData() {
            return data;
        }
    }

    private static class CounterReducer extends Reducer<Counter> {

        @Nullable
        @Override
        public Counter reduce(@NonNull Counter oldState, @NonNull Action<?> action) {
            if (action instanceof IntAction) {
                final IntAction actionInt = (IntAction)action;
                switch (action.getActionType()) {
                    case INCREMENT_ACTION: {
                        // Handle increment action
                        int incrementValue = actionInt.getData();
                        return new Counter(oldState.count + incrementValue);
                    }
                    case DECREMENT_ACTION: {
                        // Handle decrement action
                        int decrementValue = actionInt.getData();
                        return new Counter(oldState.count - decrementValue);
                    }
                }
            }
            // Important: If action does not affect, return null
            return null;
        }

        @NonNull
        @Override
        public Counter getInitialState() {
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
