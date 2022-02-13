package com.danhtran12797.thd.foodyapp.service;

public class APIService {
    public static String base_url="https://applon12797.000webhostapp.com/foody/server/";
//    private static String base_url="http://192.168.42.107/foody/server/";
    public static DataService getService(){
        return APIRetrofitClient.getClient(base_url).create(DataService.class);
    }
}
