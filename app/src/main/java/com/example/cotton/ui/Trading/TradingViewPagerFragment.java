package com.example.cotton.ui.Trading;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cotton.R;

public class TradingViewPagerFragment extends Fragment {

    ImageView registered_book_image_view;
    TextView registered_book_title_text_view;
    TextView registered_book_author_text_view;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.trading_page_view, container, false);

        //ui에서 id 참조
        registered_book_image_view=view.findViewById(R.id.registered_book_image_view);
        registered_book_title_text_view=view.findViewById(R.id.registered_book_title_text_view);
        registered_book_author_text_view=view.findViewById(R.id.registered_book_author_text_view);

        //fragment를 이용하여 viewpager에 값 넣어주기
        registered_book_image_view.setImageResource(R.drawable.book_imsi);
        registered_book_title_text_view.setText("종이 여자");
        registered_book_author_text_view.setText("기욤 뮈소");

        return view;
    }
}
