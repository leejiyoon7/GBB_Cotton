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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
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
     */

/*
    public static void insertBookInfo(String pictureLink, String major, String bookName, String bookWriter, String walletInfo,String userName) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        bookSaveForm booksave = new bookSaveForm(pictureLink, major, bookName, bookWriter, userName);

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
*/

    // New DB Structure
    public static void insertBookInfo2(String barcode, String bookName, String pictureLink, String bookWriter, String major,
                                       String registerDate, int rentCount){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        bookSaveForm booksave = new bookSaveForm(pictureLink, major, bookName, bookWriter);
        //bookSaveForm booksave = new bookSaveForm("pictureLink", "major", "bookName", "bookWriter");
        BookDateSaveForm bookDateSaveForm = new BookDateSaveForm(registerDate, rentCount, "a");
        //BookDateSaveForm bookDateSaveForm = new BookDateSaveForm("registerDate", 10);

        db.collection("bookSave/").document(barcode).set(booksave) // 책 정보 (북네임, 이미지, 저자, 학과) 저장
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

        db.collection("bookSave/").document(barcode).collection("RegisteredUsers/")
                .document(user.getUid()).set(bookDateSaveForm) // 책을 등록한 날짜, Rent 횟수 저장
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

    //가장 대여횟수가 낮고 대여중이 아닌 책 주인의 uuid 가져오기
    public void getUuid(String barcode, Function<String, Void> complete){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uuid = "test";
        db.collection("bookSave/").document(barcode).collection("RegisteredUsers/")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("testtetetet", document.getId());
                                complete.apply(document.getId());
                            }
                        } else {

                        }
                    }
                });
    }


    //대여하기 버튼 클릭했을시 대여자 필드 변경
    public void updateRentMember(String barcode, String name){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        getUuid(barcode, (result) -> {
             final DocumentReference sfDocRef = db.collection("bookSave/").document(barcode).collection("RegisteredUsers/").document(result);
             db.runTransaction(new Transaction.Function<Void>() {
                 @Override
                 public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                     DocumentSnapshot snapshot = transaction.get(sfDocRef);
                     int newRentCount = Integer.parseInt(snapshot.getString("rentCount")) + 1;
                     transaction.update(sfDocRef, "rentCount", newRentCount);
                     transaction.update(sfDocRef, "rentedMember", name);
                     return null;
                 }
             }).addOnSuccessListener(new OnSuccessListener<Void>() {
                 @Override
                 public void onSuccess(Void aVoid) {
                     Log.d("", "Transaction success!");
                 }
             })
                     .addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                             Log.w("", "Transaction failure.", e);
                         }
                     });

             return null;
         });

    }



    // 유저가 등록한 책을 user개인정보에 저장합니다.
    public void insertRegisteredBookInfoToUser(String barcode, String bookName, String bookWriter)
    {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        UserRegisteredBookSaveForm userRegisteredBookSaveForm = new UserRegisteredBookSaveForm(bookName, bookWriter);
        db.collection("users/" + user.getUid() + "/RegisteredBook/").document(barcode).set(userRegisteredBookSaveForm)
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



    // 현재 로그인된 유저가 등록한 책에 관한 정보를 받아옵니다.
    public void myRegisteredBookListGet(Function<List<UserRegisteredBookSaveForm>, Void> complete) { //모든 책 정보 받아오기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final ArrayList<Map<String, Object>> bookSaveInit = new ArrayList<Map<String, Object>>();
        final List<UserRegisteredBookSaveForm> bookSaveList = new ArrayList<>();
        db.collection("users/"+user.getUid() + "/RegisteredBook")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("testtetetet", document.getId());
                                bookSaveInit.add(document.getData());
                            }
                            for (int i=0;i<bookSaveInit.size();i++) {
                                UserRegisteredBookSaveForm bookSaveFormProto = new UserRegisteredBookSaveForm(
                                        (String)bookSaveInit.get(i).get("bookName"),
                                        (String)bookSaveInit.get(i).get("bookWriter"));
                                bookSaveList.add(bookSaveFormProto);
                                Log.d("TTTTTTTT",(String)bookSaveInit.get(i).get("bookName") );
                            }
                            complete.apply(bookSaveList);

                        } else {

                        }
                    }
                });

    }

    // 현재 로그인된 유저가 빌린 책에 관한 정보를 받아옵니다.
    public void myRentedBookListGet(Function<List<UserRentedBookSaveForm>, Void> complete) { //모든 책 정보 받아오기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final ArrayList<Map<String, Object>> bookSaveInit = new ArrayList<Map<String, Object>>();
        final List<UserRentedBookSaveForm> bookSaveList = new ArrayList<>();
        db.collection("users/"+user.getUid() + "/RentedBook")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                bookSaveInit.add(document.getData());
                            }
                            for (int i=0;i<bookSaveInit.size();i++) {
                                UserRentedBookSaveForm bookSaveFormProto = new UserRentedBookSaveForm(
                                        (String)bookSaveInit.get(i).get("bookName"),
                                        (String)bookSaveInit.get(i).get("bookWriter"),
                                        (String)bookSaveInit.get(i).get("status"));
                                bookSaveList.add(bookSaveFormProto);
                                Log.d("TTTTTTTT",(String)bookSaveInit.get(i).get("bookName") );
                            }
                            complete.apply(bookSaveList);
                        } else {

                        }
                    }
                });

    }

    public void searchBook(String word, Function<List<bookSaveForm>, Void> complete) { // 전공별로 가져와서 리스트에 저장할꺼임
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final ArrayList<Map<String, Object>> bookSaveInit = new ArrayList<Map<String, Object>>();
        final List<bookSaveForm> bookSaveList = new ArrayList<>();
        db.collection("bookSave")
                .whereEqualTo("major", word)
                .whereEqualTo("rentedMember" , "a")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                bookSaveInit.add(document.getData());
                            }
                            for (int i=0;i<bookSaveInit.size();i++) {
                                bookSaveForm bookSaveFormProto = new bookSaveForm((String)bookSaveInit.get(i).get("pictureLink"),
                                        (String)bookSaveInit.get(i).get("major"),
                                        (String)bookSaveInit.get(i).get("bookName"),
                                        (String)bookSaveInit.get(i).get("bookWriter"));
                                bookSaveList.add(bookSaveFormProto);

                            }
                            complete.apply(bookSaveList);
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
                        memberInfo = new MemberInfo((String) diaryM.get(0).get("name"),
                                (String) diaryM.get(0).get("phoneNumber"),
                                (String) diaryM.get(0).get("wallet"),
                                (Long) diaryM.get(0).get("ticket"),
                                (String) diaryM.get(0).get("profileLink")); // 모든 정보를 다시 memberinfo에 저장
                        memberInfoList.add(0, memberInfo);  //리스트형식 첫번째 칸에 memberinfo 저장
                        Log.d("사용자 이름", memberInfoList.get(0).getName());
                        Log.d("사용자 지갑주소", memberInfoList.get(0).getWallet());
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
     */
    /*
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
                                bookSaveForm bookSaveFormProto = new bookSaveForm((String)bookSaveInit.get(i).get("pictureLink"),
                                        (String)bookSaveInit.get(i).get("major"),
                                        (String)bookSaveInit.get(i).get("bookName"),
                                        (String)bookSaveInit.get(i).get("bookWriter"),
                                        (String)bookSaveInit.get(i).get("userName"));
                                bookSaveList.add(bookSaveFormProto);
                            }
                            complete.apply(bookSaveList);
                        } else {

                        }
                    }
                });

    }
*/



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
                .override(192)
                .placeholder(R.drawable.cotton_icon)
                .into(image_button); //이미지 버튼 아이디가 들어간다.
    }


}
