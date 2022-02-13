package com.danhtran12797.thd.foodyapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.activity.ForgotPassActivity;
import com.danhtran12797.thd.foodyapp.activity.listener.ILoading;
import com.danhtran12797.thd.foodyapp.activity.listener.ILoginZalo;
import com.danhtran12797.thd.foodyapp.model.Product;
import com.danhtran12797.thd.foodyapp.module.login.ILogin;
import com.danhtran12797.thd.foodyapp.module.login.LoginManage;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    private CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private LoginManage loginManage;

    View view;
    TextView txtForgotPass, txtHelp;
    EditText edtEmail;
    EditText edtPass;
    Button btnLogin;

    Button btnGoolge;
    Button btnFacebook;
    Button btnZalo;

//    Product product = null;
//    String login_to = "";

    ILoading mListener;
    ILoginZalo mLoginZaloListener;
    ILogin mLoginListener;

    private static final int RC_SIGN_IN = 9001;

    public static LoginFragment getInstance(Product product, String login_to) {
        LoginFragment fragment = new LoginFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("product", product);
        bundle.putString("login_to", login_to);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Bundle bundle = getArguments();
//        if (bundle != null) {
//            product = (Product) bundle.getSerializable("product");
//            login_to = bundle.getString("login_to");
//            Log.d("AAA", "LoginFragment: " + login_to);
//        }
        loginManage = new LoginManage(mLoginListener);

        callbackManager = CallbackManager.Factory.create();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        initView();
        eventView();
        callBackFacebook();

        return view;
    }

    public void callBackFacebook() {
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "Login SUCCESS");
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.d(TAG, response.getJSONObject().toString());

                                        // Application code
                                        try {
                                            String id = object.getString("id");
                                            String name = object.getString("name");
//                                            String short_name = object.getString("short_name");

                                            String url_facebook = "https://graph.facebook.com/" + id + "/picture?type=large";

//                                            checkUser(id, name, url_facebook);
                                            mListener.start_loading();
                                            loginManage.loginWithSocial(id, name, url_facebook);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Log.d(TAG, "onCompleted: " + e.getMessage());
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,first_name,middle_name" +
                                ",last_name,name_format,short_name");

                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getContext(), "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void setTextUsername(String username) {
        edtEmail.setText(username);
        edtPass.setFocusable(true);
    }


    private void eventView() {
        txtHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Help !!!", Toast.LENGTH_SHORT).show();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Ultil.isNetworkConnected(getContext())) {
                    String email = edtEmail.getText().toString().trim();
                    String password = edtPass.getText().toString().trim();

                    if (validateForm(email, password)) {
//                        GetToken(email, password);
                        loginManage.loginWithUserName(email, password);
                    }
                } else {
                    Snackbar.make(view, "Vui lòng kiểm tra Internet", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
        btnGoolge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Ultil.isNetworkConnected(getContext())) {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                } else {
                    Ultil.show_snackbar(view, view);
                }
            }
        });
        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Ultil.isNetworkConnected(getContext())) {
                    LoginManager.getInstance().logInWithReadPermissions(LoginFragment.this, Arrays.asList("public_profile", "email"));
                } else {
                    Ultil.show_snackbar(view, view);
                }
            }
        });
        btnZalo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Ultil.isNetworkConnected(getContext())) {
                    mLoginZaloListener.login();
                } else {
                    Ultil.show_snackbar(view, view);
                }
            }
        });
        txtForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ForgotPassActivity.class));
                getActivity().finish();
            }
        });
    }

//    private void checkUser(String id_user, String name, String url) {
//        mListener.start_loading();
//
//        DataService dataService = APIService.getService();
//        Call<JWTToken> callback = dataService.LoginSocial(id_user);
//        callback.enqueue(new Callback<JWTToken>() {
//            @Override
//            public void onResponse(Call<JWTToken> call, Response<JWTToken> response) {
//                if (response.isSuccessful()) {
//                    JWTToken jwtToken = response.body();
//                    Log.d(TAG, "token: " + jwtToken.getToken());
//                    GetUser(jwtToken);
//                } else {
//                    if (response.code() == 404) {
//                        mListener.stop_loading(true);
//                        Intent intent = new Intent(getContext(), SignupActivity.class);
//                        intent.putExtra("login_to", login_to);
//                        intent.putExtra("product", product);
//                        intent.putExtra("id_user", id_user);
//                        intent.putExtra("name", name);
//                        intent.putExtra("url", url);
//                        startActivity(intent);
//                        getActivity().finish();
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

