package com.danhtran12797.thd.foodyapp.service;

import com.danhtran12797.thd.foodyapp.model.AddressDetail;
import com.danhtran12797.thd.foodyapp.model.ListProvin;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DataAddressService {
    @GET("city")
    Call<ListProvin> GetAllProvin();

    @GET("city/{id}/district")
    Call<List<AddressDetail>> GetAllDistrict(@Path("id") int id);

    @GET("district/{id}/ward")
    Call<List<AddressDetail>> GetAllWard(@Path("id") int id);
}
