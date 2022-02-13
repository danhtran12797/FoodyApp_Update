package com.danhtran12797.thd.foodyapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.danhtran12797.thd.foodyapp.R;

public class SendEmailSuccessActivity extends AppCompatActivity {

    TextView txtSendMail;
    Button btnContinue;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email_success);

        txtSendMail=findViewById(R.id.txtSendMail);
        btnContinue=findViewById(R.id.btnContinue);

        initActionBar();

        if(getIntent()!=null){
            String email=getIntent().getStringExtra("email");
            int status=getIntent().getIntExtra("status_send_mail", 0);
            if(status==-1){
                toolbar.setTitle("Xác thực email");
                txtSendMail.setText(getResources().getString(R.string.send_mail, "xác thực", email));
            }else{
                toolbar.setTitle("Quên mật khẩu");
                txtSendMail.setText(getResources().getString(R.string.send_mail, "hướng dẫn tạo mật khẩu", email));
            }
        }

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SendEmailSuccessActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void initActionBar() {
        toolbar = findViewById(R.id.toolbar_send_mail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
