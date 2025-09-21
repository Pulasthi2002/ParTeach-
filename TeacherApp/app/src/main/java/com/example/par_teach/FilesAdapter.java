package com.example.par_teach;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FileViewHolder> {

    private List<String> fileList;

    public FilesAdapter(List<String> fileList) {
        this.fileList = fileList;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    static class FileViewHolder extends RecyclerView.ViewHolder {
        TextView textViewFileName;
        Button buttonDownload, buttonDelete;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFileName = itemView.findViewById(R.id.textViewFileName);
            buttonDownload = itemView.findViewById(R.id.buttonDownload);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        final String fileName = fileList.get(position);
        holder.textViewFileName.setText(fileName);
        holder.buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile(fileName, v.getContext());
            }
        });
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFile(fileName, v.getContext());
            }
        });
    }

    private void deleteFile(String fileName, Context context) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Fetch the grade of the logged-in teacher
            DatabaseReference teacherRef = FirebaseDatabase.getInstance().getReference("teachers").child(currentUser.getUid());
            teacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String teacherGrade = dataSnapshot.child("grade").getValue(String.class);
                        StorageReference fileRef = FirebaseStorage.getInstance().getReference().child(teacherGrade).child(fileName);
                        fileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // File deleted successfully
                                fileList.remove(fileName);
                                notifyDataSetChanged();
                                Toast.makeText(context, "File deleted successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle any errors
                                Toast.makeText(context, "Failed to delete file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // Handle case where teacher data does not exist
                        Toast.makeText(context, "Teacher data not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                    Toast.makeText(context, "Failed to fetch teacher data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Handle case where current user is null
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadFile(String fileName, Context context) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Fetch the grade of the logged-in teacher
            DatabaseReference teacherRef = FirebaseDatabase.getInstance().getReference("teachers").child(currentUser.getUid());
            teacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String teacherGrade = dataSnapshot.child("grade").getValue(String.class);
                        StorageReference fileRef = FirebaseStorage.getInstance().getReference().child(teacherGrade).child(fileName);
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // File downloaded successfully, start download
                                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                                DownloadManager.Request request = new DownloadManager.Request(uri);
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setTitle(fileName);
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName); // Save in Downloads directory
                                downloadManager.enqueue(request);
                                Toast.makeText(context, "File downloaded successfully.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle any errors
                                Toast.makeText(context, "Failed to download file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // Handle case where teacher data does not exist
                        Toast.makeText(context, "Teacher data not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                    Toast.makeText(context, "Failed to fetch teacher data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Handle case where current user is null
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
