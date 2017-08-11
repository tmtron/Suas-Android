package com.example.suas.todo;

import android.support.v4.util.Pair;
import zendesk.suas.Action;

class TodoActionFactory {

    static final String ADD_ITEM = "ADD_ITEM";
    static final String DELETE_ITEM = "DELETE_ITEM";
    static final String MOVE_ITEM = "MOVE_ITEM";
    static final String TOGGLE_ITEM = "TOGGLE_ITEM";

    static Action addAction(String itemTitle) {
        return new Action<>(ADD_ITEM, itemTitle);
    }

    static Action deleteAction(int itemIndex) {
        return new Action<>(DELETE_ITEM, itemIndex);
    }

    static Action moveAction(Pair<Integer, Integer> indexesToMove) {
        return new Action<>(MOVE_ITEM, indexesToMove);
    }

    static Action toggleAction(int itemIndex) {
        return new Action<>(TOGGLE_ITEM, itemIndex);
    }
}
