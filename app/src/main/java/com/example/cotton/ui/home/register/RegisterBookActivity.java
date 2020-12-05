package com.example.cotton.ui.home.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.cotton.LoginActivity;
import com.example.cotton.MainActivity;
import com.example.cotton.R;
import com.example.cotton.ui.home.HomeFragment;

import java.lang.reflect.Array;

public class RegisterBookActivity extends Activity {

    HomeFragment homeFragment;//이동할 homefragment
    ImageButton register_book_image_Button;//도서등록 사진추가 이미지뷰
    Spinner register_book_card_department_spinner;//학과 스피너
    EditText register_book_card_book_title_edit_text;//도서 제목 editText
    EditText register_book_card_book_writer_edit_text;//도서 저자 editText
    AppCompatButton register_book_app_compat_button;//등록하기 버튼

    ArrayAdapter spinnerAdapter;//스피너 어댑터

    String major;//전공 value

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_book);

        homeFragment=new HomeFragment();
        register_book_image_Button=findViewById(R.id.register_book_image_Button);
        register_book_card_department_spinner=findViewById(R.id.register_book_card_department_spinner);
        register_book_card_book_title_edit_text=(EditText)findViewById(R.id.register_book_card_book_title_edit_text);
        register_book_card_book_writer_edit_text=(EditText)findViewById(R.id.register_book_card_book_writer_edit_text);
        register_book_app_compat_button=findViewById(R.id.register_book_app_compat_button);

        registerBookImage();//사진 등록 method

        majorPickSpinner();//전공 선택 스피너

        registerBook();//도서 등록 버튼 클릭 이벤트 method

    }
    //사진 등록 method, 추후 구현 예정
    public void registerBookImage(){
        register_book_image_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RegisterBookActivity.this,"사진추가 클릭 이벤트 발생",Toast.LENGTH_SHORT).show();
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
}
