package com.example.streamingapp.presentation.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.streamingapp.R;

public class LoginActivity extends AppCompatActivity {
    EditText emailEdt, passwordEdt;
    TextView loginTv, signUpTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailEdt = findViewById(R.id.emailEdt);
        passwordEdt = findViewById(R.id.passwordEdt);
        loginTv = findViewById(R.id.loginTv);
        signUpTv = findViewById(R.id.signUpTv);

        String login = "login";

        loginTv.setOnClickListener(v -> {
            if (validateInputs()) {
                Intent intent = new Intent(this, PickVideoTypeActivity.class);
                intent.putExtra("login", login);
                startActivity(intent);
            }
        });

        signUpTv.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });

    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(emailEdt.getText().toString())) {
            emailEdt.setError("Email is required");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailEdt.getText().toString()).matches()) {
            emailEdt.setError("Enter a valid email address");
            return false;
        }
        if (TextUtils.isEmpty(passwordEdt.getText().toString())) {
            passwordEdt.setError("Password is required");
            return false;
        }

        return true;
    }
}