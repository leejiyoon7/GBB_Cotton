package com.example.cotton.ui.home.register;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.content.CursorLoader;

import com.example.cotton.BarCodeService;
import com.example.cotton.LoginActivity;
import com.example.cotton.MainActivity;
import com.example.cotton.MemberInfo;
import com.example.cotton.R;
import com.example.cotton.bookSaveForm;
import com.example.cotton.firebaseFunction;
import com.example.cotton.ui.home.HomeFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;



public class RegisterBookActivity extends Activity {



    HomeFragment homeFragment;//이동할 homefragment
    AppCompatButton register_book_go_to_home_button;//Homefragment 이동 버튼
    ImageButton register_book_image_Button;//도서등록 사진추가 이미지뷰
    Spinner register_book_card_department_spinner;//학과 스피너
    EditText register_book_card_book_title_edit_text;//도서 제목 editText
    EditText register_book_card_book_writer_edit_text;//도서 저자 editText
    AppCompatButton register_book_app_compat_button;//등록하기 버튼

    ArrayAdapter spinnerAdapter;//스피너 어댑터

    String major;//전공 value

    List<MemberInfo> getMemberName= new ArrayList<>();
    Uri selectedImageUri;
    String bookImageLink;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_book);

        homeFragment=new HomeFragment();
        register_book_go_to_home_button=findViewById(R.id.register_book_go_to_home_button);
        register_book_image_Button=findViewById(R.id.register_book_image_Button);
        register_book_card_department_spinner=findViewById(R.id.register_book_card_department_spinner);
        register_book_card_book_title_edit_text=(EditText)findViewById(R.id.register_book_card_book_title_edit_text);
        register_book_card_book_writer_edit_text=(EditText)findViewById(R.id.register_book_card_book_writer_edit_text);
        register_book_app_compat_button=findViewById(R.id.register_book_app_compat_button);

        goToHome();//MainActivity 이동 이벤트 method

        registerBookImage();//사진 등록 method

        majorPickSpinner();//전공 선택 스피너

        registerBook();//도서 등록 버튼 클릭 이벤트 method




    }
    
    //MainActivity 이동 이벤트 method
    public void goToHome(){
        register_book_go_to_home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RegisterBookActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    //사진 등록 method, 추후 구현 예정
    public void registerBookImage(){
        register_book_image_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RegisterBookActivity.this,"사진추가 클릭 이벤트 발생",Toast.LENGTH_SHORT).show();
                showGallery();
            }
        });
    }

    //spinner 구현 method
    public void majorPickSpinner(){
        String[] items=getResources().getStringArray(R.array.major);
        spinnerAdapter= new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,items);
        register_book_card_department_spinner.setAdapter(spinnerAdapter);

        register_book_card_department_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                switch(position){
                    case 0:
                        Toast.makeText(RegisterBookActivity.this,"컴퓨터공학과 선택",Toast.LENGTH_SHORT).show();
                        major="컴퓨터공학과";
                        break;
                    case 1:
                        Toast.makeText(RegisterBookActivity.this,"전자공학과 선택",Toast.LENGTH_SHORT).show();
                        major="전자공학과";
                        break;
                    case 2:
                        Toast.makeText(RegisterBookActivity.this,"전기공학과 선택",Toast.LENGTH_SHORT).show();
                        major="전기공학과";
                        break;
                    case 3:
                        Toast.makeText(RegisterBookActivity.this,"AI.소프트웨어학부 선택",Toast.LENGTH_SHORT).show();
                        major="AI.소프트웨어학부";
                        break;
                    case 4:
                        Toast.makeText(RegisterBookActivity.this,"에너지IT학과",Toast.LENGTH_SHORT).show();
                        major="에너지IT학과";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(RegisterBookActivity.this,"아무것도 선택되지 않음",Toast.LENGTH_SHORT).show();
            }
        });
    }

    //등록하기 버튼 클릭 event, firebase에 등록하도록 추후에 코딩할 예정
    public void registerBook(){

        register_book_app_compat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(register_book_card_book_title_edit_text.getText().length()!=0 && register_book_card_book_writer_edit_text.getText().length()!=0){
                    Toast.makeText(RegisterBookActivity.this,"전공: "+major+" 도서 제목: "+register_book_card_book_title_edit_text.getText().toString()+" 도서 저자: "+register_book_card_book_writer_edit_text.getText().toString(),Toast.LENGTH_SHORT).show();
                    localUpoad();
                    Intent intent=new Intent(RegisterBookActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(RegisterBookActivity.this,"정보를 제대로 입력해 주십시오.",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    //안드로이드 백버튼 막기
    public void onBackPressed() {
        Intent intent=new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }




    private void showGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, 200);
    }

    public String getPath(Uri uri){
        String[]proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this,uri,proj,null,null,null);

        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(index);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            register_book_image_Button.setImageURI(selectedImageUri);

            // 바코드 인식 실험코드
            FirebaseVisionImage image = null;
            try {
                image = FirebaseVisionImage.fromFilePath(this, selectedImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            BarCodeService barCodeService = new BarCodeService();
            FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
                    .getVisionBarcodeDetector(barCodeService.getOptions());

            Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)
                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                            for (FirebaseVisionBarcode barcode: barcodes) {
                                Log.d("Barcode Result : ", "Success(" + barcode.getRawValue() + ")");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Barcode Result : ", "Barcode Fail ");
                        }
                    });
        }
    }
    private void localUpoad() {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Uri file = Uri.fromFile(new File(getPath(selectedImageUri)));
        firebaseFunction firebaseInput = new firebaseFunction();
        firebaseInput.profileGet(getMemberName, (resultList) -> { // 모든 책정보 가져오기 / for문을 size로 돌리면 모든 책정보 가져올수 있음
            final StorageReference riversRef = storageRef.child("bookSave/" + register_book_card_book_title_edit_text.getText().toString() + "_" + resultList.get(0).getName());
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
                        bookImageLink = downloadUri.toString();
                        firebaseFunction firebaseInput = new firebaseFunction();
                        firebaseInput.profileGet(getMemberName, (resultList) -> { // 모든 책정보 가져오기 / for문을 size로 돌리면 모든 책정보 가져올수 있음
                            Log.d("home에서 확인",resultList.get(0).getName());
                            firebaseInput.insertBookInfo("사진링크",major,register_book_card_book_title_edit_text.getText().toString(),register_book_card_book_writer_edit_text.getText().toString(),"지갑정보",resultList.get(0).getName());
                            return null;
                        });
                        finish();
                    }
                }
            });

            return null;
        });

    }
}
