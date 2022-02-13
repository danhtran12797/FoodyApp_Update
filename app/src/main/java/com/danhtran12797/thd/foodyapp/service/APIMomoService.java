package com.danhtran12797.thd.foodyapp.service;

public class APIMomoService {
    private static String base_url = "https://test-payment.momo.vn/";

    //private static String base_url="http://192.168.1.11:8012/foody/server/";
    public static DataMomoService getService() {
        return APIRetrofitMomoClient.getClient(base_url).create(DataMomoService.class);
    }
}
