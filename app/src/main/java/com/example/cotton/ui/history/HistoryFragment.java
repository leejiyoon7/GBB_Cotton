package com.example.cotton.ui.history;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;

import com.example.cotton.BookSaveForm;
import com.example.cotton.LogForm;
import com.example.cotton.MemberInfo;
import com.example.cotton.R;
import com.example.cotton.FirebaseFunction;
import com.example.cotton.ui.home.HomeFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;

import static android.app.Activity.RESULT_OK;

public class HistoryFragment extends Fragment {


    SearchView history_search_view;//거래내역 검색 searchview
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

    FirebaseFunction firebaseFunction;//firebase log output

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        firebaseFunction=new FirebaseFunction();//firebase log output

        segmentedButtonGroup = (SegmentedButtonGroup)view.findViewById(R.id.segmentedButtonGroup);

        transactional_information_list=view.findViewById(R.id.transactional_information_listView);//listview 참조

        //segmentButtonGroup 버튼 클릭 이벤트(position)별, 추후 구현 예정
        segmentButtonClickEvent();

        //거래내역 Listview 설정
        showHistoryListFunc(ALL);

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

        dateCompare=new DateCompare();
        return view;
    }


    //segmentButtonGroup 버튼 클릭 이벤트(position)별, 추후 구현 예정
    public void segmentButtonClickEvent(){
        segmentedButtonGroup.setOnClickedButtonListener(new SegmentedButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(int position) {
                switch (position) {
                    case 0:
                        showHistoryListFunc(ALL);
                        break;
                    case 1:
                        showHistoryListFunc(INCOME);
                        break;
                    case 2:
                        showHistoryListFunc(EXPENDITURE);
                        break;
                }
            }
        });

        segmentedButtonGroup.setPosition(0, 0);
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
                firebaseFunction.logAllOutput(logForms -> {
                    logFormList=logForms;
                    Collections.sort(logFormList, dateCompare);
                    for(int i=0;i<logFormList.size();i++){

                        //지출 UID가 현재 로그인한 UID와 같다면
                        if(logFormList.get(i).getFrom().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                            adapter.addItem(R.drawable.ic_out,
                                    logFormList.get(i).getCategory(),
                                    logFormList.get(i).getMessage(),
                                    logFormList.get(i).getDate().replaceAll("/",".").substring(0,10),
                                    "- "+logFormList.get(i).getAmount(),
                                    logFormList.get(i).getDate().replaceAll("/","."));
                        }
                        //수입 UID가 현재 로그인한 UID와 같다면
                        else if(logFormList.get(i).getTo().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                            adapter.addItem(R.drawable.ic_in,
                                    logFormList.get(i).getCategory(),
                                    logFormList.get(i).getMessage(),
                                    logFormList.get(i).getDate().replaceAll("/",".").substring(0,10),
                                    "+ "+logFormList.get(i).getAmount(),
                                    logFormList.get(i).getDate().replaceAll("/","."));
                        }
                        adapter.notifyDataSetChanged();
                    }

                    return null;
                });

                break;
            case INCOME:
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



    public String getPath(Uri uri){     //사진 경로받기
        String[]proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(getActivity(),uri,proj,null,null,null);

        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(index);
    }

    private void showGallery() {        //갤러리 띄우기
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, 200);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {        //사진 클릭시 화면에 보이기
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            bookImg.setImageURI(selectedImageUri);
        }
    }

    private void localUpload(String bookName, String major, String bookWriter) {         //업로드
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFunction firebaseTest = new FirebaseFunction();

        firebaseTest.profileGet(memberInfos, (resultList) -> {
            Log.d("home에서 확인",resultList.get(0).getName());
            Uri file = Uri.fromFile(new File(getPath(selectedImageUri)));
            final StorageReference riversRef = storageRef.child("bookSave/" + bookName + "_" + resultList.get(0).getName());
            UploadTask uploadTask = riversRef.putFile(file);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return riversRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        pictureLink = downloadUri.toString();
                        //firebaseFunction.insertBookInfo(pictureLink ,major,bookName, bookWriter,"지갑정보", resultList.get(0).getName());

                    }
                }
            });
            return null;
        });
    }

}