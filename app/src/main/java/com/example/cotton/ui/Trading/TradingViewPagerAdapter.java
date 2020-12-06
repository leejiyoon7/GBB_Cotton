package com.example.cotton.ui.Trading;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.example.cotton.R;
import com.example.cotton.ui.food.FoodListItem;

import java.util.ArrayList;
import java.util.List;

public class TradingViewPagerAdapter extends FragmentPagerAdapter {

    //fragment list
    private final List<Fragment> mFragmentList = new ArrayList<>();

    //참조형 fragment 제작
    TradingViewPagerFragment tradingViewPagerFragment;

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
    public void addFragment(int registeredBookImage, String registeredBookTitle, String registeredBookAuthor){
        tradingViewPagerFragment=new TradingViewPagerFragment();
        tradingViewPagerFragment.setItems(registeredBookImage,registeredBookTitle,registeredBookAuthor);
        mFragmentList.add(tradingViewPagerFragment);
    }



    public static class TradingViewPagerFragment extends Fragment {

        ImageView registered_book_image_view;
        TextView registered_book_title_text_view;
        TextView registered_book_author_text_view;

        TradingViewPagerItem item=new TradingViewPagerItem();

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.trading_page_view, container, false);

            //ui에서 id 참조
            registered_book_image_view=view.findViewById(R.id.registered_book_image_view);
            registered_book_title_text_view=view.findViewById(R.id.registered_book_title_text_view);
            registered_book_author_text_view=view.findViewById(R.id.registered_book_author_text_view);

            //fragment를 이용하여 viewpager에 값 넣어주기
            registered_book_image_view.setImageResource(item.getRegisteredBookImage());
            registered_book_title_text_view.setText(item.getRegisteredBookTitle());
            registered_book_author_text_view.setText(item.getRegisteredBookAuthor());

            return view;
        }

        //Item set
        public void setItems(int registeredBookImage, String registeredBookTitle, String registeredBookAuthor){
            item.setRegisteredBookImage(registeredBookImage);
            item.setRegisteredBookTitle(registeredBookTitle);
            item.setRegisteredBookAuthor(registeredBookAuthor);
        }
    }
}
