package org.openmrs.mobile.api;

import org.openmrs.mobile.retrofit.IdGenPatientIdentifiers;
import org.openmrs.mobile.retrofit.Patient;
import org.openmrs.mobile.retrofit.PatientIdentifier;
import org.openmrs.mobile.retrofit.Resource;
import org.openmrs.mobile.retrofit.Results;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RestApi {


    @GET("location")
    Call<Results<Resource>> getLocations();

    @GET("patientidentifiertype")
    Call<Results<PatientIdentifier>> getIdentifierTypes();

    @GET("module/idgen/generateIdentifier.form?source=1")
    Call<IdGenPatientIdentifiers> getPatientIdentifiers(@Query("username") String username,
                                                        @Query("password") String password);

    @POST("patient")
    Call<Patient> createPatient(
            @Body Patient patient);

}
