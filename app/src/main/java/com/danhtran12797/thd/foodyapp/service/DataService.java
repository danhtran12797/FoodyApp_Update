package com.danhtran12797.thd.foodyapp.service;

import com.danhtran12797.thd.foodyapp.model.Banner;
import com.danhtran12797.thd.foodyapp.model.Category;
import com.danhtran12797.thd.foodyapp.model.JWTToken;
import com.danhtran12797.thd.foodyapp.model.Order;
import com.danhtran12797.thd.foodyapp.model.Payment;
import com.danhtran12797.thd.foodyapp.model.Product;
import com.danhtran12797.thd.foodyapp.model.User;
import com.danhtran12797.thd.foodyapp.model.braintree.BraintreeResponse;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;


public interface DataService {
    @GET("banner.php")
    Call<List<Banner>> GetDatabanner();

    @GET("RecentProduct.php")
    Call<List<Product>> GetRecentProduct();

    @GET("DealProduct.php")
    Call<List<Product>> GetDealProduct();

    @GET("LoveProduct.php")
    Call<List<Product>> GetLoveProduct();

    @GET("category.php")
    Call<List<Category>> GetCategory();

//    @GET("GetTotalPage.php")
//    Call<String> GetTotalPage(@Query("iddmm") String iddm);
//
//    @GET("GetDetailCategory.php")
//    Call<List<Product>> GetDetailCategory(@Query("iddm") String iddm, @Query("page") int page);

    @GET("GetTotalAllPage.php")
    Call<String> GetTotalAllPage();

    @GET("GetAllProduct.php")
    Call<List<Product>> GetAllProduct(@Query("page") int page, @Query("view_type") int view_type, @Query("iddm") int iddm);

    @FormUrlEncoded
    @POST("CountLoveProduct.php")
    Call<List<User>> CountLoveProduct(@Field("idsp") String idsp);

//    @FormUrlEncoded
//    @POST("Login.php")
//    Call<List<User>> Login(@Field("username") String username, @Field("password") String password);

//    @FormUrlEncoded
//    @POST("LoginSocial.php")
//    Call<JWTToken> LoginSocial(@Field("id_user") String id_user);

    @FormUrlEncoded
    @POST("LoginSocial.php")
    Observable<JWTToken> LoginSocial(@Field("id_user") String id_user);

//    @FormUrlEncoded
//    @POST("LoginJWT.php")
//    Call<JWTToken> LoginJWT(@Field("username") String username, @Field("password") String password);
//
//    @GET("GetUser.php")
//    Call<List<User>> GetUser(@Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("LoginJWT.php")
    Observable<JWTToken> LoginJWT(@Field("username") String username, @Field("password") String password);

    @GET("GetUser.php")
    Observable<List<User>> GetUser(@Header("Authorization") String authorization);

    @Multipart
    @POST("UploadImage.php")
    Call<String> UploadImage(@Part MultipartBody.Part photo);

//    @Multipart
//    @POST("UpdateImage.php")
//    Call<String> UpdateImage(@Part MultipartBody.Part photo, @Part("name_file") RequestBody name_file);

    @FormUrlEncoded
    @POST("Register.php")
    Call<String> Register(
            @Field("id_user") String id_user
            , @Field("name") String name
            , @Field("username") String username
            , @Field("password") String password
            , @Field("email") String email
            , @Field("address") String address
            , @Field("phone") String phone
            , @Field("avatar") String avatar);

//    @FormUrlEncoded
//    @POST("RegisterSocial.php")
//    Call<String> RegisterSocial(
//            @Field("id_user") String id
//            , @Field("name") String name
//            , @Field("username") String username
//            , @Field("password") String password
//            , @Field("email") String email
//            , @Field("address") String address
//            , @Field("phone") String phone
//            , @Field("avatar") String avatar);

    @FormUrlEncoded
    @POST("UpdateUser.php")
    Call<String> UpdateUser(@Header("Authorization") String authorization
            , @Field("name") String name
            , @Field("email") String email
            , @Field("address") String address
            , @Field("phone") String phone
            , @Field("avatar") String avatar);

    @FormUrlEncoded
    @POST("UpdatePassword.php")
    Call<String> UpdatePassword(@Header("Authorization") String authorization, @Field("pass_new") String pass_new);

    @FormUrlEncoded
    @POST("GetProductToBanner.php")
    Call<List<Product>> GetProductToBanner(@Field("id_product") String id_product);

