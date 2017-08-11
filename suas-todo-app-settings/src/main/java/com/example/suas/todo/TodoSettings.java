package com.example.suas.todo;

public class TodoSettings {

    private final int backgroundColor;
    private final int textColor;

    public TodoSettings(int backgroundColor, int textColor) {
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getTextColor() {
        return textColor;
    }

    @Override
    public String toString() {
        return "TodoSettings{" +
                "backgroundColor=" + Integer.toHexString(backgroundColor) +
                " textColor=" + Integer.toHexString(textColor) +
                '}';
    }
}
