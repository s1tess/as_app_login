package com.example.hello;

public class User {
    private String username;
    private String nickname;
    private String password;
    private String avatar_url;
    private String signature;

    // 构造方法、getters 和 setters
    public User(String username, String nickname, String password, String avatar_url, String signature) {
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.avatar_url = avatar_url;
        this.signature = signature;
    }

    // Getters 和 Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
