package com.example.cotton.ui.food;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.cotton.Utils.ApiService;
import com.example.cotton.MemberInfo;
import com.example.cotton.R;
import com.example.cotton.Utils.BaseUrlInterface;
import com.example.cotton.Utils.RetrofitClientJson;
import com.example.cotton.ValueObject.GetBalance.GetBalanceResultVO;
import com.example.cotton.firebaseFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodFragment extends Fragment {

    ListView ticketList;
    FoodListAdapter adapter;
    double money;
    TextView walletText;



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //인플레이션
        View view = inflater.inflate(R.layout.fragment_food, container, false);


        ticketList=view.findViewById(R.id.food_list); //listview 참조
        walletText =view.findViewById(R.id.food_wallet_textView);

        List<MemberInfo> getMemberName= new ArrayList<>();

        firebaseFunction firebaseInput = new firebaseFunction();
        firebaseInput.profileGet(getMemberName, (resultList) -> {

            String wallet = resultList.get(0).getWallet();
            searchMoney(wallet);
            return null;
        });



        showFoodListFunc();//food list RecyclerView 설정

        return view;
    }
    
    //food list Listview 설정
    public void showFoodListFunc(){

        //adapter 생성
        adapter=new FoodListAdapter(getContext());
        //adapter 달기
        ticketList.setAdapter(adapter);

        //listview에 add
        adapter.addItem(R.drawable.ic_food,"식권 x 1","600GBB",600);
        adapter.addItem(R.drawable.ic_food,"식권 x 2","1200GBB",1200);
        adapter.addItem(R.drawable.ic_food,"식권 x 5","3000GBB",3000);
        adapter.addItem(R.drawable.ic_food,"식권 x 10","6000GBB",6000);

        adapter.notifyDataSetChanged();//adapter의 변경을 알림
    }

    public void searchMoney(String wallet){

        ApiService call = RetrofitClientJson.getApiService(BaseUrlInterface.LUNIVERSE);

        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Content-Type", "application/json");
        headerMap.put("Authorization", "Pr35dc2sqok4JsPXjRkZ63T1R1MTujVwqfwzNHZBo9Z2oVPDvBbmqdsk28FhLenv"); //Dapp API키값

        call.getMoney(wallet,headerMap).enqueue(new Callback<GetBalanceResultVO>() {
            @Override
            public void onResponse(Call<GetBalanceResultVO> call, Response<GetBalanceResultVO> response) {
                Log.d("성공 : ", "result : " + response.body().getResult());
                Log.d("성공 : ", "address : " + response.body().getDataBalance().getBalance());
                money = Double.parseDouble(response.body().getDataBalance().getBalance());
                money = (money*0.000000000000000001);
                walletText.setText((int)money+"GBB");
            }

            @Override
            public void onFailure(Call<GetBalanceResultVO> call, Throwable t) {
                Log.d("실패 : ", t.toString());
            }

        });
    }
}