    @FormUrlEncoded
    @POST("DeleteUserLoveProduct.php")
    Call<String> DeleteUserLoveProduct(@Field("idsp") String idsp, @Header("Authorization") String authorization);

    @GET("DeleteAllUserLoveProduct.php")
    Call<String> DeleteAllUserLoveProduct(@Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("InsertUserLoveProduct.php")
    Call<String> InsertUserLoveProduct(@Field("idsp") String idsp, @Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("InsertToken.php")
    Call<String> InsertToken(@Field("token") String token);

    @FormUrlEncoded
    @POST("GetProductUserLove.php")
    Call<List<Product>> GetProductUserLove(@Field("iduser") String iduser);

//    @GET("GetProductFromSearch.php")
//    Call<List<Product>> GetProductFromSearch(@Query("key_name") String key_name);

    @GET("GetProductFromSearchAll.php")
    Call<List<Product>> GetProductFromSearchAll(@Query("key_name") String key_name, @Query("view_type") String view_type);

    @FormUrlEncoded
    @POST("GetCategoryProduct.php")
    Call<List<Category>> GetCategoryProduct(@Field("idsp") String idsp);

    @FormUrlEncoded
    @POST("Payment.php")
    Observable<String> Payment(@Header("Authorization") String authorization
            , @Field("trans_id") String trans_id
            , @Field("order_id") String order_id
            , @Field("amount") String amount
            , @Field("type") String type
            , @Field("number") String number
            , @Field("create_date") String create_date
    );

    @FormUrlEncoded
    @POST("Orders.php")
    Observable<String> Orders(@Header("Authorization") String authorization
            , @Field("name") String name
            , @Field("phone") String phone
            , @Field("address") String address
            , @Field("request") String request
            , @Field("email") String email
            , @Field("delivery") String delivery
            , @Field("payment") String payment
            , @Field("status") String status
            , @Field("id_order") String id_order
            , @Field("create_date") String create_date
    );

    @FormUrlEncoded
    @POST("OrdersDetail.php")
    Observable<String> OrdersDetail(@Field("json_shoping_cart") String json);

    @FormUrlEncoded
    @POST("GetOrder.php")
    Call<List<Order>> GetOrder(@Field("user_id") String user_id, @Field("order_id") String order_id);

    @FormUrlEncoded
    @POST("Update_Order.php")
    Observable<String> Update_Order(@Header("Authorization") String authorization, @Field("id_order") String id_order
            , @Field("status") String status, @Field("payment") String payment, @Field("request") String request
            , @Field("delivery") String delivery, @Field("phone") String phone, @Field("address") String address
            , @Field("email") String email, @Field("name") String name, @Field("update_date") String update_date);

    @GET("CheckRequestTimeOut.php")
    Call<String> CheckRequestTimeOut(@Header("Authorization") String authorization);

    @GET("VerifiEmail.php")
    Call<String> VerifiEmail(@Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("CheckToken.php")
    Call<String> CheckToken(@Header("Authorization") String authorization, @Field("token") String token);

    @FormUrlEncoded
    @POST("GetProduct.php")
    Call<List<Product>> GetProduct(@Field("id_product") String id_product);

    @FormUrlEncoded
    @POST("GetPayment.php")
    Call<Payment> GetPayment(@Field("order_id") String order_id);

    @FormUrlEncoded
    @POST("DownloadSocial.php")
    Call<String> DownloadSocial(@Field("name_file") String name_file, @Field("url_avatar") String url_avatar);

    @FormUrlEncoded
    @POST("CheckPass.php")
    Call<String> CheckPass(@Header("Authorization") String authorization, @Field("password") String password);

    @FormUrlEncoded
    @POST("ChangePass.php")
    Call<String> ChangePass(@Field("email") String email, @Field("pass_new") String pass_new);

    @FormUrlEncoded
    @POST("ResetPassword.php")
    Call<String> ResetPassword(@Field("email") String email);

    @FormUrlEncoded
    @POST("braintree/checkout.php")
    Call<BraintreeResponse> sendPaymentBriantree(@Field("amount") String amount, @Field("nonce") String nonce, @Field("order_id") String order_id);

    @GET("braintree/get_token.php")
    Call<String> getTokenBriantree();

    @FormUrlEncoded
    @POST("braintree/trans_refund.php")
    Call<String> transRefundBriantree(@Field("transaction_id") String transaction_id);

}
