package com.example.suas.todo;

class TodoListViewModel {

    private final TodoList todoList;
    private final TodoSettings todoSettings;

    TodoListViewModel(TodoList todoList, TodoSettings todoSettings) {
        this.todoList = todoList;
        this.todoSettings = todoSettings;
    }

    TodoList getTodoList() {
        return todoList;
    }

    TodoSettings getTodoSettings() {
        return todoSettings;
    }
}
