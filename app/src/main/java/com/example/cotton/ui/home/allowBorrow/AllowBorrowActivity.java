package com.example.cotton.ui.home.allowBorrow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cotton.FirebaseFunction;
import com.example.cotton.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

public class AllowBorrowActivity extends Activity {


    TextView headerTitle;
    Button selectAllBtn;
    RecyclerView recyclerView;
    Button completeBtn;
    UserRentedBookListAdapter userRentedBookListAdapter;//대여 도서 목록 adapter

    String borrowerUID;
    int selectButtonStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allow_borrow);

        headerTitle = findViewById(R.id.allow_borrow_header_title_text_view);
        selectAllBtn = findViewById(R.id.allow_borrow_select_all_btn);
        recyclerView = findViewById(R.id.allow_borrow_recycler_view);
        completeBtn = findViewById(R.id.allow_borrow_complete_btn);
        selectButtonStatus = SelectButtonStatusInterface.DISABLED;

        Intent intent = getIntent();
        borrowerUID = intent.getStringExtra("borrowerUID");
        showMyRentedBookListFunc(borrowerUID);
        setSelectAllBtnInit();
        setCompleteBtnInit();
    }


    public void showMyRentedBookListFunc(String borrowerUID) {

        FirebaseFunction firebaseTest = new FirebaseFunction();

        firebaseTest.myRentedBookListGet(borrowerUID, (myRentedBookList) -> {
            // null check
            if (myRentedBookList.size() > 0) {
                userRentedBookListAdapter = new UserRentedBookListAdapter();
                userRentedBookListAdapter.delegate = this;
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                //adapter 달기
                recyclerView.setAdapter(userRentedBookListAdapter);

                int num = myRentedBookList.size();
                for (int i = 0; i < num; i++) {
                    userRentedBookListAdapter.addItem(
                            myRentedBookList.get(i).getBookName(),
                            myRentedBookList.get(i).getBookWriter(),
                            myRentedBookList.get(i).getStatus(),
                            myRentedBookList.get(i).getBarcode(),
                            myRentedBookList.get(i).getBookOwnerUUID()
                    );
                }
                userRentedBookListAdapter.notifyDataSetChanged();//adapter의 변경을 알림
            }
            changeSelectBtnStatus();
            return null;
        });
    }

    private void setSelectAllBtnInit() {
        selectAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (selectButtonStatus) {
                    case SelectButtonStatusInterface.SELECT_ALL:
                        userRentedBookListAdapter.selectAll();
                        break;
                    case SelectButtonStatusInterface.DELETE_ALL:
                        userRentedBookListAdapter.deleteAll();
                        break;
                }
                changeSelectBtnStatus();
            }
        });
    }


    private void setCompleteBtnInit() {
        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                WriteBatch batch = db.batch();

                userRentedBookListAdapter.userRentedBookSelectedList.forEach(bookInfo -> {
                    String bookBarcode = bookInfo.getList_my_rented_book_barcode();
                    String bookOwnerUID = bookInfo.getList_my_rented_book_owner_uid();

                    DocumentReference bookSaveRef = db
                            .collection("bookSave")
                            .document(bookBarcode)
                            .collection("RegisteredUsers")
                            .document(bookOwnerUID);
                    batch.update(bookSaveRef, "rentedMember", borrowerUID);


                    DocumentReference usersRef = db
                            .collection("users")
                            .document(borrowerUID)
                            .collection("RentedBook")
                            .document(bookBarcode);
                    batch.update(usersRef, "status", "대여중");
                    });

                batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(AllowBorrowActivity.this, "대여 승인이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
    }



    public void changeSelectBtnStatus() {
        int originalListSize = userRentedBookListAdapter.userRentedBookList.size();
        int selectedListSize = userRentedBookListAdapter.userRentedBookSelectedList.size();
        if (originalListSize == 0 && selectedListSize == 0) {
            selectButtonStatus = SelectButtonStatusInterface.DISABLED;
            selectAllBtn.setText("전체선택");
            selectAllBtn.setEnabled(false);
        }
        else if(originalListSize == selectedListSize) {
            selectButtonStatus = SelectButtonStatusInterface.DELETE_ALL;
            selectAllBtn.setText("전체해제");
            selectAllBtn.setEnabled(true);
        }
        else {
            selectButtonStatus = SelectButtonStatusInterface.SELECT_ALL;
            selectAllBtn.setText("전체선택");
            selectAllBtn.setEnabled(true);
        }
    }
}