package com.example.cotton.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.cotton.LoginActivity;
import com.example.cotton.MemberInfo;
import com.example.cotton.R;
import com.example.cotton.bookSaveForm;
import com.example.cotton.firebaseFunction;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    Button btnLogout;
    List<MemberInfo> memberInfos = new ArrayList<>();
    List<bookSaveForm> bookSaveFormList= new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
// <<<<<<< firebase_function
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


//         firebaseFunction firebaseTest = new firebaseFunction();
//         //임의의 값 넣는거
//         firebaseTest.insertBookInfo("124","전공","책이름123","책저자","지갑정보","사람이름123");
//         //여기서는 단어하나로 검색가능
//         firebaseTest.serchBook("4");


//         firebaseTest.profileGet(memberInfos, (resultList) -> {  // 맴버 정보 가져오기 / get(0).get 으로 모든정보가져올수있음
//                                                                 // 해당 정보 이용시 여기 안에다 코딩해야함
//             Log.d("home에서 확인",resultList.get(0).getName());
//             return null;
//         });


//         firebaseTest.bookListGet(bookSaveFormList, (resultList) -> { // 모든 책정보 가져오기 / for문을 size로 돌리면 모든 책정보 가져올수 있음
//             Log.d("home에서 확인",resultList.get(0).getBookName());
//             Log.d("home에서 확인",resultList.get(1).getBookName());
//             return null;
//         });



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

        return root;
    }
}