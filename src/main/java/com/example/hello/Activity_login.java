package com.example.hello;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
public class Activity_login extends AppCompatActivity {

    private EditText editTextNumberRE;
    private EditText editTextNumberRE2;
    private TextView logP;
    private TextView loginResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        // 设置状态栏透明
        getWindow().setFlags(
                android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        );

        editTextNumberRE = findViewById(R.id.editTextNumber);
        editTextNumberRE2 = findViewById(R.id.editTextNumber2);
        logP = findViewById(R.id.Uselog2);
        loginResultTextView = findViewById(R.id.Uselog);

        Button button1 = findViewById(R.id.UseButtonIN);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editTextNumberRE.getText().toString();
                String password = editTextNumberRE2.getText().toString();

                Log.d("LoginActivity", "Username: " + username + ", Password: " + password);

                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    if (username.length() >= 5 && password.length() >= 6) {
                        logP.setText("正在登录...");
                        new LoginTask(loginResultTextView, Activity_login.this)
                                .execute(username, password);
                    } else {
                        logP.setText("用户名或密码不符合要求！");
                    }
                } else {
                    logP.setText("请填写用户名和密码！");
                }
            }
        });
    }

    public class LoginTask extends AsyncTask<String, Void, String> {

        private TextView resultTextView;
        private Activity activity;

        public LoginTask(TextView textView, Activity activity) {
            this.resultTextView = textView;
            this.activity = activity;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String username = params[0];
                String password = params[1];

                String jsonInputString = "{"
                        + "\"username\": \"" + username + "\","
                        + "\"password\": \"" + password + "\""
                        + "}";

                URL url = new URL("http://112.124.22.157:3000/login.php");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                OutputStream os = connection.getOutputStream();
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
                os.flush();
                os.close();
                Log.d("LoginActivity", "Request JSON: " + jsonInputString);

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

                return response.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d("LoginActivity", "Server Response: " + result);

            try {
                if (result.trim().startsWith("{") && result.trim().endsWith("}")) {
                    JSONObject jsonResponse = new JSONObject(result);
                    String status = jsonResponse.getString("status");

                    Log.d("LoginActivity", "Parsed JSON: " + jsonResponse.toString());

                    if ("success".equals(status)) {
                        Log.d("LoginResult", "Login successful");
                        resultTextView.setText("登录成功");
                        Log.d("LoginResult", "Starting MainActivity2...");
                        Intent intent = new Intent(activity, MainActivity2.class);
                        activity.startActivity(intent);
                        activity.finish();
                    } else {
                        Log.d("LoginResult", "Login failed: " + status);
                        resultTextView.setText("登录失败: " + status);
                    }
                } else {
                    Log.d("LoginResult", "Invalid JSON Response");
                    resultTextView.setText("登录失败，返回的不是有效的 JSON 数据！");
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("LoginResult", "Error parsing response: " + e.getMessage());
                resultTextView.setText("登录过程中出现错误: " + e.getMessage());
            }
        }
    }
}
