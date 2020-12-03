package com.example.cotton;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

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

import java.util.ArrayList;
import java.util.Map;

public class firebaseFunction {

    public void insertBookInfo(String pictureLink, String major, String bookName, String bookWriter){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        bookSaveForm booksave = new bookSaveForm(pictureLink, major, bookName, bookWriter);

        db.collection("bookSave/").document(bookName).set(booksave)
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

    public void serchBook(String word){
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

    public void profileGet() {
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
                        diaryM.add(document.getData());
                        MemberInfo memberInfo = new MemberInfo((String)diaryM.get(0).get("name"), (String)diaryM.get(0).get("phoneNumber"), (String)diaryM.get(0).get("wallet"),4,(String)diaryM.get(0).get("profileLink"));
                        Log.d("ffffffffffffffffffffff",memberInfo.getName());

                    } else {
                        Log.d("fuck", "No such document");
                    }
                } else {
                    Log.d("shit", "get failed with ", task.getException());
                }
            }
        });
    }


}
