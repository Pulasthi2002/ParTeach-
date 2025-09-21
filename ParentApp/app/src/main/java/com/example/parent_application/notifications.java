package com.example.parent_application;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
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
    private List<String> absentDatesList;
    private AbsentDatesAdapter adapter;
    private DatabaseReference mDatabase;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications);

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference(); // Initialize mDatabase here

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        absentDatesList = new ArrayList<>();
        adapter = new AbsentDatesAdapter(absentDatesList);
        recyclerView.setAdapter(adapter);
        progressBar = findViewById(R.id.progressBar);

        ConstraintLayout back=findViewById(R.id.Constraint);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(notifications.this,parent_home.class);
                startActivity(intent);
                finish();
            }
        });

        // Assuming you have a method to retrieve the current user's email
        String currentUserEmail = getCurrentUserEmail();

        // Retrieve and display absent dates
        displayAbsentDatesForCurrentUser(currentUserEmail);
    }


    private void displayAbsentDatesForCurrentUser(String currentUserEmail) {
        final String decodedEmail = decodeEmail(currentUserEmail);

        // Database reference
        DatabaseReference attendanceRef = mDatabase.child("attendance");

        attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    String date = dateSnapshot.getKey();

                    // Check if the "absent" subfolder exists for this date
                    DataSnapshot absentSnapshot = dateSnapshot.child("absent");
                    if (absentSnapshot.exists()) {
                        // Iterate through the "absent" subfolder
                        for (DataSnapshot emailSnapshot : absentSnapshot.getChildren()) {
                            String encodedEmail = emailSnapshot.getKey();
                            String userEmail = decodeEmail(encodedEmail); // Decode email
                            if (userEmail.equals(decodedEmail)) {
                                // Current user was absent on this date
                                absentDatesList.add(date);
                                break; // No need to continue checking for this date
                            }

                        }
                    }
                }

                // Update RecyclerView with absent dates
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors
            }
        });
    }

    private String decodeEmail(String encodedEmail) {
        // Implement email decoding logic here
        // For demonstration purposes, let's assume simple decoding
        return encodedEmail.replace(",", "."); // Decode %40 back to @
    }

    private String getCurrentUserEmail() {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = currentUser.getEmail();
        return userEmail;
    }
}

class AbsentDatesAdapter extends RecyclerView.Adapter<AbsentDatesAdapter.ViewHolder> {

    private List<String> absentDatesList;

    public AbsentDatesAdapter(List<String> absentDatesList) {
        this.absentDatesList = absentDatesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_absent_date, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String date = absentDatesList.get(position);
        holder.bind(date);
    }

    @Override
    public int getItemCount() {
        return absentDatesList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
        }

        public void bind(String date) {
            textViewDate.setText(date);
        }
    }
}
