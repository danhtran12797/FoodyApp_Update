package com.danhtran12797.thd.foodyapp.ultil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.View;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.activity.AddressOrderActivity;
import com.danhtran12797.thd.foodyapp.activity.DetailProductActivity;
import com.danhtran12797.thd.foodyapp.activity.ListUserProductLoveActivity;
import com.danhtran12797.thd.foodyapp.activity.LoginActivity;
import com.danhtran12797.thd.foodyapp.activity.MainActivity;
import com.danhtran12797.thd.foodyapp.activity.OrderActivity;
import com.danhtran12797.thd.foodyapp.activity.SplashActivity;
import com.danhtran12797.thd.foodyapp.model.AddressShipping;
import com.danhtran12797.thd.foodyapp.model.JWTToken;
import com.danhtran12797.thd.foodyapp.model.Product;
import com.danhtran12797.thd.foodyapp.model.ShopingCart;
import com.danhtran12797.thd.foodyapp.model.User;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

public class Ultil {
    public static String url_image_banner = "https://applon12797.000webhostapp.com/foody/anh/quangcao/";
    public static String url_image_product = "https://applon12797.000webhostapp.com/foody/anh/";
    public static String url_image_category = "https://applon12797.000webhostapp.com/foody/anh/danhmuc/";
    public static String url_image_avatar = "https://applon12797.000webhostapp.com/foody/anh/anhuser/";
    public static String url_image_notify = "https://applon12797.000webhostapp.com/foody/anh/thongbao/";

//    public static String url_image_banner = "http://192.168.42.107/foody/anh/quangcao/";
//    public static String url_image_product = "http://192.168.42.107/foody/anh/";
//    public static String url_image_category = "http://192.168.42.107/foody/anh/danhmuc/";
//    public static String url_image_avatar = "http://192.168.42.107/foody/anh/anhuser/";
//    public static String url_image_notify = "http://192.168.42.107/foody/anh/thongbao/";
    public static String NAME_SHARED_PREFERENCES = "shared preferences";
    public static String INFOR_USER = "infor user";
    public static String SHOPING_CART = "shoping cart";
    public static String ADDRESS_SHIPPING = "address shipping";
    public static String HISTORY_SEARCH = "history search";
    public static String TOKEN_USER = "token user";
    public static ArrayList<ShopingCart> arrShoping = null;
    public static ArrayList<AddressShipping> arrAddressShipping = null;
    public static ArrayList<String> arrHistory = null;
    public static User user = null;
    public static ProgressDialog progressDialog;

    public static void add_product_shoping_cart(ShopingCart cart) {
        boolean check = false;
        if (arrShoping != null) {
            for (int i = 0; i < arrShoping.size(); i++) {
                if (arrShoping.get(i).getId().equals(cart.getId())) {
                    int quantity = arrShoping.get(i).getQuantity();
                    arrShoping.get(i).setQuantity(quantity + 1);
                    check = true;
                    break;
                }
            }
            if (!check) {
                arrShoping.add(cart);
            }
        } else {
            Log.d("ooo", "arrShoping = null ");
            arrShoping = new ArrayList<>();
            arrShoping.add(cart);
        }
    }

