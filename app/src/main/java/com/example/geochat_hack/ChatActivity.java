package com.example.geochat_hack;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class ChatActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder> mFirebaseAdapter;
    private String locality;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent extras = getIntent();
        this.locality = extras.getStringExtra("locality");
        getSupportActionBar().setTitle(locality);

        // Disable the send button when there's no text in the input field
        // See MyButtonObserver.java for details
        ((Button) findViewById(R.id.sendButton)).setBackgroundColor(Color.TRANSPARENT);
        ((EditText) findViewById(R.id.messageEditText)).addTextChangedListener(new MyButtonObserver((Button) findViewById(R.id.sendButton)));
        // When the send button is clicked, send a text message
        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("current account", FirebaseAuth.getInstance().getCurrentUser().getUid());
                FriendlyMessage friendlyMessage = new FriendlyMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        ((EditText) findViewById(R.id.messageEditText)).getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                        String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()),
                        null, (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'", Locale.ENGLISH).format(new Date())),
                        locality);
                mDatabase.getReference().child("messages").push().setValue(friendlyMessage);
                ((EditText) findViewById(R.id.messageEditText)).setText("");
            }
        });

        // When the image button is clicked, launch the image picker
        findViewById(R.id.addMessageImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        // Initialize Realtime Database and Locality query
        mDatabase = FirebaseDatabase.getInstance();
        final Query messagesRef = mDatabase.getReference("messages").orderByChild("locality").equalTo(locality);


        FirebaseRecyclerOptions<FriendlyMessage> options =
                new FirebaseRecyclerOptions.Builder<FriendlyMessage>()
                        .setQuery(messagesRef, FriendlyMessage.class)
                        .build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>(options) {
            @Override
            public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                final MessageViewHolder mvh = new MessageViewHolder(inflater.inflate(R.layout.item_message, viewGroup, false));
                mvh.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final String key = mFirebaseAdapter.getRef(((RecyclerView) findViewById(R.id.messageRecyclerView)).getChildLayoutPosition(v)).getKey();

                        Query dbUID = mDatabase.getReference("messages").child(key).child("uid");
                        dbUID.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue() != null) {
                                    String savedUID = (String) snapshot.getValue();
                                    if (savedUID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                        mDatabase.getReference("messages").child(key).removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("deletion attempt", "Failed to get UID for message deletion");
                            }
                        });
                        return false;
                    }
                });
                return mvh;
            }

            @Override
            protected void onBindViewHolder(MessageViewHolder vh, int position, FriendlyMessage message) {
                findViewById(R.id.progressBar).setVisibility(ProgressBar.INVISIBLE);
                vh.bindMessage(message);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (mFirebaseAdapter.getItemCount() == 0) {
                    findViewById(R.id.progressBar).setVisibility(ProgressBar.INVISIBLE);
                    findViewById(R.id.lonely).setVisibility(TextView.VISIBLE);
                }
                if (mFirebaseAdapter.getItemCount() > 0 && findViewById(R.id.lonely).getVisibility() == TextView.VISIBLE) {
                    findViewById(R.id.lonely).setVisibility(TextView.INVISIBLE);
                }
            }
        };

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        ((RecyclerView) findViewById(R.id.messageRecyclerView)).setLayoutManager(mLinearLayoutManager);
        ((RecyclerView) findViewById(R.id.messageRecyclerView)).setAdapter(mFirebaseAdapter);

        // Scroll down when a new message arrives :)
        // See MyScrollToBottomObserver.java for details
        mFirebaseAdapter.registerAdapterDataObserver(
                new MyScrollToBottomObserver(((RecyclerView) findViewById(R.id.messageRecyclerView)), mFirebaseAdapter, mLinearLayoutManager));
    }

    public void notifyDelete() {

    }

    @Override
    public void onPause() {
        mFirebaseAdapter.stopListening();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAdapter.startListening();
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
                FriendlyMessage tempMessage = new FriendlyMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        null, FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                        String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()), null,
                        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'", Locale.ENGLISH).format(new Date()),
                        this.locality);

                mDatabase.getReference().child("messages").push()
                        .setValue(tempMessage, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    Log.w("TAG", "Unable to write message to database.",
                                            databaseError.toException());
                                    return;
                                }

                                // Build a StorageReference and then upload the file
                                String key = databaseReference.getKey();
                                StorageReference storageReference =
                                        FirebaseStorage.getInstance()
                                                .getReference(user.getUid())
                                                .child(key)
                                                .child(uri.getLastPathSegment());

                                putImageInStorage(storageReference, uri, key);
                            }
                        });
            }
        }
    }

    private void putImageInStorage(StorageReference storageReference, Uri uri, final String key) {
        // First upload the image to Cloud Storage
        storageReference.putFile(uri)
                .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // After the image loads, get a public downloadUrl for the image
                        // and add it to the message.
                        taskSnapshot.getMetadata().getReference().getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        FriendlyMessage friendlyMessage = new FriendlyMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                                null, FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                                                String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()), uri.toString(),
                                                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'", Locale.ENGLISH).format(new Date()), locality);
                                        mDatabase.getReference()
                                                .child("messages")
                                                .child(key)
                                                .setValue(friendlyMessage);
                                    }
                                });
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Image upload task was not successful.", e);
                    }
                });
    }
}