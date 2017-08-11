package com.example.suas.todo;

class TodoItem {

    private final String title;
    private final boolean isCompleted;

    TodoItem(String title, boolean isCompleted) {
        this.title = title;
        this.isCompleted = isCompleted;
    }

    String getTitle() {
        return title;
    }

    boolean isCompleted() {
        return isCompleted;
    }

    @Override
    public String toString() {
        return "TodoItem{" +
                "title=" + title +
                " isCompleted=" + isCompleted +
                '}';
    }
}