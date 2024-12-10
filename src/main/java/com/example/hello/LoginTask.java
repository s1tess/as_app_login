package com.example.hello;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
public class LoginTask extends AsyncTask<String, Void, String>{
  private TextView resultTextView;  // 用于显示结果的 TextView

    // 构造函数，接收一个 TextView 作为参数
    public LoginTask(TextView resultTextView) {
        this.resultTextView = resultTextView;
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

            // 请求的 PHP 后端接口 URL
            URL url = new URL("http://112.124.22.157:3000/login.php");

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
                // 解析返回的 JSON 响应
                String responseString = response.toString();

                // 创建 JSON 对象解析响应
                JSONObject jsonResponse = new JSONObject(responseString);

                String status = jsonResponse.getString("status");
                if ("success".equals(status)) {
                    return "登录成功";  // 登录成功
                } else {
                    String errorMessage = jsonResponse.getString("message");
                    return "登录失败: " + errorMessage;  // 登录失败
                }
            } else {
                return "Error: " + responseCode;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
            // 异常处理
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        // 解析服务器响应并判断登录是否成功
        try {
            // 假设服务器返回的是 JSON 格式
            JSONObject jsonResponse = new JSONObject(result);

            String status = jsonResponse.getString("status");
            String message = jsonResponse.getString("message");

            if ("success".equals(status)) {
                // 登录成功
                Log.d("LoginResult", "Login successful: " + message);
                // 在 TextView 中显示成功消息
                resultTextView.setText("登录成功: " + message);
            } else {
                // 登录失败
                Log.d("LoginResult", "Login failed: " + message);
                // 在 TextView 中显示失败消息
               resultTextView.setText("登录失败: " + message);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // 如果 JSON 解析失败或其他异常，显示错误信息
            Log.d("LoginResult", "Error parsing response: " + e.getMessage());
           resultTextView.setText("登录过程中出现错误: " + e.getMessage());
        }
    }


}
