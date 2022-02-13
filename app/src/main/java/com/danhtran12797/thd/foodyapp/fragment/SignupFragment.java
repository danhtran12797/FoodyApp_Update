package com.danhtran12797.thd.foodyapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.activity.listener.ILoading;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.DataService;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class SignupFragment extends Fragment {

    private static final String TAG = "SignupFragment";

    View view;
    CircleImageView imgDefaultAvatar;
    CircleImageView imgCamera;
    EditText edtUsername;
    EditText edtEmail;
    EditText edtPass;
    EditText edtConfirmPass;
    EditText edtPhone;
    EditText edtName;
    EditText edtAddress;
    Button btnRegister;

    Uri resultUri = null;
    String name_image_default = "user_default1.png";

    String address = "";
    String name = "";
    String username = "";
    String email = "";
    String pass = "";
    String confirm = "";
    String phone = "";

    private SignupFragmentListener listener;
    private ILoading mListener;

    public interface SignupFragmentListener {
        void onInputRegister(String username);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_signup, container, false);

        initView();
        evenView();

        return view;
    }

    private void evenView() {
        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BringImagePicker();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Ultil.isNetworkConnected(getContext())) {
                    address = edtAddress.getText().toString().trim();
                    name = edtName.getText().toString().trim();
                    username = edtUsername.getText().toString().trim();
                    email = edtEmail.getText().toString().trim();
                    pass = edtPass.getText().toString().trim();
                    confirm = edtConfirmPass.getText().toString().trim();
                    phone = edtPhone.getText().toString().trim();

                    if (validateForm(name, username, email, phone, pass)) {
                        if (validateForm1(pass, confirm)) {
                            String id_user = Ultil.generate_unique_id();
                            String name_file = System.currentTimeMillis() + ".png";
                            if (resultUri != null) {
                                uploadDataUser(id_user, name, username, email, phone, pass, name_file, address);
                            } else {
                                uploadDataUser(id_user, name, username, email, phone, pass, name_image_default, address);
                            }
                        }
                    }
                } else {
                    Ultil.show_snackbar(view, view);
                }
            }
        });
    }

    private void uploadDataUser(String id_user, String name, String username, String email, String phone, String pass, String name_image, String address) {
        mListener.start_loading();

        DataService dataService = APIService.getService();
        Call<String> callback = dataService.Register(id_user, name, username, pass, email, address, phone, name_image);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mListener.stop_loading(true);

                String message = response.body();
                Log.d(TAG, "message: " + message);
                if (message.equals("success")) {
                    if (resultUri != null) {
                        uploadImage(name_image);
                    } else {
                        listener.onInputRegister(username);
                    }
                } else if (message.equals("failed")) {
                    Ultil.showDialog(getContext(), null, "Sự cố thêm tài khoản thất bại, chúng tôi sẽ khắc phục ngay.", null, null);
                } else {
                    if (message.equals("username"))
                        message = "Tên đăng nhập";
                    else if (message.equals("email"))
                        message = "Email";
                    else
                        message = "Số điện thoại";
                    Ultil.showDialog(getContext(), null, message + " này đã tồn tại. Vui lòng nhập lại!", null, null);
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

        mListener.start_loading();
        DataService service = APIService.getService();
        Call<String> callback = service.UploadImage(body);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mListener.stop_loading(true);
                String message = response.body();
                Log.d(TAG, "uploadImage: " + message);
                if (message.equals(name_file)) {
                    Toast.makeText(getContext(), "Đăng ký thành công", Toast.LENGTH_LONG).show();
                    listener.onInputRegister(username);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                mListener.stop_loading(false);
                Log.d(TAG, t.getMessage());
            }
        });
    }

    private boolean validateForm(String name, String username, String email, String phone, String pass) {
        boolean valid = true;

        // kiểm tra email
        if (!email.isEmpty()) {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtEmail.setError("Định dạng email không hợp lệ");
                valid = false;
            }
        }

        //kiểm tra pass
        if (pass.isEmpty()) {
            edtPass.setError("Không được bỏ trống");
            valid = false;
        } else if (pass.length() < 6) {
            edtPass.setError("Mật khẩu ít nhất phải 6 ký tự");
            valid = false;
        } else {
            edtPass.setError(null);
        }

        // kiểm tra username
        if (username.isEmpty()) {
            edtUsername.setError("Không được bỏ trống");
            valid = false;
        } else if (username.length() < 6) {
            edtUsername.setError("Tên đăng nhập ít nhất phải 6 ký tự");
            valid = false;
        } else {
            edtUsername.setError(null);
        }

        // kiểm tra phone
        if (!phone.isEmpty()) {
            if (!(phone.length() == 10 && Ultil.check_phone_valid(phone, getContext()))) {
                edtPhone.setError("Số điện thoại không hợp lệ");
                valid = false;
            }
        }

        // kiểm tra name
        if (name.isEmpty()) {
            edtName.setError("Không được bỏ trống");
            valid = false;
        }

        return valid;
    }

    private boolean validateForm1(String pass, String confirm) {
        boolean valid = true;

        if (!pass.equals(confirm)) {
            edtConfirmPass.setError("Xác nhận mật khẩu không đúng!");
            valid = false;
        } else {
            edtConfirmPass.setError(null);
        }

        return valid;
    }

    private void initView() {
        imgDefaultAvatar = view.findViewById(R.id.imgDefaultAvatar);
        imgCamera = view.findViewById(R.id.imgCamera);
        edtEmail = view.findViewById(R.id.edtEmailRegister);
        edtAddress = view.findViewById(R.id.edtAddress);
        edtUsername = view.findViewById(R.id.edtUserName);
        edtPhone = view.findViewById(R.id.edtPhone);
        edtPass = view.findViewById(R.id.edtPassSignup);
        edtConfirmPass = view.findViewById(R.id.edtConfirmPassSignup);
        edtName = view.findViewById(R.id.edtName);
        btnRegister = view.findViewById(R.id.btnRegister);
    }

    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(getContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                imgDefaultAvatar.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d(TAG, error.getMessage());
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SignupFragmentListener || context instanceof ILoading) {
            listener = (SignupFragmentListener) context;
            mListener = (ILoading) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentAListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        mListener = null;
    }

}
