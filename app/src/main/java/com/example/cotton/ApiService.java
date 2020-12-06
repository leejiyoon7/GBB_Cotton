package com.example.cotton;

import com.example.cotton.ValueObject.BookSearchByBarcode.BookSearchResultVO;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("v1.1/wallets")
        Call<RetrofitV0> listRepos(@Body HashMap<String, String> gradeBody, @HeaderMap HashMap<String, String> map);

    @GET("v1.1/wallets/{wallet}/GCC/GBBGC/balance")
        Call<RetrofitV1> getMoney(@Path("wallet") String wallet, @HeaderMap HashMap<String, String> map);



    @GET("v1/search/book_adv.xml")
    @Headers({
            "X-Naver-Client-Id: 2BZk7zU17z265z9ODeX4",
            "X-Naver-Client-Secret: NuY6KgaU2t"
    })
    Call<BookSearchResultVO> searchBookByBarcode(@Query("d_isbn") String wallet);
}

