package com.example.parent_application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;

public class parent_settings extends AppCompatActivity {
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_settings);

        ImageView back_btn=findViewById(R.id.button8);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent_settings.this,parent_home.class);
                startActivity(intent);
                finish();
            }
        });
        ConstraintLayout feedback_btn=findViewById(R.id.feedback);

        feedback_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent_settings.this,parent_feedback.class);
                startActivity(intent);
                finish();
            }
        });
        ConstraintLayout trblshoot = findViewById(R.id.troubleshoot);

        trblshoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent_settings.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        ConstraintLayout profile=findViewById(R.id.button);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent_settings.this, parent_profile.class);
                startActivity(intent);
                finish();
            }
        });

        ConstraintLayout logout=findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent_settings.this, parent_login.class);
                startActivity(intent);
                finish();
            }
        });

        ConstraintLayout about=findViewById(R.id.button4);

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent_settings.this, about.class);
                startActivity(intent);
                finish();
            }
        });

        ImageView support=findViewById(R.id.imageView4);

        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent_settings.this, parent_support.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
