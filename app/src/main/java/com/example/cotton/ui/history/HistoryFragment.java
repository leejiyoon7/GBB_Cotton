package com.example.cotton.ui.history;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;

import com.example.cotton.LoginActivity;
import com.example.cotton.MainActivity;
import com.example.cotton.MemberInfo;
import com.example.cotton.R;
import com.example.cotton.firebaseFunction;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;

import static android.app.Activity.RESULT_OK;

public class HistoryFragment extends Fragment {

    EditText history_search_editText;//거래내역 검색 edittext
    SegmentedButtonGroup segmentedButtonGroup;//segmentButtonGroup 생성

    String pictureLink;
    private static final String TAG_TEXT = "text";
    Uri selectedImageUri;
    Button test_btn;
    ImageView bookImg;
    List<MemberInfo> memberInfos = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

//        history_search_editText=view.findViewById(R.id.history_search_editText);
        segmentedButtonGroup = (SegmentedButtonGroup)view.findViewById(R.id.segmentedButtonGroup);

        //editText 부분 검색함에 따라 실시간으로 변화있게 구현 예정
//        historySearchEditTextEvent();

        //segmentButtonGroup 버튼 클릭 이벤트(position)별, 추후 구현 예정
        segmentButtonClickEvent();

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

//    //editText 검색 이벤트
//    public void historySearchEditTextEvent(){
//        history_search_editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
//                //Toast.makeText(getActivity(), history_search_editText.getText()+" 검색", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
//                //Toast.makeText(getActivity(), history_search_editText.getText()+" 검색", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                //Toast.makeText(getActivity(), history_search_editText.getText()+" 검색", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    //segmentButtonGroup 버튼 클릭 이벤트(position)별, 추후 구현 예정
    public void segmentButtonClickEvent(){
        segmentedButtonGroup.setOnClickedButtonListener(new SegmentedButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(int position) {
                switch (position) {
                    case 0:
                        Toast.makeText(getActivity(), " 전체 버튼 눌림", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(getActivity(), " 수입 버튼 눌림", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(getActivity(), " 지출 버튼 눌림", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        segmentedButtonGroup.setPosition(2, 0);
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
        firebaseFunction firebaseTest = new firebaseFunction();

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
                        firebaseFunction.insertBookInfo(pictureLink ,major,bookName, bookWriter,"지갑정보", resultList.get(0).getName());

                    }
                }
            });
            return null;
        });


    }


}