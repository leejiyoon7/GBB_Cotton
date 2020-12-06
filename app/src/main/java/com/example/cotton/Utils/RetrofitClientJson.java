package com.example.cotton.Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientJson {

    public static ApiService getApiService(String baseUrl){
        return getInstance(baseUrl).create(ApiService.class);
    }

    private static Retrofit getInstance(String baseUrl){
        Gson gson = new GsonBuilder().setLenient().create();
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
}