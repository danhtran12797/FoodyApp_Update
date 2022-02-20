package com.danhtran12797.thd.foodyapp.module.payment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.braintreepayments.api.dropin.DropInRequest;
import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.activity.DetailProductActivity;
import com.danhtran12797.thd.foodyapp.activity.OrderSuccessActivity;
import com.danhtran12797.thd.foodyapp.activity.listener.ILoading;
import com.danhtran12797.thd.foodyapp.constants.Parameter;
import com.danhtran12797.thd.foodyapp.model.AddressShipping;
import com.danhtran12797.thd.foodyapp.model.Payment;
import com.danhtran12797.thd.foodyapp.model.Product;
import com.danhtran12797.thd.foodyapp.model.braintree.BraintreeResponse;
import com.danhtran12797.thd.foodyapp.model.momo.AppPayRequest;
import com.danhtran12797.thd.foodyapp.model.momo.AppPayResponse;
import com.danhtran12797.thd.foodyapp.model.momo.PayConfirmationRequest;
import com.danhtran12797.thd.foodyapp.model.momo.PayConfirmationResponse;
import com.danhtran12797.thd.foodyapp.model.momo.TransactionRefundRequest;
import com.danhtran12797.thd.foodyapp.model.momo.TransactionRefundResponse;
import com.danhtran12797.thd.foodyapp.model.zalopay.ResponseCreateOrder;
import com.danhtran12797.thd.foodyapp.model.zalopay.ResponseRefund;
import com.danhtran12797.thd.foodyapp.service.APIMomoService;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.APIZaloPayService;
import com.danhtran12797.thd.foodyapp.service.DataMomoService;
import com.danhtran12797.thd.foodyapp.service.DataService;
import com.danhtran12797.thd.foodyapp.service.DataZaloPayService;
import com.danhtran12797.thd.foodyapp.ultil.Config;
import com.danhtran12797.thd.foodyapp.ultil.Encoder;
import com.danhtran12797.thd.foodyapp.ultil.RequestOrderZaloPay;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import maes.tech.intentanim.CustomIntent;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;
import vn.momo.momo_partner.AppMoMoLib;
import vn.zalopay.listener.ZaloPayListener;
import vn.zalopay.sdk.ZaloPayErrorCode;
import vn.zalopay.sdk.ZaloPaySDK;

import static com.danhtran12797.thd.foodyapp.constants.Parameter.APP_PAY_TYPE;
import static com.danhtran12797.thd.foodyapp.constants.Parameter.VERSION;

public class PaymentManager {

    public static final int REQUEST_CODE_BRAINTREE = 123;

    private String merchantName;
    private String merchantCode;
    private String order_id;
    private String storeId;
    private String storeName;
    private String description;
    private String secretKey;
    private String publicKey;
    private String request;
    private double amount;
    private double amount_usd;
    private String token_id_user;
    private AddressShipping address;
    private String delivery;
    private String payment;
    private boolean update;
    private String createDate;

    Activity mActivity;
    ILoading mLoading;
    OnInsertOrderListener mInserOrder;
    OnUpdateOrderListener onUpdateOrderListener;
    OnCancelOrderListener onCancelOrderListener;

    public PaymentManager(){
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        createDate = formatter.format(cld.getTime());
    }

    public PaymentManager(Activity activity, ILoading mLoading, String payment, OnCancelOrderListener onCancelOrderListener) {
        this();
        this.mActivity = activity;
        this.mLoading = mLoading;
        this.payment = payment;
        this.onCancelOrderListener = onCancelOrderListener;
        this.publicKey = mActivity.getString(R.string.publicKey);
        this.merchantCode = mActivity.getString(R.string.merchantCode);
    }

    public PaymentManager(Activity activity, ILoading mLoading) {
        this.mActivity = activity;
        this.mLoading = mLoading;
    }

    public PaymentManager(Activity activity, ILoading mLoading, String token_id_user, OnUpdateOrderListener listener) {
        this();
        this.mActivity = activity;
        this.onUpdateOrderListener = listener;
        this.mLoading = mLoading;
        this.token_id_user = token_id_user;
    }

