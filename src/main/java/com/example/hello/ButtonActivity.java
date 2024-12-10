package com.example.hello;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

public class ButtonActivity  extends AppCompatActivity {

        private ViewPager2 viewPager2;
        private Button buttonPrev, buttonNext;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.weather);

            // 初始化 ViewPager2 和控制按钮
//            viewPager2 = findViewById(R.id.viewPager2);
            buttonPrev = findViewById(R.id.buttonPrev);
            buttonNext = findViewById(R.id.buttonNext);

            // 设置 ViewPager2 的适配器
            MyPagerAdapter adapter = new MyPagerAdapter(this);
            viewPager2.setAdapter(adapter);

            // 向左滑动
            buttonPrev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentItem = viewPager2.getCurrentItem();
                    if (currentItem > 0) {
                        viewPager2.setCurrentItem(currentItem - 1, true);  // true: 带动画
                    }
                }
            });

            // 向右滑动
            buttonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentItem = viewPager2.getCurrentItem();
                    viewPager2.setCurrentItem(currentItem + 1, true);  // true: 带动画
                }
            });
        }
    }
