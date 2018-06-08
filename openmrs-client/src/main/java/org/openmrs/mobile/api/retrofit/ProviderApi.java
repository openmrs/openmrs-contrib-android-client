package org.openmrs.mobile.api.retrofit;


import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.api.promise.SimpleDeferredObject;
import org.openmrs.mobile.api.promise.SimplePromise;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ToastUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Chathuranga on 21/05/2018.
 */

public class ProviderApi extends RetrofitApi {
    private RestApi restApi;


    public SimplePromise<Provider> getProviders() {
        final SimpleDeferredObject<Provider> deferred = new SimpleDeferredObject<>();

        RestApi apiService =
                RestServiceBuilder.createService(RestApi.class);
        Call<Results<Provider>> call = apiService.getProviderList();
        call.enqueue(new Callback<Results<Provider>>() {
            @Override
            public void onResponse(Call<Results<Provider>> call, Response<Results<Provider>> response) {
                Results<Provider> providerList = response.body();
                for (Provider result : providerList.getResults()) {

                        deferred.resolve(result);

                }
            }

            @Override
            public void onFailure(Call<Results<Provider>> call, Throwable t) {
                ToastUtil.notify(t.toString());
                deferred.reject(t);
            }




        });

        return deferred.promise();
    }
}
