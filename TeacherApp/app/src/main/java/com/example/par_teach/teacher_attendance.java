package com.example.par_teach;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.par_teach.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class teacher_attendance extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> userList;
    private Map<String, String> nameToEmailMap; // Map to associate name with email
    private String teacherGrade; // Added to store the grade of the logged-in teacher
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_attendence);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        listView = findViewById(R.id.listView);
        ConstraintLayout back_btn=findViewById(R.id.Constraint);
        userList = new ArrayList<>();
        nameToEmailMap = new HashMap<>(); // Initialize the map
        adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.item_text, userList);
        listView.setAdapter(adapter);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Fetch the grade of the logged-in teacher
            DatabaseReference teacherRef = FirebaseDatabase.getInstance().getReference("teachers").child(currentUser.getUid());
            teacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        teacherGrade = dataSnapshot.child("grade").getValue(String.class);
                        fetchDataFromRealtimeDatabase(); // Fetch data once we have the teacher's grade
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                }
            });
        }

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(teacher_attendance.this,teacher_home.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void fetchDataFromRealtimeDatabase() {
        mDatabase.child("users")
                .orderByChild("grade")
                .equalTo(teacherGrade) // Filter users by the logged-in teacher's grade
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            String name = snapshot.child("name").getValue(String.class);
                            String email = snapshot.child("email").getValue(String.class);
                            String grade = snapshot.child("grade").getValue(String.class);

                            progressBar.setVisibility(View.GONE);
                            // Add the name to the list
                            userList.add(name + " - " + grade);
                            // Associate the name with its email in the map
                            nameToEmailMap.put(name, email);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
    @Override
    protected void onStart() {
        super.onStart();

        Button submitButton = findViewById(R.id.button1);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAttendance();
            }
        });
    }

    private String encodeEmail(String email) {
        return email.replace(".", ",");
    }

    private void saveAttendance() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(Calendar.getInstance().getTime());
        boolean notify = ((Switch) findViewById(R.id.switch1)).isChecked();

        for (int i = 0; i < listView.getCount(); i++) {
            View view = listView.getChildAt(i);
            CheckBox checkBox = view.findViewById(R.id.checkBox);

            String item = adapter.getItem(i);
            String[] parts = item.split(" - ");
            String name = parts[0]; // Extract name from the list item
            String email = nameToEmailMap.get(name); // Retrieve email using name

            DatabaseReference attendanceRef;
            if (checkBox.isChecked()) {
                attendanceRef = mDatabase.child("attendance")
                        .child(currentDate)
                        .child("yes") // Save under "yes" collection
                        .child(encodeEmail(email)); // Encode email and save
                Toast.makeText(teacher_attendance.this, "Attendance saved successfully.", Toast.LENGTH_SHORT).show();
            } else {
                if (notify) {
                    // Save under "absent" collection if switch is on
                    attendanceRef = mDatabase.child("attendance")
                            .child(currentDate)
                            .child("absent")
                            .child(encodeEmail(email)); // Encode email and save
                    Toast.makeText(teacher_attendance.this, "Attendance saved successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    // Save under "no" collection if switch is off
                    attendanceRef = mDatabase.child("attendance")
                            .child(currentDate)
                            .child("no")
                            .child(encodeEmail(email)); // Encode email and save
                    Toast.makeText(teacher_attendance.this, "Attendance saved successfully.", Toast.LENGTH_SHORT).show();
                }
            }

            attendanceRef.setValue(true); // You can set any value here, for example, true
        }
    }

}