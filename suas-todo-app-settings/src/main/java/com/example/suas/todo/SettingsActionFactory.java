package com.example.suas.todo;

import zendesk.suas.Action;

class SettingsActionFactory {

    static final String CHANGE_BACKGROUND_COLOR = "CHANGE_BACKGROUND_COLOR";
    static final String CHANGE_TEXT_COLOR = "CHANGE_TEXT_COLOR";

    static Action changeBackgroundColorAction(int backgroundColor) {
        return new Action<>(CHANGE_BACKGROUND_COLOR, backgroundColor);
    }

    static Action changeTextColorAction(int textColor) {
        return new Action<>(CHANGE_TEXT_COLOR, textColor);
    }
}
