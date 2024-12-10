package com.example.hello;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;
    private Button submitButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // 初始化视图
        newPasswordEditText = findViewById(R.id.et_new_password);
        confirmPasswordEditText = findViewById(R.id.et_confirm_password);
        submitButton = findViewById(R.id.btn_submit);

        // 设置按钮点击事件
        submitButton.setOnClickListener(v -> {
            // 获取用户输入的新密码和确认密码
            String newPassword = newPasswordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            // 校验新密码和确认密码是否为空
            if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(ChangePasswordActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            // 校验新密码和确认密码是否一致
            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(ChangePasswordActivity.this, "新密码和确认密码不一致", Toast.LENGTH_SHORT).show();
                return;
            }

            // 校验新密码的长度是否符合要求（可以根据实际需求修改）
            if (newPassword.length() < 6) {
                Toast.makeText(ChangePasswordActivity.this, "密码长度至少为6位", Toast.LENGTH_SHORT).show();
                return;
            }

            // 进行密码修改的操作
            GlobalData data = GlobalData.getInstance();
            data.password = newPassword;
            data.updateDataToServer();
            Toast.makeText(ChangePasswordActivity.this, "更改成功", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

}
