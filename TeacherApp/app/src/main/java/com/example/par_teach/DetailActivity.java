package com.example.par_teach;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailActivity extends AppCompatActivity {

    private TextView nameTextView;
    private EditText subject1EditText, subject2EditText, subject3EditText, subject4EditText, subject5EditText;
    private Button saveButton;
    private DatabaseReference mDatabase;
    private RadioGroup termRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        nameTextView = findViewById(R.id.nameTextView);
        subject1EditText = findViewById(R.id.subject1EditText);
        subject2EditText = findViewById(R.id.subject2EditText);
        subject3EditText = findViewById(R.id.subject3EditText);
        subject4EditText = findViewById(R.id.subject4EditText);
        subject5EditText = findViewById(R.id.subject5EditText);
        saveButton = findViewById(R.id.saveButton);
        termRadioGroup = findViewById(R.id.termRadioGroup); // Initialize the RadioGroup

        mDatabase = FirebaseDatabase.getInstance().getReference().child("studentmarks");

        // Retrieve data from intent
        String selectedStudentId = getIntent().getStringExtra("selectedUserId");
        // Fetch name from users collection
        fetchUserName(selectedStudentId);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMarks(selectedStudentId);
            }
        });
    }

    private void fetchUserName(String studentId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(studentId);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    nameTextView.setText(name);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    private void saveMarks(String studentId) {
        // Get selected term
        String selectedTerm = getSelectedTerm();
        if (selectedTerm == null) {
            Toast.makeText(this, "Please select a term", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get marks entered by the user
        String subject1Marks = subject1EditText.getText().toString();
        String subject2Marks = subject2EditText.getText().toString();
        String subject3Marks = subject3EditText.getText().toString();
        String subject4Marks = subject4EditText.getText().toString();
        String subject5Marks = subject5EditText.getText().toString();
        // Get marks for other subjects similarly

        // Update the user's data with the marks in the studentmarks collection under the selected term
        DatabaseReference termRef = mDatabase.child(studentId).child(selectedTerm);
        termRef.child("English").setValue(subject1Marks);
        termRef.child("Mathematics").setValue(subject2Marks);
        termRef.child("Sinhala").setValue(subject3Marks);
        termRef.child("Buddhism").setValue(subject4Marks);
        termRef.child("Tamil").setValue(subject5Marks);
        // Update marks for other subjects similarly

        subject1EditText.setText("");
        subject2EditText.setText("");
        subject3EditText.setText("");
        subject4EditText.setText("");
        subject5EditText.setText("");

        Toast.makeText(DetailActivity.this, "Marks saved successfully for " + selectedTerm, Toast.LENGTH_SHORT).show();
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
