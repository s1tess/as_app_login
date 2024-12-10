package com.example.hello;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;  // 使用 EditText 替代 TextView 以便可以接受输入
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SelectMassenger extends AppCompatActivity {

    private EditText maNameEditText;
    private EditText maStatusEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.masseager);

        // 初始化视图
        maNameEditText = findViewById(R.id.ma_name);
        maStatusEditText = findViewById(R.id.ma_status);
        saveButton = findViewById(R.id.iv_arrow);

        GlobalData data = GlobalData.getInstance();

        // 设置初始提示文本内容
        maNameEditText.setHint("请输入昵称");
        maStatusEditText.setHint("请输入个性签名");

        // 设置输入框获取焦点时清空提示词
        maNameEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && maNameEditText.getText().toString().equals("请输入昵称")) {
                maNameEditText.setText("");  // 清空提示词
            }
        });

        maStatusEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && maStatusEditText.getText().toString().equals("请输入个性签名")) {
                maStatusEditText.setText("");  // 清空提示词
            }
        });

        // 设置按钮点击事件
        saveButton.setOnClickListener(v -> {
            // 获取当前输入的昵称和个性签名
            String nickname = maNameEditText.getText().toString();
            String status = maStatusEditText.getText().toString();

            if (nickname.isEmpty() || status.isEmpty()) {
                // 如果为空，显示提示消息
                Toast.makeText(SelectMassenger.this, "昵称和个性签名不能为空", Toast.LENGTH_SHORT).show();
            } else {
                // 进行保存数据和更新操作
                data.signature = status;
                data.nickname = nickname;
                data.logData();
                data.updateDataToServer();

                // 显示保存成功的提示
                Toast.makeText(SelectMassenger.this, "保存成功", Toast.LENGTH_SHORT).show();

                // 等待提示显示完毕后返回
                finish();
            }
        });
    }
}
