package com.example.suas.todo;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import zendesk.suas.Action;
import zendesk.suas.Reducer;


public class TodoReducer extends Reducer<TodoList> {

    @NonNull
    @Override
    public TodoList reduce(@NonNull TodoList oldState, @NonNull Action<?> action) {

        switch (action.getActionType()) {
            case ActionFactory.ADD_ITEM: {
                final List<String> items = oldState.getItems();
                final String item = action.getData();
                items.add(item);
                return new TodoList(items);
            }

            case ActionFactory.DELETE_ITEM: {
                final List<String> items = oldState.getItems();
                final String item = action.getData();
                items.remove(item);
                return new TodoList(items);
            }

            default: {
                return oldState;
            }
        }
    }

    @NonNull
    @Override
    public TodoList getInitialState() {
        return new TodoList(new ArrayList<String>());
    }

}
