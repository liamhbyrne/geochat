package com.example.geochat_hack;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;

public class MyButtonObserver implements TextWatcher {

    private Button mButton;

    public MyButtonObserver(Button button) {
        this.mButton = button;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.toString().trim().length() > 0) {
            mButton.setEnabled(true);
            mButton.setBackgroundColor(Color.parseColor("#05F481"));
        } else {
            mButton.setEnabled(false);
            mButton.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

}
