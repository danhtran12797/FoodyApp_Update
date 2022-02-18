package com.danhtran12797.thd.foodyapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.activity.listener.ILoading;
import com.danhtran12797.thd.foodyapp.model.JWTToken;
import com.danhtran12797.thd.foodyapp.model.Product;
import com.danhtran12797.thd.foodyapp.model.User;
import com.danhtran12797.thd.foodyapp.module.login.ILogin;
import com.danhtran12797.thd.foodyapp.module.login.LoginManage;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.DataService;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.victor.loading.rotate.RotateLoading;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener, ILoading, ILogin {

    private static final String TAG = "SignupActivity";

    private LinearLayout layout_change_image;
    private CircleImageView img_close, img_user;
    private EditText edt_username, edt_pass, edt_confirm_pass;
    private Button btn_next;
    private TextView txt_link;
    private RotateLoading rotateLoading;
    private FrameLayout layout_container;
    private ConstraintLayout layout_signup;

    private Uri resultUri = null;

    private String id_user, name, url, login_to;
    private String confirm_pass, username, pass;
    private Product product = null;

    ILoading mListener;
    LoginManage loginManage;

//    private boolean temp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mListener = this;
        loginManage = new LoginManage(this, mListener);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            id_user = bundle.getString("id_user");
            name = bundle.getString("name");
            url = bundle.getString("url");
            login_to = bundle.getString("login_to", "");
            product = (Product) bundle.getSerializable("product");

            Log.d(TAG, "login_to: " + login_to);
            Log.d(TAG, "url: " + url);
        }

        initView();
        setImage();
    }

    private void setImage() {
        Picasso.get().load(url)
                .placeholder(R.drawable.noimage)
                .error(R.drawable.error)
                .into(img_user);
    }

    private void initView() {
        layout_change_image = findViewById(R.id.layout_change_image);
        img_close = findViewById(R.id.img_close);
        img_user = findViewById(R.id.img_user);
        edt_username = findViewById(R.id.edt_username);
        edt_pass = findViewById(R.id.edt_pass);
        edt_confirm_pass = findViewById(R.id.edt_confirm_pass);
        btn_next = findViewById(R.id.btn_next);
        txt_link = findViewById(R.id.txt_link);
        txt_link.setMovementMethod(LinkMovementMethod.getInstance());
        rotateLoading = findViewById(R.id.rotateLoading);
        layout_container = findViewById(R.id.layout_container);
        layout_container.setVisibility(View.GONE);
        layout_signup = findViewById(R.id.layout_signup);

        btn_next.setOnClickListener(this);
        layout_change_image.setOnClickListener(this);
        img_close.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                event_next();
                break;
            case R.id.layout_change_image:
                BringImagePicker();
                break;
            case R.id.img_close:
                finish();
                break;
        }
    }

    private void event_next() {
        username = edt_username.getText().toString().trim();
        pass = edt_pass.getText().toString().trim();
        confirm_pass = edt_confirm_pass.getText().toString().trim();
        if (validateForm(username, pass)) {
            String name_file = System.currentTimeMillis() + ".png";
            if (validateForm1(pass, confirm_pass)) {
                uploadDataUser(name_file);
            }
        }
    }

    private void uploadDataUser(String name_image) {
        mListener.start_loading();

        DataService dataService = APIService.getService();
        Call<String> callback = dataService.Register(id_user, name, username, pass, "", "", "", name_image);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                String message = response.body();
                Log.d(TAG, "message: " + message);
                if (message.equals("success")) {
//                    checkUser(id_user);
                    if (resultUri != null) {
                        uploadImage(name_image);
                    } else {
                        download_avatar(name_image);
                    }
                } else if (message.equals("failed")) {
                    Ultil.showDialog(SignupActivity.this, null, "Sự cố thêm tài khoản thất bại, chúng tôi sẽ khắc phục ngay.", null, null);
                } else {
                    mListener.stop_loading(true);

                    if (message.equals("username"))
                        message = "Tên đăng nhập";
                    else if (message.equals("email"))
                        message = "Email";
                    else
                        message = "Số điện thoại";
                    Ultil.showDialog(SignupActivity.this, null, message + " này đã tồn tại. Vui lòng nhập lại!", null, null);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                mListener.stop_loading(false);
                Log.d(TAG, "error register: " + t.getMessage());
            }
        });
    }

    private void uploadImage(String name_file) {
        File file = new File(resultUri.getPath());

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("upload_file", name_file, requestBody);

        DataService service = APIService.getService();
        Call<String> callback = service.UploadImage(body);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mListener.stop_loading(true);
                String message = response.body();
                Log.d(TAG, "uploadImage: " + message);
                if (message.equals(name_file)) {
                    loginManage.loginWithSocial(id_user, name, url);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                mListener.stop_loading(false);
            }
        });
    }

