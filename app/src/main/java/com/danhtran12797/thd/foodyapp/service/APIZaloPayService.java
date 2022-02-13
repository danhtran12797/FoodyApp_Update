package com.danhtran12797.thd.foodyapp.service;

public class APIZaloPayService {
    private static String base_url = "https://sandbox.zalopay.com.vn/v001/tpe/";

    //private static String base_url="http://192.168.1.11:8012/foody/server/";
    public static DataZaloPayService getService() {
        return APIRetrofitZaloPayClient.getClient(base_url).create(DataZaloPayService.class);
    }
}
