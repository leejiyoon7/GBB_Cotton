package com.example.cotton.ui.food;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.cotton.R;

import java.util.ArrayList;

public class FoodFragment extends Fragment {

    ListView ticketList;
    FoodListAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //인플레이션
        View view = inflater.inflate(R.layout.fragment_food, container, false);

        //adapter 생성
        adapter=new FoodListAdapter(getContext());

        //listview 참조 및 adapter 달기
        ticketList=view.findViewById(R.id.food_list);
        ticketList.setAdapter(adapter);

        //listview에 add
        adapter.addItem(R.drawable.ic_food,"식권 x 1","600GBB",600);
        adapter.addItem(R.drawable.ic_food,"식권 x 2","1200GBB",1200);
        adapter.addItem(R.drawable.ic_food,"식권 x 5","3000GBB",3000);
        adapter.addItem(R.drawable.ic_food,"식권 x 10","6000GBB",6000);

        adapter.notifyDataSetChanged();//adapter의 변경을 알림

        return view;
    }
}