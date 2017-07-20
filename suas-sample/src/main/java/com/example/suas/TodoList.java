package com.example.suas;

import java.util.ArrayList;
import java.util.List;

public class TodoList {

    private final List<String> items;

    TodoList(List<String> items) {
        this.items = new ArrayList<>(items);
    }

    List<String> getItems() {
        return new ArrayList<>(items);
    }

    @Override
    public String toString() {
        return "TodoList{" +
                "items=" + items +
                '}';
    }
}
