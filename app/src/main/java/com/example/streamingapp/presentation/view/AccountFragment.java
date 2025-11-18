package com.example.streamingapp.presentation.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.streamingapp.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class AccountFragment extends Fragment {
    ImageView userIv;
    TextView userGmailTv, editProfileTv, helpCenterTv, settingsTv, contactBtvTv, aboutBtvtv, logoutTv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        userIv = view.findViewById(R.id.userIv);
        userGmailTv = view.findViewById(R.id.userGmailTv);
        editProfileTv = view.findViewById(R.id.editProfileTv);
        helpCenterTv = view.findViewById(R.id.helpCenterTv);
        settingsTv = view.findViewById(R.id.settingsTv);
        contactBtvTv = view.findViewById(R.id.contactBtvTv);
        aboutBtvtv = view.findViewById(R.id.aboutBtvTv);
        logoutTv = view.findViewById(R.id.logout);

        String userGmail = userGmailTv.getText().toString();

        editProfileTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = userIv.getDrawable();
                if (drawable != null) {
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    File imageFile = saveBitmapToFile(bitmap);

                    if (imageFile != null) {
                        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                        intent.putExtra("userGmail", userGmail);
                        intent.putExtra("userImgPath", imageFile.getAbsolutePath());
                        startActivity(intent);
                    }
                }
            }
        });

        settingsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        helpCenterTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ContactActivity.class);
                intent.putExtra("userGmail", userGmail);
                startActivity(intent);
            }
        });

        contactBtvTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ParentalControlActivity.class);
                startActivity(intent);
            }
        });

        logoutTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private File saveBitmapToFile(Bitmap bitmap) {
        File directory = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "ProfilePictures");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File imageFile = new File(directory, "user_profile.png");
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            return imageFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}