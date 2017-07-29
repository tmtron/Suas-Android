package com.example.suas.todo;

import zendesk.suas.Action;

public class ActionFactory {

    static final String ADD_ITEM = "ADD_ITEM";
    static final String DELETE_ITEM = "DELETE_ITEM";

    static Action addAction(String item) {
        return new Action<>(ADD_ITEM, item);
    }

    static Action deleteAction(String item) {
        return new Action<>(DELETE_ITEM, item);
    }

}
