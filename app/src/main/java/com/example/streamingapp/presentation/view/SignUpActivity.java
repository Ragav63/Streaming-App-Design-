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

public class SignUpActivity extends AppCompatActivity {
    EditText firstNameEdt, lastNameEdt, emailEdt, passwordEdt;
    TextView signUpTv, loginTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firstNameEdt = findViewById(R.id.fNameEdt);
        lastNameEdt = findViewById(R.id.lNameEdt);;
        emailEdt = findViewById(R.id.emailEdt);
        passwordEdt = findViewById(R.id.passwordEdt);
        signUpTv = findViewById(R.id.signUpTv);
        loginTv = findViewById(R.id.loginTv);

        String login = "login";

        loginTv.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));

        signUpTv.setOnClickListener(v -> {
            if (validateInputs()){
                Intent intent = new Intent(this, PickVideoTypeActivity.class);
                intent.putExtra("login", login);
                startActivity(intent);
            }
        });
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(firstNameEdt.getText().toString())) {
            firstNameEdt.setError("First Name is required");
            return false;
        }

        if (TextUtils.isEmpty(lastNameEdt.getText().toString())) {
            lastNameEdt.setError("Last Name is required");
            return false;
        }

        if (TextUtils.isEmpty(emailEdt.getText().toString()) ) {
            emailEdt.setError("Email is required");
            return false;
        }  else if (!Patterns.EMAIL_ADDRESS.matcher(emailEdt.getText().toString()).matches()) {
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