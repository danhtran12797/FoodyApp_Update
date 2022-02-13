package com.danhtran12797.thd.foodyapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import android.widget.Toast;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.activity.listener.ILoading;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.DataService;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.victor.loading.rotate.RotateLoading;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassActivity extends AppCompatActivity implements ILoading{

    private static final String TAG = "ChangePassActivity";

    TextInputEditText edtPass, edtConfirmPass;
    TextInputLayout passInputLayout, confirmPassInputLayout;
    Button btnConfirm;
    Toolbar toolbar;
    FrameLayout layout_container;
    LinearLayout layout_change_pass;
    RotateLoading rotateLoading;

    ILoading mListenerLoading;

    String email="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        mListenerLoading=this;
        if(getIntent()!=null){
            email=getIntent().getStringExtra("email_change_pass");
            Log.d(TAG, "onCreate: "+email);
            Toast.makeText(this, "Email: "+email, Toast.LENGTH_SHORT).show();
        }else{
            Log.d(TAG, "onCreate: FAILED ");
        }

        initView();
        initActionBar();

        btnConfirm.setEnabled(false);

        edtConfirmPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0&&!s.toString().equals(edtPass.getText().toString())){
                    Drawable img = getResources().getDrawable(R.drawable.custom_btn_validate);
                    btnConfirm.setBackgroundDrawable(img);
                    edtConfirmPass.setError("Xác nhận mật khẩu không đúng");
                    btnConfirm.setEnabled(false);
                    btnConfirm.setTextColor(Color.GRAY);
                }else{
                    Drawable img = getResources().getDrawable(R.drawable.custom_btn_empty_shoping_cart);
                    btnConfirm.setBackgroundDrawable(img);
                    confirmPassInputLayout.setError(null);
                    btnConfirm.setEnabled(true);
                    btnConfirm.setTextColor(Color.WHITE);
                }
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePass(email, edtConfirmPass.getText().toString());
            }
        });
    }

    private void changePass(String email, String pass) {
        mListenerLoading.start_loading();

        DataService dataService = APIService.getService();
        Call<String> callback = dataService.ChangePass(email, pass);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mListenerLoading.stop_loading(true);
                if(response.body().equals("success")){
                    Log.d(TAG, "onResponse: CHANGE PASS SUCCESS");
                    Intent intent = new Intent(ChangePassActivity.this, SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else{
                    Ultil.showDialog(ChangePassActivity.this, null, "Lỗi hệ thống!", null, null);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                mListenerLoading.stop_loading(false);
            }
        });
    }

    private void initActionBar() {
        toolbar = findViewById(R.id.toolbar_change_pass);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView() {
        edtPass=findViewById(R.id.edtPass);
        edtConfirmPass=findViewById(R.id.edtConfirmPass);
        passInputLayout=findViewById(R.id.passInputLayout);
        confirmPassInputLayout=findViewById(R.id.confirmPassInputLayout);
        btnConfirm=findViewById(R.id.btnConfirmChangePass);

        layout_container=findViewById(R.id.layout_container);
        layout_container.setVisibility(View.GONE);
        layout_change_pass=findViewById(R.id.layout_change_pass);

        rotateLoading=findViewById(R.id.rotateLoading);
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
            Ultil.show_snackbar(layout_change_pass, btnConfirm);
        }
    }
}
