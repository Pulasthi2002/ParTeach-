package com.example.par_teach;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.widget.ProgressBar;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import android.widget.AdapterView;

public class teacher_view_students extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> userList;
    private FirebaseAuth mAuth;
    private String teacherGrade; // Added to store the grade of the logged-in teacher
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_view_students);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        listView = findViewById(R.id.listView);
        ConstraintLayout back_btn=findViewById(R.id.Constraint);
        userList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        listView.setAdapter(adapter);
        progressBar = findViewById(R.id.progressBar); // Initialize ProgressBar

        // Initially hide ListView and show ProgressBar
        listView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Fetch the grade of the logged-in teacher
            DatabaseReference teacherRef = FirebaseDatabase.getInstance().getReference("teachers").child(currentUser.getUid());
            teacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        teacherGrade = dataSnapshot.child("grade").getValue(String.class);
                        fetchDataFromFirebase(); // Fetch data once we have the teacher's grade
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
                Intent intent = new Intent(teacher_view_students.this, teacher_home.class);
                startActivity(intent);
                finish();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle item click, navigate to DetailActivity
                final String selectedEmail = userList.get(position).split(" - ")[1]; // Extract email from the selected item
                mDatabase.orderByChild("email").equalTo(selectedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String selectedUserId = snapshot.getKey(); // Get the user ID corresponding to the selected email
                            Intent intent = new Intent(teacher_view_students.this, DetailActivity.class);
                            // Pass user ID to DetailActivity
                            intent.putExtra("selectedUserId", selectedUserId);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors
                    }
                });
            }
        });
    }

    private void fetchDataFromFirebase() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String grade = snapshot.child("grade").getValue(String.class); // Fetch the grade of each user

                    if (email != null && grade != null && grade.equals(teacherGrade)) {
                        userList.add(name + " - " + email);
                    }
                }
                adapter.notifyDataSetChanged();

                // Once data is loaded, hide ProgressBar and show ListView
                progressBar.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }
}
