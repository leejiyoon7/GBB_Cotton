package com.example.cotton.ui.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cotton.R;
import com.example.cotton.ui.food.FoodListItem;

import java.util.ArrayList;

public class HistoryAdapter extends BaseAdapter {

    private ImageView historyIcon;
    private TextView historyType;
    private TextView historyBookname;
    private TextView historyDate;
    private TextView historyVariance;
    private TextView historyBalance;

    private ArrayList<HistoryListItem> historyItemsList=new ArrayList<>();

    //constructor
    public HistoryAdapter(){

    }

    //Adapter에 사용되는 데이터의 개수를 리턴
    @Override
    public int getCount() {
        return historyItemsList.size();
    }

    //position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context=parent.getContext();

        //"history_listview_item" Layout을 inflate하여 convertView 참조 획득
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.history_listview_item,parent,false);
        }

        //화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        historyIcon=convertView.findViewById(R.id.history_listview_ic);
        historyType=convertView.findViewById(R.id.history_listview_type_textView);
        historyBookname=convertView.findViewById(R.id.history_listview_bookname_textView);
        historyDate=convertView.findViewById(R.id.history_listview_date_textView);
        historyVariance=convertView.findViewById(R.id.history_listview_variance_textView);
        historyBalance=convertView.findViewById(R.id.history_listview_balance_textView);

        HistoryListItem historyListItem=historyItemsList.get(position);
        //아이템 내 각 위젯에 데이터 반영
        historyIcon.setImageResource(historyListItem.getHistoryIcon());
        historyType.setText(historyListItem.getHistoryType());
        historyBookname.setText(historyListItem.getHistoryBookname());
        historyDate.setText(historyListItem.getHistoryDate());
        historyVariance.setText(historyListItem.getHistoryVariance());
        historyBalance.setText(historyListItem.getHistoryBalance());

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
        return historyItemsList.get(position);
    }

    //아이템 데이터 추가를 위한 함수
    public void addItem(int historyIcon, String historyType, String historyBookname, String historyDate, String historyVariance, String historyBalance){
        HistoryListItem item=new HistoryListItem();

        item.setHistoryIcon(historyIcon);
        item.setHistoryType(historyType);
        item.setHistoryBookname(historyBookname);
        item.setHistoryDate(historyDate);
        item.setHistoryVariance(historyVariance);
        item.setHistoryBalance(historyBalance);

        historyItemsList.add(item);
    }
}
