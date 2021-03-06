package com.example.cotton.ui.home.register;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.example.cotton.Utils.ApiService;
import com.example.cotton.BarCodeService;
import com.example.cotton.MainActivity;
import com.example.cotton.MemberInfo;
import com.example.cotton.R;
import com.example.cotton.Utils.BaseUrlInterface;
import com.example.cotton.Utils.RetrofitClientXml;
import com.example.cotton.Utils.ImageLoadTask;
import com.example.cotton.ValueObject.BookSearchByBarcode.BookSearchResultVO;
import com.example.cotton.FirebaseFunction;
import com.example.cotton.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterBookActivity extends Activity {



    HomeFragment homeFragment;//이동할 homefragment
    AppCompatButton register_book_go_to_home_button;//Homefragment 이동 버튼
    ImageButton register_book_image_Button;//도서등록 사진추가 이미지뷰
    Spinner register_book_card_department_spinner;//학과 스피너
    TextView register_book_card_book_title_result_text_view;//도서 제목 editText
    TextView register_book_card_book_writer_result_text_view;//도서 저자 editText
    AppCompatButton register_book_app_compat_button;//등록하기 버튼

    ArrayAdapter spinnerAdapter;//스피너 어댑터

    String major;//전공 value

    List<MemberInfo> getMemberName= new ArrayList<>();
    Uri selectedImageUri;
    String bookImageLink;

    // Firebase에 넘길 HashTable
    HashMap<String, String> bookInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_book);

        homeFragment=new HomeFragment();
        register_book_go_to_home_button=findViewById(R.id.register_book_go_to_home_button);
        register_book_image_Button=findViewById(R.id.register_book_image_Button);
        register_book_card_department_spinner=findViewById(R.id.register_book_card_department_spinner);
        register_book_card_book_title_result_text_view =findViewById(R.id.register_book_card_book_title_result_text_view);
        register_book_card_book_writer_result_text_view =findViewById(R.id.register_book_card_book_writer_result_text_view);
        register_book_app_compat_button=findViewById(R.id.register_book_app_compat_button);
        bookInfo = new HashMap<String, String>();

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

    /**
     * Bottom Sheet Dialog를 통해서
     * 바코드를 인식할 사진을 카메라로 찍을지 앨범에서 가져올지 정함.
     */
    public void registerBookImage(){
        register_book_image_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(RegisterBookActivity.this);
                bottomSheetDialog.setContentView(R.layout.bottom_dialog_camera_or_album);

                TextView cameraTextView = bottomSheetDialog.findViewById(R.id.camera_or_album_camer);
                TextView albumTextView = bottomSheetDialog.findViewById(R.id.camera_or_album_album);
                TextView activeTextView = bottomSheetDialog.findViewById(R.id.camera_or_album_active);

                // 카메라 선택시
                cameraTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showCamera();
                        bottomSheetDialog.dismiss();
                    }
                });

                // 앨 선택시
                albumTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showGallery();
                        register_book_app_compat_button.setEnabled(false);
                        bottomSheetDialog.dismiss();
                    }
                });

                //수동 선택시
                activeTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BottomSheetDialog bottomDialog = new BottomSheetDialog(RegisterBookActivity.this);
                        bottomDialog.setContentView(R.layout.bottom_dialog_camera_or_album_active);

                        EditText book_name_edit_text=bottomDialog.findViewById(R.id.book_name_edit_text);
                        EditText book_writer_edit_text=bottomDialog.findViewById(R.id.book_writer_edit_text);
                        EditText book_barcode_edit_text=bottomDialog.findViewById(R.id.book_barcode_edit_text);
                        TextView book_info_submit_text_view=bottomDialog.findViewById(R.id.book_info_submit_text_view);

                        book_info_submit_text_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(
                                        book_name_edit_text.getText().length()!=0 &&
                                        book_writer_edit_text.getText().length()!=0 &&
                                        book_barcode_edit_text.getText().length()==13 &&
                                                checkBarcodeNumber(book_barcode_edit_text.getText().toString())){

                                    register_book_card_book_title_result_text_view.setText(book_name_edit_text.getText());
                                    register_book_card_book_writer_result_text_view.setText(book_writer_edit_text.getText());

                                    //book_barcode_edit_text에 대한 정보 처리
                                    bookInfo.put("barcode", book_barcode_edit_text.getText().toString());
                                    bookInfo.put("bookName", book_name_edit_text.getText().toString());
                                    bookInfo.put("bookWriter", book_writer_edit_text.getText().toString());
                                    bookInfo.put("pictureLink", "");

                                    new ImageLoadTask(R.drawable.book_replace, register_book_image_Button).execute();
                                    bottomDialog.dismiss();
                                }
                                else{
                                    Toast.makeText(RegisterBookActivity.this,"정보를 제대로 입력해 주십시오.",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        bottomDialog.show();
                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.show();

            }
        });
    }

    //ISBN 체크 method
    public boolean checkBarcodeNumber(String book_barcode){
        Log.d("barcodeArr","book_barcode: "+book_barcode);
        int[] barcodeArr=new int[13];
        int barcodeSum=0;

        for(int i=0;i<barcodeArr.length;i++){
            barcodeArr[i]= (book_barcode.charAt(i)-'0');
            Log.d("barcodeArr","barcodeArr("+i+"): "+barcodeArr[i]);

            if(i%2==0){
                barcodeSum+=barcodeArr[i];
            }
            else{
                barcodeSum+=(barcodeArr[i]*3);
            }
        }
        Log.d("barcodeArr","barcodeSum: "+barcodeSum);

        if(barcodeSum%10==0){
            return true;
        }
        else{
            return false;
        }
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
                        major="컴퓨터공학과";
                        break;
                    case 1:
                        major="전자공학과";
                        break;
                    case 2:
                        major="전기공학과";
                        break;
                    case 3:
                        major="AI.소프트웨어학부";
                        break;
                    case 4:
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
                if(register_book_card_book_title_result_text_view.getText().length()!=0 && register_book_card_book_writer_result_text_view.getText().length()!=0){
                    localUpoad();

                    //저장 방식은 localUpload에 명시되어 있습니다.
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


    private void showCamera() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(RegisterBookActivity.this);
        intentIntegrator.setBeepEnabled(true);//바코드 인식시 소리
        intentIntegrator.setDesiredBarcodeFormats(String.valueOf(BarcodeFormat.EAN_13));
        intentIntegrator.initiateScan();
    }


    private void showGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, 200);
    }


    /**
     * 바코드를 인식할 사진의 정보를 받을 경우
     * A. uri를 바탕으로 이미지를 Bitmap형태로 변환
     * B. Bitmap Image를 바탕으로 바코드 인식 수행.
     * C. 바코드 인식 성공 시 네이버 API를 통해서 책 정보 받아오기.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 200) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 200 && resultCode == RESULT_OK && data != null && data.getData() != null) {

                Log.d("Type:", data.getClass().getName());
                Bitmap bitmap = null;
                selectedImageUri = (Uri) data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                getBarcodeFromImage(bitmap);
            }
        }
        else {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(result != null) {
                if(result.getContents() == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    getBookInfoByBarcode(result.getContents());
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    public void getBarcodeFromImage(Bitmap bookBitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bookBitmap);
        BarCodeService barCodeService = new BarCodeService();
        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance().getVisionBarcodeDetector(barCodeService.getOptions());
        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    // 바코드 인식 성공시
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                        for (FirebaseVisionBarcode barcode : barcodes) {
                            String detectedBarcode = barcode.getRawValue();

                            // C. 네이버 API를 통해서 책 정보 받아오기.
                            getBookInfoByBarcode(detectedBarcode);
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

    private void getBookInfoByBarcode(String barcode) {
        ApiService call = RetrofitClientXml.getApiService(BaseUrlInterface.NAVER);
        call.searchBookByBarcode(barcode).enqueue(new Callback<BookSearchResultVO>() {

            @Override
            public void onResponse(Call<BookSearchResultVO> call, Response<BookSearchResultVO> response) {
                Log.d("BookSearchResult Successs", String.valueOf(response.message()));
                String resultBookTitle = response.body().channel.item.getTitle();
                String resultBookWriter = response.body().channel.item.getAuthor();
                String resultImageUrl = response.body().channel.item.getImage();

                new ImageLoadTask(resultImageUrl, register_book_image_Button).execute();
                register_book_card_book_title_result_text_view.setText(resultBookTitle);
                register_book_card_book_writer_result_text_view.setText(resultBookWriter);
                register_book_app_compat_button.setEnabled(true);


                bookInfo.put("barcode", barcode);
                bookInfo.put("bookName", resultBookTitle);
                bookInfo.put("bookWriter", resultBookWriter);
                bookInfo.put("pictureLink", resultImageUrl);
            }
            @Override
            public void onFailure(Call<BookSearchResultVO> call, Throwable t) {
                Log.d("BookSearchResult Successs", String.valueOf(t));
            }
        });
    }


    private void localUpoad() {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        FirebaseFunction firebaseInput = new FirebaseFunction();
        firebaseInput.profileGet(getMemberName, (resultList) -> { // 모든 책정보 가져오기 / for문을 size로 돌리면 모든 책정보 가져올수 있음
            Log.d("home에서 확인",resultList.get(0).getName());
            long now = System.currentTimeMillis();
            Date dateNow = new Date(now);
            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String formatDate = sdfNow.format(dateNow);
            // 책 저장 방식입니다.
            // 인자 값으로 (String 바코드, String 책제목, String 이미지링크, String 저자, String 학과, String 등록날짜, int 빌려준 횟수(0으로 초기화해서 사용해주세요.) )
            firebaseInput.insertBookInfo2(bookInfo.get("barcode"), bookInfo.get("bookName"), bookInfo.get("pictureLink"), bookInfo.get("bookWriter"), major, formatDate, 0);
            firebaseInput.insertRegisteredBookInfoToUser(bookInfo.get("barcode"), bookInfo.get("bookName"), bookInfo.get("bookWriter"));

            return null;
        }); //EOF

    }
}















