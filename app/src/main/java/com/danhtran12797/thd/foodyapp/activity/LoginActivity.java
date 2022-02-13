package com.danhtran12797.thd.foodyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.activity.listener.ILoading;
import com.danhtran12797.thd.foodyapp.activity.listener.ILoginZalo;
import com.danhtran12797.thd.foodyapp.adapter.LoginAdapter;
import com.danhtran12797.thd.foodyapp.fragment.LoginFragment;
import com.danhtran12797.thd.foodyapp.fragment.SignupFragment;
import com.danhtran12797.thd.foodyapp.model.JWTToken;
import com.danhtran12797.thd.foodyapp.model.Product;
import com.danhtran12797.thd.foodyapp.model.User;
import com.danhtran12797.thd.foodyapp.module.login.ILogin;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.google.android.material.tabs.TabLayout;
import com.victor.loading.rotate.RotateLoading;
import com.zing.zalo.zalosdk.oauth.OAuthCompleteListener;
import com.zing.zalo.zalosdk.oauth.OauthResponse;
import com.zing.zalo.zalosdk.oauth.ZaloOpenAPICallback;
import com.zing.zalo.zalosdk.oauth.ZaloSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.HttpException;

public class LoginActivity extends AppCompatActivity implements SignupFragment.SignupFragmentListener, ILoading, ILoginZalo, ILogin {

    private static final String TAG = "LoginActivity";

    TabLayout tabLogin;
    ViewPager viewPager;
    LoginAdapter loginAdapter;
    int postion;
    LoginFragment loginFragment;
    SignupFragment registerFragment;
    RotateLoading rotateLoading;
    FrameLayout layout_container;
    RelativeLayout layout_login;

    Intent intent = null;
    Product product = null;
    String login_to = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        get_intent();
        setLoginAdapter();
    }

    private void get_intent() {
        loginFragment = new LoginFragment();
        registerFragment = new SignupFragment();

        intent = getIntent();
        if (intent.hasExtra("user_love_product")) {
            product = (Product) intent.getSerializableExtra("user_love_product");
            login_to = intent.getStringExtra("login_to");
//            loginFragment = LoginFragment.getInstance(product, login_to);
            postion = 0;
        } else if (intent.hasExtra("login_register")) {
            postion = getIntent().getIntExtra("login_register", -1);
        } else if (intent.hasExtra("login_to")) {
            login_to = intent.getStringExtra("login_to");
//            loginFragment = LoginFragment.getInstance(product, login_to);
            postion = 0;
        }
    }

    private void setLoginAdapter() {
        loginAdapter = new LoginAdapter(getSupportFragmentManager());
        loginAdapter.addFragment(loginFragment, "Đăng nhập");
        loginAdapter.addFragment(registerFragment, "Đăng ký");

        viewPager.setAdapter(loginAdapter);
        tabLogin.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(postion);
    }

    private void initView() {
        tabLogin = findViewById(R.id.tabLogin);
        viewPager = findViewById(R.id.viewPagerLogin);
        rotateLoading = findViewById(R.id.rotateLoading);
        layout_container = findViewById(R.id.layout_container);
        layout_login = findViewById(R.id.layout_login);
        layout_container.setVisibility(View.GONE);
    }

    @Override
    public void onInputRegister(String username) {
        viewPager.setCurrentItem(0);
        loginFragment.setTextUsername(username);
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
        if (!isConnect) {
            Ultil.show_snackbar(layout_login, null);
        }
    }

    OAuthCompleteListener listenerZalo = new OAuthCompleteListener() {
        @Override
        public void onAuthenError(int errorCode, String message) {
            //Đăng nhập thất bại..
            Log.d(TAG, "onAuthenError: " + message);
        }

        @Override
        public void onGetOAuthComplete(OauthResponse response) {
            String code = response.getOauthCode();
            //Đăng nhập thành công..
            Log.d(TAG, "onGetOAuthComplete: ZALO LOGIN SUCCESS");
            ZaloSDK.Instance.getProfile(LoginActivity.this, new ZaloOpenAPICallback() {
                @Override
                public void onResult(JSONObject jsonObject) {
                    Log.d(TAG, "onResult: " + jsonObject.toString());
                    try {
                        String id = jsonObject.getString("id");
                        String name = jsonObject.getString("name");
                        String url = jsonObject.getJSONObject("picture").getJSONObject("data").getString("url");
                        register(id, name, url);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new String[]{"id", "name", "picture"});
        }
    };

    @Override
    public void login() {
        Log.d(TAG, "login: ZALO");
        ZaloSDK.Instance.authenticate(this, listenerZalo);
    }

    // request code zalo: 64725
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        ZaloSDK.Instance.onActivityResult(this, requestCode, resultCode, data);
//        Log.d(TAG, "requestCode: " + requestCode);
//        Log.d(TAG, "resultCode: " + resultCode);
//        Bundle bundle = data.getExtras();
//
//        if (bundle != null) {
//            for (String key : bundle.keySet()) {
//                Log.d(TAG, key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
//            }
//        }
    }


    @Override
    public void loginSuccess(List<User> users) {
        stop_loading(true);
        Ultil.user = users.get(0);
        Ultil.setUserPreference(this);
        Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

        Intent intent = Ultil.create_intent(login_to, this, product);
        startActivity(intent);
        finish();
    }

    @Override
    public void register(String idUser, String name, String url) {
        stop_loading(true);
        Intent intent = new Intent(this, SignupActivity.class);
        intent.putExtra("login_to", login_to);
        intent.putExtra("product", product);
        intent.putExtra("id_user", idUser);
        intent.putExtra("name", name);
        intent.putExtra("url", url);
        startActivity(intent);
        finish();
    }

    @Override
    public void setToken(JWTToken token) {
        Log.d(TAG, "setToken: " + token.getToken());
        Ultil.setTokenPreference(this, token);
    }

    @Override
    public void failure(Throwable throwable) {
        stop_loading(true);
        int code = ((HttpException) throwable).code();
        if (code == 401) {
            Ultil.dialogRequestTimeOut(this);
        } else if (code == 402) {
            Ultil.showDialog(this, null, "Tài khoản người dùng này không còn tồn tại trong cơ sở dữ liệu!", null, null);
        } else if (code == 403) {
            Ultil.showDialog(this, null, "Bạn đăng nhập sai. Vui lòng kiểm tra lại!", null, null);
        }
    }
}
