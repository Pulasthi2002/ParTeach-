package com.example.par_teach;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class teacher_feedback extends AppCompatActivity {
    private EditText editTextFeedback;
    private Button buttonSubmitFeedback;
    private FirebaseAuth mAuth;
    private DatabaseReference feedbackRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_feedback);

        mAuth = FirebaseAuth.getInstance();
        feedbackRef = FirebaseDatabase.getInstance().getReference().child("teacher_feedback");

        editTextFeedback = findViewById(R.id.editTextFeedback);
        buttonSubmitFeedback = findViewById(R.id.buttonSubmitFeedback);

        ConstraintLayout backButton = findViewById(R.id.Constraint);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(teacher_feedback.this,teacher_settings.class);
                startActivity(intent);
                finish();
            }
        });

        buttonSubmitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFeedback();
            }
        });
    }

    private void submitFeedback() {
        String feedbackText = editTextFeedback.getText().toString().trim();

        if (!feedbackText.isEmpty()) {
            String userId = mAuth.getCurrentUser().getUid(); // Get the current user's ID
            String userEmail = mAuth.getCurrentUser().getEmail(); // Get the current user's email

            if (userId != null) {
                Calendar currentTime = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String currentDateTimeString = dateFormat.format(currentTime.getTime());

                DatabaseReference userFeedbackRef = feedbackRef.child(userId).child(currentDateTimeString); // Create a reference with user ID

                Map<String, Object> feedbackData = new HashMap<>();
                feedbackData.put("email", userEmail);
                feedbackData.put("feedback", feedbackText);

                userFeedbackRef.setValue(feedbackData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                editTextFeedback.setText("");
                                Toast.makeText(teacher_feedback.this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(teacher_feedback.this, "Failed to submit feedback: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } else {
            Toast.makeText(this, "Please enter your feedback", Toast.LENGTH_SHORT).show();
        }
    }
}
