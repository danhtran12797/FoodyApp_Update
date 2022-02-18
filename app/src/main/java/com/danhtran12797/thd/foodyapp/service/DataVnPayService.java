package com.danhtran12797.thd.foodyapp.service;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface DataVnPayService {

    @GET("merchant.html")
    Call<String> GetQueryPAY(@QueryMap Map<String, Object> order);

    @GET("merchant.html")
    Call<String> GetRefundPAY(@QueryMap Map<String, Object> order);
}
