package com.danhtran12797.thd.foodyapp.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class APIRetrofitVnPayClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String base_url) {
        if (retrofit==null){
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new AuthenticationInterceptor())
                    .readTimeout(20000, TimeUnit.MILLISECONDS)
                    .writeTimeout(20000, TimeUnit.MILLISECONDS)
                    .connectTimeout(20000, TimeUnit.MILLISECONDS)
                    .retryOnConnectionFailure(true)
                    .protocols(Arrays.asList(Protocol.HTTP_1_1))
                    .build();

            Gson gson = new GsonBuilder().setLenient().create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(base_url)
                    .client(okHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build();
        }

        return retrofit;
    }

    private static class AuthenticationInterceptor implements Interceptor {

/*        private String authToken;

        public AuthenticationInterceptor(String token) {
            this.authToken = token;
        }*/

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();

            Request.Builder builder = original.newBuilder()
                    .header("Content-Type", "text/plain");

            Request request = builder.build();
            return chain.proceed(request);
        }
    }
}
