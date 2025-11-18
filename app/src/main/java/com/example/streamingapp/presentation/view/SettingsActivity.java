package com.example.streamingapp.presentation.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.streamingapp.R;

public class SettingsActivity extends AppCompatActivity {
    ImageView backIv;
    TextView reChooseInterest;
    Switch parentalControlSwitch, downloadviaWifiSwitch, autoplaySwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backIv = findViewById(R.id.backIv);
        reChooseInterest = findViewById(R.id.reChooseInterestTv);
        parentalControlSwitch = findViewById(R.id.parentalControlSwitch);
        downloadviaWifiSwitch = findViewById(R.id.downloadWifiSwitch);
        autoplaySwitch = findViewById(R.id.autoPlaySwitch);

        backIv.setOnClickListener(v -> finish());

        reChooseInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, PickVideoTypeActivity.class);
                intent.putExtra("origin", "settings");
                startActivity(intent);
            }
        });
        parentalControlSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Change track and thumb tint based on state
            if (isChecked){
                Toast.makeText(this, "Parental Control Enabled,\nGo to Contact Btv Support in Account to View or Manage", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Parental Control Disabled", Toast.LENGTH_SHORT).show();
            }
        });
        
        downloadviaWifiSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                Toast.makeText(this, "Download Via Wifi Enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Download Via Wifi Disabled", Toast.LENGTH_SHORT).show();
            }
        });
        
        autoplaySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                Toast.makeText(this, "Auto Play Enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Auto Play Disabled", Toast.LENGTH_SHORT).show();
            }
        });
    }
}