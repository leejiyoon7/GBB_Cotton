package com.example.cotton.ui.Trading;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TradingViewPagerAdapter extends FragmentPagerAdapter {

    //fragment list
    private final List<Fragment> mFragmentList = new ArrayList<>();

    //adapter 생성자
    public TradingViewPagerAdapter(FragmentManager manager){
        super(manager);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    //fragment를 이용해서 viewpager add
    public void addFragment(Fragment fragment){
        mFragmentList.add(fragment);

    }
}
