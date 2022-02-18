package com.danhtran12797.thd.foodyapp.activity;

import static com.danhtran12797.thd.foodyapp.ultil.Ultil.arrShoping;
import static com.danhtran12797.thd.foodyapp.ultil.Ultil.user;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.activity.listener.ILoading;
import com.danhtran12797.thd.foodyapp.adapter.MainFragmentAdapter;
import com.danhtran12797.thd.foodyapp.fragment.ConnectionFragment;
import com.danhtran12797.thd.foodyapp.fragment.DealFragment;
import com.danhtran12797.thd.foodyapp.fragment.MostLikeFragment;
import com.danhtran12797.thd.foodyapp.fragment.NewProductFragment;
import com.danhtran12797.thd.foodyapp.model.JWTToken;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.DataService;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;
import com.victor.loading.rotate.RotateLoading;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ILoading{

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LinearLayout layout_infor_user;
    private LinearLayout layout_login_register;
    private Button btn_login, btn_register;
    private Intent intent;
    private View view_header_drawer;
    private TextView txtNameUser, txtEmailUser, txtSetEmailUser, txtVerifiedEmailUser;
    private CircleImageView imgUserHeader;
    private TextView txtAllProduct;
    private TextView textCartItemCount;
    private View view_badge_cart;
    private RotateLoading rotateLoading;
    private FrameLayout layout_container;
    private RelativeLayout layout_main;

    private long backPressedTime;
    private Toast backToast;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initDrawer();
        if (Ultil.isNetworkConnected(this)) {
            initTabLayout();
            initTextViewAll();
            Ultil.arrShoping = Ultil.getShopingCart(this);
            checkRequestTimeOut();
            getToken();
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.layout_container, new ConnectionFragment()).commit();
        }
    }

    private void checkToken(String token_notifi){
        user = Ultil.getUserPreference(this);
        if (user != null) {
            JWTToken jwtToken=Ultil.getTokenPreference(this);
            DataService dataService = APIService.getService();
            Call<String> callback = dataService.CheckToken(jwtToken.getToken(), token_notifi);
            callback.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.d(TAG, "update token: "+response.body());
                    if(response.isSuccessful()){
                        if(response.body().equals("update_token_success")){
                            Log.d(TAG, "onResponse: Update Token Success");
                        }
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });
        }
    }

    private void checkRequestTimeOut() {
        user = Ultil.getUserPreference(this);
        if (user != null) {
            JWTToken jwtToken=Ultil.getTokenPreference(this);
            DataService dataService = APIService.getService();
            Call<String> callback = dataService.CheckRequestTimeOut(jwtToken.getToken());
            callback.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if(response.isSuccessful()){
                        if(response.body().equals("success")){

                        }
                    }else{
                        Ultil.logout_account(MainActivity.this);
                        Ultil.dialogRequestTimeOut(MainActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });
        }
    }

    private void initTextViewAll() {
        txtAllProduct = findViewById(R.id.txt_all_product);
        txtAllProduct.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("BBB", "onCreateOptionsMenu: ");
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);

        final MenuItem menuItem = menu.findItem(R.id.menu_shoping_activity_main);

        view_badge_cart = menuItem.getActionView();
        textCartItemCount = view_badge_cart.findViewById(R.id.cart_badge);

        setupBadge();

        view_badge_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void setupBadge() {
        if (Ultil.arrShoping != null) {
            textCartItemCount.setText(String.valueOf(Math.min(arrShoping.size(), 99)));
            if (textCartItemCount.getVisibility() != View.VISIBLE) {
                textCartItemCount.setVisibility(View.VISIBLE);
            }
        } else {
            if (textCartItemCount.getVisibility() != View.GONE) {
                textCartItemCount.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refesh_activity_main:
                finish();
                startActivity(getIntent());
                break;
            case R.id.menu_search_activity_main:
                intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_shoping_activity_main:
                intent = new Intent(this, ShopingCartActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initHeaderView() {
        layout_infor_user = view_header_drawer.findViewById(R.id.layout_infor_user_header);
        layout_login_register = view_header_drawer.findViewById(R.id.layout_header_login_register);
        btn_login = view_header_drawer.findViewById(R.id.btn_login_header);
        btn_register = view_header_drawer.findViewById(R.id.btn_register_header);
        txtNameUser = view_header_drawer.findViewById(R.id.txt_name_header);
        txtEmailUser = view_header_drawer.findViewById(R.id.txt_email_header);
        txtSetEmailUser = view_header_drawer.findViewById(R.id.txt_set_email_header);
        txtVerifiedEmailUser = view_header_drawer.findViewById(R.id.txt_notifi_email_header);
        imgUserHeader = view_header_drawer.findViewById(R.id.img_avatar_user_header);

        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("BBB", "onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("BBB", "onStart");
        if (view_badge_cart != null) {
            setupBadge();
        }
        user = Ultil.getUserPreference(this);
        if (user != null) {
            Log.d("BBB", "good login");
            layout_infor_user.setVisibility(View.VISIBLE);
            layout_login_register.setVisibility(View.GONE);

            set_nav_header();
        } else {
            Log.d("BBB", "faild login");
            layout_infor_user.setVisibility(View.GONE);
            layout_login_register.setVisibility(View.VISIBLE);
        }
    }

    private void set_nav_header() {
        if (!user.getEmail().equals("")) {
            txtSetEmailUser.setVisibility(View.GONE);
            txtEmailUser.setVisibility(View.VISIBLE);
            txtEmailUser.setText(user.getEmail());
            Log.d(TAG, "getVerified: "+user.getVerified());
            if(user.getVerified().equals("1")){
                txtVerifiedEmailUser.setVisibility(View.GONE);
            }
        }else{
            txtVerifiedEmailUser.setVisibility(View.GONE);
        }
        txtNameUser.setText(user.getName());
//        .memoryPolicy(MemoryPolicy.NO_CACHE)

        Picasso.get().load(Ultil.url_image_avatar + user.getAvatar())
                .placeholder(R.drawable.noimage)
                .error(R.drawable.error)
                .into(imgUserHeader);
        txtSetEmailUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AccountUserActivity.class));
            }
        });
        txtVerifiedEmailUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AccountUserActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("BBB", "onResume");
    }

    private void initDrawer() {
        rotateLoading = findViewById(R.id.rotateLoading);
        layout_main = findViewById(R.id.layout_main);
        layout_container = findViewById(R.id.layout_container);
        layout_container.setVisibility(View.VISIBLE);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        view_header_drawer = navigationView.getHeaderView(0);
        initHeaderView();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initTabLayout() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        MainFragmentAdapter adapter = new MainFragmentAdapter(getSupportFragmentManager());

        Fragment newProductFragment = new NewProductFragment();

        adapter.addFragment(newProductFragment, "Mới Nhất");
        adapter.addFragment(new DealFragment(), "Khuyến Mãi");
        adapter.addFragment(new MostLikeFragment(), "Lượt thích");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
    }

    private void dialog_infor_app() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông tin ứng dụng");
        builder.setIcon(R.drawable.icon_app_design);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_infor_app, null);
        builder.setView(dialogView);

        TextView txtDeveloper = dialogView.findViewById(R.id.txtDeveloper);
        txtDeveloper.setMovementMethod(LinkMovementMethod.getInstance());

        TextView txtPrivacy = dialogView.findViewById(R.id.txtPrivacyPolicy);
        txtPrivacy.setMovementMethod(LinkMovementMethod.getInstance());

        builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void exit() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("THD Foody");
        alertDialogBuilder
                .setMessage("Bạn có muốn thoát ứng dụng?")
                .setCancelable(false)
                .setPositiveButton("Đồng ý",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })

                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_category:
                startActivity(new Intent(this, CategoryActivity.class));
                break;
            case R.id.nav_my_fav:
                if (user != null) {
                    intent = new Intent(this, ListUserProductLoveActivity.class);
                    startActivity(intent);
                } else {
                    Ultil.dialogQuestionLogin("Bạn cần đăng nhập để xem danh sách thực đơn bạn đã thích", "favorites", this, null);
                }
                break;
            case R.id.nav_account:
                if (user != null) {
                    startActivity(new Intent(this, AccountUserActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_list_order:
                if (user != null) {
                    startActivity(new Intent(this, OrderActivity.class));
                } else {
                    Ultil.dialogQuestionLogin("Bạn cần đăng nhập để xem danh sách hóa đơn của bạn", "orders", this, null);
                }
                break;
            case R.id.nav_shop:
                startActivity(new Intent(this, ShopingCartActivity.class));
                break;
            case R.id.nav_location:
                startActivity(new Intent(this, MapsActivity.class));
                break;
            case R.id.nav_about:
                //Ultil.removeShopingCart(this);
                //Ultil.removeAddressShipping(this);
                dialog_infor_app();
                break;
            case R.id.nav_rate:
                rateMe();
                break;
            case R.id.nav_share:
                shareAppLinkViaFacebook();
                break;
            case R.id.nav_exit:
                exit();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult();
                        checkToken(token);
                        Log.d(TAG, "Token: " + token);
                    }
                });
    }



    public void shareAppLinkViaFacebook() {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.SUBJECT", "Trải nghiệm THD Foody với mình nào bạn!");
        intent.putExtra("android.intent.extra.TEXT", "https://play.google.com/store/apps/details?id=" + getPackageName()); // or BuildConfig.APPLICATION_ID
        startActivity(Intent.createChooser(intent, "Chia THD Foody cho bạn bè!"));
    }

    public void rateMe() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login_header:
                intent = new Intent(this, LoginActivity.class);
                intent.putExtra("login_register", 0);
                startActivity(intent);
                break;
            case R.id.btn_register_header:
                intent = new Intent(this, LoginActivity.class);
                intent.putExtra("login_register", 1);
                startActivity(intent);
                break;
            case R.id.txt_all_product:
                intent = new Intent(this, AllProductActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("BBB", "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("BBB", "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("BBB", "onDestroy: ");
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Chạm lần nữa để thoát", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();
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
            Ultil.show_snackbar(layout_main, null);
        }
    }
}
