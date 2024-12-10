package com.example.hello;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class WeatherPagerAdapter extends FragmentStateAdapter {

    private final ArrayList<Fragment> weatherFragments;

    public WeatherPagerAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<Fragment> weatherFragments) {
        super(fragmentActivity);
        this.weatherFragments = weatherFragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return weatherFragments.get(position);
    }

    @Override
    public int getItemCount() {
        return weatherFragments.size();
    }
}
