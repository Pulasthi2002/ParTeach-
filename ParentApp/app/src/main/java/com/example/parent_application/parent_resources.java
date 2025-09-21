package com.example.parent_application;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class parent_resources extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private RecyclerView recyclerViewFiles;
    private FilesAdapter filesAdapter;
    private List<String> fileList;
    private StorageReference storageReference;
    private static final int PICK_FILE_REQUEST = 1;
    private ProgressBar progressBar;
    private String parentGrade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_resources);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        recyclerViewFiles = findViewById(R.id.recyclerViewFiles);
        fileList = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            DatabaseReference parentRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
            parentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        parentGrade = dataSnapshot.child("grade").getValue(String.class);
                        filesAdapter = new FilesAdapter(fileList, parentGrade, parent_resources.this); // Pass the context here
                        recyclerViewFiles.setLayoutManager(new LinearLayoutManager(parent_resources.this));
                        recyclerViewFiles.setAdapter(filesAdapter);
                        retrieveFilesFromStorage(); // Retrieve files once we have the parent's grade
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                }
            });
        }

        ImageView back_btn = findViewById(R.id.button8);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent_resources.this, parent_home.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void retrieveFilesFromStorage() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference teacherRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            teacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        parentGrade = dataSnapshot.child("grade").getValue(String.class);
                        fetchFilesFromStorage(parentGrade);
                    } else {
                        Toast.makeText(parent_resources.this, "Teacher data not found", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(parent_resources.this, "Failed to fetch teacher data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private void fetchFilesFromStorage(String teacherGrade) {
        StorageReference gradeRef = storageReference.child(teacherGrade);
        gradeRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    fileList.add(item.getName());
                }
                filesAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(parent_resources.this, "Failed to fetch files: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}

