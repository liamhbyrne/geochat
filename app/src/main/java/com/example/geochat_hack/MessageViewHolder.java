package com.example.geochat_hack;

import android.graphics.Color;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "MessageViewHolder";

    TextView messageTextView;
    ImageView messageImageView;
    TextView messengerTextView;
    CircleImageView messengerImageView;
    TextView dateTextView;

    public MessageViewHolder(View v) {
        super(v);
        messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
        messageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);
        messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
        messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
        dateTextView = (TextView) itemView.findViewById(R.id.dateTextView);
    }

    public void bindMessage(FriendlyMessage friendlyMessage) {
        if (friendlyMessage.getName() != null && FirebaseAuth.getInstance().getCurrentUser().getDisplayName() != null) {
            if (friendlyMessage.getName().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
                //way of telling if its the same user (better than same display name)
                messengerTextView.setTextColor(Color.parseColor("#7289DA"));
            }
        }
        if (friendlyMessage.getPhotoUrl() != null) {
            String ppUrl = friendlyMessage.getPhotoUrl();
            if (ppUrl.startsWith("gs://")) {
                Log.i("uri", ppUrl);
                StorageReference storageReference = FirebaseStorage.getInstance()
                        .getReferenceFromUrl(ppUrl);

                storageReference.getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadUrl = uri.toString();
                                Glide.with(messengerImageView.getContext())
                                        .load(downloadUrl)
                                        .into(messengerImageView);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Getting download url was not successful.", e);
                            }
                        });
            } else {
                Glide.with(messengerImageView.getContext())
                        .load(friendlyMessage.getPhotoUrl())
                        .into(messengerImageView);
            }
        }

        if(friendlyMessage.getName() != null) {
            if (friendlyMessage.getDate() != null) {
                String date = friendlyMessage.getDate();
                SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'", Locale.ENGLISH);
                try {
                    Date ISO_Date = ISO_8601_FORMAT.parse(date);
                    SimpleDateFormat newFormat;
                    if (ISO_Date.getDate() == (new Date().getDate())) {
                        newFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
                        dateTextView.setText(newFormat.format(ISO_Date));
                        messengerTextView.setText(friendlyMessage.getName());
                        messengerTextView.setVisibility(TextView.VISIBLE);
                    } else {
                        newFormat = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH);
                        dateTextView.setText(newFormat.format(ISO_Date));
                        messengerTextView.setText(friendlyMessage.getName());
                        messengerTextView.setVisibility(TextView.VISIBLE);
                    }


                } catch (ParseException e) {
                    Log.e("PARSE", "Incorrect date format");
                }
            }



        }
        if (friendlyMessage.getText() != null) {
            messageTextView.setText(friendlyMessage.getText());
            messageTextView.setVisibility(TextView.VISIBLE);
            messageImageView.setVisibility(ImageView.GONE);
        } else if (friendlyMessage.getImageUrl() != null) {
            String imageUrl = friendlyMessage.getImageUrl();
            if (imageUrl.startsWith("gs://")) {
                StorageReference storageReference = FirebaseStorage.getInstance()
                        .getReferenceFromUrl(imageUrl);

                storageReference.getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadUrl = uri.toString();
                                Glide.with(messageImageView.getContext())
                                        .load(downloadUrl)
                                        .into(messageImageView);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Getting download url was not successful.", e);
                            }
                        });
            } else {
                Glide.with(messageImageView.getContext())
                        .load(friendlyMessage.getImageUrl())
                        .into(messageImageView);
            }

            messageImageView.setVisibility(ImageView.VISIBLE);
            messageTextView.setVisibility(TextView.GONE);
        }
    }
}