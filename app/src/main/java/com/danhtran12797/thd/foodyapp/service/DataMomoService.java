package com.danhtran12797.thd.foodyapp.service;

import com.danhtran12797.thd.foodyapp.model.momo.AppPayRequest;
import com.danhtran12797.thd.foodyapp.model.momo.AppPayResponse;
import com.danhtran12797.thd.foodyapp.model.momo.PayConfirmationRequest;
import com.danhtran12797.thd.foodyapp.model.momo.PayConfirmationResponse;
import com.danhtran12797.thd.foodyapp.model.momo.TransactionRefundRequest;
import com.danhtran12797.thd.foodyapp.model.momo.TransactionRefundResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DataMomoService {
    @POST("pay/app")
    Call<AppPayResponse> getAppPayResponse(@Body AppPayRequest body);

    @POST("pay/confirm")
    Call<PayConfirmationResponse> getPayConfirmationResponse(@Body PayConfirmationRequest body);

    @POST("pay/refund")
    Call<TransactionRefundResponse> getPayRefundResponse(@Body TransactionRefundRequest body);
}
