package com.danhtran12797.thd.foodyapp.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.activity.listener.ILoading;
import com.danhtran12797.thd.foodyapp.adapter.DialogUserLoveAdapter;
import com.danhtran12797.thd.foodyapp.fragment.ConnectionFragment;
import com.danhtran12797.thd.foodyapp.model.Category;
import com.danhtran12797.thd.foodyapp.model.JWTToken;
import com.danhtran12797.thd.foodyapp.model.Product;
import com.danhtran12797.thd.foodyapp.model.ShopingCart;
import com.danhtran12797.thd.foodyapp.model.User;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.DataService;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;
import com.victor.loading.rotate.RotateLoading;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.danhtran12797.thd.foodyapp.ultil.Ultil.arrShoping;

public class DetailProductActivity extends AppCompatActivity implements View.OnClickListener, ILoading {

    private static final String TAG = "DetailProductActivity";
    CoordinatorLayout coordinatorLayout;
    Toolbar toolbar;
    LottieAnimationView button_like;
    boolean isCheckLike = false;
    ImageView imageView;
    Button btnAddToCard;
    TextView txtCountLove;
    TextView txtSale1;
    TextView txtSale2;
    TextView txtPrice;
    TextView txtCost;
    TextView txtDesc;
    TextView txtCompo;
    TextView txtCategoryProduct;

    RotateLoading rotateLoading;
    FrameLayout layout_container;

    Product product;
    ArrayList<User> arrCountLove = null;
    DecimalFormat decimalFormat;

    BottomSheetDialog bottomSheetDialog;
    View viewBottomSheet;

    // bottom sheet
    Button btn_seen_shoping;
    ImageView img_botoom_sheet;
    ImageView img_close_botoom_sheet;
    TextView txt_name_bottom_sheet;
    TextView txt_price_bottom_sheet;
    TextView txt_category_bottom_sheet;

    int position = -1;
    double gia;
    int sale1;
    String category;

    private TextView textCartItemCount;
    private View actionView;

    private ILoading mLisener;

