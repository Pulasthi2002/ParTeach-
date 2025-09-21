package com.example.par_teach;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class notifications extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications);

        ConstraintLayout back_btn = findViewById(R.id.Constraint);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerViewEvents);

        mAuth = FirebaseAuth.getInstance();
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(notificationList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(notificationAdapter);

        progressBar.setVisibility(View.VISIBLE);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(notifications.this,teacher_home.class);
                startActivity(intent);
                finish();
            }
        });
        // Fetch absent requests for the current user
        fetchAbsentRequests();
    }

    private void fetchAbsentRequests() {
        DatabaseReference absentRequestsRef = FirebaseDatabase.getInstance().getReference("absent_requests");

        absentRequestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    for (DataSnapshot snapshot : userSnapshot.getChildren()) {
                        String value = snapshot.getValue(String.class);

                        String[] parts = value.split("\n");
                        String userName = parts[0].substring(6); // Remove "User: " from the beginning
                        String date = parts[1].substring(6); // Remove "Date: " from the beginning
                        String absentReason = parts[2].substring(14); // Remove "Absent Reason: " from the beginning
                        Notification notification = new Notification(userName, date, absentReason);
                        notificationList.add(notification);
                    }
                }
                notificationAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                progressBar.setVisibility(View.GONE);
                Toast.makeText(notifications.this, "Failed to fetch absent requests: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
