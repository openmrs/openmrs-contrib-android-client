package org.openmrs.mobile.api;

import org.openmrs.mobile.models.retrofit.GenID;
import org.openmrs.mobile.models.retrofit.Location;
import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.models.retrofit.PatientResponse;
import org.openmrs.mobile.models.retrofit.Patientidentifier;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RestApi {


    @GET("location")
    Call<Location> getlocationlist();

    @GET("patientidentifiertype")
    Call<Patientidentifier> getidentifiertypelist();

    @GET("module/idgen/generateIdentifier.form?source=1")
    Call<GenID> getidlist(@Query("username") String username,
                          @Query("password") String password);

    @POST("patient")
    Call<PatientResponse> createpatient(
            @Body Patient patient);

}
