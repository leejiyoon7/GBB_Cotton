package com.example.cotton;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.loader.content.CursorLoader;

import com.bumptech.glide.Glide;
import com.example.cotton.ui.home.HomeFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;


public class firebaseFunction {
    File localFile;
    List<MemberInfo> memberInfoList = new ArrayList<>();
    List<MemberInfo> memberTest = new ArrayList<>();
    MemberInfo memberInfo;
    private Object HomeFragment;

    //책에 관한 정보 저장

    //region 책의 입력과 단어하나에 대한 검색
    /**
     *
     * @param pictureLink   사진링크
     * @param major         전공
     * @param bookName      책제목
     * @param bookWriter    책저자
     * @param walletInfo    지갑정보
     * @param userName      사용자이름
     */
    public static void insertBookInfo(String pictureLink, String major, String bookName, String bookWriter, String walletInfo,String userName) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        bookSaveForm booksave = new bookSaveForm(pictureLink, major, bookName, bookWriter, walletInfo, userName);

        db.collection("bookSave/").document(bookName + "_" + userName).set(booksave) // 책 저장하기
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        Log.d("testing", "성공");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void serchBook(String word) { //책 검색 , 하나밖에 검색안됨 / 저자별
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("bookSave")
                .whereEqualTo("bookWriter", word)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("testingFor", document.getId() + " => " + document.getData());
                            }
                        } else {

                        }
                    }
                });

    }
    //endregion


    public void profileGet(List<MemberInfo> memberInfoList, Function<List<MemberInfo>, Void> complete) { //회원정보 받아오기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final ArrayList<Map<String, Object>> diaryM = new ArrayList<Map<String, Object>>();

        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        diaryM.add(document.getData());     //arraylist에 모든정보를 받아와서 저장
                        memberInfo = new MemberInfo((String) diaryM.get(0).get("name"), (String) diaryM.get(0).get("phoneNumber"), (String) diaryM.get(0).get("wallet"), 4, (String) diaryM.get(0).get("profileLink")); // 모든 정보를 다시 memberinfo에 저장
                        memberInfoList.add(0, memberInfo);  //리스트형식 첫번째 칸에 memberinfo 저장
                        Log.d("ffffffffffffffffffffff", memberInfoList.get(0).getName());
                        complete.apply(memberInfoList);
                    } else {

                    }
                } else {

                }
            }
        });


    }

    /**
     * 모든책 받아오기 페이지
     * List<bookSaveForm> bookSaveFormList= new ArrayList<>(); 이렇게 전역변수로 선언하나 해주고
     * (resultList) -> {}
     * @param bookSaveFormList
     * @param complete
     */
    public void bookListGet(List<bookSaveForm> bookSaveFormList, Function<List<bookSaveForm>, Void> complete) { //모든 책 정보 받아오기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final ArrayList<Map<String, Object>> bookSaveInit = new ArrayList<Map<String, Object>>();
        final List<bookSaveForm> bookSaveList = new ArrayList<>();
        db.collection("bookSave")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                bookSaveInit.add(document.getData());
                            }
                            for (int i=0;i<bookSaveInit.size();i++) {
                                bookSaveForm bookSaveFormProto = new bookSaveForm((String)bookSaveInit.get(i).get("pictureLink"),(String)bookSaveInit.get(i).get("major"),(String)bookSaveInit.get(i).get("bookName"),(String)bookSaveInit.get(i).get("bookWriter"),(String)bookSaveInit.get(i).get("walletInfo"),(String)bookSaveInit.get(i).get("userName"));
                                bookSaveList.add(bookSaveFormProto);
                            }
                            complete.apply(bookSaveList);
                        } else {

                        }
                    }
                });

    }




    public static void profileUpdate(String name, String phoneNumber, String walletAdress, int ticket, String profileLink) {
        // 프로필 올리기
        ticket = 0;
        if(name.length()>0 && phoneNumber.length() > 9) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            // Access a Cloud Firestore instance from your Activity
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            MemberInfo memberInfo = new MemberInfo(name, phoneNumber, walletAdress, ticket, profileLink);
            db.collection("users").document(user.getUid()).set(memberInfo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

        }else{

        }

    }

    // 파이어베이스에서 저장된 프로필 이미지 가져오기.
    public void profileImageDownload(ImageButton home_profile_image_button, Context fragment)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        storageRef.child("users/" + user.getUid() +"/" + "Profile Image").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //이미지를 불러오는데 성공
                //Glide를 사용
                glideUtility(uri, home_profile_image_button, fragment);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    //파이어베이스에서 저장된 책 이미지 가져오기.
    public void bookImageDownload(ImageButton book_image_button, Context fragment, String bookName, String userName)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
       // 먼저 bookListGet으로 모든 정보를 받아온뒤 필터링을 걸어서 해당 필드의 책 이름과 사용자이름 추출해서 변수 두개에 넣으면 작동
        storageRef.child("bookSave/" + bookName +"_" + userName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //이미지를 불러오는데 성공
                //Glide를 사용
                glideUtility(uri, book_image_button, fragment);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void glideUtility(Uri uri,ImageButton image_button, Context fragment){
        Glide.with(fragment)
                .load(uri)
                .circleCrop()
                .override(130)
                .into(image_button); //이미지 버튼 아이디가 들어간다.

    }
}
