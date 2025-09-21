package com.example.parent_application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class parent_support extends AppCompatActivity {
    private EditText editTextFeedback;
    private Button buttonSubmitFeedback;
    private FirebaseAuth mAuth;
    private DatabaseReference feedbackRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_support);

        mAuth = FirebaseAuth.getInstance();
        feedbackRef = FirebaseDatabase.getInstance().getReference().child("parent_support");

        editTextFeedback = findViewById(R.id.editTextFeedback);
        buttonSubmitFeedback = findViewById(R.id.buttonSubmitFeedback);

        buttonSubmitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFeedback();
            }
        });

        RelativeLayout back = findViewById(R.id.button1);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent_support.this,parent_settings.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void submitFeedback() {
        String feedbackText = editTextFeedback.getText().toString().trim();

        if (!feedbackText.isEmpty()) {
            String userId = mAuth.getCurrentUser().getUid(); // Get the current user's ID
            String userEmail = mAuth.getCurrentUser().getEmail(); // Get the current user's email

            if (userId != null) {
                // Get current date and time
                Calendar currentTime = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String currentDateTimeString = dateFormat.format(currentTime.getTime());

                DatabaseReference userFeedbackRef = feedbackRef.child(userId).child(currentDateTimeString); // Create a reference with user ID

                Map<String, Object> feedbackData = new HashMap<>();
                feedbackData.put("email", userEmail);
                feedbackData.put("message", feedbackText);

                userFeedbackRef.setValue(feedbackData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(parent_support.this, "Message submitted successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(parent_support.this, "Failed to submit message: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } else {
            Toast.makeText(this, "Please enter your message", Toast.LENGTH_SHORT).show();
        }
    }
}
