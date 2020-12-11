package com.example.cotton.ui.home.ReturnBook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ReturnBookActivity extends AppCompatActivity {


    TextView headerTitle;
    Button scanToggleBtn;
    RecyclerView recyclerView;
    Button completeBtn;
    ReturnRentedBookListAdapter returnRentedBookListAdapter;//대여 도서 목록 adapter

    int scanToggleBtnStatus = -1;
    String borrowerUID;

    private BeepManager beepManager;
    private String lastText;
    DecoratedBarcodeView barcodeView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allow_borrow);

        headerTitle = findViewById(R.id.allow_borrow_header_title_text_view);
        scanToggleBtn = findViewById(R.id.allow_borrow_select_all_btn);
        recyclerView = findViewById(R.id.allow_borrow_recycler_view);
        completeBtn = findViewById(R.id.allow_borrow_complete_btn);
        barcodeView = findViewById(R.id.allow_borrow_barcode_view);


        Intent intent = getIntent();
        borrowerUID = intent.getStringExtra("borrowerUID");
        headerTitle.setText("반납 도서 목록");
        showMyRentedBookListFunc(borrowerUID);
        setScanToggleBtnInit();
        setCompleteBtnInit();


        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.EAN_13);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(getIntent());
        barcodeView.decodeContinuous(callback);

        beepManager = new BeepManager(this);
    }


    public void showMyRentedBookListFunc(String borrowerUID) {

        FirebaseFunction firebaseTest = new FirebaseFunction();

        firebaseTest.myRentedBookListGet(borrowerUID, (myRentedBookList) -> {
            // null check
            if (myRentedBookList.size() > 0) {
                returnRentedBookListAdapter = new ReturnRentedBookListAdapter();
                returnRentedBookListAdapter.delegate = this;
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                //adapter 달기
                recyclerView.setAdapter(returnRentedBookListAdapter);

                int num = myRentedBookList.size();
                for (int i = 0; i < num; i++) {
                    returnRentedBookListAdapter.addItem(
                            myRentedBookList.get(i).getBookName(),
                            myRentedBookList.get(i).getBookWriter(),
                            myRentedBookList.get(i).getStatus(),
                            myRentedBookList.get(i).getBarcode(),
                            myRentedBookList.get(i).getBookOwnerUUID()
                    );
                }
                returnRentedBookListAdapter.notifyDataSetChanged();//adapter의 변경을 알림
            }
            return null;
        });
    }



    private void setCompleteBtnInit() {
        completeBtn.setText("반 납 처 리");
        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                WriteBatch batch = db.batch();

                returnRentedBookListAdapter.returnRentedBookScanList.forEach(bookInfo -> {
                    String bookBarcode = bookInfo.getList_my_rented_book_barcode();
                    String bookOwnerUID = bookInfo.getList_my_rented_book_owner_uid();

                    DocumentReference bookSaveRef = db
                            .collection("bookSave")
                            .document(bookBarcode)
                            .collection("RegisteredUsers")
                            .document(bookOwnerUID);
                    batch.update(bookSaveRef, "rentedMember", "a");


                    DocumentReference usersRef = db
                            .collection("users")
                            .document(borrowerUID)
                            .collection("RentedBook")
                            .document(bookBarcode);
                    batch.delete(usersRef);
                });

                batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ReturnBookActivity.this, "반납 처리가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
    }



    private void setScanToggleBtnInit() {
        scanToggleBtn.setText("스캔시작");
        scanToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scanToggleBtnStatus == -1) {
                    barcodeView.setVisibility(View.VISIBLE);
                }
                else {
                    barcodeView.setVisibility(View.GONE);
                }
                scanToggleBtnStatus *= -1;
                changeScanToggleBtn();
            }
        });
    }



    private void changeScanToggleBtn() {
        if (scanToggleBtnStatus == -1) {
           scanToggleBtn.setText("스캔열기");
        }
        else {
            scanToggleBtn.setText("스캔닫기");
        }
    }

    public void noBookFound() {
        Toast.makeText(this, "일치하는 책이 없습니다.", Toast.LENGTH_SHORT).show();
    }





    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null || result.getText().equals(lastText)) {
                // Prevent duplicate scans
                return;
            }


            lastText = result.getText();

            returnRentedBookListAdapter.addToScanList(lastText);

            barcodeView.setStatusText(result.getText());

            beepManager.playBeepSoundAndVibrate();
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    public void pause(View view) {
        barcodeView.pause();
    }

    public void resume(View view) {
        barcodeView.resume();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

}