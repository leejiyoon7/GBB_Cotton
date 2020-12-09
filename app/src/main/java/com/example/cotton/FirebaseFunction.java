package com.example.cotton;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


public class FirebaseFunction {
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

        BookSaveForm booksave = new BookSaveForm(pictureLink, major, bookName, bookWriter, barcode);
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

    //반납 시 user의 개인정보 안 빌린책 목록에서 책을 삭제합니다.
    public void deleteRentedBook(String barcode, Function<Void, Void> complete, Function<Void, Void> failed){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users/").document(user.getUid()).collection("RentedBook/")
                .document(barcode)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("deleteBook", "user정보의 Rentedbook에서 삭제 성공");
                        complete.apply(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("deleteBook", "user정보의 Rentedbook에서 삭제 실패");
                        failed.apply(null);
                    }
                });
    }


    //가장 대여횟수가 낮고 대여중이 아닌 책 주인의 uuid 가져오기
    public void getUuid(String barcode, Function<String, Void> complete){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("bookSave/").document(barcode).collection("RegisteredUsers/")
                .limit(1)
                .orderBy("rentCount")
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

    //로그정보를 파이어베이스에 저장합니다.
    public void logInput(String from, String to, String message, String category, String amount){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        long now = System.currentTimeMillis();
        Date dateNow = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String formatDate = sdfNow.format(dateNow);

        LogForm logForm = new LogForm(from, to,message,category,amount, formatDate);

        db.collection("Log/").document().set(logForm)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        Log.d("Log testing", "성공!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    //로그정보를 파이어베이스에서 받아옵니다. (from)
    public void logFromOutput(Function<List<LogForm>, Void> complete)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final List<LogForm> logFormList = new ArrayList<>();
        final ArrayList<Map<String, Object>> logSaveInit = new ArrayList<Map<String, Object>>();
        db.collection("Log/")
                .whereEqualTo("from", user.getUid()) // 필터링 조건은 변경가능합니다.
                //.whereEqualTo("to" , user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                logSaveInit.add(document.getData());
                            }
                            for (int i=0;i<logSaveInit.size();i++) {
                                LogForm logSaveFormProto = new LogForm(
                                        (String)logSaveInit.get(i).get("from"),
                                        (String)logSaveInit.get(i).get("to"),
                                        (String)logSaveInit.get(i).get("message"),
                                        (String)logSaveInit.get(i).get("category"),
                                        (String)logSaveInit.get(i).get("message"),
                                        (String)logSaveInit.get(i).get("date")
                                );
                                logFormList.add(logSaveFormProto);
                            }
                            complete.apply(logFormList);
                        } else {

                        }
                    }
                });
    }

    //로그정보를 파이어베이스에서 받아옵니다. (to)
    public void logToOutput(Function<List<LogForm>, Void> complete)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final List<LogForm> logFormList = new ArrayList<>();
        final ArrayList<Map<String, Object>> logSaveInit = new ArrayList<Map<String, Object>>();
        db.collection("Log/")
                .whereEqualTo("to" , user.getUid()) // 필터링 조건은 변경가능합니다.
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                logSaveInit.add(document.getData());
                            }
                            for (int i=0;i<logSaveInit.size();i++) {
                                LogForm logSaveFormProto = new LogForm(
                                        (String)logSaveInit.get(i).get("from"),
                                        (String)logSaveInit.get(i).get("to"),
                                        (String)logSaveInit.get(i).get("message"),
                                        (String)logSaveInit.get(i).get("category"),
                                        (String)logSaveInit.get(i).get("message"),
                                        (String)logSaveInit.get(i).get("date")
                                );
                                logFormList.add(logSaveFormProto);
                            }
                            complete.apply(logFormList);
                        } else {

                        }
                    }
                });
    }

    /**
     * 바코드와 나의 UID를 바탕으로 내가 빌린 책 주인의 UID를 가져온다.
     * @param barcode : 내가 빌린 책의 바코드
     * @param borrowerUID : 대여자의 UID
     * @param complete : 완료 시 후속 작업.
     */
    public void getMyRentedBook(String barcode, String borrowerUID, Function<String, Void> complete){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("bookSave/"+barcode+"/RegisteredUsers")
                .whereEqualTo("rentedMember", borrowerUID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getId();
                                complete.apply(document.getId());       //내가 빌린책의 바코드와 내이름으로 빌려준사람 uuid 를 받아옴
                            }
                        } else {

                        }
                    }
                });
    }


    /**
     * 바코드와 나의 UID를 바탕으로 책 주인의 UID를 가져오고,
     * 이를 통해서 빌린 책을 반환 상태로 변경.
     * @param barcode : 내가 빌린 책의 바코드
     * @param borrowerUID : 대여자의 UID
     * @param complete : 완료 시 후속 작업.
     */
    public void returnBook(String barcode, String borrowerUID, Function<Void, Void> complete){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        getMyRentedBook(barcode, borrowerUID, (result)->{
            DocumentReference washingtonRef = db.collection("bookSave/"+barcode+"/RegisteredUsers").document(result);
            washingtonRef
                    .update("rentedMember", "a")
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            complete.apply(null);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
            return null;
        });
    }



    /**
     * 내가 빌린책이 반납 완료 되었는지 체크 후
     * 반납이 완료되었을 경우, 나의 대여목록에서 해당 책 삭제.
     * 반납이 안된 경우, 다시 실행 메시지 표시.
     * @param barcode : 반납 체크할 책의 바코드
     * @param bookOwnerUid : 반납 체크할 책 주인의 UID
     * @param bookNotReturnedMsg : 반납이 완료 되지 않았을 때 메시지.
     * @param complete : 삭제 완료 메시지.
     * @param failed : 삭제 실패 메시지.
     */
    public void deleteRentedBookIfReturnedSuccessfully(String barcode,
                                                       String bookOwnerUid,
                                                       Function<Void, Void> bookNotReturnedMsg,
                                                       Function<Void, Void> complete,
                                                       Function<Void, Void> failed) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.document("bookSave/" + barcode + "/RegisteredUsers/" + bookOwnerUid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String rentedMember = (String) document.get("rentedMember");
                                if (!rentedMember.equals(getMyUID())) {
                                    deleteRentedBook(barcode, complete, failed);
                                }
                                else {
                                    bookNotReturnedMsg.apply(null);
                                }
                            }
                        }
                    }
                });
    }

    //ticket구매시 user의 보유티켓개수가 증가합니다.
    public void raiseMyTicketCount(int ticket)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference userDocRef = db.collection("users/").document(user.getUid());
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(userDocRef);
                Long newTicketCount = (snapshot.getLong("ticket")) + ticket;
                transaction.update(userDocRef, "ticket", newTicketCount);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("", "Ticket Up success!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("", "Ticket Up Failure.", e);
                    }
                });
    }


    public void countMember(String barcode, Function<Integer, Void> complete){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("bookSave/").document(barcode).collection("RegisteredUsers/")
                .whereEqualTo("rentedMember", "a")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count =0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("testtetetet", document.getId());
                                count += 1;

                            }
                            complete.apply(count);
                        } else {

                        }
                    }
                });

    }

    public void returnUuid(String uuid, Function<String, Void> complete) { //회원정보 받아오기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final ArrayList<Map<String, Object>> diaryM = new ArrayList<Map<String, Object>>();
        DocumentReference docRef = db.collection("users").document(uuid);
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
                        Log.d("userName", "" + diaryM.get(0).get("name"));
                        memberInfoList.add(0, memberInfo);  //리스트형식 첫번째 칸에 memberinfo 저장
                        Log.d("사용자 이름", memberInfoList.get(0).getName());
                        Log.d("사용자 지갑주소", memberInfoList.get(0).getWallet());
                        complete.apply(memberInfoList.get(0).getWallet());
                    } else {

                    }
                } else {

                }
            }
        });
    }


    //대여하기 버튼 클릭했을시 대여자 필드 변경
    public void updateRentMember(String barcode){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        getUuid(barcode, (result) -> {
            final DocumentReference sfDocRef = db.collection("bookSave/" + barcode + "/RegisteredUsers/").document(result);
            db.runTransaction(new Transaction.Function<Void>() {
                @Override
                public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot snapshot = transaction.get(sfDocRef);
//                     Log.d("tagaagag", snapshot.getString("rentCount"));

                    Long newRentCount = (snapshot.getLong("rentCount")) + 1;
                    transaction.update(sfDocRef, "rentCount", newRentCount);
                    transaction.update(sfDocRef, "rentedMember", user.getUid());
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

    //유저가 대여한 책을 user개인정보에 저장합니다.
    public void insertRentedBookInfoToUser(String barcode, String bookName, String bookWriter, String status, String uuid)
    {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        UserRentedBookSaveForm userRentedBookSaveForm = new UserRentedBookSaveForm(bookName, bookWriter, status, barcode, uuid);
        db.collection("users/" + user.getUid() + "/RentedBook/").document(barcode).set(userRentedBookSaveForm)
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
        final List<UserRentedBookSaveForm> bookSaveList = new ArrayList<>();
        db.collection("users/"+user.getUid() + "/RentedBook")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserRentedBookSaveForm bookSaveFormProto = new UserRentedBookSaveForm(

                                        (String)document.getData().get("bookName"),
                                        (String)document.getData().get("bookWriter"),
                                        (String)document.getData().get("status"),
                                        (String)document.getId(),
                                        (String)document.get("bookOwnerUUID"));

                                bookSaveList.add(bookSaveFormProto);
                            }

                            complete.apply(bookSaveList);
                        } else {
                            // do something
                        }
                    }
                });

    }

    public void searchBook(String word, Function<List<BookSaveForm>, Void> complete) { // 전공별로 가져와서 리스트에 저장할꺼임
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final ArrayList<Map<String, Object>> bookSaveInit = new ArrayList<Map<String, Object>>();
        final List<BookSaveForm> bookSaveList = new ArrayList<>();
        db.collection("bookSave")
                .whereEqualTo("major", word)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                bookSaveInit.add(document.getData());
                            }
                            for (int i=0;i<bookSaveInit.size();i++) {
                                BookSaveForm bookSaveFormProto = new BookSaveForm((String)bookSaveInit.get(i).get("pictureLink"),
                                        (String)bookSaveInit.get(i).get("major"),
                                        (String)bookSaveInit.get(i).get("bookName"),
                                        (String)bookSaveInit.get(i).get("bookWriter"),
                                        (String)bookSaveInit.get(i).get("barcode"));

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
                        Log.d("userName", "" + diaryM.get(0).get("name"));
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

    public String getMyUID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user.getUid();
    }

}
