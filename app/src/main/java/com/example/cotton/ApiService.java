package com.example.cotton;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    @POST("v1.1/wallets")
        Call<RetrofitV0> listRepos(@Body HashMap<String, String> gradeBody, @HeaderMap HashMap<String, String> map);
}
