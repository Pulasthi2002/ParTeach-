package com.example.parent_application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class parent_view_marks extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private TextView subject1TextView, subject2TextView, subject3TextView, subject4TextView, subject5TextView;
    private RadioGroup termRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_view_marks);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("studentmarks");

        subject1TextView = findViewById(R.id.subject1TextView);
        subject2TextView = findViewById(R.id.subject2TextView);
        subject3TextView = findViewById(R.id.subject3TextView);
        subject4TextView = findViewById(R.id.subject4TextView);
        subject5TextView = findViewById(R.id.subject5TextView);
        termRadioGroup = findViewById(R.id.termRadioGroup);

        ConstraintLayout back_btn = findViewById(R.id.button1);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent_view_marks.this, parent_home.class);
                startActivity(intent);
                finish();
            }
        });

        // Retrieve the current user's ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Add listener for term selection
            termRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // Get selected term and load corresponding marks
                    loadMarks(userId);
                }
            });

            // Load marks initially with the default selected term
            loadMarks(userId);
        }
    }

    private void loadMarks(String userId) {
        // Get selected term
        String selectedTerm = getSelectedTerm();
        if (selectedTerm == null) {
            return; // No term selected, do nothing
        }

        // Access the corresponding folder in the studentmarks collection
        DatabaseReference userMarksRef = mDatabase.child(userId).child(selectedTerm);
        userMarksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve marks from the database and display them in the TextViews
                    String subject1Marks = dataSnapshot.child("English").getValue(String.class);
                    String subject2Marks = dataSnapshot.child("Mathematics").getValue(String.class);
                    String subject3Marks = dataSnapshot.child("Sinhala").getValue(String.class);
                    String subject4Marks = dataSnapshot.child("Buddhism").getValue(String.class);
                    String subject5Marks = dataSnapshot.child("Tamil").getValue(String.class);

                    subject1TextView.setText("English : " + subject1Marks);
                    subject2TextView.setText("Mathematics : " + subject2Marks);
                    subject3TextView.setText("Sinhala : " + subject3Marks);
                    subject4TextView.setText("Buddhism : " + subject4Marks);
                    subject5TextView.setText("Tamil : " + subject5Marks);
                } else {
                    // Handle the case where no marks are found for the user
                    // You may want to display a message or take appropriate action
                    subject1TextView.setText("English : No Marks");
                    subject2TextView.setText("Mathematics : No Marks");
                    subject3TextView.setText("Sinhala : No Marks");
                    subject4TextView.setText("Buddhism : No Marks");
                    subject5TextView.setText("Tamil : No Marks");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    // Method to get the selected term from RadioGroup
    private String getSelectedTerm() {
        int selectedRadioButtonId = termRadioGroup.getCheckedRadioButtonId();
        if (selectedRadioButtonId == -1) {
            return null; // No term selected
        }

        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
        return selectedRadioButton.getText().toString(); // Return the text of selected RadioButton
    }
}