    public PaymentManager(Activity mActivity, ILoading mLoading, OnInsertOrderListener mInserOrder, String request
            , double amount, String token_id_user, AddressShipping adress, String delivery, String payment, String order_id, boolean update) {
        this();
        this.mActivity = mActivity;
        this.mLoading = mLoading;
        this.mInserOrder = mInserOrder;

        this.address = adress;
        this.delivery = delivery;
        this.payment = payment;
        this.token_id_user = token_id_user;
        this.amount = amount;
        this.amount_usd = Math.ceil(amount / 23000);
        this.request = request;
        this.order_id = order_id;
        this.update = update;
        this.merchantName = mActivity.getString(R.string.merchantName);
        this.merchantCode = mActivity.getString(R.string.merchantCode);
        this.storeName = mActivity.getString(R.string.storeName);
        this.description = mActivity.getString(R.string.description);
        this.publicKey = mActivity.getString(R.string.publicKey);
        this.secretKey = mActivity.getString(R.string.secretKey);
        this.storeId = mActivity.getString(R.string.storeId);
    }

    public String getCreateDate(){
        return createDate;
    }

    public String getOrderId(){
        return order_id;
    }

    private Observable<String> create_obserable_update_order(String order_id, String payment, String status, String request, String delivery, String phone, String address, String email, String name) {
        return APIService.getService().Update_Order(token_id_user, order_id, status, payment, request, delivery, phone, address, email, name, createDate);
    }

