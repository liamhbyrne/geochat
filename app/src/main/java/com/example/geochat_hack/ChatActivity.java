package com.example.geochat_hack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ChatActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder> mFirebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Disable the send button when there's no text in the input field
        // See MyButtonObserver.java for details
        ((EditText) findViewById(R.id.messageEditText)).addTextChangedListener(new MyButtonObserver((Button) findViewById(R.id.sendButton)));

        // When the send button is clicked, send a text message
        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendlyMessage friendlyMessage = new FriendlyMessage(
                        ((EditText) findViewById(R.id.messageEditText)).getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                        null,
                        null);  //add image functionality here!
                mDatabase.getReference().child("messages").push().setValue(friendlyMessage);
                ((EditText) findViewById(R.id.messageEditText)).setText("");
            }
        });

        // Initialize Realtime Database
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference messagesRef = mDatabase.getReference().child("messages");


        FirebaseRecyclerOptions<FriendlyMessage> options =
                new FirebaseRecyclerOptions.Builder<FriendlyMessage>()
                        .setQuery(messagesRef, FriendlyMessage.class)
                        .build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>(options) {
            @Override
            public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new MessageViewHolder(inflater.inflate(R.layout.item_message, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(MessageViewHolder vh, int position, FriendlyMessage message) {
                findViewById(R.id.progressBar).setVisibility(ProgressBar.INVISIBLE);
                vh.bindMessage(message);
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
}