    public static void removeAddressShipping(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(NAME_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.remove(ADDRESS_SHIPPING);
        editor.apply();
    }

    public static ArrayList<AddressShipping> getAddressShipping(Context context) {
        ArrayList<AddressShipping> addressShipping = null;
        SharedPreferences preferences = context.getSharedPreferences(NAME_SHARED_PREFERENCES, MODE_PRIVATE);
        String json_shoping_cart = preferences.getString(ADDRESS_SHIPPING, "");
        if (json_shoping_cart.equals("")) {
            return addressShipping;
        } else {
            Gson gson = new Gson();
            Type familyType = new TypeToken<ArrayList<AddressShipping>>() {
            }.getType();
            addressShipping = gson.fromJson(json_shoping_cart, familyType);
            return addressShipping;
        }
    }

    public static void setAddressShipping(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(NAME_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String json_address_shipping = gson.toJson(arrAddressShipping);
        editor.putString(ADDRESS_SHIPPING, json_address_shipping);
        editor.apply();
    }

    public static ArrayList<ShopingCart> getShopingCart(Context context) {
        ArrayList<ShopingCart> shopingCarts = null;
        SharedPreferences preferences = context.getSharedPreferences(NAME_SHARED_PREFERENCES, MODE_PRIVATE);
        String json_shoping_cart = preferences.getString(SHOPING_CART, "");
        Log.d("yyy", json_shoping_cart);
        if (json_shoping_cart.equals("")) {
            return shopingCarts;
        } else {
            Gson gson = new Gson();
            Type familyType = new TypeToken<ArrayList<ShopingCart>>() {
            }.getType();
            shopingCarts = gson.fromJson(json_shoping_cart, familyType);
            return shopingCarts;
        }
    }

    public static void setShopingCart(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(NAME_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String json_shoping_cart = gson.toJson(arrShoping);
        editor.putString(SHOPING_CART, json_shoping_cart);
        editor.apply();
    }

    public static void removeShopingCart(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(NAME_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.remove(SHOPING_CART);
        editor.apply();
    }

    public static User getUserPreference(Context context) {
        User user = null;
        SharedPreferences preferences = context.getSharedPreferences(NAME_SHARED_PREFERENCES, MODE_PRIVATE);
        String json_user = preferences.getString(INFOR_USER, "");
        if (json_user.equals("")) {
            return user;
        } else {
            Gson gson = new Gson();
            user = gson.fromJson(json_user, User.class);
        }
        return user;
    }

    public static void setUserPreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(NAME_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        if (user != null) {
            String json_user = gson.toJson(user);
            editor.putString(INFOR_USER, json_user);
            editor.apply();
        }
    }

    public static JWTToken getTokenPreference(Context context) {
        JWTToken jwtToken = null;
        SharedPreferences preferences = context.getSharedPreferences(NAME_SHARED_PREFERENCES, MODE_PRIVATE);
        String json_token = preferences.getString(TOKEN_USER, "");
        if (json_token.equals("")) {
            return jwtToken;
        } else {
            Gson gson = new Gson();
            jwtToken = gson.fromJson(json_token, JWTToken.class);
        }
        return jwtToken;
    }

    public static void setTokenPreference(Context context, JWTToken jwtToken) {
        SharedPreferences preferences = context.getSharedPreferences(NAME_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        if (jwtToken != null) {
            String json_token = gson.toJson(jwtToken);
            editor.putString(TOKEN_USER, json_token);
            editor.apply();
        }
    }

    public static void removeUserPreference(Context context) {
        user = null;
        SharedPreferences preferences = context.getSharedPreferences(NAME_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.remove(INFOR_USER);
        editor.apply();
    }

    public static void setHistorySearchPreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(NAME_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String json_history = gson.toJson(arrHistory);
        editor.putString(HISTORY_SEARCH, json_history);
        editor.apply();
    }

    public static ArrayList getHistorySearchPreference(Context context) {
        ArrayList<String> historys = null;
        SharedPreferences preferences = context.getSharedPreferences(NAME_SHARED_PREFERENCES, MODE_PRIVATE);
        String json_history = preferences.getString(HISTORY_SEARCH, "");
        Log.d("yyy", json_history);
        if (json_history.equals("")) {
            return historys;
        } else {
            Gson gson = new Gson();
            Type familyType = new TypeToken<ArrayList<String>>() {
            }.getType();
            historys = gson.fromJson(json_history, familyType);
            return historys;
        }
    }

    public static void removeHistorySearchPreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(NAME_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.remove(HISTORY_SEARCH);
        editor.apply();
    }

    public static void showDialog(Context context, String title, String message, Drawable drawable, View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title)
                .setIcon(drawable)
                .setView(view)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
    }

    public static void dialogQuestionLogin(String message, String code, Activity context, Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.icon_app_design);
        builder.setTitle("Bạn có muốn đăng nhập");
        builder.setMessage(message);
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(context, LoginActivity.class);
                intent.putExtra("login_to", code);

                if (product != null) {
                    intent.putExtra("user_love_product", product);
                    context.startActivity(intent);
                    context.finish();
                } else {
                    context.startActivity(intent);
                }
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    public static void dialogRequestTimeOut(Activity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Đã hết phiên làm việc. Vui lòng đăng nhập lại!");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(context, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static boolean restorePrefData(Context context) {
        SharedPreferences pref = context.getSharedPreferences(NAME_SHARED_PREFERENCES, MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("isIntroOpnend", false);
        return isIntroActivityOpnendBefore;
    }

    public static void savePrefsData(Context context) {
        SharedPreferences pref = context.getSharedPreferences(NAME_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpnend", true);
        editor.commit();
    }

    public static void show_snackbar(View layout, View view) {
        Snackbar snackbar = Snackbar.make(layout, "Vui lòng kiểm tra kết nối Internet!", Snackbar.LENGTH_LONG);
        if (view != null) {
            snackbar.setAction("Thử lại", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.performClick();
                }
            });
        }
        snackbar.show();
    }

    public static String generate_unique_id() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static boolean check_phone_valid(String phone, Context context) {
        String[] arr = context.getResources().getStringArray(R.array.frist_number_phone);
        String frist_number = phone.substring(0, 3);
        for (String s : arr) {
            if (s.equals(frist_number))
                return true;
        }
        return false;
    }

    public static Intent create_intent(String login_to, Activity context, Product product) {
        Intent intent = null;
        switch (login_to) {
            case "favorites":
                intent = new Intent(context, ListUserProductLoveActivity.class);
                break;
            case "orders":
                intent = new Intent(context, OrderActivity.class);
                break;
            case "carts":
                intent = new Intent(context, AddressOrderActivity.class);
                break;
            case "product":
                intent = new Intent(context, DetailProductActivity.class);
                intent.putExtra("detail_product", product);
                break;
            default:
                intent = new Intent(context, MainActivity.class);
                break;
        }
        return intent;
    }

    public static void logout_account(Activity activity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn) {
            LoginManager.getInstance().logOut();
        } else if (mGoogleSignInClient != null) {
            mGoogleSignInClient.signOut();
            mGoogleSignInClient.revokeAccess();
        }

        removeUserPreference(activity);
    }
}
