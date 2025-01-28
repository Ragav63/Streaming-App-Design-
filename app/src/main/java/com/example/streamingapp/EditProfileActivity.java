package com.example.streamingapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;

public class EditProfileActivity extends AppCompatActivity {
    ImageView backIv, userIv;
    TextView gmailTv, deleteTv, cancelTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backIv = findViewById(R.id.backIv);
        userIv = findViewById(R.id.userIv);
        gmailTv = findViewById(R.id.userGmailTv);
        deleteTv = findViewById(R.id.deleteTv);
        cancelTv = findViewById(R.id.cancelTv);

        Intent intent = getIntent();
        String userGmail = intent.getStringExtra("userGmail");
        String userImgPath = intent.getStringExtra("userImgPath");

        if (userGmail != null) {
            gmailTv.setText(userGmail);
        }

        if (userImgPath != null) {
            File imgFile = new File(userImgPath);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                userIv.setImageBitmap(bitmap);
            }
        }

        backIv.setOnClickListener(v -> finish());

        deleteTv.setOnClickListener(v -> {
            Toast.makeText(this, "Your Account Deleted Successfully", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(this, LoginActivity.class);
            startActivity(intent1);
        });

        cancelTv.setOnClickListener(v -> finish());

    }
}