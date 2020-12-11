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

    /**
     * 등록할 책의 바코드 인식시 자동으로 바코드, 책이름, 사진, 책 저자 데이터가 입력됩니다.
     * @param barcode 등록할 책의 바코드 정보 (String)
     * @param bookName 등록할 책의 제목 정보 (String)
     * @param pictureLink 등록할 책의 사진 URI(String)
     * @param bookWriter 등록할 책의 저자 이름(String)
     * @param major 등록할 책의 학과 정보(String)
     * @param registerDate 등록한 날짜 정보(String)
     * @param rentCount 빌린횟수(등록시 0으로 초기화)
     */
    public static void insertBookInfo2(String barcode, String bookName, String pictureLink, String bookWriter, String major,
                                       String registerDate, int rentCount){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        BookSaveForm booksave = new BookSaveForm(pictureLink, major, bookName, bookWriter, barcode);
        BookDateSaveForm bookDateSaveForm = new BookDateSaveForm(registerDate, rentCount, "a");

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

    /**
     * 반납 시 user의 개인정보 안 빌린책 목록에서 책을 삭제합니다.
     * @param barcode 반납할 책의 바코드 정보(String)
     * @param complete
     * @param failed
     */
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


    /**
     * 가장 대여횟수가 낮고 대여중이 아닌 책 주인의 uuid 가져오기
     * @param barcode 자신이 대여를 원하는 책의 바코드 정보(String)
     * @param complete
     */
    public void getRentAvailableBookOwnerUID(String barcode, Function<String, Void> complete){
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

    /**
     * 토큰 수입/지출 정보를 파이어베이스 DB에 저장합니다.
     * @param from 지출이 발생한 UUID정보
     * @param to 수입이 발생한 UUID정보
     * @param message 
     * @param category 토큰거래 항목 (ex.도서거래, 티켓구매 등등...)
     * @param amount 거래한 토큰의 양
     */
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

    /**
     * 로그정보를 파이어베이스에서 리스트 형식으로 받아옵니다. (from)
     * @param complete
     */
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

    /**
     * 로그정보를 파이어베이스에서 받아옵니다. (to)
     * @param complete
     */
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
     * 로그정보를 파이어베이스에서 받아옵니다. (From/To 합본)
     * @param complete
     */
    public void logAllOutput(Function<List<LogForm>, Void> complete)
    {
        int saveSize = 0;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final List<LogForm> logFormList = new ArrayList<>();
        final ArrayList<Map<String, Object>> logSaveInit = new ArrayList<Map<String, Object>>();
        final ArrayList<Map<String, Object>> logSaveInit2 = new ArrayList<Map<String, Object>>();

        db.collection("Log/")
                .whereEqualTo("from", user.getUid()) // 필터링 조건은 변경가능합니다.
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
                                //Log.d("로그태스트", logFormList.get(0).getFrom());
                            }
                            //complete.apply(logFormList);
                        } else {
                        }
                    }
                });

        db.collection("Log/")
                .whereEqualTo("to" , user.getUid()) // 필터링 조건은 변경가능합니다.
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                logSaveInit2.add(document.getData());
                            }
                            for (int i=0;i<logSaveInit2.size();i++) {
                                LogForm logSaveFormProto = new LogForm(
                                        (String)logSaveInit2.get(i).get("from"),
                                        (String)logSaveInit2.get(i).get("to"),
                                        (String)logSaveInit2.get(i).get("message"),
                                        (String)logSaveInit2.get(i).get("category"),
                                        (String)logSaveInit2.get(i).get("message"),
                                        (String)logSaveInit2.get(i).get("date")
                                );
                                logFormList.add(logSaveFormProto);
                            }
                            Log.d("로그태스트", logFormList.get(0).getTo());
                            Log.d("로그태스트", logFormList.get(0).getFrom());
                            Log.d("로그태스트", logFormList.get(1).getTo());
                            Log.d("로그태스트", logFormList.get(1).getFrom());
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

    /**
     * ticket구매시 user의 보유티켓개수가 증가합니다.
     * @param ticket 구매한 티켓개수를 입력하면 입력데이터만큼 보유티켓수 증가
     */
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


    /**
     * 대여하려는 책의 바코드를 입력하면 대여가능한 책의 권수를 나타냅니다.
     * 결과값이 0이면 대여불가
     * @param barcode 대여하려는 책의 바코드 정보(String)
     * @param complete
     */
    public void getRentAvailableBookAmountByBarcode(String barcode, Function<Integer, Void> complete){
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

    /**
     * UUID를 통해서 유저가 토큰을 보낼 유저의 지갑정보를 받아옵니다.
     * @param uuid 토큰을 받을 사람의 UUID정보 (String)
     * @param complete
     */
    public void getUserWalletByUID(String uuid, Function<String, Void> complete) { //회원정보 받아오기
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


    /**
     * 대여하기 버튼 클릭했을시 대여자 필드 변경
     * @param barcode 대여하려는 책의 바코드정보(String)
     * @param isRentAllowed 대여신청일 경우 False, 대여승인일 경우 True
     */
    public void updateRentMember(String barcode, Boolean isRentAllowed){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        getRentAvailableBookOwnerUID(barcode, (result) -> {
            final DocumentReference sfDocRef = db.collection("bookSave/" + barcode + "/RegisteredUsers/").document(result);
            db.runTransaction(new Transaction.Function<Void>() {
                @Override
                public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot snapshot = transaction.get(sfDocRef);
//                     Log.d("tagaagag", snapshot.getString("rentCount"));

                    Long newRentCount = (snapshot.getLong("rentCount")) + 1;
                    transaction.update(sfDocRef, "rentCount", newRentCount);
                    if (isRentAllowed) {
                        transaction.update(sfDocRef, "rentedMember", user.getUid());
                    }
                    else {
                        transaction.update(sfDocRef, "rentedMember", user.getUid()+"(reserve)");
                    }
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

    /**
     * 유저가 등록한 책을 user개인정보에 저장합니다.
     * @param barcode 등록하려는 책의 바코드 정보(String)
     * @param bookName 등록하려는 책의 제목(String)
     * @param bookWriter 등록하려는 책의 저자(String)
     */
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

    /**
     * 유저가 대여한 책을 user개인정보에 저장합니다.
     * @param barcode 대여한 책의 바코드 정보(String)
     * @param bookName 대여한 책의 제목(String)
     * @param bookWriter 대여한 책의 저자(String)
     * @param status 대여시 책의 상태를 변경합니다. (ex. 대여중..)
     * @param uuid 책 주인의 UUID를 넣습니다. (String)
     */
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


    /**
     * 현재 로그인된 유저가 등록한 책에 관한 정보를 받아옵니다.
     * @param complete
     */
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

    /**
     * 유저가 대여한 책에 관한 정보를 받아옵니다.
     * @param userUID 대여책 정보를 가져올 User UID
     * @param complete
     */
    public void myRentedBookListGet(String userUID, Function<List<UserRentedBookSaveForm>, Void> complete) { //모든 책 정보 받아오기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final List<UserRentedBookSaveForm> bookSaveList = new ArrayList<>();
        db.collection("users/"+ userUID + "/RentedBook")
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

    /**
     * 입력한 전공데이터로 필터링하여 등록된 책 리스트(BookSaveForm) 형식으로 반환합니다.
     * @param word 전공데이터를 입력받습니다.
     * @param complete
     */
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

    /**
     * 현재 로그인한 유저의 정보를 받아옵니다.
     * @param memberInfoList MemberInfo 형식으로 받아옵니다.
     * @param complete
     */
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
     * 회원가입시 유저정보를 DB에 등록합니다.
     * @param name 유저의 이름
     * @param phoneNumber 유저의 전화번호
     * @param walletAdress 유저의 지갑정보(자동으로 입력됩니다.)
     * @param ticket 유저의 티켓정보 (0으로 초기화)
     * @param profileLink 유저의 프로필사진 URI
     */
    public static void profileUpdate(String name, String phoneNumber, String walletAdress, int ticket, String profileLink) {
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

    /**
     * 파이어베이스에서 저장된 프로필 이미지 가져오기
     * @param home_profile_image_button 프로필이미지를 출력할 공간.
     * @param fragment 버튼이 있는 프래그먼트.
     */
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


    /**
     * 파이어베이스에서 저장된 책 이미지 가져오기.
     * @param book_image_button 책 이미지를 출력할 공간.
     * @param fragment 버튼이 있는 프래그먼트.
     * @param bookName 책 이름.
     * @param userName 책을 등록한 유저이름.
     */
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

    /**
     * Glide 기능을 사용합니다.
     * @param uri 이미지 URI
     * @param image_button 이미지 출력될 공간.
     * @param fragment 버튼이 있는 프래그먼트.
     */
    public void glideUtility(Uri uri,ImageButton image_button, Context fragment){
        Glide.with(fragment)
                .load(uri)
                .circleCrop()
                .override(192)
                .placeholder(R.drawable.cotton_icon)
                .into(image_button); //이미지 버튼 아이디가 들어간다.
    }

    /**
     * 현재 로그인 된 유저의 UUID를 반환합니다.
     * @return
     */
    public String getMyUID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user.getUid();
    }
}
