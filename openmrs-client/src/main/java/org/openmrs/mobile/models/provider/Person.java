
package org.openmrs.mobile.models.provider;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Person implements Parcelable
{

    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("display")
    @Expose
    private String display;
    @SerializedName("links")
    @Expose
    private List<Link> links = null;
    public final static Parcelable.Creator<Person> CREATOR = new Creator<Person>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        public Person[] newArray(int size) {
            return (new Person[size]);
        }

    }
    ;

    protected Person(Parcel in) {
        this.uuid = ((String) in.readValue((String.class.getClassLoader())));
        this.display = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.links, (org.openmrs.mobile.models.provider.Link.class.getClassLoader()));
    }

    public Person() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(uuid);
        dest.writeValue(display);
        dest.writeList(links);
    }

    public int describeContents() {
        return  0;
    }

}
