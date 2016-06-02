package org.openmrs.mobile.api;

import org.openmrs.mobile.models.retrofit.GenID;
import org.openmrs.mobile.models.retrofit.Location;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GenIDApi {

    @GET("module/idgen/generateIdentifier.form?source=1")
    Call<GenID> getidlist(@Query("username") String username,
                          @Query("password") String password);

}
