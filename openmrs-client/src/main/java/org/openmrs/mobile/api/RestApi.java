package org.openmrs.mobile.api;

import org.openmrs.mobile.models.location.Location;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by Avijit Ghosh on 01/06/16.
 */
public interface RestApi {


    @GET("location")
    Call<Location> getlocationlist();


    @POST("patient")
    void createpatient(@Header("Authorization") String authorization);

}
