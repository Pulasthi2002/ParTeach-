package com.example.par_teach;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class teacher_settings extends AppCompatActivity {
    FirebaseAuth mAuth;
    ConstraintLayout chatbot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_settings);

        chatbot=findViewById(R.id.button);

        chatbot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(teacher_settings.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ConstraintLayout feedback_btn=findViewById(R.id.button8);

        feedback_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(teacher_settings.this,teacher_feedback.class);
                startActivity(intent);
                finish();
            }
        });

        ConstraintLayout logout=findViewById(R.id.button5);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(teacher_settings.this,teacher_login.class);
                startActivity(intent);
                Toast.makeText(teacher_settings.this, "Logout successful.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        ImageView support=findViewById(R.id.imageView4);

        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(teacher_settings.this,teacher_support.class);
                startActivity(intent);
                finish();
            }
        });
        ConstraintLayout about=findViewById(R.id.button4);

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(teacher_settings.this,about.class);
                startActivity(intent);
                finish();
            }
        });
        ConstraintLayout profile_btn=findViewById(R.id.button1);

        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(teacher_settings.this,teacher_profile.class);
                startActivity(intent);
                finish();
            }
        });
        ConstraintLayout back_btn=findViewById(R.id.Constraint);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(teacher_settings.this,teacher_home.class);
                startActivity(intent);
                finish();
            }
        });
    }
}