package com.example.hello;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;

public class MainActivity2 extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;  // 底部导航栏

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // 设置布局文件

        // 获取 BottomNavigationView 实例
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 默认加载第一个 Fragment，如果是第一次打开，初始化加载 VideoPreviewFragment
        if (savedInstanceState == null) {
            loadFragment(new VideoPreviewFragment());  // 默认加载 VideoPreviewFragment
        }

        // 设置 BottomNavigationView 的监听事件
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                // 根据 item 的 ID 加载对应的 Fragment
                if (item.getItemId() == R.id.navigation_home) {
                    selectedFragment = new VideoPreviewFragment();  // 视频预览 Fragment
                } else if (item.getItemId() == R.id.navigation_dashboard) {
                    selectedFragment = new LocationManagerFragment();  // DashboardFragment
                } else if (item.getItemId() == R.id.navigation_notifications) {
                    selectedFragment = new MeFragment();  // MeFragment
                }

                // 加载选中的 Fragment
                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                }
                return true;
            }
        });
    }

    // 加载 Fragment 的方法
    private void loadFragment(Fragment fragment) {
        // 开始一个 FragmentTransaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // 替换现有的 Fragment
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);  // 将事务加入回退栈，支持后退操作
        transaction.commit();  // 提交事务
    }
}


