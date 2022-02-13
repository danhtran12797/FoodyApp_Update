package com.danhtran12797.thd.foodyapp.service;

public class APIAddressService {
    private static String base_url = "https://thongtindoanhnghiep.co/api/";

    //private static String base_url="http://192.168.1.11:8012/foody/server/";
    public static DataAddressService getService() {
        return APIRetrofitAddressClient.getClient(base_url).create(DataAddressService.class);
    }
}
