package com.example.suas.todo;

import java.util.ArrayList;
import java.util.List;

public class TodoList {

    private final List<TodoItem> items;

    TodoList(List<TodoItem> items) {
        this.items = new ArrayList<>(items);
    }

    List<TodoItem> getItems() {
        return new ArrayList<>(items);
    }

    @Override
    public String toString() {
        return "TodoList{" +
                "items=" + items +
                '}';
    }
}