//    private void GetToken(String username, String password) {
//        mListener.start_loading();
//
//        DataService dataService = APIService.getService();
//        Call<JWTToken> callback = dataService.LoginJWT(username, password);
//        callback.enqueue(new Callback<JWTToken>() {
//            @Override
//            public void onResponse(Call<JWTToken> call, Response<JWTToken> response) {
//                if (response.isSuccessful()) {
//                    JWTToken jwtToken = response.body();
//                    GetUser(jwtToken);
//                } else {
//                    if (response.code() == 403) {
//                        Ultil.showDialog(getContext(), null, "Bạn đăng nhập sai. Vui lòng kiểm tra lại!", null, null);
//                        mListener.stop_loading(true);
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

//    private void GetUser(JWTToken token) {
//        DataService dataService = APIService.getService();
//        Call<List<User>> callback = dataService.GetUser(token.getToken());
//        callback.enqueue(new Callback<List<User>>() {
//            @Override
//            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
//                mListener.stop_loading(true);
//                if (response.isSuccessful()) {
//                    ArrayList<User> arrUser = (ArrayList<User>) response.body();
//                    Ultil.user = arrUser.get(0);
//                    Ultil.setUserPreference(getActivity());
//                    Ultil.setTokenPreference(getActivity(), token);
//                    Toast.makeText(getContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
//
//                    Intent intent = Ultil.create_intent(login_to, getActivity(), product);
//                    getActivity().startActivity(intent);
//                    getActivity().finish();
//                } else {
//                    if (response.code() == 401) {
//                        Ultil.dialogRequestTimeOut(getActivity());
//                    } else if (response.code() == 402) {
//                        Ultil.showDialog(getContext(), null, "Tài khoản người dùng này không còn tồn tại trong cơ sở dữ liệu!", null, null);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<User>> call, Throwable t) {
//                Log.d(TAG, "onFailure: " + t.getMessage());
//                mListener.stop_loading(false);
//            }
//        });
//    }

    private boolean validateForm(String email, String password) {
        boolean valid = true;

        if (email.isEmpty()) {
            //Field can't be empty
            edtEmail.setError("Không được bỏ trống");
            valid = false;
        } else {
            edtEmail.setError(null);
        }

        if (password.isEmpty()) {
            edtPass.setError("Không được bỏ trống");
            valid = false;
        } else {
            edtPass.setError(null);
        }

        return valid;
    }

    private void initView() {
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPass = view.findViewById(R.id.edtPass);
        txtHelp = view.findViewById(R.id.mHelp);
        txtForgotPass = view.findViewById(R.id.txtForgotPass);
        btnLogin = view.findViewById(R.id.btnLogin);

        btnFacebook = view.findViewById(R.id.button_facebook);
        btnGoolge = view.findViewById(R.id.button_google);
        btnZalo = view.findViewById(R.id.button_zalo);
    }

    private void LoginGoogle(@Nullable GoogleSignInAccount account) {
        if (account != null) {

//            String personName = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
//            String personEmail = account.getEmail();
            String personId = account.getId();
            Uri personPhoto = account.getPhotoUrl();
            Log.d(TAG, "personGivenName: " + personGivenName + " - personFamilyName: " + personFamilyName);
            mListener.start_loading();
            if (personPhoto != null) {
                Log.d(TAG, "updateUI: NOT NULL");
                Log.d(TAG, "LoginGoogle: " + personPhoto.toString());
//                checkUser(personId, personFamilyName, personPhoto.toString());
                loginManage.loginWithSocial(personId, personFamilyName, personPhoto.toString());
            } else {
                Log.d(TAG, "updateUI: NULL");
                String url_google_default = "https://avapp.000webhostapp.com/foody/anh/anhuser/google_default.png";
//                checkUser(personId, personFamilyName, url_google_default);
                loginManage.loginWithSocial(personId, personFamilyName, url_google_default);
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            LoginGoogle(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d(TAG, "signInResult:failed code=" + e.getStatusCode());
            LoginGoogle(null);
        }
    }

    // request code facebook: 64206
    // request code zalo: 64725
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "requestCode: " + requestCode);
        Log.d(TAG, "resultCode: " + resultCode);
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Log.e(TAG, key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
            }
        }

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else if (requestCode == 64206) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ILoginZalo || context instanceof ILoading || context instanceof ILogin) {
            mListener = (ILoading) context;
            mLoginZaloListener = (ILoginZalo) context;
            mLoginListener = (ILogin) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentAListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mLoginZaloListener = null;
        mLoginListener = null;
    }

}
