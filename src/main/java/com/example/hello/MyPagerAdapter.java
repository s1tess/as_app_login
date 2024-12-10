package com.example.hello;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

public class MyPagerAdapter extends RecyclerView.Adapter<MyPagerAdapter.ViewHolder> {

    private Context context;

    public MyPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // 设置页面内容
        holder.itemView.setText("Page " + (position + 1));
    }

    @Override
    public int getItemCount() {
        return 5; // 假设有5页
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = (TextView) itemView;
        }
    }
}