    JWTToken jwtToken = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);

        mLisener = this;

        Log.d(TAG, "onCreate");
        decimalFormat = new DecimalFormat("###,###,###");

        initView();
        initActionBar();
        eventAddToCard();
        eventLove();

        if (Ultil.isNetworkConnected(this)) {
            Intent intent = getIntent();
            if (intent.hasExtra("notify")) {
                getProduct(intent.getStringExtra("notify"));
                Log.d(TAG, "notify: " + intent.getStringExtra("notify"));
            } else {
                Log.d(TAG, "detail_product: ");
                product = (Product) intent.getSerializableExtra("detail_product");
                setDataUIProduct(product);
                getDataCountLove(product);
                GetCategoryProduct(product.getId());
                toolbar.setTitle(product.getName());
            }
            Ultil.arrShoping = Ultil.getShopingCart(this);
            Ultil.user = Ultil.getUserPreference(this);
            jwtToken = Ultil.getTokenPreference(this);
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.layout_container, new ConnectionFragment()).commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (actionView != null) {
            setupBadge();
        }
    }

    private void setTitle(Product product) {
        toolbar.setTitle(product.getName());
    }

    private void getProduct(String id_product) {
        mLisener.start_loading();

        DataService dataService = APIService.getService();
        Call<List<Product>> callback = dataService.GetProduct(id_product);
        callback.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                mLisener.stop_loading(true);
                ArrayList<Product> arrayList = (ArrayList<Product>) response.body();
                product = arrayList.get(0);
                Log.d(TAG, "onResponse: " + product.toString());
                setDataUIProduct(product);
                getDataCountLove(product);
                GetCategoryProduct(product.getId());
                setTitle(product);
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                mLisener.stop_loading(false);
            }
        });
    }

    private void GetCategoryProduct(String idsp) {
        mLisener.start_loading();

        DataService dataService = APIService.getService();
        Call<List<Category>> callback = dataService.GetCategoryProduct(idsp);
        callback.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                ArrayList<Category> arrayList = (ArrayList<Category>) response.body();
                Log.d(TAG, "name category: " + arrayList.get(0).getName());
                txtCategoryProduct.setVisibility(View.VISIBLE);
                category = arrayList.get(0).getName();
                txtCategoryProduct.setText(arrayList.get(0).getName());
                txt_category_bottom_sheet.setText(arrayList.get(0).getName());

                mLisener.stop_loading(true);
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                mLisener.stop_loading(false);
            }
        });
    }

    private void eventLove() {
        button_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCheckLike) {
                    deleteUserLoveProduct(jwtToken.getToken());
                } else {
                    if (Ultil.user != null) {
                        insertUserLoveProduct(jwtToken.getToken());
                    } else {
                        Ultil.dialogQuestionLogin("Bạn cần đăng nhập để thích sản phẩm này", "product", DetailProductActivity.this, product);
                    }
                }
            }
        });
    }

    private void set_button_like(boolean status) {
        Log.d(TAG, "set_button_like: " + status);
        if (status) {
            button_like.setSpeed(1);
            button_like.playAnimation();
            isCheckLike = true;
        } else {
            button_like.setSpeed(-1);
            button_like.playAnimation();
            isCheckLike = false;
        }
    }

    private void deleteUserLoveProduct(String token) {
        mLisener.start_loading();

        DataService dataService = APIService.getService();
        Call<String> callback = dataService.DeleteUserLoveProduct(product.getId(), token);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mLisener.stop_loading(true);
                if (response.isSuccessful()) {
                    String message = response.body();
                    if (message.equals("success")) {
                        getDataCountLove(product);
                    }
                } else {
                    Ultil.logout_account(DetailProductActivity.this);
                    Ultil.dialogRequestTimeOut(DetailProductActivity.this);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                mLisener.stop_loading(false);
            }
        });
    }

    private void insertUserLoveProduct(String token) {
        mLisener.start_loading();
        DataService dataService = APIService.getService();
        Call<String> callback = dataService.InsertUserLoveProduct(product.getId(), token);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mLisener.stop_loading(true);
                if (response.isSuccessful()) {
                    String message = response.body();
                    if (message.equals("success")) {
                        getDataCountLove(product);
                    }
                } else {
                    Ultil.logout_account(DetailProductActivity.this);
                    Ultil.dialogRequestTimeOut(DetailProductActivity.this);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                mLisener.stop_loading(false);
            }
        });
    }

    private void eventAddToCard() {
        btnAddToCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShopingCart shopingCart = new ShopingCart(product.getId(), product.getName(), product.getImage(), gia, 1);
                shopingCart.setCategoty(category);
                Ultil.add_product_shoping_cart(shopingCart);
                Ultil.setShopingCart(DetailProductActivity.this);
                bottomSheetDialog.show();
                setupBadge();
            }
        });
    }

    private void getDataCountLove(Product product) {
        mLisener.start_loading();
        DataService dataService = APIService.getService();
        Call<List<User>> callback = dataService.CountLoveProduct(product.getId());
        callback.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                mLisener.stop_loading(true);
                Log.d(TAG, "code: " + response.code());
                if (response.isSuccessful()) {
                    arrCountLove = (ArrayList<User>) response.body();
                    setDataLinkUserLove();
                } else {
                    if (response.code() == 403) {
                        if (arrCountLove != null) {
                            arrCountLove.clear();
                            arrCountLove = null;
                            setDataLinkUserLove();
                        } else {
                            txtCountLove.setText("Hãy là người đầu tiên thích thực đơn này");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                mLisener.stop_loading(false);
                Log.d(TAG, "Error: " + t.getMessage());
            }
        });
    }

    private void setDataLinkUserLove() {
        if (arrCountLove != null) {
            txtCountLove.setText(arrCountLove.size() + " lượt yêu thích");
            txtCountLove.setPaintFlags(txtCountLove.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            set_button_like(setButtonLove());
        } else {
            txtCountLove.setPaintFlags(0);
            txtCountLove.setText("Hãy là người đầu tiên thích thực đơn này");
            set_button_like(setButtonLove());
        }
    }

    private void setDataUIProduct(Product product) {
        Picasso.get().load(Ultil.url_image_product + product.getImage())
                .placeholder(R.drawable.noimage)
                .error(R.drawable.error)
                .into(imageView);
        gia = Double.parseDouble(product.getPrice());
        sale1 = Integer.parseInt(product.getSale1());

        if (sale1 != 0) {
            txtSale1.setVisibility(View.VISIBLE);
            txtCost.setVisibility(View.VISIBLE);
            txtSale1.setText(sale1 + "% OFF");
            txtCost.setPaintFlags(txtCost.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            txtCost.setText(decimalFormat.format(gia) + " Đ/Phần");
            gia = gia * (1 - (double) sale1 / 100);
        } else {
            txtSale1.setVisibility(View.GONE);
            txtCost.setVisibility(View.GONE);
        }

        if (!product.getSale2().equals("")) {
            txtSale2.setVisibility(View.VISIBLE);
            txtSale2.setText(product.getSale2());
        } else {
            txtSale2.setVisibility(View.GONE);
        }

        txtPrice.setText(decimalFormat.format(gia) + " Đ/Phần");
        txtDesc.setText(product.getDesc());
        txtCompo.setText(product.getCompo());

        //bottom sheet
        txt_name_bottom_sheet.setText(product.getName());
        txt_price_bottom_sheet.setText(decimalFormat.format(gia) + " VNĐ");

        Picasso.get().load(Ultil.url_image_product + product.getImage())
                .placeholder(R.drawable.noimage)
                .error(R.drawable.error)
                .into(img_botoom_sheet);
    }

    private void show_dialog_user_love() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_list_user_love);

        TextView txtCountUser = dialog.findViewById(R.id.txt_dialog_count_love);
        CircleImageView imgClose = dialog.findViewById(R.id.img_exit_dialog);
        ListView listView = dialog.findViewById(R.id.lv_dialog_user_love);

        DialogUserLoveAdapter adapter = new DialogUserLoveAdapter(dialog.getContext(), arrCountLove);
        listView.setAdapter(adapter);
        txtCountUser.setText(arrCountLove.size() + " lượt yêu thích");

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void initView() {
        layout_container = findViewById(R.id.layout_container);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        rotateLoading = findViewById(R.id.rotateLoading);
        layout_container = findViewById(R.id.layout_container);

        button_like = findViewById(R.id.button_like);
        imageView = findViewById(R.id.imgProductDetail);
        txtCost = findViewById(R.id.txtCostDetail);
        txtPrice = findViewById(R.id.txtPriceDetail);
        txtDesc = findViewById(R.id.txtDescDetail);
        txtSale1 = findViewById(R.id.txtSale1Detail);
        txtSale2 = findViewById(R.id.txtSale2Detail);
        txtCountLove = findViewById(R.id.txtCountLoveDetail);
        txtCompo = findViewById(R.id.txtCompo);
        txtCategoryProduct = findViewById(R.id.txtCategoryProduct);

        btnAddToCard = findViewById(R.id.btnAddToCard);

        // bottom sheet
        bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);

        viewBottomSheet = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_shoping_cart, null);
        txt_price_bottom_sheet = viewBottomSheet.findViewById(R.id.txt_price_bottom_sheet);
        txt_name_bottom_sheet = viewBottomSheet.findViewById(R.id.txt_name_bottom_sheet);
        img_botoom_sheet = viewBottomSheet.findViewById(R.id.img_bottom_sheet);
        img_close_botoom_sheet = viewBottomSheet.findViewById(R.id.img_close_bottom_sheet);
        btn_seen_shoping = viewBottomSheet.findViewById(R.id.btn_seen_shoping);
        txt_category_bottom_sheet = viewBottomSheet.findViewById(R.id.txt_category_bottom_sheet);
        btn_seen_shoping.setOnClickListener(this);
        img_close_botoom_sheet.setOnClickListener(this);
        txtCountLove.setOnClickListener(this);

        bottomSheetDialog.setContentView(viewBottomSheet);
    }

    private boolean setButtonLove() {
        if (Ultil.user != null) {
            if (arrCountLove != null) {
                for (int i = 0; i < arrCountLove.size(); i++) {
                    if (Ultil.user.getId().equals(arrCountLove.get(i).getId())) {
                        position = i;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void initActionBar() {
        toolbar = findViewById(R.id.toolbar_detail_product);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_product, menu);
        final MenuItem menuItem = menu.findItem(R.id.menu_detail_product_card);

        actionView = menuItem.getActionView();
        textCartItemCount = actionView.findViewById(R.id.cart_badge);

        setupBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_detail_product_card:
                startActivity(new Intent(this, ShopingCartActivity.class));
                break;
            case R.id.menu_detail_product_share:
                getStoragePermission();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void ShareImage() {
        Uri uri;
        try {
            File file = saveBitmap(takeScreenshot());
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            Intent intent = new Intent("android.intent.action.SEND");
            intent.putExtra("android.intent.extra.SUBJECT", "Hỏi bạn bè!");
            intent.putExtra("android.intent.extra.TITLE", "Tải Game ứng dụng THD Foody với mình nào!");
            intent.putExtra("android.intent.extra.STREAM", uri);
            intent.setType("image/*");
            startActivity(intent);
        } catch (Exception unused) {
            Toast.makeText(this, "Error2: " + unused.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("UUU", "ShareImage: " + unused.getMessage());
        }
    }

    public File saveBitmap(Bitmap bitmap) {
        File imagepath = null;
        if (Environment.getExternalStorageState().equals("mounted")) {
            File externalStorageDirectory = Environment.getExternalStorageDirectory();
            StringBuilder sb = new StringBuilder();
            sb.append(externalStorageDirectory.getAbsolutePath());
            sb.append("/THD_Foody");
            File file = new File(sb.toString());
            if (!file.exists()) {
                file.mkdirs();
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append(file.getPath());
            sb2.append("/screen_thd_foody.jpg");
            imagepath = new File(sb2.toString());
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(imagepath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (Exception e) {
                Log.d(TAG, "saveBitmap: " + e.getMessage());
            }
        }
        return imagepath;
    }

    public Bitmap takeScreenshot() {
        View rootView = getWindow().getDecorView().getRootView().findViewById(android.R.id.content);
        rootView.setDrawingCacheEnabled(false);
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    private void getStoragePermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            ShareImage();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ShareImage();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_seen_shoping:
                startActivity(new Intent(this, ShopingCartActivity.class));
                bottomSheetDialog.dismiss();
                break;
            case R.id.img_close_bottom_sheet:
                bottomSheetDialog.dismiss();
                break;
            case R.id.txtCountLoveDetail:
                if (arrCountLove != null) {
                    show_dialog_user_love();
                }
                break;
        }
    }

    @Override
    public void start_loading() {
        rotateLoading.start();
        layout_container.setVisibility(View.VISIBLE);
        btnAddToCard.setVisibility(View.GONE);
    }

    @Override
    public void stop_loading(boolean isConnect) {
        rotateLoading.stop();
        layout_container.setVisibility(View.GONE);
        btnAddToCard.setVisibility(View.VISIBLE);
        if (!isConnect) {
            Ultil.show_snackbar(coordinatorLayout, button_like);
        }
    }

}
