package com.example.par_teach;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

public class message extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private EditText messageEditText;
    private ListView messageListView;
    private List<View> messageViews;
    private FirebaseAuth mAuth;
    private String selectedUserId; // Added to store the ID of the selected user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);

        Intent intent = getIntent();
        if (intent != null) {
            selectedUserId = intent.getStringExtra("selectedUserId");
        }

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("messages");
        mAuth = FirebaseAuth.getInstance();

        messageEditText = findViewById(R.id.messageEditText);
        messageListView = findViewById(R.id.messageListView);
        messageViews = new ArrayList<>();

        ImageView backButton = findViewById(R.id.button6);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button sendButton = findViewById(R.id.sendButton);
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

        // Listen for messages under the selected user's node
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && selectedUserId != null) {
            mDatabase.child(selectedUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Clear existing messageViews before updating
                    messageViews.clear();

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
                            messageTextView.setText(messageContent);

                            // Format and display the timestamp
                            if (timestamp != null) {
                                TextView timestampTextView = messageItemView.findViewById(R.id.timestampTextView);
                                String formattedTime = new java.text.SimpleDateFormat("hh:mm a").format(new java.util.Date(timestamp));
                                timestampTextView.setText(formattedTime);
                            }

                            // Retrieve sender's name from the "users" collection
                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(senderId);
                            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        String senderName = dataSnapshot.child("name").getValue(String.class);
                                        TextView senderTextView = messageItemView.findViewById(R.id.name);
                                        senderTextView.setText(senderName);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle database error
                                }
                            });

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
        if (currentUser != null && selectedUserId != null) {
            DatabaseReference userMessagesRef = mDatabase.child(selectedUserId);
            String userId = mAuth.getCurrentUser().getUid();
            long timestamp = System.currentTimeMillis(); // Get the current time

            DatabaseReference newMessageRef = userMessagesRef.push();
            newMessageRef.child("senderId").setValue(userId);
            newMessageRef.child("message").setValue(message);
            newMessageRef.child("timestamp").setValue(timestamp); // Save the current time

            View messageItemView = getLayoutInflater().inflate(determineLayoutForMessage(userId), null);
            TextView messageTextView = messageItemView.findViewById(R.id.messageTextView);
            messageTextView.setText(message);
            messageViews.add(messageItemView);
            updateMessageListView();

        } else {

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


    // Update the messageListView with the new messages
    private void updateMessageListView() {
        // Check if the adapter is already set
        if (messageListView.getAdapter() == null) {
            // If not, create a new adapter and set it
            MessageAdapter messageAdapter = new MessageAdapter(messageViews);
            messageListView.setAdapter(messageAdapter);
        } else {
            // If yes, notify the adapter of the data set change
            ((MessageAdapter) messageListView.getAdapter()).notifyDataSetChanged();
            // Scroll to the bottom of the ListView after updating
            messageListView.setSelection(messageViews.size() -0);
        }
    }
}