package com.example.cotton.ui.home.allowBorrow;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cotton.FirebaseFunction;
import com.example.cotton.R;
import com.example.cotton.ui.home.HomeFragment;
import com.example.cotton.ui.home.MyRentedBookListAdapter;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class AllowBorrowActivity extends AppCompatActivity {


    TextView headerTitle;
    Button selectBtn;
    Button selectAllBtn;
    RecyclerView recyclerView;
    Button completeBtn;
    MyRentedBookListAdapter myRentedBookListAdapter;//대여 도서 목록 adapter


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allow_borrow);

        headerTitle = findViewById(R.id.allow_borrow_header_title_text_view);
        selectBtn = findViewById(R.id.allow_borrow_select_each_btn);
        selectAllBtn = findViewById(R.id.allow_borrow_select_all_btn);
        recyclerView = findViewById(R.id.allow_borrow_recycler_view);
        completeBtn = findViewById(R.id.allow_borrow_complete_btn);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "바코드 인식 취소 됨.", Toast.LENGTH_LONG).show();
            } else { // 바코드 스캔 성공
                String borrowerUID = data.getStringExtra("borrowerUID");
                showMyRentedBookListFunc(borrowerUID);
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void showMyRentedBookListFunc(String borrowerUID) {

        FirebaseFunction firebaseTest = new FirebaseFunction();

        firebaseTest.myRentedBookListGet(borrowerUID, (myRentedBookList) -> {
            // null check
            if (myRentedBookList.size() > 0) {
                myRentedBookListAdapter = new MyRentedBookListAdapter();
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                //adapter 달기
                recyclerView.setAdapter(myRentedBookListAdapter);

                int num = myRentedBookList.size();
                for (int i = 0; i < num; i++) {
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
}