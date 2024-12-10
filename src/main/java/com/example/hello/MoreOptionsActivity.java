package com.example.hello;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MoreOptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.last); // 设置布局文件
        TextView text = findViewById(R.id.register_time_value);
        GlobalData data =  GlobalData.getInstance();
        text.setText(data.dateTimeStr);
    }
}
