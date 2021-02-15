package com.example.geochat_hack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;


public class SettingsActivity extends AppCompatActivity {
    Switch notificationsSwitch, messageNoisesSwitch;

    Button back_to_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().hide();

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

        back_to_main = findViewById(R.id.back_to_main);
        // Assign button methods
        back_to_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
            }
        });

        Button addPP = (Button) findViewById(R.id.addpp);
        addPP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, 1);

            }
        });
        refreshUI(); //set icon imageView

    }

    protected void refreshUI() {
        final CircleImageView pp = findViewById(R.id.pp);
        if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null) {
            StorageReference storageReference = FirebaseStorage.getInstance()
                    .getReferenceFromUrl(String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()));

            storageReference.getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();
                            Glide.with(findViewById(R.id.pp).getContext())
                                    .load(downloadUrl)
                                    .into(pp);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("TAG", "Getting download url was not successful.", e);
                        }
                    });
        } else {
            pp.setImageDrawable(getDrawable(R.drawable.ic_account_circle_black_36dp));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("images", "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null) {

                final Uri uri = data.getData();
                Log.d("im", "Uri: " + uri.toString());

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                // Build a StorageReference and then upload the file
                final StorageReference storageReference =
                        FirebaseStorage.getInstance()
                                .getReference(user.getUid())
                                .child(uri.getLastPathSegment());

                storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                            @Override
                            public void onSuccess(Uri uri) {
                                Uri downloadUrl = uri;
                                //Do what you want with the url
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(downloadUrl)
                                        .build();

                                user.updateProfile(profileUpdates);
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                refreshUI();
                            }
                        });
                    }
                });

            }
        }

    }

}