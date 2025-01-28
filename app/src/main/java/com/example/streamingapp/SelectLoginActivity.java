package com.example.streamingapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SelectLoginActivity extends AppCompatActivity {
    TextView loginTv, fbLoginTv, googleTv, signUpTv;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginTv = findViewById(R.id.loginTv);
        fbLoginTv = findViewById(R.id.loginFbTv);
        googleTv = findViewById(R.id.loginGoogleTv);
        signUpTv = findViewById(R.id.signUpTv);

        loginTv.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));

        fbLoginTv.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));

        googleTv.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));

        signUpTv.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));

    }
}