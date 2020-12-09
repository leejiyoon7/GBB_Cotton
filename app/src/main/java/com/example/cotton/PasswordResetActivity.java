package com.example.cotton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        findViewById(R.id.sendButton).setOnClickListener(onClickListener);
    }

    //재설정 버튼 온클릭
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.sendButton:
                    send();
                    finish();
                    break;
            }
        }
    };

    //비밀번호 재설정 함수
    private void send() {
        String email = ((EditText)findViewById(R.id.passwordCheckEditText)).getText().toString();

        if(email.length()>0) {
            FirebaseAuth auth = FirebaseAuth.getInstance();

            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                startToast("이메일이 전송되었습니다.");
                            }else {
                                startToast("이메일전송에 실패했습니다.");
                            }
                        }
                    });
        }
        else{
            startToast("이메일을 입력해 주세요.");
        }
    }

    //토스트 메세지 출력 함수
    private void startToast(String msg) {
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
    }
}