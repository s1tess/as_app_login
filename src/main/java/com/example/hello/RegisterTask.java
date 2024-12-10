package com.example.hello;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterTask extends AsyncTask<String, Void, String> {

    private TextView resultTextView;  // 用于显示结果的 TextView
    private Activity activity;   // 当前 Activity，便于跳转

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
            connection.setRequestProperty("Content-Type", "application/json");  // 设置请求体类型为 JSON

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
                // 返回成功的 JSON 响应
                return response.toString();
            } else {
                return "Error: " + responseCode;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
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

                // 注册成功后跳转到 HomeActivity
                Intent intent = new Intent(activity, MainActivity.class);
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
