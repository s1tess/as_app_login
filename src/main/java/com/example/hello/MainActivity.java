package com.example.hello;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.hello.MainActivity;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private EditText editTextNumberRE; // 账号输入框
    private EditText editTextNumberRE2; // 密码输入框
    private TextView logP; // 显示登录状态信息
    private TextView loginResultTextView; // 显示登录结果的 TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);  // 确保这个是正确的布局

        // 设置状态栏透明
        getWindow().setFlags(
                android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        );

        // 初始化 EditText 和 TextView
        editTextNumberRE = findViewById(R.id.editTextNumber); // 用户名输入框
        editTextNumberRE2 = findViewById(R.id.editTextNumber2); // 密码输入框
        logP = findViewById(R.id.Uselog2); // 显示登录状态信息
        loginResultTextView = findViewById(R.id.Uselog); // 显示登录结果

        // 设置登录按钮的点击事件
        Button button1 = findViewById(R.id.UseButtonIN);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editTextNumberRE.getText().toString(); // 获取输入的用户名
                String password = editTextNumberRE2.getText().toString(); // 获取输入的密码

                Log.d("LoginActivity", "Username: " + username + ", Password: " + password);

                // 检查输入是否为空
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    // 检查用户名和密码长度
                    if (username.length() >= 5 && password.length() >= 6) {
                        logP.setText("正在登录...");

                        // 创建并执行 LoginTask
                        new MainActivity.LoginTask(loginResultTextView, MainActivity.this)
                                .execute(username, password);
                        GlobalData data = GlobalData.getInstance();
                        data.initializeData(username,null,password,null,null,null);
                        data.logData();
                    } else {
                        logP.setText("用户名或密码不符合要求！"); // 提示用户名或密码不符合要求
                    }
                } else {
                    logP.setText("请填写用户名和密码！"); // 提示用户输入完整信息
                }
            }
        });
        Button button2 = findViewById(R.id.UseButtonZC);  // 获取注册按钮
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 点击注册按钮时，跳转到注册页面
                Intent intent = new Intent(MainActivity.this, Activity_regist.class);
                startActivity(intent);
            }
        });

    }

    // LoginTask 类：负责处理后台登录请求
    public class LoginTask extends AsyncTask<String, Void, String> {

        private TextView resultTextView;  // 用于显示结果的 TextView
        private Activity activity;        // 当前 Activity，便于跳转

        // 构造函数，接收一个 TextView 作为参数和一个 Activity 实例
        public LoginTask(TextView textView, Activity activity) {
            this.resultTextView = textView;
            this.activity = activity;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                // 获取传入的参数
                String username = params[0];
                String password = params[1];

                // 构建请求的数据
                String jsonInputString = "{"
                        + "\"username\": \"" + username + "\","
                        + "\"password\": \"" + password + "\""
                        + "}";

                // 请求的 PHP 后端接口 URL (登录接口)
                URL url = new URL("http://112.124.22.157:3000/login.php");

                // 打开连接
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                // 写入请求数据
                OutputStream os = connection.getOutputStream();
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
                os.flush();
                os.close();
                Log.d("LoginActivity", "Request JSON: " + jsonInputString);

                // 获取响应
                int responseCode = connection.getResponseCode();
                Log.d("LoginActivity", "Response Code: " + responseCode);

                BufferedReader in;
                if (responseCode >= 200 && responseCode < 300) {
                    in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                } else {
                    in = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "UTF-8"));
                }

                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // 返回服务器响应的内容
                return response.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();  // 返回异常信息
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // 打印服务器返回的原始响应，帮助调试
            Log.d("LoginActivity", "Server Response: " + result);

            try {
                // 如果服务器返回的是有效的 JSON 格式，继续处理
                if (result.trim().startsWith("{") && result.trim().endsWith("}")) {
                    // 解析服务器响应并判断登录是否成功
                    JSONObject jsonResponse = new JSONObject(result);

                    // 提取返回的各个字段
                    String status = jsonResponse.getString("status");
                    String username = jsonResponse.getString("username");
                    String nickname = jsonResponse.getString("nickname");
                    String password = jsonResponse.getString("password");
                    String avatarUrl = jsonResponse.getString("avatar_url");
                    String signature = jsonResponse.getString("signature");
                    String createdAt = jsonResponse.getString("created_at");
                    GlobalData datas = GlobalData.getInstance();
                    datas.initializeData(username,nickname,password,avatarUrl,signature,createdAt);
                    datas.logData();

                    // 打印解析结果，确认字段提取成功
                    Log.d("LoginActivity", "Parsed JSON:");
                    Log.d("LoginActivity", "Status: " + status);
                    Log.d("LoginActivity", "Username: " + username);
                    Log.d("LoginActivity", "Nickname: " + nickname);
                    Log.d("LoginActivity", "Password: " + password);
                    Log.d("LoginActivity", "Avatar URL: " + avatarUrl);
                    Log.d("LoginActivity", "Signature: " + signature);
                    Log.d("LoginActivity", "Created At: " + createdAt);

                    // 判断登录状态
                    if ("success".equals(status)) {
                        // 登录成功
                        Log.d("LoginResult", "Login successful");

                        // 更新 UI 显示成功消息
                        resultTextView.setText("登录成功");

                        // 可以把用户名、昵称等数据保存到全局数据中或用于后续的页面跳转
                        GlobalData data = GlobalData.getInstance();
                        Log.d("masss",username+nickname+password);
                        data.initializeData(username, nickname, password, avatarUrl, signature, createdAt);
                        data.logData();

                        // 打印日志，确认跳转逻辑
                        Log.d("LoginResult", "Starting MainActivity2...");

                        // 登录成功后跳转到 MainActivity2
                        Intent intent = new Intent(activity, MainActivity2.class);
                        activity.startActivity(intent);
                        activity.finish();  // 可选：结束当前 Activity，防止用户按返回键回到登录页面
                    } else {
                        // 登录失败
                        Log.d("LoginResult", "Login failed: " + status);
                        resultTextView.setText("登录失败: " + status);
                    }
                } else {
                    // 如果返回的不是有效的 JSON 数据
                    Log.d("LoginResult", "Invalid JSON Response");
                    resultTextView.setText("登录失败，返回的不是有效的 JSON 数据！");
                }

            } catch (Exception e) {
                e.printStackTrace();
                // 如果 JSON 解析失败或其他异常，显示错误信息
                Log.d("LoginResult", "Error parsing response: " + e.getMessage());
                resultTextView.setText("登录过程中出现错误: " + e.getMessage());
            }
        }



    }

}
