package com.example.desafioteste5;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IpApiService {

    // Defina a chamada GET para a API
    @GET("json/{ipAddress}")
    Call<IpApiResult> getIpInfo(@Path("ipAddress") String ipAddress, @Query("fields") String fields);
}
