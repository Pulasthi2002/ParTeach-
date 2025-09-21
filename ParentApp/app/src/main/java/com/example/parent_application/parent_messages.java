package com.example.parent_application;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class parent_messages extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private EditText messageEditText;
    private ListView messageListView;
    private ArrayAdapter<String> messageAdapter;
    private List<View> messageViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_messages);
        messageViews = new ArrayList<>();

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("messages");

        messageEditText = findViewById(R.id.messageEditText);
        messageListView = findViewById(R.id.messageListView);

        List<String> messages = new ArrayList<>();
        messageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);
        messageListView.setAdapter(messageAdapter);


        ImageView backButton = findViewById(R.id.button8);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent_messages.this,parent_home.class);
                startActivity(intent);
                finish();
            }
        });
        MaterialButton sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(message)) {
                    sendMessage(message);
                    messageEditText.setText("");
                }
            }
        });

        // Listen for messages under the "messages" node
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            mDatabase.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    messageAdapter.clear(); // Clear the existing messages
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Get the sender ID and message content
                        String senderId = snapshot.child("senderId").getValue(String.class);
                        String messageContent = snapshot.child("message").getValue(String.class);
                        Long timestamp = snapshot.child("timestamp").getValue(Long.class);
                        // Check if message content is empty
                        if (!TextUtils.isEmpty(messageContent)) {
                            // Determine the layout based on the sender ID
                            int layout = determineLayoutForMessage(senderId);

                            // Inflate the message view
                            View messageItemView = getLayoutInflater().inflate(layout, null);
                            TextView messageTextView = messageItemView.findViewById(R.id.messageTextView);

                            // Format and display the timestamp
                            if (timestamp != null) {
                                TextView timestampTextView = messageItemView.findViewById(R.id.timestampTextView); // Add a TextView for timestamp in your XML layouts
                                String formattedTime = new java.text.SimpleDateFormat("hh:mm a").format(new java.util.Date(timestamp));
                                timestampTextView.setText(formattedTime);
                            }

                            // Set the message content
                            messageTextView.setText(messageContent);

                            // Add the message view to the list
                            messageViews.add(messageItemView);
                        }
                    }
                    // Update the message list view
                    updateMessageListView();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            });
        }
    }

    // Update the sendMessage() method to push messages under the selected user's node with sender's ID as key
    private void sendMessage(String message) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid(); // Get the current user's ID
            DatabaseReference userMessagesRef = mDatabase.child(userId); // Reference the user's node under "messages"
            long timestamp = System.currentTimeMillis();
            // Generate a unique key for each message using push()
            DatabaseReference newMessageRef = userMessagesRef.push();
            newMessageRef.child("senderId").setValue(userId); // Save sender's ID
            newMessageRef.child("message").setValue(message);
            newMessageRef.child("timestamp").setValue(timestamp);

            View messageItemView = getLayoutInflater().inflate(determineLayoutForMessage(userId), null);
            TextView messageTextView = messageItemView.findViewById(R.id.messageTextView);
            messageTextView.setText(message);
            messageViews.add(messageItemView);

        } else {
            // Handle the case when the current user is null (not signed in)
            // You may want to show a message or prompt the user to sign in
        }
    }
    // Determine the layout for the message view
    private int determineLayoutForMessage(String senderId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && senderId != null && senderId.equals(currentUser.getUid())) {
            // Message sent by the current user, use sent_message_item layout
            return R.layout.sent_message_item;
        } else {
            // Message received or senderId is null, use received_message_item layout
            return R.layout.recieved_message_item;
        }
    }
    private void updateMessageListView() {
        MessageAdapter messageAdapter = new MessageAdapter(messageViews);
        messageListView.setAdapter(messageAdapter);
    }

}
