
package org.openmrs.mobile.models.provider;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Provider implements Parcelable
{

    @SerializedName("results")
    @Expose
    private List<Result> results = null;
    public final static Parcelable.Creator<Provider> CREATOR = new Creator<Provider>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Provider createFromParcel(Parcel in) {
            return new Provider(in);
        }

        public Provider[] newArray(int size) {
            return (new Provider[size]);
        }

    }
    ;

    protected Provider(Parcel in) {
        in.readList(this.results, (org.openmrs.mobile.models.provider.Result.class.getClassLoader()));
    }

    public Provider() {
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(results);
    }

    public int describeContents() {
        return  0;
    }

}
