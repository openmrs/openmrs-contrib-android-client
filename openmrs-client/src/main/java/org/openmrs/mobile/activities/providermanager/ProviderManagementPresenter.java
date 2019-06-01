package org.openmrs.mobile.activities.providermanager;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.dao.ProviderDAO;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.NetworkUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;

public class ProviderManagementPresenter extends BasePresenter implements ProviderManagerContract.Presenter {

    final String TAG = "PMP";
    RestApi restApi;
    @NotNull
    private final ProviderManagerContract.View providerManagerView;
    private ProviderDAO providerDAO;

    public ProviderManagementPresenter(@NotNull ProviderManagerContract.View providerManagerView) {
        this.providerManagerView = providerManagerView;
        this.providerManagerView.setPresenter(this);
        restApi = RestServiceBuilder.createService(RestApi.class);
        this.providerDAO = new ProviderDAO();
    }

    public ProviderManagementPresenter(ProviderManagerContract.View view, RestApi restApi, ProviderDAO providerDAO){
        this.restApi = restApi;
        this.providerManagerView = view;
        this.providerDAO = providerDAO;
    }

    @Override
    public void getProviders() {

        if (NetworkUtils.hasNetwork()) {
            Call<Results<Provider>> call = restApi.getProviderList();

            call.enqueue(new Callback<Results<Provider>>() {
                @Override
                public void onResponse(Call<Results<Provider>> call, Response<Results<Provider>> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Success");
                        if (!response.body().getResults().isEmpty()) {
                            for (Provider provider : response.body().getResults()) {

                                providerDAO.saveProvider(provider)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(
                                                visit -> Log.d(TAG, "Saved"),
                                                error -> Log.e(TAG, "Error: "+error)

                                        );
                            }

                            providerManagerView.updateAdapter(response.body().getResults());
                            providerManagerView.updateVisibility(true, null);

                        } else {
                            providerManagerView.updateVisibility(false, "No Data to display.");
                        }
                    } else {
                        Log.e(TAG, "Error");
                    }
                }

                @Override
                public void onFailure(Call<Results<Provider>> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    @Override
    public void subscribe() {

    }
}
