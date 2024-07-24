package com.example.todaybuddy;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class textutil {

    public static void setupPasswordToggle(Context context, TextInputLayout textInputLayout, TextInputEditText textInputEditText) {
        // Define your drawable and color resources
        final int visibleIcon = R.drawable.on;
        final int invisibleIcon = R.drawable.off;
        final int tintColor = R.color.black;

        // Set up the end icon manually for clear text
        textInputLayout.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
        textInputLayout.setEndIconDrawable(invisibleIcon); // Set your crossed eye icon
        textInputLayout.setEndIconContentDescription("Show password");

        // Set initial tint if needed
        textInputLayout.getEndIconDrawable().setColorFilter(
                new PorterDuffColorFilter(context.getResources().getColor(tintColor), PorterDuff.Mode.SRC_IN)
        );


        // Add TextWatcher to show/hide the clear icon based on text presence
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayout.setEndIconVisible(s.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Initially hide the clear icon if there's no text
        textInputLayout.setEndIconVisible(textInputEditText.getText().length() > 0);

        textInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int inputType = textInputEditText.getInputType();
                if (inputType == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                    textInputEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    textInputLayout.setEndIconDrawable(visibleIcon); // Set your normal eye icon
                    textInputLayout.setEndIconContentDescription("Hide password");

                    // Set tint for visibility icon
                    textInputLayout.getEndIconDrawable().setColorFilter(
                            new PorterDuffColorFilter(context.getResources().getColor(tintColor), PorterDuff.Mode.SRC_IN)
                    );
                } else {
                    textInputEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    textInputLayout.setEndIconDrawable(invisibleIcon); // Set your crossed eye icon
                    textInputLayout.setEndIconContentDescription("Show password");

                    // Set tint for visibility off icon
                    textInputLayout.getEndIconDrawable().setColorFilter(
                            new PorterDuffColorFilter(context.getResources().getColor(tintColor), PorterDuff.Mode.SRC_IN)
                    );
                }
                // Maintain the cursor position
                textInputEditText.setSelection(textInputEditText.getText().length());
            }
        });
    }
}
