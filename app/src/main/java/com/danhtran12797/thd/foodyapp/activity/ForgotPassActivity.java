package com.danhtran12797.thd.foodyapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.activity.listener.ILoading;
import com.danhtran12797.thd.foodyapp.model.JWTToken;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.DataService;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.victor.loading.rotate.RotateLoading;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassActivity extends AppCompatActivity implements ILoading {

    private static final String TAG = "ForgotPassActivity";

    TextInputEditText edtEmail;
    TextInputLayout emailInputLayout;
    Button btnConfirmEmail;
    Toolbar toolbar;
    FrameLayout layout_container;
    LinearLayout layout_forgot_pass;
    RotateLoading rotateLoading;

    ILoading mListenerLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        mListenerLoading=this;

        initView();
        initActionBar();

        edtEmail.requestFocus();
        emailInputLayout.setError("Vui lòng nhập vào email");
        btnConfirmEmail.setEnabled(false);

        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().contains("@gmail.com")) {
                    Drawable img = getResources().getDrawable(R.drawable.custom_btn_empty_shoping_cart);
                    btnConfirmEmail.setBackgroundDrawable(img);
                    btnConfirmEmail.setEnabled(true);
                    emailInputLayout.setError(null);
                    btnConfirmEmail.setTextColor(Color.WHITE);
                } else {
                    Drawable img = getResources().getDrawable(R.drawable.custom_btn_validate);
                    btnConfirmEmail.setBackgroundDrawable(img);
                    emailInputLayout.setError("Vui lòng nhập vào email");
                    btnConfirmEmail.setEnabled(false);
                    btnConfirmEmail.setTextColor(Color.GRAY);
                }
            }
        });

        btnConfirmEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPass(edtEmail.getText().toString());
            }
        });
    }

    private void resetPass(String email) {
        mListenerLoading.start_loading();

        DataService dataService = APIService.getService();
        Call<String> callback = dataService.ResetPassword(email);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mListenerLoading.stop_loading(true);
                String message=response.body();
                if(message.equals("email_not_exits")){
                    Ultil.showDialog(ForgotPassActivity.this, null, "Email này không tồn tại. Vui lòng nhập lại!", null, null);
                }else if(!message.equals("false")){
                    Intent intent=new Intent(ForgotPassActivity.this, SendEmailSuccessActivity.class);
                    intent.putExtra("email", message);
                    intent.putExtra("status_send_mail", -2);
                    startActivity(intent);
                    finish();
                }else{
                    Ultil.showDialog(ForgotPassActivity.this, null, "Lỗi hệ thống!", null, null);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                mListenerLoading.stop_loading(false);
            }
        });
    }

    private void initView() {
        edtEmail = findViewById(R.id.edtEmail);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        btnConfirmEmail = findViewById(R.id.btnConfirmEmail);

        layout_container=findViewById(R.id.layout_container);
        layout_container.setVisibility(View.GONE);
        layout_forgot_pass=findViewById(R.id.layout_forgot_pass);

        rotateLoading=findViewById(R.id.rotateLoading);
    }

    private void initActionBar() {
        toolbar = findViewById(R.id.toolbar_forgot_pass);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void start_loading() {
        rotateLoading.start();
        layout_container.setVisibility(View.VISIBLE);
    }

    @Override
    public void stop_loading(boolean isConnect) {
        rotateLoading.stop();
        layout_container.setVisibility(View.GONE);
        if(!isConnect){
            Ultil.show_snackbar(layout_forgot_pass, btnConfirmEmail);
        }
    }
}
