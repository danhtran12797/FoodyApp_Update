package com.danhtran12797.thd.foodyapp.service;

import com.danhtran12797.thd.foodyapp.model.momo.AppPayRequest;
import com.danhtran12797.thd.foodyapp.model.momo.AppPayResponse;
import com.danhtran12797.thd.foodyapp.model.momo.PayConfirmationRequest;
import com.danhtran12797.thd.foodyapp.model.momo.PayConfirmationResponse;
import com.danhtran12797.thd.foodyapp.model.momo.TransactionRefundRequest;
import com.danhtran12797.thd.foodyapp.model.momo.TransactionRefundResponse;
import com.danhtran12797.thd.foodyapp.model.zalopay.ResponseCreateOrder;
import com.danhtran12797.thd.foodyapp.model.zalopay.ResponseRefund;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface DataZaloPayService {
    @FormUrlEncoded
    @POST("createorder")
    Call<ResponseCreateOrder> PayOrder(@FieldMap Map<String, Object> order);

    @FormUrlEncoded
    @POST("partialrefund")
    Call<ResponseRefund> RefundOrder(@FieldMap Map<String, Object> order);

    @FormUrlEncoded
    @POST("getpartialrefundstatus")
    Call<ResponseRefund> Getpartialrefundstatus(@FieldMap Map<String, Object> order);
}
