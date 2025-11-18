package com.example.streamingapp.presentation.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.streamingapp.R;

import java.util.Arrays;
import java.util.List;

public class ContactActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    EditText nameEdt, emailEdt, feedbackEdt;
    Spinner mobileTypeSpinner, purposeTypeSpinner;
    ImageView backIv, imgIv;
    TextView attachFileTv, submitTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backIv = findViewById(R.id.backIv);
        nameEdt = findViewById(R.id.nameEdt);
        emailEdt = findViewById(R.id.emailEdt);
        feedbackEdt = findViewById(R.id.feedbackEdt);
        mobileTypeSpinner = findViewById(R.id.mobileType);
        purposeTypeSpinner = findViewById(R.id.purposeType);
        imgIv = findViewById(R.id.imgIv);
        attachFileTv = findViewById(R.id.attachFileTv);
        submitTv = findViewById(R.id.submitTv);

        backIv.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        String userGmail = intent.getStringExtra("userGmail");

        if (userGmail != null) {
            emailEdt.setText(userGmail);
        }


        List<String> mobileTypeList = Arrays.asList("Android", "iOS", "Other");
        List<String> purposeTypeList = Arrays.asList("Feedback", "Support", "General Inquiry");

        ArrayAdapter<String> mobileTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mobileTypeList);
        mobileTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mobileTypeSpinner.setAdapter(mobileTypeAdapter);

        ArrayAdapter<String> purposeTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, purposeTypeList);
        purposeTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        purposeTypeSpinner.setAdapter(purposeTypeAdapter);

        attachFileTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        submitTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    Toast.makeText(ContactActivity.this, "Details Submitted Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });


    }

    private boolean validateInputs() {
        boolean valid = true;

        String name = nameEdt.getText().toString().trim();
        String email = emailEdt.getText().toString().trim();
        String feedback = feedbackEdt.getText().toString().trim();

        if (name.isEmpty()) {
            nameEdt.setError("Name is required");
            valid = false;
        }

        if (email.isEmpty()) {
            emailEdt.setError("Email is required");
            valid = false;
        }

        if (feedback.isEmpty()) {
            feedbackEdt.setError("Feedback is required");
            valid = false;
        }

        if (imageUri == null) {
            attachFileTv.setError("Image is required");
            Toast.makeText(this, "Image is required", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            attachFileTv.setError(null);  // Clear error if image is present
        }

        return valid;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgIv.setImageURI(imageUri);
            attachFileTv.setError(null);
        }
    }
}