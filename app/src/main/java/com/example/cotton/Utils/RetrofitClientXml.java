package com.example.cotton.Utils;

import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

//public class RetrofitClient2 {
//
//    public static ApiService getApiService(String baseURL){
//        return getInstance(baseURL).create(ApiService.class);
//    }
//
//    private static Retrofit getInstance(String baseURL){
//        Gson gson = new GsonBuilder().setLenient().create();
//        return new Retrofit.Builder()
//                .baseUrl(baseURL)
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build();
//    }
//}


public class RetrofitClientXml {

    public static ApiService getApiService(String baseURL){
        return getInstance(baseURL).create(ApiService.class);
    }

    private static Retrofit getInstance(String baseURL){
        return new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
    }
}