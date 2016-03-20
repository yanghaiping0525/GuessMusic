package com.yang.guessmusic.bean;

import android.util.Log;
import android.view.View;
import android.widget.Button;

public class WordButton {
    private int index;
    private boolean isVisible;
    private String word;
    private Button button;

    public WordButton() {
        isVisible = true;
        word = "";
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
        this.button.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

}
