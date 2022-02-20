package com.danhtran12797.thd.foodyapp.activity;

import static com.zing.zalo.zalosdk.core.helper.Utils.genCodeChallenge;
import static com.zing.zalo.zalosdk.oauth.LoginVia.APP;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.danhtran12797.thd.foodyapp.activity.listener.IOAuthCode;
import com.danhtran12797.thd.foodyapp.adapter.LoginAdapter;
import com.danhtran12797.thd.foodyapp.fragment.LoginFragment;
import com.danhtran12797.thd.foodyapp.fragment.SignupFragment;
import com.danhtran12797.thd.foodyapp.model.JWTToken;
import com.danhtran12797.thd.foodyapp.model.Product;
import com.danhtran12797.thd.foodyapp.model.User;
import com.danhtran12797.thd.foodyapp.module.login.ILogin;
import com.danhtran12797.thd.foodyapp.ultil.RandomString;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.google.android.material.tabs.TabLayout;
import com.victor.loading.rotate.RotateLoading;
import com.zing.zalo.zalosdk.oauth.OAuthCompleteListener;
import com.zing.zalo.zalosdk.oauth.OauthResponse;
import com.zing.zalo.zalosdk.oauth.ZaloOpenAPICallback;
import com.zing.zalo.zalosdk.oauth.ZaloSDK;
import com.zing.zalo.zalosdk.oauth.model.ErrorResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.HttpException;

public class LoginActivity extends AppCompatActivity implements SignupFragment.SignupFragmentListener, ILoading, ILoginZalo, ILogin, IOAuthCode {

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

    private String code_verifier;

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

    @Override
    public void getAuthCodeSuccess(String accessToken) {
        ZaloSDK.Instance.getProfile(this, accessToken, new ZaloOpenAPICallback() {
            @Override
            public void onResult(JSONObject jsonObject) {
                Log.d(TAG, "onResult: " + jsonObject.toString());
                try {
                    String id = jsonObject.getString("id");
                    String name = jsonObject.getString("name");
                    String url = jsonObject.getJSONObject("picture").getJSONObject("data").getString("url");
                    Log.d(TAG, "id: "+id);
                    Log.d(TAG, "name: "+name);
                    Log.d(TAG, "url: "+url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new String[]{"id", "name", "picture"});
    }

    private static class ZaloLoginAsyncTask extends AsyncTask<String, Void, String> {

        Context context;
        IOAuthCode listener;

        public ZaloLoginAsyncTask(Context context) {
            this.context = context;
        }

        public ZaloLoginAsyncTask(Context context, IOAuthCode listener) {
            this.context = context;
            this.listener=listener;
        }

        @Override
        protected String doInBackground(String... code) {
            final String[] accesstoken = {"danh"};
            ZaloSDK.Instance.getAccessTokenByOAuthCode(
                    context, code[0], code[1], new ZaloOpenAPICallback() {
                        @Override
                        public void onResult(JSONObject data) {
                            int err = data.optInt("error");
                            if (err == 0) {
                                //clearOauthCodeInfo(); //clear used oacode

                                String access_token = data.optString("access_token");
                                Log.d(TAG, "access_token: " + access_token);

                                accesstoken[0] =access_token;
//                                listener.getAuthCodeSuccess(access_token);

                                /*refresh_token = data.optString("refresh_token");
                                long expires_in = Long.parseLong(data.optString("expires_in"));*/
                            }
                        }
                    });
            return accesstoken[0];
        }

    }

    @Override
    public void login() {
        Log.d(TAG, "login: ZALO");
        try {
            code_verifier = new RandomString().nextString();
                    /*MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] hash = digest.digest(code_verifier.getBytes(StandardCharsets.UTF_8));*/
            String code_challenge = genCodeChallenge(code_verifier);
            ZaloSDK.Instance.authenticateZaloWithAuthenType(this, APP, code_challenge,
                    listenerZalo);
        } catch (Exception e) {
            Log.d("III", "eventView: " + e.getMessage());
        }
    }

    OAuthCompleteListener listenerZalo = new OAuthCompleteListener() {
        @Override
        public void onAuthenError(ErrorResponse errorResponse) {
            //Đăng nhập thất bại..
            Log.d(TAG, "onAuthenError: " + errorResponse.getErrorMsg());
        }

        @Override
        public void onGetOAuthComplete(OauthResponse response) {
            String code = response.getOauthCode();
            //Đăng nhập thành công..
            Log.d(TAG, "onGetOAuthComplete: ZALO LOGIN SUCCESS");

            try {
                String access= new ZaloLoginAsyncTask(getApplicationContext()).execute(code, code_verifier).get();
                Log.d(TAG, "onGetOAuthComplete: access" +access);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    };

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "requestCode: " + requestCode);
        Log.d(TAG, "resultCode: " + resultCode);
        ZaloSDK.Instance.onActivityResult(this, requestCode, resultCode, data);
    }
}
