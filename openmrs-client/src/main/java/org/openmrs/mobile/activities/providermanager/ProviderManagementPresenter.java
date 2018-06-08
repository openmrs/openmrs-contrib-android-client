package org.openmrs.mobile.activities.providermanager;

import android.support.annotation.NonNull;
import android.util.Log;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.api.retrofit.VisitApi;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.dao.ProviderDAO;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.NetworkUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;


/**
 * Created by Chathuranga on 16/05/2018.
 */

public class ProviderManagementPresenter extends BasePresenter implements ProviderManagerContract.Presenter {
    RestApi restApi;
    @NonNull
    private final ProviderManagerContract.View providerManagerView;

    public ProviderManagementPresenter(@NonNull ProviderManagerContract.View providerManagerView) {
        this.providerManagerView = providerManagerView;
        this.providerManagerView.setPresenter(this);
        restApi = RestServiceBuilder.createService(RestApi.class);
    }

    @Override
    public void subscribe() {

    }


    @Override
    public void getProviders() {

        if (NetworkUtils.hasNetwork()) {
            Call<Results<Provider>> call =
                    restApi.getProviderList();
            call.enqueue(new Callback<Results<Provider>>() {
                @Override
                public void onResponse(Call<Results<Provider>> call, Response<Results<Provider>> response) {
                    if (response.isSuccessful()) {
                        Log.e("Response: ", "Success");
                        if (!response.body().getResults().isEmpty()) {
                            for(Provider provider : response.body().getResults()){
                                new ProviderDAO().saveProvider(provider)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Observer<Long>() {
                                            @Override
                                            public void onCompleted() {
                                                Log.e(provider.getDisplay(),": Saved");
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                e.printStackTrace();

                                            }

                                            @Override
                                            public void onNext(Long aLong) {

                                            }
                                        });
                            }

                            providerManagerView.updateAdapter(response.body().getResults());
                            providerManagerView.updateVisibility(true, null);
                        } else {
                            providerManagerView.updateVisibility(false, "No Data to Display");
                        }


                    } else {
                        Log.e("Response: ", "ERRROR");
                    }

                }

                @Override
                public void onFailure(Call<Results<Provider>> call, Throwable t) {
                    t.printStackTrace();
                }


            });
        }
    }
}
