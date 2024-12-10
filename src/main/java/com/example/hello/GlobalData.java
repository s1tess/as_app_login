package com.example.hello;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.os.AsyncTask;
import org.json.JSONObject;

public class GlobalData {

    public String username ;
    public String nickname = "新用户";
    public String password;
    public String avatar_url ;
    public String signature = "请输入签名";
    public String dateTimeStr;

    private static GlobalData instance;

    // 构造方法私有化，防止外部创建多个实例
    private GlobalData() {}

    // 提供一个公共的静态方法来获取单例实例
    public static GlobalData getInstance() {
        if (instance == null) {
            instance = new GlobalData();
        }
        return instance;
    }

    // 初始化数据
    public void initializeData(String username, String nickname, String password, String avatar_url, String signature, String dateTimeStr) {
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.avatar_url = avatar_url;
        this.signature = signature;
        this.dateTimeStr = dateTimeStr;
    }

    // 上传数据到服务器
    public void updateDataToServer() {
        // 创建并执行后台任务
        new UploadDataTask().execute(username, nickname, password, avatar_url, signature);
    }

    // 打印数据，方便调试
    public void logData() {
        Log.d("GlobalData", "Username: " + username);
        Log.d("GlobalData", "Nickname: " + nickname);
        Log.d("GlobalData", "Password: " + password);
        Log.d("GlobalData", "Avatar URL: " + avatar_url);
        Log.d("GlobalData", "Signature: " + signature);
        Log.d("GlobalData", "Date Time: " + dateTimeStr);
    }

    // AsyncTask 用于后台上传数据
    private static class UploadDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            for (String param : params) {
                Log.d("UploadDataTask", "Param: " + param);
            }
            try {
                // 获取传入的参数
                String username = params[0];
                String nickname = params[1];
                String password = params[2];
                String avatar_url = params[3];
                String signature = params[4];
                Log.d("UploadDataTask", "Username: " + username);
                Log.d("UploadDataTask", "Nickname: " + nickname);
                Log.d("UploadDataTask", "Password: " + password);
                Log.d("UploadDataTask", "Avatar URL: " + avatar_url);
                Log.d("UploadDataTask", "Signature: " + signature);

                // 构建请求的数据
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", username);
                jsonObject.put("nickname", nickname);
                jsonObject.put("password", password);
                jsonObject.put("avatar_url", avatar_url);
                jsonObject.put("signature", signature);

                // 服务器接口 URL
                String urlString = "http://112.124.22.157:3000/update.php";  // PHP 更新接口
                // 使用 HttpURLConnection 来发送请求
                java.net.URL url = new java.net.URL(urlString);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                // 写入请求数据
                java.io.OutputStream os = connection.getOutputStream();
                byte[] input = jsonObject.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
                os.flush();
                os.close();

                // 获取响应
                int responseCode = connection.getResponseCode();
                Log.d("UploadDataTask", "Response Code: " + responseCode);
                java.io.BufferedReader in;
                if (responseCode >= 200 && responseCode < 300) {
                    in = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream(), "UTF-8"));
                } else {
                    in = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getErrorStream(), "UTF-8"));
                }

                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // 返回响应内容
                return response.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();  // 返回异常信息
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // 打印服务器返回的响应，方便调试
            Log.d("UploadDataTask", "Server Response: " + result);

            try {
                // 检查返回的数据是否为有效的 JSON 格式
                if (result.trim().startsWith("{") && result.trim().endsWith("}")) {
                    // 处理返回的 JSON 对象
                    JSONObject jsonResponse = new JSONObject(result);
                    String status = jsonResponse.getString("status");
                    String message = jsonResponse.getString("message");
                    int errorCode = jsonResponse.getInt("error_code");

                    // 打印响应信息
                    Log.d("UploadDataTask", "Status: " + status);
                    Log.d("UploadDataTask", "Message: " + message);
                    Log.d("UploadDataTask", "Error Code: " + errorCode);

                    // 根据返回的状态进行不同处理
                    if ("success".equals(status)) {
                        Log.d("UploadDataTask", "Upload Success.");
                    } else {
                        Log.d("UploadDataTask", "Upload Failed. Status: " + status);
                        Log.d("UploadDataTask", "Error Message: " + message);
                        Log.d("UploadDataTask", "Error Code: " + errorCode);
                        // 可以显示一个错误提示给用户，告诉他们哪些参数缺失
                    }
                } else {
                    Log.d("UploadDataTask", "Invalid JSON response: " + result);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("UploadDataTask", "Error parsing response: " + e.getMessage());
            }
        }

    }
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    // 构造方法私有化，防止外部创建多个实例
    private GlobalData(Context context) {
        sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // 提供一个公共的静态方法来获取单例实例
    public static GlobalData getInstance(Context context) {
        if (instance == null) {
            instance = new GlobalData(context);
        }
        return instance;
    }

    // 初始化数据

    // 保存数据到 SharedPreferences
    private void saveDataToPreferences() {
        editor.putString("nickname", nickname);
        editor.putString("avatar_url", avatar_url);
        editor.putString("signature", signature);
        editor.apply();
    }

    // 更新 nickname 并保存
    public void updateNickname(String newNickname) {
        if (!nickname.equals(newNickname)) {
            nickname = newNickname;
            editor.putString("nickname", newNickname);
            editor.apply();
        }
    }

    // 更新 avatar_url 并保存
    public void updateAvatarUrl(String newAvatarUrl) {
        if (!avatar_url.equals(newAvatarUrl)) {
            avatar_url = newAvatarUrl;
            editor.putString("avatar_url", newAvatarUrl);
            editor.apply();
        }
    }

    // 更新 signature 并保存
    public void updateSignature(String newSignature) {
        if (!signature.equals(newSignature)) {
            signature = newSignature;
            editor.putString("signature", newSignature);
            editor.apply();
        }
    }

    // 获取 nickname
    public String getNickname() {
        return sharedPreferences.getString("nickname", "新用户");
    }

    // 获取 avatar_url
    public String getAvatarUrl() {
        return sharedPreferences.getString("avatar_url", "");
    }

    // 获取 signature
    public String getSignature() {
        return sharedPreferences.getString("signature", "请输入签名");
    }



}
