package com.example.suas.todo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import zendesk.suas.Listener;
import zendesk.suas.Store;

public class SettingsActivity extends AppCompatActivity implements Listener<TodoSettings> {

    private Store store;
    private View backgroundColorPreview;
    private View textColorPreview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        backgroundColorPreview = findViewById(R.id.background_color_preview);
        textColorPreview = findViewById(R.id.text_color_preview);

        store = ((TodoApplication) getApplication()).getStore();
    }

    @Override
    protected void onStart() {
        super.onStart();
        store.addListener(TodoSettings.class, this).informWithCurrentState();
    }

    @Override
    protected void onStop() {
        super.onStop();
        store.removeListener(this);
    }

    @Override
    public void update(@NonNull TodoSettings settings) {
        backgroundColorPreview.setBackgroundColor(settings.getBackgroundColor());
        textColorPreview.setBackgroundColor(settings.getTextColor());
    }

    public void selectColor(View colorOption) {
        switch (colorOption.getId()) {
            case R.id.background_black: {
                store.dispatch(SettingsActionFactory.changeBackgroundColorAction(TodoColors.BLACK));
                return;
            }

            case R.id.background_white: {
                store.dispatch(SettingsActionFactory.changeBackgroundColorAction(TodoColors.WHITE));
                return;
            }

            case R.id.background_red: {
                store.dispatch(SettingsActionFactory.changeBackgroundColorAction(TodoColors.RED));
                return;
            }

            case R.id.background_blue: {
                store.dispatch(SettingsActionFactory.changeBackgroundColorAction(TodoColors.BLUE));
                return;
            }

            case R.id.text_black: {
                store.dispatch(SettingsActionFactory.changeTextColorAction(TodoColors.BLACK));
                return;
            }

            case R.id.text_white: {
                store.dispatch(SettingsActionFactory.changeTextColorAction(TodoColors.WHITE));
                return;
            }

            case R.id.text_red: {
                store.dispatch(SettingsActionFactory.changeTextColorAction(TodoColors.RED));
                return;
            }

            case R.id.text_blue: {
                store.dispatch(SettingsActionFactory.changeTextColorAction(TodoColors.BLUE));
                return;
            }

            default: {
                throw new IllegalArgumentException("Invalid option");
            }
        }
    }
}
