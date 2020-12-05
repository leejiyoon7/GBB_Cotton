package com.example.cotton.ui.home;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.cotton.LoginActivity;
import com.example.cotton.MemberInfo;
import com.example.cotton.R;
import com.example.cotton.bookSaveForm;
import com.example.cotton.firebaseFunction;
import com.example.cotton.ui.food.FoodListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.example.cotton.ui.home.register.RegisterBookActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

   // Button btnLogout;
    List<MemberInfo> memberInfos = new ArrayList<>();
    List<bookSaveForm> bookSaveFormList= new ArrayList<>();

    ImageButton home_profile_image_button;//프로필 이미지
    TextView home_my_point_user_name_text_view;//사용자명
    ImageButton home_my_point_plus_btn;//플러스 버튼
    TextView home_my_point_amount_text_view;//현재 보유 포인트 양
    TextView home_my_point_food_ticket_text_view;//보유 식권

    RecyclerView home_my_rented_book_recycler_view;//대여 도서 목록 RecyclerView
    MyRentedBookListAdapter myRentedBookListAdapter;//대여 도서 목록 adapter

    RecyclerView home_my_registered_book_recycler_view;//나의 등록 도서 목록 RecyclerView
    MyRegisteredBookListAdapter myRegisteredBookListAdapter;//나의 등록 도서 목록 adapter

    Button home_register_book_btn;//도서등록 버튼

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

        //대여 도서 목록 RecyclerView 설정 method
        showMyRentedBookListFunc();

        //나의 도서 목록 RecyclerView 설정 method
        showMyRegisteredBookFunc();

        // firebase_function_ProfileImageDownload + FirebaseFunction 파일 안 설명참조
        // 프로필 사진 받아오기 (주석 지우면 실행됩니다.)
        firebaseFunction firebaseTest = new firebaseFunction();
        firebaseTest.profileImageDownload(home_profile_image_button, this.getContext());


        home_register_book_btn=view.findViewById(R.id.home_register_book_btn);

        home_register_book_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), RegisterBookActivity.class);
                startActivity(intent);
            }
        });

        // Home화면에 UserName 출력
        firebaseTest.profileGet(memberInfos, (resultList) -> {
             Log.d("home에서 확인",resultList.get(0).getName());
            home_my_point_user_name_text_view.setText(resultList.get(0).getName());
             return null;
         });

        // Home화면에 보유티겟 수 출력
        firebaseTest.profileGet(memberInfos, (resultList) -> {
            Log.d("home에서 확인2",Integer.toString(resultList.get(0).getTicket()));
            home_my_point_food_ticket_text_view.setText("보유식권: " + Integer.toString(resultList.get(0).getTicket()) + "장");
            return null;
        });



//         btnLogout=root.findViewById(R.id.btn_logout);
//         //로그아웃 버튼 구현
//         btnLogout.setOnClickListener(new View.OnClickListener() {
//             @Override
//             public void onClick(View view) {
//                 FirebaseAuth.getInstance().signOut();
//                 Intent intent=new Intent(getActivity(), LoginActivity.class);
//                 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                 startActivity(intent);
//             }
//         });



//
//         //여기서는 단어하나로 검색가능
//         firebaseTest.serchBook("4");


//         firebaseTest.profileGet(memberInfos, (resultList) -> {  // 맴버 정보 가져오기 / get(0).get 으로 모든정보가져올수있음
//                                                                 // 해당 정보 이용시 여기 안에다 코딩해야함
//             Log.d("home에서 확인",resultList.get(0).getName());
//             return null;
//         });
        
        firebaseTest.bookListGet(bookSaveFormList, (resultList) -> { // 모든 책정보 가져오기 / for문을 size로 돌리면 모든 책정보 가져올수 있음
             Log.d("home에서 확인",resultList.get(0).getBookName());
             Log.d("home에서 확인",resultList.get(1).getBookName());

             return null;
         });



//        btnLogout=root.findViewById(R.id.btn_logout);
//        //로그아웃 버튼 구현
//        btnLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseAuth.getInstance().signOut();
//                Intent intent=new Intent(getActivity(), LoginActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//            }
//        });



        return view;
    }

    //대여 도서 목록 RecyclerView 설정
    public void showMyRentedBookListFunc(){
        //adapter 생성
        myRentedBookListAdapter = new MyRentedBookListAdapter() ;
        
        home_my_rented_book_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        //adapter 달기
        home_my_rented_book_recycler_view.setAdapter(myRentedBookListAdapter);

        // 리사이클러뷰에 표시할 데이터 리스트 생성(예시로 3개), 추후 firebase에서 data 받아와 동적으로 생성되게 짤 예정
        myRentedBookListAdapter.addItem("C언어 콘서트","김동주","도서대여");
        myRentedBookListAdapter.addItem("Java 콘서트","김민석","연체");
        myRentedBookListAdapter.addItem("C++ 콘서트","이정일","반납대기");
        myRentedBookListAdapter.addItem("C# 콘서트","심민수","반납대기");
        myRentedBookListAdapter.notifyDataSetChanged();//adapter의 변경을 알림
    }

    //나의 도서 목록 RecyclerView 설정
    public void showMyRegisteredBookFunc(){
        //adapter 생성
        myRegisteredBookListAdapter = new MyRegisteredBookListAdapter() ;

        home_my_registered_book_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        //adapter 달기
        home_my_registered_book_recycler_view.setAdapter(myRegisteredBookListAdapter);

        // 리사이클러뷰에 표시할 데이터 리스트 생성(예시로 3개), 추후 firebase에서 data 받아와 동적으로 생성되게 짤 예정
        myRegisteredBookListAdapter.addItem("알기 쉽게 해설한 데이터구조","김동주");
        myRegisteredBookListAdapter.addItem("케라스 창시자에게 배우는 딥러닝","김민석");
        myRegisteredBookListAdapter.addItem("JAVA Programming","이정일");
        myRegisteredBookListAdapter.addItem("컴퓨터 아키텍쳐","심민수");

        myRegisteredBookListAdapter.notifyDataSetChanged();//adapter의 변경을 알림
    }
}