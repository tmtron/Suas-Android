package com.example.suas.todo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import zendesk.suas.Action;
import zendesk.suas.Reducer;

public class SettingsReducer extends Reducer<TodoSettings> {

    @Nullable
    @Override
    public TodoSettings reduce(@NonNull TodoSettings oldState, @NonNull Action<?> action) {
        switch (action.getActionType()) {
            case SettingsActionFactory.CHANGE_BACKGROUND_COLOR: {
                int backgroundColor = action.getData();
                int textColor = oldState.getTextColor();

                return new TodoSettings(backgroundColor, textColor);
            }

            case SettingsActionFactory.CHANGE_TEXT_COLOR: {
                int backgroundColor = oldState.getBackgroundColor();
                int textColor = action.getData();

                return new TodoSettings(backgroundColor, textColor);
            }

            default: {
                return oldState;
            }
        }
    }

    @NonNull
    @Override
    public TodoSettings getInitialState() {
        return new TodoSettings(TodoColors.WHITE, TodoColors.BLACK);
    }
}
