package com.example.geochat_hack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {
    Switch notificationsSwitch, messageNoisesSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        // Gets the value (false means if it fails to get the value
        boolean sendNotifications = sharedPref.getBoolean(getString(R.string.send_notifications), false);
        boolean messageNoise = sharedPref.getBoolean(getString(R.string.message_noise), false);
        boolean rememberEmail = sharedPref.getBoolean(getString(R.string.remember_email), false);
        notificationsSwitch = (Switch)findViewById(R.id.notificationsSwitch);
        messageNoisesSwitch = (Switch)findViewById(R.id.messageNoiseSwitch);
        // set switch to same value as saved
        notificationsSwitch.setChecked(sendNotifications);
        messageNoisesSwitch.setChecked(messageNoise);


        // update saved value when switch is pressed
        notificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                // Sets the value
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.send_notifications), b);
                editor.apply();
            }
        });
        messageNoisesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                // Sets the value
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.message_noise), b);
                editor.apply();
            }
        });
    }
}