//    private void checkUser(String id_user) {
//        DataService dataService = APIService.getService();
//        Call<JWTToken> callback = dataService.LoginSocial(id_user);
//        callback.enqueue(new Callback<JWTToken>() {
//            @Override
//            public void onResponse(Call<JWTToken> call, Response<JWTToken> response) {
//                if (response.isSuccessful()) {
//                    JWTToken jwtToken = response.body();
//                    Log.d(TAG, "jwtToken: " + jwtToken.getToken());
//                    Ultil.setTokenPreference(SignupActivity.this, jwtToken);
//                    temp = true;
//                } else {
//                    if (response.code() == 401) {
//                        Toast.makeText(SignupActivity.this, "Tài khoản không còn tồn tại trong cơ sở dữ liệu!", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JWTToken> call, Throwable t) {
//                Log.d(TAG, "onFailure: " + t.getMessage());
//                mListener.stop_loading(false);
//            }
//        });
//    }

    private void download_avatar(String name_file) {
        DataService dataService = APIService.getService();
        Call<String> callback = dataService.DownloadSocial(name_file, url);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mListener.stop_loading(true);
                String message = response.body();
                Log.d(TAG, "onResponse: " + message);
                if (message.equals("success")) {
                    loginManage.loginWithSocial(id_user, name, url);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "onFailure: download_avatar : " + t.getMessage());
                mListener.stop_loading(true);
                open_activity(name_file);
//                if(t.getMessage().equals("timeout")){
//                    mListener.stop_loading(true);
//                    open_activity(name_file);
//                }else{
//                    mListener.stop_loading(false);
//                }
            }
        });
    }

    private void open_activity(String name_file) {
        Ultil.user = new User(id_user, name, username, "", pass, "", "", name_file, "0");
        Ultil.setUserPreference(SignupActivity.this);

        Intent intent = Ultil.create_intent(login_to, this, product);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                img_user.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d(TAG, "Error: " + error.getMessage());
            }
        }
    }

    private boolean validateForm(String username, String pass) {
        boolean valid = true;

        //kiểm tra pass
        if (pass.isEmpty()) {
            edt_pass.setError("Không được bỏ trống");
            valid = false;
        } else if (pass.length() < 6) {
            edt_pass.setError("Mật khẩu ít nhất phải 6 ký tự");
            valid = false;
        } else {
            edt_pass.setError(null);
        }

        // kiểm tra username
        if (username.isEmpty()) {
            edt_username.setError("Không được bỏ trống");
            valid = false;
        } else if (username.length() < 6) {
            edt_username.setError("Tên đăng nhập ít nhất phải 6 ký tự");
            valid = false;
        } else {
            edt_username.setError(null);
        }

        return valid;
    }

    private boolean validateForm1(String pass, String confirm) {
        boolean valid = true;

        if (!pass.equals(confirm)) {
            edt_confirm_pass.setError("Xác nhận mật khẩu không đúng!");
            valid = false;
        } else {
            edt_confirm_pass.setError(null);
        }

        return valid;
    }

    @Override
    public void start_loading() {
        rotateLoading.start();
        layout_container.setVisibility(View.VISIBLE);
        img_close.setVisibility(View.INVISIBLE);
    }

    @Override
    public void stop_loading(boolean isConnect) {
        rotateLoading.stop();
        layout_container.setVisibility(View.GONE);
        img_close.setVisibility(View.VISIBLE);
        if (!isConnect) {
            Ultil.show_snackbar(layout_signup, null);
        }
    }

    @Override
    public void loginSuccess(List<User> users) {
        stop_loading(true);
        Ultil.user = users.get(0);
        Ultil.setUserPreference(this);

        Intent intent = Ultil.create_intent(login_to, this, product);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void register(String idUser, String name, String url) {

    }

    @Override
    public void setToken(JWTToken token) {
        Log.d(TAG, "setToken: " + token.getToken());
        Ultil.setTokenPreference(this, token);
    }

    @Override
    public void failure(Throwable throwable) {
        stop_loading(true);
        Log.d(TAG, "failure: " + throwable.getMessage());
    }
}