    public void Update_Order(String order_id, String payment, String status, String request, String delivery, String phone, String address, String email, String name) {
        mLoading.start_loading();
        create_obserable_update_order(order_id, payment, status, request, delivery, phone, address, email, name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    Log.d("AAA", "Update_Order: " + s);
                    if (s.equals("success")) {
                        mLoading.stop_loading(true);
                        onUpdateOrderListener.orderSuccess();
                    }
                }, throwable -> {
                    Log.d("AAA", "Update_Order_error: " + throwable.getMessage());
                    check_response_code(((HttpException) throwable).code());
                });
    }

    public void getProduct(String id_product) {
        mLoading.start_loading();

        DataService dataService = APIService.getService();
        Call<List<Product>> callback = dataService.GetProduct(id_product);
        callback.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                mLoading.stop_loading(true);
                ArrayList<Product> arrayList = (ArrayList<Product>) response.body();
                Product product = arrayList.get(0);
                Intent intent = new Intent(mActivity, DetailProductActivity.class);
                intent.putExtra("detail_product", product);
                mActivity.startActivity(intent);
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.d("AAA", "onFailure: " + t.getMessage());
                mLoading.stop_loading(false);
            }
        });
    }

    //Get token through MoMo app
    public void requestPaymentMoMo(double amount) {
        AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT);
        AppMoMoLib.getInstance().setAction(AppMoMoLib.ACTION.PAYMENT);
        AppMoMoLib.getInstance().setActionType(AppMoMoLib.ACTION_TYPE.GET_TOKEN);

        Map<String, Object> eventValue = new HashMap<>();
        //client Required
        eventValue.put("merchantname", merchantName); //Tên đối tác. được đăng ký tại https://business.momo.vn. VD: Google, Apple, Tiki , CGV Cinemas
        eventValue.put("merchantcode", merchantCode); //Mã đối tác, được cung cấp bởi MoMo tại https://business.momo.vn
        eventValue.put("amount", amount); //Kiểu integer
        eventValue.put("action", "gettoken"); //gán nhãn
        eventValue.put("partner", "merchant"); //gán nhãn
        eventValue.put("username", Ultil.user.getUsername()); //tài khoản
        eventValue.put("description", description); //mô tả đơn hàng - short description

        AppMoMoLib.getInstance().requestMoMoCallBack(mActivity, eventValue);
    }

    private String jsonStringPayProcessing() {
        Gson gson = new Gson();

        Map<String, Object> rawData = new HashMap<>();
        rawData.put(Parameter.PARTNER_CODE, merchantCode);
        rawData.put(Parameter.PARTNER_REF_ID, order_id);
        rawData.put(Parameter.AMOUNT, amount);
        rawData.put(Parameter.DESCRIPTION, description);
        rawData.put(Parameter.PARTNER_NAME, merchantName);
        rawData.put(Parameter.PARTNER_TRANS_ID, String.valueOf(System.currentTimeMillis()));
        rawData.put(Parameter.STORE_ID, storeId);
        rawData.put(Parameter.STORE_NAME, storeName);

        return gson.toJson(rawData);
    }

    public String jsonRefund(){
        //vnp_Command = refund
        // https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?vnp_Amount=1100000&vnp_BankCode=NCB&vnp_Command=pay&vnp_CreateDate=20220218084856&vnp_CurrCode=VND&vnp_ExpireDate=20220218090356&vnp_IpAddr=192.165.0.151&vnp_Locale=vn&vnp_OrderInfo=Thanh+toan+don+hang+%2370338977&vnp_OrderType=other&vnp_ReturnUrl=https%3A%2F%2Fsandbox.vnpayment.vn%2Ftryitnow%2FHome%2FVnPayIPN&vnp_TmnCode=LD62C7RJ&vnp_TxnRef=70338977&vnp_Version=2.1.0&vnp_SecureHash=c0a38beb1033bc7fe6eba659c2151270610adcd8d167c189aa5cd8561ab515c5a1422a5072dec3c830371452bafc9464dc5229076d4cb1a8f41a8cedcc03a56a
        String vnp_TxnRef = "70338977";
        String vnp_TransDate = "20220218084856";
        String email = "danhtran12797@gmai.com";
        int amount = 1100000;
        String trantype = "02";
        String vnp_TmnCode = Config.vnp_TmnCode;
        String vnp_IpAddr = Config.getIpAddress(mActivity);

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "refund");
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Kiem tra ket qua GD OrderId:" + vnp_TxnRef);
        vnp_Params.put("vnp_TransDate", vnp_TransDate);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_CreateBy", email);
        vnp_Params.put("vnp_TransactionType", trantype);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        //Build data to hash and querystring
        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                }catch (Exception e){
                    Log.d("III", "jsonRefund: "+e.getMessage());
                }

                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = Config.hmacSHA512(Config.vnp_HashSecret , hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        return Config.vnp_apiUrl + "?" + queryUrl;
    }

    public String getUrlPaymentVNPAY(){
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other"; // billpayment, topup, fashion, other
        String vnp_TxnRef = order_id;
        String vnp_OrderInfo = "Thanh toan don hang #"+vnp_TxnRef;
        String vnp_IpAddr = Config.getIpAddress(mActivity);
        String vnp_TmnCode = Config.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(Math.round(amount)*100));
        vnp_Params.put("vnp_CurrCode", "VND");
        String bank_code = "NCB";
        if (bank_code != null && !bank_code.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bank_code);
        }
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = "vn";
        if (locate != null && !locate.isEmpty()) {
            vnp_Params.put("vnp_Locale", locate);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }
        vnp_Params.put("vnp_ReturnUrl", Config.vnp_Returnurl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        createDate = formatter.format(cld.getTime());

        vnp_Params.put("vnp_CreateDate", createDate);
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        //Add Params of 2.0.1 Version
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
//        //Billing
        String phoneUser=address.getPhone();
        vnp_Params.put("vnp_Bill_Mobile", phoneUser.replaceFirst("0", "84"));
//        vnp_Params.put("vnp_Bill_Email", req.getParameter("txt_billing_email"));
        String fullName = address.getName();
        if (fullName != null && !fullName.isEmpty()) {
            int idx = fullName.indexOf(' ');
            String firstName = fullName.substring(0, idx);
            String lastName = fullName.substring(fullName.lastIndexOf(' ') + 1);
            vnp_Params.put("vnp_Bill_FirstName", firstName);
            vnp_Params.put("vnp_Bill_LastName", lastName);

        }
        String fullAddress = address.getAddress();
        String splitAddress[] = address.getAddress().split(",");
        int idxCity = fullAddress.lastIndexOf(',');
        vnp_Params.put("vnp_Bill_Address", fullAddress.substring(0, idxCity-1));
        vnp_Params.put("vnp_Bill_City", splitAddress[3].trim());
        vnp_Params.put("vnp_Bill_Country", "VN");
//        // Invoice
        vnp_Params.put("vnp_Inv_Phone", "84362258040");
        vnp_Params.put("vnp_Inv_Email", "danhtran12797@gmail.com");
        vnp_Params.put("vnp_Inv_Customer", address.getName());
        vnp_Params.put("vnp_Inv_Address", address.getAddress());
//        vnp_Params.put("vnp_Inv_Company", req.getParameter("txt_inv_company"));
//        vnp_Params.put("vnp_Inv_Taxcode", req.getParameter("txt_inv_taxcode"));
        vnp_Params.put("vnp_Inv_Type", "I");
        //Build data to hash and querystring
        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                try{
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                }catch (Exception e){
                    Log.d("AAA", "jsonStringPayProcessing2: ERROR: "+e.getMessage());
                }

                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = Config.hmacSHA512(Config.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        return Config.vnp_PayUrl + "?" + queryUrl;
    }

    public String getRequestQueryVnPay(){
        String field1[] = Ultil.getVnPayPreference(mActivity);

        String vnp_Version = "2.1.0";
        String vnp_Command = "querydr";
        String vnp_TxnRef = field1[0];
        String vnp_OrderInfo = "Query transaction result #"+vnp_TxnRef;
        String vnp_IpAddr = Config.getIpAddress(mActivity);
        String vnp_TmnCode = Config.vnp_TmnCode;

        Map<String, Object> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_TransDate", field1[1]);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);

        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String createDate = formatter.format(cld.getTime());

        vnp_Params.put("vnp_CreateDate", createDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                try{
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                }catch (Exception e){
                    Log.d("AAA", "jsonStringPayProcessing2: ERROR: "+e.getMessage());
                }

                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = Config.hmacSHA512(Config.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
//        String paymentUrl = Config.vnp_apiUrl + "?" + queryUrl;
//        vnp_Params.put("vnp_SecureHash", vnp_SecureHash);

        return Config.vnp_apiUrl + "?" + queryUrl;
    }

    public void insertUpdateDBVnPay(String trans_id){
        Observable<String> observableA = create_obserable_order(token_id_user, request, "1", order_id);
        if (update) {
            observableA = create_obserable_update_order(order_id, payment, "1", request, delivery, address.getPhone(), address.getAddress(), Ultil.user.getEmail(), address.getName());
        }
        insert_order_payment(observableA
                , create_obserable_payment(token_id_user, order_id, trans_id
                        , String.valueOf(amount), "VNPAY", ""));
    }

    private String jsonStringTransRefund(Payment payment) {
        Log.d("AAA", "order id: " + payment.getOrderId());
        Gson gson = new Gson();

        Map<String, Object> rawData = new HashMap<>();
        rawData.put(Parameter.PARTNER_CODE, merchantCode);
        rawData.put(Parameter.PARTNER_REF_ID, payment.getOrderId());
        rawData.put(Parameter.AMOUNT, payment.getAmount());
        rawData.put(Parameter.MOMO_TRANS_ID, payment.getTransId());
        rawData.put(Parameter.DESCRIPTION, "Hoàn tiền đơn hàng THD Foody");

        return gson.toJson(rawData);
    }

    private void cancleOrderMoMo(Payment payment) {
        String jsonStrRefund = jsonStringTransRefund(payment);
        Log.d("AAA", "jsonStr Refund: " + jsonStrRefund);

        String hashRSA = "";
        try {
            hashRSA = Encoder.encrypt(jsonStrRefund, publicKey).replace("\n", "").replace("\r", "");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("AAA", "transRefund Error: " + e.getMessage());
        }
        Log.d("AAA", "hashRSA refund: " + hashRSA);

//        String requestId = String.valueOf(System.currentTimeMillis());
        String requestId = payment.getOrderId();
        Log.d("AAA", "request Id: " + requestId);

        TransactionRefundRequest refundRequest = new TransactionRefundRequest(merchantCode, VERSION, requestId, hashRSA);
        Log.d("AAA", "refundRequest: " + new Gson().toJson(refundRequest));

        DataMomoService dataMomoService = APIMomoService.getService();
        Call<TransactionRefundResponse> call = dataMomoService.getPayRefundResponse(refundRequest);
        call.enqueue(new Callback<TransactionRefundResponse>() {
            @Override
            public void onResponse(Call<TransactionRefundResponse> call, Response<TransactionRefundResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 0) {
                        TransactionRefundResponse refundResponse = response.body();
                        Log.d("AAA", "refundResponse: " + new Gson().toJson(refundResponse));
                        onCancelOrderListener.cancelSuccess();
                    }
                }
                Log.d("AAA", "onResponse MOMO: " + new Gson().toJson(response.body()));
                Log.d("AAA", "code MOMO: " + response.code());
            }

            @Override
            public void onFailure(Call<TransactionRefundResponse> call, Throwable t) {
                Log.d("AAA", "onFailure: " + t.getMessage());
                mLoading.stop_loading(false);
            }
        });
    }

    public void getPayment(String order_id, OnCallBackRefundListener listener) {
        mLoading.start_loading();
        DataService dataService = APIService.getService();
        Call<Payment> callback = dataService.GetPayment(order_id);
        callback.enqueue(new Callback<Payment>() {
            @Override
            public void onResponse(Call<Payment> call, Response<Payment> response) {
                Payment paymentCard = response.body();
                Log.d("AAA", "onResponse Payment: " + new Gson().toJson(paymentCard));
                if (payment.equals("2")) {
                    cancleOrderBraintree(paymentCard);
                } else if (payment.equals("3")) {
                    cancleOrderMoMo(paymentCard);
                } else if (payment.equals("4")) {
                    cancleOrderZaloPay(paymentCard);
                }else if(payment.equals("5")){
                    listener.callBackRefundVnPay();
                }
            }

            @Override
            public void onFailure(Call<Payment> call, Throwable t) {
                Log.d("AAA", "onFailure: " + t.getMessage());
                mLoading.stop_loading(false);
            }
        });
    }

    public void paymentProcessing(String token_momo, String number) {
        String jsonStr = jsonStringPayProcessing();
        Log.d("AAA", "jsonStr: " + jsonStr);
        String hashRSA = "";
        try {
            hashRSA = Encoder.encrypt(jsonStr, publicKey);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("AAA", "paymentProcessing: " + e.getMessage());
        }
        Log.d("AAA", "hashRSA: " + hashRSA);
        mLoading.start_loading();
        DataMomoService dataMomoService = APIMomoService.getService();
        AppPayRequest appPayRequest = new AppPayRequest(merchantCode, order_id, number, description, VERSION, APP_PAY_TYPE, token_momo, hashRSA);
        Log.d("AAA", "appPayRequest: " + new Gson().toJson(appPayRequest));
        Call<AppPayResponse> call = dataMomoService.getAppPayResponse(appPayRequest);
        call.enqueue(new Callback<AppPayResponse>() {
            @Override
            public void onResponse(Call<AppPayResponse> call, Response<AppPayResponse> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    if (response.body().getStatus() == 0) {
                        AppPayResponse appPayResponse = response.body();
                        Log.d("AAA", "appPayResponse: " + new Gson().toJson(appPayResponse));
                        paymentCofirmation(appPayResponse.getTransid(), appPayRequest.getCustomerNumber(), true);
                    } else {
                        mLoading.stop_loading(true);
                        insert_order("5");
                    }
                }
                Log.d("AAA", "onResponse MOMO: " + new Gson().toJson(response.body()));
                Log.d("AAA", "code MOMO: " + response.code());
            }

            @Override
            public void onFailure(Call<AppPayResponse> call, Throwable t) {
                Log.d("AAA", "onFailure: " + t.getMessage());
                mLoading.stop_loading(false);
            }
        });
    }

    private String json_array_order_detail(String order_id) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < Ultil.arrShoping.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id_order", order_id);
                jsonObject.put("price", Ultil.arrShoping.get(i).getPrice());
                jsonObject.put("quantity", Ultil.arrShoping.get(i).getQuantity());
                jsonObject.put("name", Ultil.arrShoping.get(i).getName());
                jsonObject.put("id_product", Ultil.arrShoping.get(i).getId());
                jsonObject.put("image", Ultil.arrShoping.get(i).getImage());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }

    private Observable<String> create_obserable_payment(String token, String order_id, String trans_id, String amount, String type, String number) {
        return APIService.getService().Payment(token, trans_id, order_id, amount, type, number, createDate);
    }

    private Observable<String> create_obserable_order_detail(String json_array) {
        return APIService.getService().OrdersDetail(json_array);
    }

    private Observable<String> create_obserable_order(String token, String request, String status, String id_order) {
        return APIService.getService().Orders(token, address.getName(), address.getPhone(),
                address.getAddress(), request, Ultil.user.getEmail(),
                delivery, payment, status, id_order, createDate).flatMap(s -> {
            return create_obserable_order_detail(json_array_order_detail(s));
        });
    }

    private void insert_order_payment(Observable<String> observableA, Observable<String> observableB) {
        Observable.merge(observableA, observableB)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                            Log.d("AAA", "on Next insert_order_payment: " + s);
                        }
                        , throwable -> {
                            check_response_code(((HttpException) throwable).code());
                            Log.d("AAA", "error: " + throwable.getMessage());
                            Log.d("AAA", "code insert_order_payment: " + ((HttpException) throwable).code());
                        }
                        , () -> {
                            Log.d("AAA", "on complete insert_order_payment: GOOD JOB");
                            mLoading.stop_loading(true);
                            if (!update) {
                                Ultil.removeShopingCart(mActivity);
                            }
                            Intent intent = new Intent(mActivity, OrderSuccessActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            mActivity.startActivity(intent);
                            mActivity.finish();
                            CustomIntent.customType(mActivity, "fadein-to-fadeout");
                        });
    }

    private void check_response_code(int code) {
        Log.d("AAA", "check_response_code: " + code);
        if (code == 401) {
            mLoading.stop_loading(true);
            Ultil.logout_account(mActivity);
            Ultil.dialogRequestTimeOut(mActivity);
        } else {
            mLoading.stop_loading(false);
        }
    }

    public void insert_order(String status) {
        if (update) {
            mInserOrder.status_order(status, order_id);
            return;
        }
        mLoading.start_loading();
        create_obserable_order(token_id_user, request, status, order_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                            Log.d("AAA", "on Next insert_order: " + s);
                            if (s.equals("success")) {
                                mLoading.stop_loading(true);
                                Ultil.removeShopingCart(mActivity);
                                mInserOrder.status_order(status, order_id);
                            }
                        }
                        , throwable -> {
                            check_response_code(((HttpException) throwable).code());
                            Log.d("AAA", "error insert_order: " + throwable.getMessage());
                            Log.d("AAA", "code insert_order: " + ((HttpException) throwable).code());
                        }
                        , () -> {
                            Log.d("AAA", "on complete insert_order: GOOD JOB");
                        });
    }

    private void paymentCofirmation(String trans_id, String number, boolean success) {
        PayConfirmationRequest payConfirmationRequest = createAppAppConfirmRequest(order_id
                , success ? "capture" : "revertAuthorize"
                , order_id
                , trans_id
                , number
                , description);

        Log.d("AAA", "PayConfirmationRequest: " + new Gson().toJson(payConfirmationRequest));

        DataMomoService dataMomoService = APIMomoService.getService();
        Call<PayConfirmationResponse> call = dataMomoService.getPayConfirmationResponse(payConfirmationRequest);
        call.enqueue(new Callback<PayConfirmationResponse>() {
            @Override
            public void onResponse(Call<PayConfirmationResponse> call, Response<PayConfirmationResponse> response) {
                mLoading.stop_loading(true);
                Log.d("AAA", "PayConfirmationResponse: " + new Gson().toJson(response.body()));
                PayConfirmationResponse confirmationResponse = response.body();
                if (confirmationResponse.getStatus() == 0) {
                    Observable<String> observableA = create_obserable_order(token_id_user, request, "1", order_id);
                    if (update) {
                        observableA = create_obserable_update_order(order_id, payment, "1", request, delivery, address.getPhone(), address.getAddress(), Ultil.user.getEmail(), address.getName());
                    }
                    insert_order_payment(observableA
                            , create_obserable_payment(token_id_user, order_id, trans_id
                                    , String.valueOf(amount), "MoMo", number));
                }
            }

            @Override
            public void onFailure(Call<PayConfirmationResponse> call, Throwable t) {
                Log.d("AAA", "onFailure: " + t.getMessage());
            }
        });
    }


    private PayConfirmationRequest createAppAppConfirmRequest(String partnerRefId, String
            requestType, String requestId, String momoTransId, String customerNumber, String description) {
        try {
            String requestRawData = new StringBuilder()
                    .append(Parameter.PARTNER_CODE).append("=").append(merchantCode).append("&")
                    .append(Parameter.PARTNER_REF_ID).append("=").append(partnerRefId).append("&")
                    .append(Parameter.REQUEST_TYPE).append("=").append(requestType).append("&")
                    .append(Parameter.REQUEST_ID).append("=").append(requestId).append("&")
                    .append(Parameter.MOMO_TRANS_ID).append("=").append(momoTransId)
                    .toString();

            String signRequest = Encoder.signHmacSHA256(requestRawData, secretKey);
            Log.d("AAA", "requestRawData: " + requestRawData + ", [Signature] -> " + signRequest);

            return new PayConfirmationRequest(merchantCode, partnerRefId, customerNumber, description, momoTransId, requestType, requestId, signRequest);
        } catch (Exception e) {
            Log.d("AAA", "createAppAppConfirmRequest: " + e.getMessage());
        }

        return null;
    }

    public void getTokenBraintree() {
        mLoading.start_loading();
        DataService dataService = APIService.getService();
        Call<String> call = dataService.getTokenBriantree();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mLoading.stop_loading(true);
                Log.d("AAA", "token braintree: " + response.body());
                Log.d("AAA", "code braintree: " + response.code());
                if (response.isSuccessful()) {
                    requestPaymentBraintree(response.body());
                } else {
                    check_response_code(response.code());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("AAA", "onFailure: " + t.getMessage());
                mLoading.stop_loading(false);
            }
        });
    }

    private void requestPaymentBraintree(String token) {
        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(token);
        mActivity.startActivityForResult(dropInRequest.getIntent(mActivity), REQUEST_CODE_BRAINTREE);
    }

    public void sendPaymentBraintree(String amount, String strNone) {
        mLoading.start_loading();
        DataService dataService = APIService.getService();
        Call<BraintreeResponse> call = dataService.sendPaymentBriantree(amount, strNone, order_id);
        call.enqueue(new Callback<BraintreeResponse>() {
            @Override
            public void onResponse(Call<BraintreeResponse> call, Response<BraintreeResponse> response) {
                Log.d("AAA", "braintreeResponse: " + new Gson().toJson(response.body()));
                if (response.isSuccessful()) {
                    BraintreeResponse braintreeResponse = response.body();
                    String card_type = braintreeResponse.getTransaction().getCreditCard().getCardType();
                    if (card_type == null) {
                        card_type = "PayPal";
                    }
                    Observable<String> observableA = create_obserable_order(token_id_user, request, "1", order_id);
                    if (update) {
                        Log.d("AAA", "update order: " + update);
                        observableA = create_obserable_update_order(order_id, payment, "1", request, delivery, address.getPhone(), address.getAddress(), Ultil.user.getEmail(), address.getName());
                    }
                    insert_order_payment(observableA
                            , create_obserable_payment(token_id_user, order_id, braintreeResponse.getTransaction().getId()
                                    , String.valueOf(amount_usd), card_type, ""));
                } else {
                    if (response.code() == 401) {
                        Log.d("AAA", "braintreeResponse: FALSE");
                        insert_order("5");
                    }
                }
            }

            @Override
            public void onFailure(Call<BraintreeResponse> call, Throwable t) {
                mLoading.stop_loading(false);
                Log.d("AAA", "onFailure sendPaymentBriantree : " + t.getMessage());
            }
        });
    }

