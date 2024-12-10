package com.example.hello;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class imgselect extends Fragment {
    private ImageView ivAvatar;
    private static final int REQUEST_IMAGE_UPLOAD = 1001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_contact, container, false);

        // 初始化头像ImageView
        ivAvatar = view.findViewById(R.id.iv_avatar);

        // 加载保存的头像
        loadSavedAvatar();

        // 设置头像点击事件
        ivAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ImageUploadActivity.class);
            startActivityForResult(intent, REQUEST_IMAGE_UPLOAD);
        });

        return view;
    }

    private void loadSavedAvatar() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserProfile", Context.MODE_PRIVATE);
        String savedAvatarUrl = prefs.getString("avatar_url", null);
        if (savedAvatarUrl != null) {
            updateAvatar(savedAvatarUrl);
        }
    }

    private void updateAvatar(String imageUrl) {
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    return BitmapFactory.decodeStream(input);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                if (result != null && ivAvatar != null) {
                    ivAvatar.setImageBitmap(result);
                }
            }
        }.execute(imageUrl);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_UPLOAD && resultCode == Activity.RESULT_OK && data != null) {
            String newAvatarUrl = data.getStringExtra("avatar_url");
            if (newAvatarUrl != null) {
                updateAvatar(newAvatarUrl);

                // 保存新的头像URL
                SharedPreferences prefs = requireActivity().getSharedPreferences("UserProfile", Context.MODE_PRIVATE);
                prefs.edit().putString("avatar_url", newAvatarUrl).apply();
            }
        }
    }
}
