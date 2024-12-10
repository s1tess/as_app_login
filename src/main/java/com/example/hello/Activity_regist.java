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


public class Activity_regist extends AppCompatActivity {

    private EditText editTextNumberRE; // 账号输入框
    private EditText editTextNumberRE2; // 密码输入框
    private TextView logP; // 显示注册状态信息
    private TextView registerResultTextView; // 显示注册结果的 TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);  // 确保这个是正确的布局

        // 设置状态栏透明
        getWindow().setFlags(
                android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        );

        // 初始化 EditText 和 TextView
        editTextNumberRE = findViewById(R.id.editTextNumberRE);
        editTextNumberRE2 = findViewById(R.id.editTextNumberRE2);
        logP = findViewById(R.id.Uselog2); // 显示注册状态信息
        registerResultTextView = findViewById(R.id.registerResultText); // 显示注册结果

        // 设置注册按钮的点击事件
        Button button1 = findViewById(R.id.UseButtonRE);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editTextNumberRE.getText().toString(); // 获取输入的用户名
                String password = editTextNumberRE2.getText().toString(); // 获取输入的密码

                Log.d("RegisterActivity", "Username: " + username + ", Password: " + password);

                // 检查输入是否为空
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    // 检查用户名和密码长度
                    if (username.length() >= 5 && password.length() >= 6) {
                        // 如果用户名未注册，继续注册
                        if (false) {  // 这里可以检查数据库或网络判断是否已注册
                            logP.setText("账号已注册！"); // 提示账号已注册
                        } else {
                            logP.setText("正在注册...");

                            // 创建并执行 RegisterTask
                            new RegisterTask(registerResultTextView, Activity_regist.this)
                                    .execute(username, null, password, null, null);
                            GlobalData data = GlobalData.getInstance();
                            data.initializeData(username,"新用户",password,null,"请输入签名",null);
                            data.logData();
                            data.updateDataToServer();
                        }
                    } else {
                        logP.setText("用户名或密码不符合要求！"); // 提示用户名或密码不符合要求
                    }
                } else {
                    logP.setText("请填写用户名和密码！"); // 提示用户输入完整信息
                }
            }
        });
    }

    // RegisterTask 类：负责处理后台注册请求
    public class RegisterTask extends AsyncTask<String, Void, String> {

        private TextView resultTextView;  // 用于显示结果的 TextView
        private Activity activity;        // 当前 Activity，便于跳转

        // 构造函数，接收一个 TextView 作为参数和一个 Activity 实例
        public RegisterTask(TextView textView, Activity activity) {
            this.resultTextView = textView;
            this.activity = activity;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                // 获取传入的参数
                String username = params[0];
                String nickname = params[1];
                String password = params[2];
                String avatarUrl = params[3];
                String signature = params[4];

                // 构建请求的数据
                String jsonInputString = "{"
                        + "\"username\": \"" + username + "\","
                        + "\"nickname\": \"" + nickname + "\","
                        + "\"password\": \"" + password + "\","
                        + "\"avatar_url\": \"" + avatarUrl + "\","
                        + "\"signature\": \"" + signature + "\""
                        + "}";

                // 请求的 PHP 后端接口 URL
                URL url = new URL("http://112.124.22.157:3000/register.php");

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

                // 获取响应
                int responseCode = connection.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // 检查响应状态
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return response.toString();  // 返回服务器响应的 JSON 数据
                } else {
                    return "Error: " + responseCode;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();  // 返回异常信息
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // 解析服务器响应并判断注册是否成功
            try {
                // 假设服务器返回的是 JSON 格式
                JSONObject jsonResponse = new JSONObject(result);

                String status = jsonResponse.getString("status");
                String message = jsonResponse.getString("message");

                if ("success".equals(status)) {
                    // 注册成功
                    Log.d("RegisterResult", "Registration successful: " + message);
                    // 在 TextView 中显示成功消息
                    resultTextView.setText("注册成功: " + message);

                    // 注册成功后跳转到 MainActivity
                    Intent intent = new Intent(activity, MainActivity2.class);
                    activity.startActivity(intent);
                    activity.finish();  // 可选：结束当前 Activity，防止用户按返回键回到注册页面
                } else {
                    // 注册失败
                    Log.d("RegisterResult", "Registration failed: " + message);
                    // 在 TextView 中显示失败消息
                    resultTextView.setText("注册失败: " + message);
                }

            } catch (Exception e) {
                e.printStackTrace();
                // 如果 JSON 解析失败或其他异常，显示错误信息
                Log.d("RegisterResult", "Error parsing response: " + e.getMessage());
                resultTextView.setText("注册过程中出现错误: " + e.getMessage());
            }
        }
    }
}

    // 方法用于验证用户名是否已存在
//    private boolean isUsernameExists(String username) {
//////        SQLiteDatabase db = dbHelper.getReadableDatabase();
////        // 查询数据库中是否有这个用户名
//////        Cursor cursor = db.query("user_info", null, "name = ?", new String[]{username},
////                null, null, null);
////
////        boolean exists = cursor != null && cursor.getCount() > 0;
////        if (cursor != null) {
////            cursor.close(); // 确保关闭cursor
////        }
////        return exists;
//        return 1;
//    }

//    // 方法用于保存用户信息到数据库
//    private void saveUserInfo(String username, String password) {
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put("name", username);
//        values.put("password", password);
//
//        // 插入数据到user_info表中
//        db.insert("user_info", null, values);
//  }

