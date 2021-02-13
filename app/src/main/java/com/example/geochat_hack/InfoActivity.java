package com.example.geochat_hack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class InfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        final EditText displayname = (EditText) findViewById(R.id.displayname);
        final EditText age = (EditText) findViewById(R.id.age);

        Button submit = (Button) findViewById(R.id.addinfo);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(age.getText().toString()) >= 18 && displayname.getText().toString().length() > 3) {
                    setInfo(displayname.getText().toString());
                } else {
                    Toast.makeText(InfoActivity.this, "min name length: 4 // 18+ app", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void setInfo(String name) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name).build();

        user.updateProfile(profileUpdates);

        Toast.makeText(InfoActivity.this, "Name is set!", Toast.LENGTH_SHORT).show();
        Intent toMain = new Intent(InfoActivity.this, MainActivity.class);
        InfoActivity.this.startActivity(toMain);
    }
}