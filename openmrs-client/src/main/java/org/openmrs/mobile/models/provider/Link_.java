
package org.openmrs.mobile.models.provider;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Link_ implements Parcelable
{

    @SerializedName("rel")
    @Expose
    private String rel;
    @SerializedName("uri")
    @Expose
    private String uri;
    public final static Parcelable.Creator<Link_> CREATOR = new Creator<Link_>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Link_ createFromParcel(Parcel in) {
            return new Link_(in);
        }

        public Link_[] newArray(int size) {
            return (new Link_[size]);
        }

    }
    ;

    protected Link_(Parcel in) {
        this.rel = ((String) in.readValue((String.class.getClassLoader())));
        this.uri = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Link_() {
    }

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(rel);
        dest.writeValue(uri);
    }

    public int describeContents() {
        return  0;
    }

}
