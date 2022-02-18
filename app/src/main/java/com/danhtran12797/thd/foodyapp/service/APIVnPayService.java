package com.danhtran12797.thd.foodyapp.service;

public class APIVnPayService {
    public static DataVnPayService getVnPayService(String urlQuery) {
        return APIRetrofitVnPayClient.getClient(urlQuery).create(DataVnPayService.class);
    }
}
