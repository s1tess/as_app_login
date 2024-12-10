package com.example.hello;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import android.os.Build;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageUploadActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int IMAGE_PICK_CODE = 200;

    private Button btnSelectImage;
    private Button btnUpload;
    private ImageView imageView;
    private Uri selectedImageUri;

    private OkHttpClient client;
    private static final String UPLOAD_URL = "http://112.124.22.157/upload.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnUpload = findViewById(R.id.btnUpload);
        imageView = findViewById(R.id.imageView);
        client = new OkHttpClient();
    }

    private void setupClickListeners() {
        btnSelectImage.setOnClickListener(v -> checkPermissionAndPickImage());
        btnUpload.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                uploadImage();
            }
        });
    }

    private void checkPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10及以上使用新的图片选择方式
            pickImageFromGallery();
        } else {
            // Android 9及以下版本需要请求存储权限
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                pickImageFromGallery();
            }
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "选择图片"), IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            } else {
                Toast.makeText(this, "需要存储权限才能选择图片", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                imageView.setImageURI(selectedImageUri);
                btnUpload.setEnabled(true);
            }
        }
    }

    private void uploadImage() {
        if (selectedImageUri == null) return;

        try {
            // 创建临时文件
            File tempFile = createTempFileFromUri(selectedImageUri);
            if (tempFile == null) {
                Toast.makeText(this, "无法创建临时文件", Toast.LENGTH_SHORT).show();
                return;
            }

            // 创建MultipartBody
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", tempFile.getName(),
                            RequestBody.create(MediaType.parse("image/*"), tempFile))
                    .build();

            // 创建请求
            Request request = new Request.Builder()
                    .url(UPLOAD_URL)
                    .post(requestBody)
                    .build();

            // 禁用上传按钮
            btnUpload.setEnabled(false);

            // 执行上传
            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                    runOnUiThread(() -> {
                        btnUpload.setEnabled(true);
                        Toast.makeText(ImageUploadActivity.this,
                                "上传失败: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) {
                    runOnUiThread(() -> {
                        btnUpload.setEnabled(true);
                        if (response.isSuccessful()) {
                            try {
                                String res = response.body().string();
                                JSONObject jsonResponse = new JSONObject(res);
                                String fileUrl = jsonResponse.getString("file_url");

                                // 获取完整的文件URL
                                String fullUrl = "http://112.124.22.157" + fileUrl;

                                // 存储数据到 SharedPreferences
                                SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("global_variable", fullUrl);
                                editor.apply();  // 异步提交

                                // 更新全局数据
                                GlobalData data = GlobalData.getInstance();
                                data.avatar_url = fullUrl;
                                data.logData(); // 假设该方法是记录或日志
                                data.updateDataToServer(); // 假设更新数据到服务器

                                // 显示成功消息
                                Toast.makeText(ImageUploadActivity.this,
                                        "上传成功: " + fullUrl,
                                        Toast.LENGTH_SHORT).show();

                                // 返回文件URL并结束Activity
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("avatar_url", fullUrl);
                                setResult(RESULT_OK, resultIntent);
                                finish();

                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(ImageUploadActivity.this,
                                        "解析上传结果时出错: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ImageUploadActivity.this,
                                    "上传失败: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        } catch (Exception e) {
            btnUpload.setEnabled(true);
            Toast.makeText(this,
                    "准备上传时出错: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private File createTempFileFromUri(Uri uri) {
        try {
            // 创建临时文件
            File tempFile = File.createTempFile("upload_", ".jpg", getCacheDir());

            // 复制文件内容
            InputStream inputStream = getContentResolver().openInputStream(uri);
            OutputStream outputStream = new FileOutputStream(tempFile);

            if (inputStream == null) return null;

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
