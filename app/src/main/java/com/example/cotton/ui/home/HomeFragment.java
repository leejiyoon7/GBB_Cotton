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
import com.example.cotton.firebaseFunction;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    Button btnLogout;
    List<MemberInfo> memberInfos = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        btnLogout=root.findViewById(R.id.btn_logout);
        //로그아웃 버튼 구현
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        firebaseFunction firebaseTest = new firebaseFunction();
        // firebaseTest.insertBookInfo("1","2","3","4");

        firebaseTest.serchBook("4");
        firebaseTest.profileGet(memberInfos);
        Log.d("home에서 확인"," ");

        return root;
    }
}