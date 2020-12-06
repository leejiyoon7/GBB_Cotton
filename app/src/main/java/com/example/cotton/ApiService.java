package com.example.cotton;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @POST("v1.1/wallets")
        Call<RetrofitV0> listRepos(@Body HashMap<String, String> gradeBody, @HeaderMap HashMap<String, String> map);

    @GET("v1.1/wallets/{wallet}/GCC/GBBGC/balance")
        Call<RetrofitV1> getMoney(@Path("wallet") String wallet, @HeaderMap HashMap<String, String> map);

    @POST("v1.1/transactions/foodParchase2")
        Call<RetrofitV2> buyFood(@Body HashMap<String, String> buyFoodBody, @HeaderMap HashMap<String, String> map);

}
