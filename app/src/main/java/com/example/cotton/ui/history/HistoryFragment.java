package com.example.cotton.ui.history;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.cotton.LogForm;
import com.example.cotton.MemberInfo;
import com.example.cotton.R;
import com.example.cotton.FirebaseFunction;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;

public class HistoryFragment extends Fragment {

    SegmentedButtonGroup segmentedButtonGroup;//segmentButtonGroup 생성
    ListView transactional_information_list;//도서거래 ListView
    HistoryAdapter adapter;//도서거래 adapter
    public static final int ALL=0;
    public static final int INCOME=1;
    public static final int EXPENDITURE=2;

    String pictureLink;
    private static final String TAG_TEXT = "text";
    Uri selectedImageUri;
    ImageView bookImg;
    List<MemberInfo> memberInfos = new ArrayList<>();
    List<LogForm> logFormList;
    DateCompare dateCompare;
    FloatingActionButton sort_action_button;

    FirebaseFunction firebaseFunction;//firebase log output

    HistoryState historyState;

    static int clickCount=0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        firebaseFunction=new FirebaseFunction();//firebase log output

        segmentedButtonGroup = (SegmentedButtonGroup)view.findViewById(R.id.segmentedButtonGroup);

        transactional_information_list=view.findViewById(R.id.transactional_information_listView);//listview 참조

        sort_action_button = view.findViewById(R.id.sort_action_button);

        historyState=new HistoryState();

        dateCompare=new DateCompare();

        //초기 화면 구성
        showHistoryListFunc(ALL);
        
        //플로팅 버튼 온클릭 함수
        sort_action_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickCount%2==0){
                    dateCompare.setState(1);
                }
                else{
                    dateCompare.setState(-1);
                }
                Log.d("KDJ1","dateCompare.getState(): "+dateCompare.getState());
                Log.d("KDJ1","clickCount: "+clickCount);
                Log.d("KDJ1","historyState.getState(): "+historyState.getState());
               switch(historyState.getState()){
                   case 0:
                       showHistoryListFunc(ALL);
                       historyState.setState(ALL);
                       break;
                   case 1:
                       showHistoryListFunc(INCOME);
                       historyState.setState(INCOME);
                       break;
                   case 2:
                       showHistoryListFunc(EXPENDITURE);
                       historyState.setState(EXPENDITURE);
                       break;
               }
                clickCount++;
            }
        });


        //segmentButtonGroup 버튼 클릭 이벤트(position)별
        segmentButtonClickEvent();

        //거래내역 list 위아래 sorting

//        test_btn=root.findViewById(R.id.test_btn);
//        bookImg=root.findViewById(R.id.bookImg);

