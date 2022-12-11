package com.penelope.acousticrecipe.utils;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class OnTextChangeListener implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        onTextChange(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public abstract void onTextChange(String text);

}
