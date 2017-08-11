package com.example.suas.todo;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
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
                String title = action.getData();
                TodoItem newItem = new TodoItem(title, false);
                List<TodoItem> todoItems = oldState.getItems();
                todoItems.add(newItem);
                return new TodoList(todoItems);
            }

            case ActionFactory.DELETE_ITEM: {
                int index = action.getData();
                List<TodoItem> todoItems = oldState.getItems();
                todoItems.remove(index);
                return new TodoList(todoItems);
            }

            case ActionFactory.MOVE_ITEM: {
                final Pair<Integer, Integer> data = action.getData();
                int from = data.first;
                int to = data.second;
                List<TodoItem> todoItems = oldState.getItems();
                TodoItem itemToMove = todoItems.remove(from);
                todoItems.add(to, itemToMove);
                return new TodoList(todoItems);
            }

            case ActionFactory.TOGGLE_ITEM: {
                int index = action.getData();
                List<TodoItem> todoItems = oldState.getItems();
                TodoItem itemToToggle = todoItems.remove(index);
                TodoItem toggledItem =
                        new TodoItem(itemToToggle.getTitle(), !itemToToggle.isCompleted());
                todoItems.add(index,toggledItem);
                return new TodoList(todoItems);
            }

            default: {
                return oldState;
            }
        }
    }

    @NonNull
    @Override
    public TodoList getInitialState() {
        return new TodoList(new ArrayList<TodoItem>());
    }
}
