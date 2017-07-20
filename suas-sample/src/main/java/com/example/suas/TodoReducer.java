package com.example.suas;

import com.zendesk.suas.Action;
import com.zendesk.suas.Reducer;

import java.util.ArrayList;
import java.util.List;


public class TodoReducer extends Reducer<TodoList> {

    @Override
    public TodoList reduce(TodoList oldState, Action<?> action) {

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

    @Override
    public TodoList getEmptyState() {
        return new TodoList(new ArrayList<String>());
    }

}
