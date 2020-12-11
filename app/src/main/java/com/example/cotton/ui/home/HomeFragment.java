package com.example.cotton.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cotton.Utils.ApiService;
import com.example.cotton.LoginActivity;
import com.example.cotton.MemberInfo;
import com.example.cotton.R;
import com.example.cotton.Utils.BaseUrlInterface;
import com.example.cotton.Utils.RetrofitClientJson;
import com.example.cotton.ValueObject.GetBalance.GetBalanceResultVO;
import com.example.cotton.UserRegisteredBookSaveForm;
import com.example.cotton.ValueObject.SetBalance.SetBalanceResultVO;
import com.example.cotton.BookSaveForm;
import com.example.cotton.FirebaseFunction;
import com.example.cotton.ui.home.allowBorrow.AllowBorrowActivity;
import com.example.cotton.ui.home.register.RegisterBookActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements Runnable{

    // Button btnLogout;
    List<MemberInfo> memberInfos = new ArrayList<>();
    List<BookSaveForm> bookSaveFormList= new ArrayList<>();
    List<UserRegisteredBookSaveForm> testList = new ArrayList<>();

    ImageButton home_profile_image_button;//프로필 이미지
    TextView home_my_point_user_name_text_view;//사용자명
    ImageButton home_my_point_plus_btn;//플러스 버튼
    TextView home_my_point_amount_text_view;//현재 보유 포인트 양
    TextView home_my_point_food_ticket_text_view;//보유 식권

    RecyclerView home_my_rented_book_recycler_view;//대여 도서 목록 RecyclerView
    MyRentedBookListAdapter myRentedBookListAdapter;//대여 도서 목록 adapter
    Button home_my_rented_book_card_view_more_button;//대여 도시 목록 더보기 버튼

    RecyclerView home_my_registered_book_recycler_view;//나의 등록 도서 목록 RecyclerView
    MyRegisteredBookListAdapter myRegisteredBookListAdapter;//나의 등록 도서 목록 adapter
    Button home_my_registered_book_card_view_more_button;//나의 등록 도서 목록 더보기 버튼

    Button home_register_book_btn;//도서등록 버튼

    SwipeRefreshLayout swipeRefreshLayout;

    double money;
    String UID;
    String returnBookBarcode;
    String borrowerUID;

    HomeFragment homeFragment;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //인플레이션
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        home_profile_image_button=view.findViewById(R.id.home_profile_image_button);
        home_my_point_user_name_text_view=view.findViewById(R.id.home_my_point_user_name_text_view);
        home_my_point_plus_btn=view.findViewById(R.id.home_my_point_plus_btn);
        home_my_point_amount_text_view=view.findViewById(R.id.home_my_point_amount_text_view);
        home_my_point_food_ticket_text_view=view.findViewById(R.id.home_my_point_food_ticket_text_view);

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        home_my_rented_book_recycler_view=view.findViewById(R.id.home_my_rented_book_recycler_view);
        home_my_registered_book_recycler_view=view.findViewById(R.id.home_my_registered_book_recycler_view);

        home_my_rented_book_card_view_more_button=view.findViewById(R.id.home_my_rented_book_card_view_more_button);//대여 도시 목록 더보기 버튼
        home_my_registered_book_card_view_more_button=view.findViewById(R.id.home_my_registered_book_card_view_more_button);//나의 등록 도서 목록 더보기 버튼

        home_my_rented_book_recycler_view.setNestedScrollingEnabled(false);
        home_my_registered_book_recycler_view.setNestedScrollingEnabled(false);

        homeFragment= this;

        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);

        FirebaseFunction firebaseFunction = new FirebaseFunction();
        UID = firebaseFunction.getMyUID();

        //대여 도서 목록 RecyclerView 설정 method
        showMyRentedBookListFunc();

        //나의 도서 목록 RecyclerView 설정 method
        showMyRegisteredBookFunc();

        // firebase_function_ProfileImageDownload + FirebaseFunction 파일 안 설명참조
        // 프로필 사진 받아오기 (주석 지우면 실행됩니다.)
        FirebaseFunction firebaseTest = new FirebaseFunction();
        firebaseTest.profileImageDownload(home_profile_image_button, this.getContext());


        home_register_book_btn=view.findViewById(R.id.allow_borrow_complete_btn);

        home_register_book_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), RegisterBookActivity.class);
                startActivity(intent);
            }
        });


        // Home화면에 지갑잔고 출력
        getCoin();

        //로그아웃 버튼 구현
        home_profile_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("로그아웃").setMessage("\n로그아웃 하시겠습니까?\n");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent=new Intent(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });

                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Toast.makeText(getContext(), "로그아웃 취소", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        home_profile_image_button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(UID.equals("jAa5lxn1IFQw9uZRu9VXk43MrlI3")) {
                    returnBookBarcode = "";
                    borrowerUID = "";

                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(homeFragment.getContext());
                    bottomSheetDialog.setContentView(R.layout.bottom_dialog_select_return_or_borrow);
                    TextView caseBorrowBtn = bottomSheetDialog.findViewById(R.id.return_or_borrow_borrow_btn);
                    TextView caseReturnBtn = bottomSheetDialog.findViewById(R.id.return_or_borrow_return_btn);
                    caseBorrowBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showUserQrScanBottomDialog();
//                            showTestPage();
                            bottomSheetDialog.dismiss();
                        }
                    });
                    caseReturnBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showReturnBottomDialog();
                            bottomSheetDialog.dismiss();
                        }
                    });
                    bottomSheetDialog.show();
                }
                else {
                    Toast.makeText(getContext(), "관리자 권한이 없습니다.", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });

        //클릭시 manager 계정에서 10000코인 송금받음
        home_my_point_plus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<String> ListItems = new ArrayList<>();
                ListItems.add("5000");
                ListItems.add("10000");
                ListItems.add("20000");
                ListItems.add("30000");
                final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("충전 금액 선택");

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        String selectedText = items[pos].toString();

                        firebaseTest.profileGet(memberInfos, (resultList) -> {
                            ApiService call = RetrofitClientJson.getApiService(BaseUrlInterface.LUNIVERSE);

                            HashMap<String, String> bodyMap2 = new HashMap<String, String>();
                            bodyMap2.put("valueAmount", selectedText + "000000000000000000"); //가격
                            bodyMap2.put("receiverAddress", resultList.get(0).getWallet());

                            HashMap<String, Object> bodyMap = new HashMap<String, Object>();
                            bodyMap.put("from", "0xfb8e77f5808121c3ecf19d92ffb56b2e3d8db57b"); //보내는사람 지갑주소
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

                            return null;
                        });
                        Toast.makeText(getContext(), selectedText+"GBB가 충전되었습니다", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }

        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });


        clickRentedViewMoreButtonFunc();//대여 도서 목록 +더보기 버튼 클릭
        clickRegisteredViewMoreButtonFunc();//나의 도서 목록 +더보기 버튼 클릭

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(HomeFragment.this.getContext(), "바코드 인식 취소 됨.", Toast.LENGTH_LONG).show();
            }
            else { // 바코드 스캔 성공
                // 대여의 경우
                if(result.getContents().length() == 28) {
                    Log.d("Borrow case:", result.getContents());
                    // 대여자의 빌린 도서 목록으로 접속하여 도서를 받아온다음
                    // 새로운 액티비티를 열어서 리사이클러뷰로 뿌려준다.
                    // Monireu
                    Intent intent = new Intent(getActivity(), AllowBorrowActivity.class);
                    intent.putExtra("borrowerUID", result.getContents());
                    startActivityForResult(intent, 100);
                }
                // 반납의 경우
                else {
                    // 책 바코드 정보가 비어있을경우 책 바코드 정보에 값을 넣고, 유저캔 QR코드 스캔
                    if (returnBookBarcode.equals("")) {
                        returnBookBarcode = result.getContents();
                        showUserQrScanBottomDialog();
                    }
                    // 책 바코드 정보가 있을 경우 책 반납절차 실행
                    else {
                        String qrStringInfo = result.getContents();
                        int slashIndex = qrStringInfo.indexOf("/");
                        String bookBarcode = qrStringInfo.substring(0, slashIndex);
                        String bookOwner = qrStringInfo.substring(slashIndex + 1);

                        if (returnBookBarcode.equals(bookBarcode)) {
                            FirebaseFunction firebaseFunction = new FirebaseFunction();
                            firebaseFunction.returnBook(
                                    bookBarcode,
                                    bookOwner,
                                    (value) -> {
                                        Toast.makeText(getContext(), "반납이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                        return null;
                                    }
                            );
                        }
                        else {
                            Toast.makeText(getContext(), "반납할 책의 정보가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }// End of: if (returnBookBarcode.equals(""))
                } // End of: if(result.getContents().length() == 28)
            } // End of: if(result.getContents() == null)
        } else {
            Log.d("ResultCode 1000 = ", "CodeFail");
            super.onActivityResult(requestCode, resultCode, data);

        }
    }

    public void goToHomeFragmentFunc(){
        Thread thread=new Thread(this);
        thread.start();
    }

    //지갑잔고 받아오기
    public void getCoin(){
        FirebaseFunction firebaseTest = new FirebaseFunction();
        firebaseTest.profileGet(memberInfos, (resultList) -> {

            home_my_point_user_name_text_view.setText(resultList.get(0).getName()); // Home화면에 UserName 출력
            home_my_point_food_ticket_text_view.setText("보유식권: " + Long.toString(resultList.get(0).getTicket()) + "장"); // Home화면에 보유티겟 수 출력

            ApiService call = RetrofitClientJson.getApiService(BaseUrlInterface.LUNIVERSE);

            call.getMoney(resultList.get(0).getWallet()).enqueue(new Callback<GetBalanceResultVO>() {
                @Override
                public void onResponse(Call<GetBalanceResultVO> call, Response<GetBalanceResultVO> response) {
                    Log.d("성공 : ", "result : " + response.body().getResult());
                    Log.d("성공 : ", "address : " + response.body().getDataBalance().getBalance());
                    money = Double.parseDouble(response.body().getDataBalance().getBalance());
                    money = (money*0.000000000000000001);
                    home_my_point_amount_text_view.setText((String.valueOf((int)money)));
                }

                @Override
                public void onFailure(Call<GetBalanceResultVO> call, Throwable t) {
                    Log.d("실패 : ", t.toString());
                }

            });

            return null;
        });
    }

    public void showTestPage() {
        Intent intent = new Intent(getActivity(), AllowBorrowActivity.class);
        intent.putExtra("borrowerUID", "0yvx4xvTcoOUdEmKcZJIKiYSses1");
        startActivity(intent);
    }


    public void showMyRentedBookListFunc(){

        FirebaseFunction firebaseTest = new FirebaseFunction();

        firebaseTest.myRentedBookListGet(firebaseTest.getMyUID(), (myRentedBookList) -> {
            // null check
            if(myRentedBookList.size()>0){
                myRentedBookListAdapter = new MyRentedBookListAdapter() ;
                home_my_rented_book_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
                //adapter 달기
                home_my_rented_book_recycler_view.setAdapter(myRentedBookListAdapter);

                int num = myRentedBookList.size();
                if(num>3) num=3;
                for(int i=0;i<num;i++){
                    myRentedBookListAdapter.addItem(
                            myRentedBookList.get(i).getBookName(),
                            myRentedBookList.get(i).getBookWriter(),
                            myRentedBookList.get(i).getStatus(),
                            myRentedBookList.get(i).getBarcode(),
                            myRentedBookList.get(i).getBookOwnerUUID()
                    );
                }
                myRentedBookListAdapter.notifyDataSetChanged();//adapter의 변경을 알림
            }
            return null;
        });
    }


    //대여 도서 목록 +더보기 버튼 클릭
    public void clickRentedViewMoreButtonFunc(){
        FirebaseFunction firebaseTest = new FirebaseFunction();
        home_my_rented_book_card_view_more_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (home_my_rented_book_card_view_more_button.getText().toString()){

                    case "+ 더보기":
                        firebaseTest.myRentedBookListGet(firebaseTest.getMyUID(), (resultList) -> {
                            if(resultList.size()>3){
                                myRentedBookListAdapter = new MyRentedBookListAdapter() ;
                                home_my_rented_book_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
                                //adapter 달기
                                home_my_rented_book_recycler_view.setAdapter(myRentedBookListAdapter);
                                for(int i=0;i<resultList.size();i++){
                                    myRentedBookListAdapter.addItem(resultList.get(i).getBookName(),resultList.get(i).getBookWriter(),resultList.get(i).getStatus(), resultList.get(i).getBarcode(), resultList.get(i).getBookOwnerUUID());
                                }
                                myRentedBookListAdapter.notifyDataSetChanged();//adapter의 변경을 알림
                                home_my_rented_book_card_view_more_button.setText("- 간단히");
                            }
                            return null;
                        });
                        break;
                    case "- 간단히":
                        showMyRentedBookListFunc();
                        home_my_rented_book_card_view_more_button.setText("+ 더보기");
                        break;
                    default:
                        break;
                }
            }
        });
    }


    public void showMyRegisteredBookFunc(){
        FirebaseFunction firebaseTest = new FirebaseFunction();
        firebaseTest.myRegisteredBookListGet((resultList) -> { //resultList안에 너가 원하는 모든게 있단다.
            if(resultList.size()>0){
                myRegisteredBookListAdapter = new MyRegisteredBookListAdapter() ;

                home_my_registered_book_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
                //adapter 달기
                home_my_registered_book_recycler_view.setAdapter(myRegisteredBookListAdapter);

                int num = resultList.size();
                if(num>3) num=3;
                for(int i=0;i<num;i++){
                    myRegisteredBookListAdapter.addItem(resultList.get(i).getBookName(), resultList.get(i).getBookWriter());
                }
                myRegisteredBookListAdapter.notifyDataSetChanged();//adapter의 변경을 알림
            }
            return null;
        });
    }

    //나의 도서 목록 +더보기 버튼 클릭
    public void clickRegisteredViewMoreButtonFunc(){
        FirebaseFunction firebaseTest = new FirebaseFunction();
        home_my_registered_book_card_view_more_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (home_my_registered_book_card_view_more_button.getText().toString()){

                    case "+ 더보기":
                        firebaseTest.myRegisteredBookListGet((resultList) -> { //resultList안에 너가 원하는 모든게 있단다.
                            if(resultList.size()>3){
                                myRegisteredBookListAdapter = new MyRegisteredBookListAdapter() ;

                                home_my_registered_book_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
                                //adapter 달기
                                home_my_registered_book_recycler_view.setAdapter(myRegisteredBookListAdapter);

                                for(int i=0;i<resultList.size();i++) {
                                    myRegisteredBookListAdapter.addItem(resultList.get(i).getBookName(), resultList.get(i).getBookWriter());
                                }

                                myRegisteredBookListAdapter.notifyDataSetChanged();//adapter의 변경을 알림
                                home_my_registered_book_card_view_more_button.setText("- 간단히");
                            }
                            return null;
                        });

                        break;
                    case "- 간단히":
                        showMyRegisteredBookFunc();
                        home_my_registered_book_card_view_more_button.setText("+ 더보기");
                        break;
                    default:
                        break;
                }
            }
        });
    }


    private void showReturnBottomDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(homeFragment.getContext());
        bottomSheetDialog.setContentView(R.layout.bottom_dialog_admin_scan_book);
        TextView scanReturnBookBtn = bottomSheetDialog.findViewById(R.id.admin_scan_book_btn);
        scanReturnBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(homeFragment);
                intentIntegrator.setBeepEnabled(true);//바코드 인식시 소리
                intentIntegrator.setDesiredBarcodeFormats(BarcodeFormat.EAN_13.toString());
                intentIntegrator.initiateScan();
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();
    }

    private void showUserQrScanBottomDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(HomeFragment.this.getContext());
        bottomSheetDialog.setContentView(R.layout.bottom_dialog_admin_scan_user_qr);
        TextView scanBorrowerQrBtn = bottomSheetDialog.findViewById(R.id.admin_scan_borrower_qr_btn);
        scanBorrowerQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(HomeFragment.this);

                intentIntegrator.setBeepEnabled(true);//바코드 인식시 소리
                intentIntegrator.setDesiredBarcodeFormats(String.valueOf(BarcodeFormat.QR_CODE));
                intentIntegrator.initiateScan();
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();
    }


    @Override
    public void run() {
        try {
            Thread.sleep(2000);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void refresh () {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
}