//        bookImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showGallery();
//            }
//        });
//
//        test_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                localUpoad("책이름","전공","저자"); //이렇게 변수 3개넣고 사진넣으면 책 저장됨
//            }
//        });


        return view;
    }


    //segmentButtonGroup 버튼 클릭 이벤트(position)별, 추후 구현 예정
    public void segmentButtonClickEvent(){
        segmentedButtonGroup.setPosition(0, 0);
        segmentedButtonGroup.setOnClickedButtonListener(new SegmentedButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(int position) {
                switch (position) {
                    case 0:
                        showHistoryListFunc(ALL);
                        historyState.setState(ALL);
                        break;
                    case 1:
                        showHistoryListFunc(INCOME);
                        historyState.setState(INCOME);
                        break;
                    case 2:
                        showHistoryListFunc(EXPENDITURE);
                        historyState.setState(EXPENDITURE);
                        break;
                }
            }
        });


    }

    //segmentButtonGroup에 따라 거래내역 Listview 설정, 추후 firebase에서 동적으로 받아와서 Variance에 따라 구분하여 조건 분기할 예정
    public void showHistoryListFunc(int state){
        //adapter 설정
        adapter=new HistoryAdapter();
        //adapter 달기
        transactional_information_list.setAdapter(adapter);

        //listview에 add
        switch(state){
            case ALL:
                Log.d("Case","Case: "+state);
                firebaseFunction.logAllOutput(logForms -> {
                    logFormList=logForms;
                    Collections.sort(logFormList, dateCompare);
                    for(int i=0;i<logFormList.size();i++){

                        if(logFormList.get(i).getFrom().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) &&
                                logFormList.get(i).getTo().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                            String amountString = logFormList.get(i).getAmount().replace("GBB", "");
                            int amountInt = Integer.parseInt(amountString);
                            amountString = String.valueOf(amountInt - 500) + "GBB";

                            adapter.addItem(R.drawable.ic_out,
                                    logFormList.get(i).getCategory(),
                                    logFormList.get(i).getMessage(),
                                    logFormList.get(i).getDate().replaceAll("/",".").substring(0,10),
                                    "- "+logFormList.get(i).getAmount(),
                                    logFormList.get(i).getDate().replaceAll("/","."));

                            adapter.addItem(R.drawable.ic_in,
                                    logFormList.get(i).getCategory(),
                                    logFormList.get(i).getMessage(),
                                    logFormList.get(i).getDate().replaceAll("/",".").substring(0,10),
                                    "+ "+amountString,
                                    logFormList.get(i).getDate().replaceAll("/","."));

                        }

                        //지출 UID가 현재 로그인한 UID와 같다면
                        else if(logFormList.get(i).getFrom().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                            adapter.addItem(R.drawable.ic_out,
                                    logFormList.get(i).getCategory(),
                                    logFormList.get(i).getMessage(),
                                    logFormList.get(i).getDate().replaceAll("/",".").substring(0,10),
                                    "- "+logFormList.get(i).getAmount(),
                                    logFormList.get(i).getDate().replaceAll("/","."));
                        }
                        //수입 UID가 현재 로그인한 UID와 같다면
                        else if(logFormList.get(i).getTo().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                            String amountString = logFormList.get(i).getAmount().replace("GBB", "");
                            int amountInt = Integer.parseInt(amountString);
                            amountString = String.valueOf(amountInt - 500) + "GBB";

                            adapter.addItem(R.drawable.ic_in,
                                    logFormList.get(i).getCategory(),
                                    logFormList.get(i).getMessage(),
                                    logFormList.get(i).getDate().replaceAll("/",".").substring(0,10),
                                    "+ "+amountString,
                                    logFormList.get(i).getDate().replaceAll("/","."));
                        }
                    }
                    adapter.notifyDataSetChanged();
                    return null;
                });
                break;
            case INCOME:
                Log.d("Case","Case: "+state);
                firebaseFunction.logToOutput(logForms -> {
                    logFormList=logForms;
                    Collections.sort(logFormList, dateCompare);

                    for(int i=0;i<logFormList.size();i++){
                        adapter.addItem(R.drawable.ic_in,
                                logFormList.get(i).getCategory(),
                                logFormList.get(i).getMessage(),
                                logFormList.get(i).getDate().replaceAll("/",".").substring(0,10),
                                "+ "+logFormList.get(i).getAmount(),
                                logFormList.get(i).getDate().replaceAll("/","."));
                    }
                    adapter.notifyDataSetChanged();
                    return null;
                });
                break;

            case EXPENDITURE:
                Log.d("Case","Case: "+state);
                firebaseFunction.logFromOutput(logForms -> {
                    logFormList=logForms;
                    Collections.sort(logFormList, dateCompare);

                    for(int i=0;i<logFormList.size();i++){
                        adapter.addItem(R.drawable.ic_out,
                                logFormList.get(i).getCategory(),
                                logFormList.get(i).getMessage(),
                                logFormList.get(i).getDate().replaceAll("/",".").substring(0,10),
                                "- "+logFormList.get(i).getAmount(),
                                logFormList.get(i).getDate().replaceAll("/","."));
                    }
                    adapter.notifyDataSetChanged();
                    return null;
                });
                break;
        }

        transactional_information_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // BottomSheetDialog 초기화.
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(HistoryFragment.this.getActivity());
                bottomSheetDialog.setContentView(R.layout.bottom_trading_details);

                String bookName=adapter.getHistoryBookname(position);
                String dateDetail=adapter.getHistoryDateDetail(position);
                String type=adapter.getHistoryType(position);
                String variance=adapter.getHistoryVariance(position);

                TextView history_type=bottomSheetDialog.findViewById(R.id.history_type);
                TextView history_bookname=bottomSheetDialog.findViewById(R.id.history_bookname);
                TextView history_date=bottomSheetDialog.findViewById(R.id.history_date);
                TextView history_variance=bottomSheetDialog.findViewById(R.id.history_variance);

                history_type.setText("상품 종류: "+type);
                history_bookname.setText("상품명: "+bookName);
                history_date.setText("거래 시간: "+dateDetail);
                history_variance.setText("수입/지출: "+variance);

                bottomSheetDialog.show();

            }
        });
    }
}