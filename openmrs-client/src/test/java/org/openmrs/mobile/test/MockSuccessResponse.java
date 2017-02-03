package org.openmrs.mobile.test;

import org.openmrs.mobile.models.Resource;
import org.openmrs.mobile.models.Results;

import java.io.IOException;
import java.util.List;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MockSuccessResponse<T> implements Call<T>{

    private Response response;

    public MockSuccessResponse(List resultList) {
        Results results = new Results<>();
        results.setResults(resultList);
        response = Response.success(results);
    }

    public MockSuccessResponse(Resource resource) {
        response = Response.success(resource);
    }

    @Override
    public Response execute() throws IOException {
        return null;
    }

    @Override
    public void enqueue(Callback<T> callback) {
        callback.onResponse(this, response);
    }

    @Override
    public boolean isExecuted() {
        return false;
    }

    @Override
    public void cancel() {
        // This method is intentionally empty
    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    @Override
    public Call<T> clone() {
        return null;
    }

    @Override
    public Request request() {
        return null;
    }
}
