package com.example.parent_application;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FileViewHolder> {

    private String parentGrade;
    private List<String> fileList;
    private Context context;

    public FilesAdapter(List<String> fileList, String parentGrade, Context context) {
        this.fileList = fileList;
        this.parentGrade = parentGrade;
        this.context = context;

        // Register BroadcastReceiver to listen for download completion
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        context.registerReceiver(downloadReceiver, filter);
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
        Button buttonDownload;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFileName = itemView.findViewById(R.id.textViewFileName);
            buttonDownload = itemView.findViewById(R.id.buttonDownload);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        final String fileName = fileList.get(position);
        holder.textViewFileName.setText(fileName);
        holder.buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile(fileName, parentGrade, v.getContext());
            }
        });
    }

    private void downloadFile(String fileName, String parentGrade, Context context) {
        Log.d("FilesAdapter", "Parent Grade: " + parentGrade);
        Log.d("FilesAdapter", "File Name: " + fileName);

        if (parentGrade != null && !parentGrade.isEmpty() && fileName != null && !fileName.isEmpty()) {
            StorageReference fileRef = FirebaseStorage.getInstance().getReference().child(parentGrade).child(fileName);
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
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle any errors
                    Toast.makeText(context, "Failed to download file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("FilesAdapter", "Invalid parentGrade or fileName");
                }
            });
        }
    }

    // BroadcastReceiver to listen for download completion
    private final BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadId != -1) {
                Toast.makeText(context, "File download complete", Toast.LENGTH_SHORT).show();
            }
        }
    };
}