//    public void insertUpdateDbVNPAY(){
//        Observable<String> observableA = create_obserable_order(token_id_user, request, "1", order_id);
//        if (update) {
//            Log.d("AAA", "update order: " + update);
//            observableA = create_obserable_update_order(order_id, payment, "1", request, delivery, address.getPhone(), address.getAddress(), Ultil.user.getEmail(), address.getName());
//        }
//        insert_order_payment(observableA
//                , create_obserable_payment(token_id_user, order_id, braintreeResponse.getTransaction().getId()
//                        , String.valueOf(amount_usd), card_type, ""));
//    }

    private void cancleOrderBraintree(Payment payment) {
        DataService dataService = APIService.getService();
        Call<String> call = dataService.transRefundBriantree(payment.getTransId());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.body().equals("success")) {
                        onCancelOrderListener.cancelSuccess();
                    }
                }
                Log.d("AAA", "onResponse Braintree: " + response.body());
                Log.d("AAA", "code Braintree: " + response.code());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("AAA", "onFailure: " + t.getMessage());
                mLoading.stop_loading(false);
            }
        });
    }

    private class MyZaloPayListener implements ZaloPayListener {
        @Override
        public void onPaymentSucceeded(String transactionId, String zpTranstoken) {
            Log.d("AAA", "onSuccess: On successful with transactionId: " + transactionId + "- zpTransToken: " + zpTranstoken);
            mLoading.start_loading();
            Observable<String> observableA = create_obserable_order(token_id_user, request, "1", order_id);
            if (update) {
                observableA = create_obserable_update_order(order_id, payment, "1", request, delivery, address.getPhone(), address.getAddress(), Ultil.user.getEmail(), address.getName());
            }
            insert_order_payment(observableA
                    , create_obserable_payment(token_id_user, order_id, transactionId
                            , String.valueOf(amount), "ZaloPay", ""));
        }

        @Override
        public void onPaymentError(ZaloPayErrorCode errorCode, int paymentErrorCode, String zpTranstoken) {
            Log.d("AAA", String.format("onPaymentError: payment error with [error: %s, paymentError: %d], zptranstoken: %s", errorCode, paymentErrorCode, zpTranstoken));
            if (errorCode == ZaloPayErrorCode.ZALO_PAY_NOT_INSTALLED) {
                new AlertDialog.Builder(mActivity)
                        .setTitle("Error Payment")
                        .setMessage("ZaloPay App not install on this Device.")
                        .setPositiveButton("Open Market", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ZaloPaySDK.getInstance().navigateToStore(mActivity);
                            }
                        })
                        .setNegativeButton("Back", null).show();
            } else if (errorCode == ZaloPayErrorCode.USER_CANCEL) {
                insert_order("5");
            }
        }
    }

    public void paymentZaloPay() {
        mLoading.start_loading();
        DataZaloPayService zaloPayService = APIZaloPayService.getService();
        Call<ResponseCreateOrder> call = zaloPayService.PayOrder(RequestOrderZaloPay.requestCreateOrder((long) amount, order_id));
        call.enqueue(new Callback<ResponseCreateOrder>() {
            @Override
            public void onResponse(Call<ResponseCreateOrder> call, Response<ResponseCreateOrder> response) {
                ResponseCreateOrder createOrder = response.body();
                Log.d("AAA", "onResponse create order zalopay: " + new Gson().toJson(createOrder));
                if (createOrder.getReturncode() == 1) {
                    ZaloPaySDK.getInstance().payOrder(
                            mActivity, response.body().getZptranstoken(), new MyZaloPayListener());
                } else {
                    insert_order("5");
                }
                mLoading.stop_loading(true);
            }

            @Override
            public void onFailure(Call<ResponseCreateOrder> call, Throwable t) {
                Log.d("AAA", "onFailure: " + t.getMessage());
                mLoading.stop_loading(false);
            }
        });

    }

    private void getPartialRefundstatusZaloPay() {
        DataZaloPayService zaloPayService = APIZaloPayService.getService();
        Call<ResponseRefund> call = zaloPayService.Getpartialrefundstatus(RequestOrderZaloPay.getpartialrefundstatus());
        call.enqueue(new Callback<ResponseRefund>() {
            @Override
            public void onResponse(Call<ResponseRefund> call, Response<ResponseRefund> response) {
                ResponseRefund responseRefund = response.body();
                Log.d("AAA", "onResponse: " + new Gson().toJson(responseRefund));
                if (responseRefund.getReturncode() == 1) {
                    onCancelOrderListener.cancelSuccess();
                }
            }

            @Override
            public void onFailure(Call<ResponseRefund> call, Throwable t) {
                Log.d("AAA", "onFailure: " + t.getMessage());
                mLoading.stop_loading(false);
            }
        });
    }

    private void cancleOrderZaloPay(Payment payment) {
        DataZaloPayService zaloPayService = APIZaloPayService.getService();
        Call<ResponseRefund> call = zaloPayService.RefundOrder(RequestOrderZaloPay.requestRefundOrder(payment));
        call.enqueue(new Callback<ResponseRefund>() {
            @Override
            public void onResponse(Call<ResponseRefund> call, Response<ResponseRefund> response) {
                mLoading.stop_loading(true);
                ResponseRefund responseRefund = response.body();
                Log.d("AAA", "onResponse: " + new Gson().toJson(responseRefund));
                if (responseRefund.getReturncode() >= 1) {
                    onCancelOrderListener.cancelSuccess();
                }
            }

            @Override
            public void onFailure(Call<ResponseRefund> call, Throwable t) {
                Log.d("AAA", "onFailure: " + t.getMessage());
                mLoading.stop_loading(false);
            }
        });
    }
}
