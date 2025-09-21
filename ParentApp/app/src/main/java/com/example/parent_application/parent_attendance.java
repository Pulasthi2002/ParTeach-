package com.example.parent_application;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class parent_attendance extends AppCompatActivity {
    private CalendarView calendarView;
    private TextView attendanceStatusTextView;
    private EditText absentReasonEditText;
    private MaterialButton selectDateButton, sendSelectedDateButton;
    private Calendar selectedDateCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_attendence);

        calendarView = findViewById(R.id.calendarView);
        attendanceStatusTextView = findViewById(R.id.attendanceStatusTextView);
        selectDateButton = findViewById(R.id.selectDateButton);
        absentReasonEditText = findViewById(R.id.reason);
        sendSelectedDateButton = findViewById(R.id.sendSelectedDateButton);

        selectedDateCalendar = Calendar.getInstance();

        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        sendSelectedDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSelectedDate();

                Button requestButton = findViewById(R.id.requestButton);
                selectDateButton.setVisibility(View.GONE);
                sendSelectedDateButton.setVisibility(View.GONE);
                absentReasonEditText.setVisibility(View.GONE);
                requestButton.setVisibility(View.VISIBLE);
            }
        });

        RelativeLayout back = findViewById(R.id.button1);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent_attendance.this, parent_home.class);
                startActivity(intent);
                finish();
            }
        });

        Button requestButton = findViewById(R.id.requestButton);
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show absent reason EditText and Send button
                selectDateButton.setVisibility(View.VISIBLE);
                sendSelectedDateButton.setVisibility(View.VISIBLE);
                absentReasonEditText.setVisibility(View.VISIBLE);
                requestButton.setVisibility(View.GONE);
            }
        });

        // Get current logged-in user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail(); // Retrieve email of the current user
            String encodedEmail = encodeEmail(userEmail); // Encode email

            DatabaseReference attendanceRef = FirebaseDatabase.getInstance().getReference("attendance");
            attendanceRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                        @Override
                        public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                            Calendar selectedDate = Calendar.getInstance();
                            selectedDate.set(year, month, dayOfMonth);

                            // Get the current date
                            Calendar currentDate = Calendar.getInstance();

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            String selectedDateString = sdf.format(selectedDate.getTime());

                            boolean present = false;

                            // Check if the selected date is in front of the current date
                            if (selectedDate.compareTo(currentDate) < 1) {
                                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                                    if (dateSnapshot.getKey().equals(selectedDateString)) {
                                        DataSnapshot yesSnapshot = dateSnapshot.child("yes");
                                        DataSnapshot noSnapshot = dateSnapshot.child("no");

                                        // Check if the user is present for this date
                                        present = yesSnapshot.hasChild(encodedEmail);
                                        break;
                                    }
                                }

                                if (present) {
                                    // If present, display "Present"
                                    attendanceStatusTextView.setText("Present");
                                    attendanceStatusTextView.setTextColor(ContextCompat.getColor(parent_attendance.this, R.color.presentColor));
                                } else {
                                    // If absent, display "Absent"
                                    attendanceStatusTextView.setText("Absent");
                                    attendanceStatusTextView.setTextColor(Color.RED);
                                }
                            } else {
                                // If the selected date is in front of the current date, display "Not Available"
                                attendanceStatusTextView.setText("Not Available");
                                attendanceStatusTextView.setTextColor(Color.GRAY);
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                }
            });
        }
    }
    private void showDatePickerDialog() {
        // Get current date for initial selection in the DatePickerDialog
        int year = selectedDateCalendar.get(Calendar.YEAR);
        int month = selectedDateCalendar.get(Calendar.MONTH);
        int dayOfMonth = selectedDateCalendar.get(Calendar.DAY_OF_MONTH);

        // Create DatePickerDialog and set initial date
        DatePickerDialog datePickerDialog = new DatePickerDialog(parent_attendance.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Set selected date to selectedDateCalendar
                        selectedDateCalendar.set(Calendar.YEAR, year);
                        selectedDateCalendar.set(Calendar.MONTH, monthOfYear);
                        selectedDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Update the text on the button with selected date
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        selectDateButton.setText(sdf.format(selectedDateCalendar.getTime()));
                    }
                }, year, month, dayOfMonth);

        // Show DatePickerDialog
        datePickerDialog.show();
    }

    private void sendSelectedDate() {
        // Get the selected date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String selectedDateString = sdf.format(selectedDateCalendar.getTime());

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Get current date and time
            Calendar currentTime = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String currentDateTimeString = dateFormat.format(currentTime.getTime());

            String absentReason = absentReasonEditText.getText().toString().trim();

            // Fetch the user's name
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userName = dataSnapshot.child("name").getValue(String.class);

                        // Construct the value to save
                        String valueToSave = "User: " + userName + "\nDate: " + selectedDateString + "\nAbsent Reason: " + absentReason;

                        // Save absent reason to Firebase under "absent_requests" collection with unique key
                        DatabaseReference absentRequestsRef = FirebaseDatabase.getInstance().getReference("absent_requests")
                                .child(userId) // Save under user's ID
                                .child(currentDateTimeString); // Use current date and time as the key
                        absentRequestsRef.setValue(valueToSave);

                        // Inform the user that absence request was successful
                        Toast.makeText(parent_attendance.this, "Absence requested successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle the case where user data does not exist
                        Toast.makeText(parent_attendance.this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                    Toast.makeText(parent_attendance.this, "Failed to fetch user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String encodeEmail(String email) {
        return email.replace(".", ",");
    }
}
