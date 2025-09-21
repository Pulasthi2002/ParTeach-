package com.example.parent_application;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class parent_home extends AppCompatActivity {

    ConstraintLayout marks_btn,attend_btn,msg_btn,resources_btn,settings_btn;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private ViewFlipper eventFlipper;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_home);

        marks_btn=findViewById(R.id.button1);
        attend_btn=findViewById(R.id.button2);
        msg_btn=findViewById(R.id.button3);
        resources_btn=findViewById(R.id.button4);
        settings_btn=findViewById(R.id.button5);
        eventFlipper = findViewById(R.id.eventFlipper);
        progressBar = findViewById(R.id.progressBar);

        TextView profile = findViewById(R.id.textView3);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent_home.this, parent_profile.class);
                startActivity(intent);
                finish();
            }
        });

        ImageView profle = findViewById(R.id.imageView3);

        profle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent_home.this, parent_profile.class);
                startActivity(intent);
                finish();
            }
        });

        eventFlipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent_home.this,parent_events.class);
                startActivity(intent);
                finish();
            }
        });

        marks_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent_home.this,parent_view_marks.class);
                startActivity(intent);
                finish();
            }
        });

        attend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent_home.this, parent_attendance.class);
                startActivity(intent);
                finish();
            }
        });

        msg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent_home.this, parent_messages.class);
                startActivity(intent);
                finish();
            }
        });

        resources_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent_home.this, parent_resources.class);
                startActivity(intent);
                finish();
            }
        });

        settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent_home.this,parent_settings.class);
                startActivity(intent);
                finish();
            }
        });

        ImageView notify = findViewById(R.id.imageView11);

        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent_home.this,notifications.class);
                startActivity(intent);
                finish();
            }
        });


        TextView username = findViewById(R.id.textView4);
        TextView clz = findViewById(R.id.textView12);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

            // Display "Loading" while fetching data
            username.setText("Loading...");
            clz.setText("Loading...");

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String grade = dataSnapshot.child("grade").getValue(String.class);

                        username.setText(name);
                        clz.setText(grade);
                    } else {
                        // Handle the case where data doesn't exist
                        username.setText("Data not found");
                        clz.setText("Data not found");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                    username.setText("Error occurred while fetching data");
                    clz.setText("Error occurred while fetching data");
                }
            });
            progressBar.setVisibility(View.VISIBLE);
            // Load events for ViewFlipper
            DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference().child("events");
            eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                        String eventTitle = eventSnapshot.child("name").getValue(String.class);
                        String eventDescription = eventSnapshot.child("msgContent").getValue(String.class);
                        String eventImageUrl = eventSnapshot.child("imageURL").getValue(String.class);

                        View eventView = LayoutInflater.from(parent_home.this).inflate(R.layout.event_item, eventFlipper, false);
                        TextView titleTextView = eventView.findViewById(R.id.eventTitle);
                        ImageView eventImageView = eventView.findViewById(R.id.eventImage);

                        titleTextView.setText(eventTitle);

                        // Load image using Glide or other image loading library
                        Glide.with(parent_home.this)
                                .load(eventImageUrl)
                                .placeholder(R.drawable.progress_bar_dots) // Show progress bar while loading
                                .into(eventImageView);

                        eventFlipper.addView(eventView);
                    }
                    progressBar.setVisibility(View.GONE);
                    eventFlipper.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            // Handle the case where currentUser is null
            username.setText("User not logged in");
            clz.setText("User not logged in");
        }

    }
}
