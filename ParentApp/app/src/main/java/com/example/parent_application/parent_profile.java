package com.example.parent_application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class parent_profile extends AppCompatActivity {
    private TextView textViewName;
    private TextView textViewGrade;
    private TextView textViewEmail;
    private TextView textViewContact;
    private TextView textViewAddress;

    private DatabaseReference teachersRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_profile);

        textViewName = findViewById(R.id.textViewName);
        textViewGrade = findViewById(R.id.textViewGrade);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewContact = findViewById(R.id.textViewContact);
        textViewAddress = findViewById(R.id.textViewAddress);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        RelativeLayout backButton = findViewById(R.id.button1);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent_profile.this,parent_settings.class);
                startActivity(intent);
                finish();
            }
        });

        if (currentUser != null) {
            String userId = currentUser.getUid();
            teachersRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            teachersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String grade = dataSnapshot.child("grade").getValue(String.class);
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String contact = dataSnapshot.child("contact").getValue(String.class);
                        String address = dataSnapshot.child("address").getValue(String.class);

                        textViewName.setText("Name: " + name);
                        textViewGrade.setText("Grade: " + grade);
                        textViewEmail.setText("Email: " + email);
                        textViewContact.setText("Contact: " + contact);
                        textViewAddress.setText("Address: " + address);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            });
        }
    }
}

