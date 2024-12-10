package com.example.hello;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
public class MeFragment extends Fragment {

    private ImageView ivAvatar;
    private TextView tvName;
    private TextView tvStatus;
    private ImageView ivArrow;
    private ImageView editButton;
    private TextView time;
    private ImageView changePasswordButton;
    private ImageView moreButton; // 添加“更多”按钮

    GlobalData data = GlobalData.getInstance();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.item_contact, container, false);

        ivAvatar = rootView.findViewById(R.id.iv_avatar);
        tvName = rootView.findViewById(R.id.tv_name);
        tvStatus = rootView.findViewById(R.id.tv_status);
        ivArrow = rootView.findViewById(R.id.iv_arrow);
        editButton = rootView.findViewById(R.id.editButton);
        changePasswordButton = rootView.findViewById(R.id.changePasswordButton);
        moreButton = rootView.findViewById(R.id.moreButton); // 获取“更多”按钮

        // 获取 SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("my_preferences", Context.MODE_PRIVATE);

        // 设置点击事件：更换头像按钮
        ivArrow.setOnClickListener(v -> {
            // 跳转到头像上传页面
            Intent intent = new Intent(getActivity(), ImageUploadActivity.class);
            startActivity(intent);
        });

        // 设置点击事件：编辑按钮
        editButton.setOnClickListener(v -> {
            // 跳转到个人资料编辑页面
            Intent intent = new Intent(getActivity(), SelectMassenger.class);
            startActivity(intent);
        });

        // 设置点击事件：修改密码按钮
        changePasswordButton.setOnClickListener(v -> {
            // 跳转到修改密码页面
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);  // 启动修改密码的 Activity
        });

        // 设置点击事件：更多按钮
        moreButton.setOnClickListener(v -> {
            // 跳转到更多选项页面
            Intent intent = new Intent(getActivity(), MoreOptionsActivity.class);
            startActivity(intent); // 启动“更多”页面
        });

        // 设置 SharedPreferences 变化的监听器
        preferenceChangeListener = (sharedPreferences, key) -> {
            if ("global_variable".equals(key)) {
                // 当 global_variable 更新时，重新加载数据
                updateUserData();
            }
        };

        // 注册监听器
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        return rootView;
    }

    // 在 onResume() 中确保每次 fragment 可见时都更新界面
    @Override
    public void onResume() {
        super.onResume();
        updateUserData(); // 更新用户数据
    }

    // 更新用户数据并刷新界面
    private void updateUserData() {
        // 更新用户的昵称、签名和头像
        tvName.setText(data.nickname);
        tvStatus.setText(data.signature);
        loadAvatar(data.avatar_url);
    }

    // 使用 Glide 加载头像图片
    private void loadAvatar(String imageUrl) {
        Glide.with(this)  // 使用当前的 Fragment 作为上下文
                .load(imageUrl)  // 图片的 URL
                .placeholder(R.drawable.img1)  // 加载中的占位图
                .error(R.drawable.img)  // 错误时的图片
                .into(ivAvatar); // 加载到 ImageView 中
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 注销 SharedPreferences 监听器，防止内存泄漏
        if (sharedPreferences != null && preferenceChangeListener != null) {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
        }
    }
}
