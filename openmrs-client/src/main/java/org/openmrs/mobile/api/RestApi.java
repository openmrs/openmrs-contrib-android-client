package org.openmrs.mobile.api;

import org.openmrs.mobile.models.retrofit.Location;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RestApi {


    @GET("location")
    Call<Location> getlocationlist();


    @POST("patient")
    void createpatient();

}
