package com.example.geochat_hack;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class ChatActivity extends AppCompatActivity {
    TextView latlong, address, header;
    LinearLayout linearLayout;

    @SuppressLint({"ResourceType", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        // Add 20 dummy messages
        for (int i = 0; i < 40; i++) {
            linearLayout.addView(ChatUtilities.GenerateTextView(this, new String[]{"lucaswarwick02", "Test message number " + i}));
        }
    }
}