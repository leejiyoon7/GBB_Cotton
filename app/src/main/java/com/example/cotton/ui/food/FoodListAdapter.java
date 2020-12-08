package com.example.cotton.ui.food;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.cotton.Utils.ApiService;
import com.example.cotton.MainActivity;
import com.example.cotton.MemberInfo;
import com.example.cotton.R;
import com.example.cotton.Utils.BaseUrlInterface;
import com.example.cotton.Utils.RetrofitClientJson;
import com.example.cotton.ValueObject.SetBalance.SetBalanceResultVO;
import com.example.cotton.firebaseFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodListAdapter extends BaseAdapter{

    //listview items
    private ImageView productIcon;
    private TextView productType;
    private TextView price;
    private Button btn_buy;
    Context context;
    FoodFragment foodFragment;
    Fragment fragment;

    private ArrayList<FoodListItem> foodItemsList=new ArrayList<FoodListItem>();

    //constructor
    public FoodListAdapter(Context context, Fragment fragment){
        this.context=context;
        this.fragment=fragment;
    }

    //Adapter에 사용되는 데이터의 개수를 리턴
    @Override
    public int getCount() {
        return foodItemsList.size();
    }

    //position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context=parent.getContext();

        //"food_listview_item" Layout을 inflate하여 convertView 참조 획득
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.food_listview_item,parent,false);
        }

        //화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        productIcon=convertView.findViewById(R.id.iv_productIcon);
        productType=convertView.findViewById(R.id.tv_productType);
        price=convertView.findViewById(R.id.tv_price);
        btn_buy=convertView.findViewById(R.id.btn_buy);

        foodFragment=new FoodFragment();

        FoodListItem foodListItem=foodItemsList.get(position);
        //아이템 내 각 위젯에 데이터 반영
        productIcon.setImageResource(foodListItem.getProductIcon());
        productType.setText(foodListItem.getProductType());
        price.setText(foodListItem.getPrice());
        btn_buy.setId(foodListItem.getButtonId());
        buyEvent();
        return convertView;
    }

    //저장할 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴
    @Override
    public long getItemId(int position) {
        return position;
    }

    //지정한 위치(position)에 있는 데이터 리턴
    @Override
    public Object getItem(int position) {
        return foodItemsList.get(position);
    }

    //아이템 데이터 추가를 위한 함수
    public void addItem(int icon,String type,String price,int buttonId){
        FoodListItem item=new FoodListItem();

        item.setProductIcon(icon);
        item.setProductType(type);
        item.setPrice(price);
        item.setButtonId(buttonId);

        foodItemsList.add(item);
    }

    //buy event
    public void buyEvent(){
        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(view.getId()){
                    case 600:
                        getWallet(view.getId());
                        increaseTicket(1);
                        Toast.makeText(context,"600GBB 식권 구매 완료하였습니다.",Toast.LENGTH_SHORT).show();
                        break;
                    case 1200:
                        getWallet(view.getId());
                        increaseTicket(2);
                        Toast.makeText(context,"1200GBB 식권 구매 완료하였습니다.",Toast.LENGTH_SHORT).show();
                        break;
                    case 3000:
                        getWallet(view.getId());
                        increaseTicket(5);
                        Toast.makeText(context,"3000GBB 식권 구매 완료하였습니다.",Toast.LENGTH_SHORT).show();
                        break;
                    case 6000:
                        getWallet(view.getId());
                        increaseTicket(10);
                        Toast.makeText(context,"6000GBB 식권 구매 완료하였습니다.",Toast.LENGTH_SHORT).show();
                        break;
                }
                //foodFragment 갱신 함수
                foodFragmentRenewFunc();

            }
        });
    }
    //foodFragment 갱신 함수
    public void foodFragmentRenewFunc(){
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FragmentTransaction ft;
                    Thread.sleep(2000);
                    if(fragment.getFragmentManager()!=null){
                        ft = fragment.getFragmentManager().beginTransaction();
                        ft.detach(fragment).attach(fragment).commit();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }




    public void getWallet(int price) {
        List<MemberInfo> getMemberName= new ArrayList<>();

        firebaseFunction firebaseInput = new firebaseFunction();
        firebaseInput.profileGet(getMemberName, (resultList) -> {

            String wallet = resultList.get(0).getWallet();
            buyFood(price ,wallet);
            return null;
        });
    }

    public void increaseTicket(int ticket) {
        firebaseFunction firebaseInput = new firebaseFunction();
        firebaseInput.raiseMyTicketCount(ticket);
    }


    public void buyFood(int value, String wallet) {

        ApiService call = RetrofitClientJson.getApiService(BaseUrlInterface.LUNIVERSE);

        HashMap<String, String> bodyMap2 = new HashMap<String, String>();
        bodyMap2.put("valueAmount", value + "000000000000000000"); //가격
        bodyMap2.put("receiverAddress", "0xfb8e77f5808121c3ecf19d92ffb56b2e3d8db57b");

        HashMap<String, Object> bodyMap = new HashMap<String, Object>();
        bodyMap.put("from", new String(wallet)); //보내는사람 지갑주소
        bodyMap.put("inputs", new HashMap<String, String>(bodyMap2)); //bodyMap2

        Log.d("성공 : ", "result : " + bodyMap.toString());

        call.buyFood(bodyMap).enqueue(new Callback<SetBalanceResultVO>() {
            @Override
            public void onResponse(Call<SetBalanceResultVO> call, Response<SetBalanceResultVO> response) {
                Log.d("성공 : ", "result : " + response.raw());
                Log.d("성공 : ", "result : " + response.body().getResult());
                Log.d("성공 : ", "TxId : " + response.body().getDataFoodBuy().getTxId());
                Log.d("성공 : ", "ReqTs : " + response.body().getDataFoodBuy().getReqTs());
            }

            @Override
            public void onFailure(Call<SetBalanceResultVO> call, Throwable t) {
                Log.d("실패 : ", t.toString());
            }

        });
    }
}
