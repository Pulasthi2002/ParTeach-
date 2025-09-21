package com.example.par_teach;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
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
import com.google.firebase.storage.UploadTask;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class teacher_resources extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button buttonSelectFile;
    private RecyclerView recyclerViewFiles;
    private FilesAdapter filesAdapter;
    private List<String> fileList;
    private StorageReference storageReference;
    private String teacherGrade; // Added to store the grade of the logged-in teacher
    private static final int PICK_FILE_REQUEST = 1;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_resources);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        buttonSelectFile = findViewById(R.id.buttonSelectFile);
        recyclerViewFiles = findViewById(R.id.recyclerViewFiles);
        fileList = new ArrayList<>();
        filesAdapter = new FilesAdapter(fileList);
        progressBar = findViewById(R.id.progressBar);

        recyclerViewFiles.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFiles.setAdapter(filesAdapter);
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
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                }
            });
        }

        buttonSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });

        retrieveFilesFromStorage();
        ConstraintLayout back_btn=findViewById(R.id.Constraint);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(teacher_resources.this,teacher_home.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Allow any file type
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri fileUri = data.getData();
            uploadFile(fileUri);
        }
    }

    private void uploadFile(Uri fileUri) {
        // Get the original file name
        String fileName = getFileName(fileUri);

        // Create a reference to the file with the original name within the teacher's grade folder
        StorageReference fileRef = storageReference.child(teacherGrade).child(fileName);
        Toast.makeText(teacher_resources.this, "Uploading file", Toast.LENGTH_SHORT).show();

        fileRef.putFile(fileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // File uploaded successfully
                        // Add file name or download URL to fileList
                        fileList.add(fileName); // Add original file name instead of fileRef.getName()
                        filesAdapter.notifyDataSetChanged();
                        Toast.makeText(teacher_resources.this, "File uploaded successfully ", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle unsuccessful uploads
                        Toast.makeText(teacher_resources.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to get the file name from the Uri
    private String getFileName(Uri fileUri) {
        String result = null;
        if (fileUri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(fileUri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (displayNameIndex != -1) {
                        result = cursor.getString(displayNameIndex);
                    } else {
                        // Handle the case where DISPLAY_NAME column doesn't exist
                        // For example, you can log an error or provide a default value
                        Log.e("getFileName", "DISPLAY_NAME column not found in cursor");
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = fileUri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void retrieveFilesFromStorage() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Fetch the grade of the logged-in teacher
            String userId = currentUser.getUid();
            DatabaseReference teacherRef = FirebaseDatabase.getInstance().getReference("teachers").child(userId);
            teacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        teacherGrade = dataSnapshot.child("grade").getValue(String.class);
                        // Call fetchFilesFromStorage() only after teacherGrade is fetched successfully
                        fetchFilesFromStorage(teacherGrade);
                    } else {
                        // Handle case where teacher data does not exist
                        Toast.makeText(teacher_resources.this, "Teacher data not found", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                    Toast.makeText(teacher_resources.this, "Failed to fetch teacher data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
                // Handle any errors
                Toast.makeText(teacher_resources.this, "Failed to fetch files: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }


}
