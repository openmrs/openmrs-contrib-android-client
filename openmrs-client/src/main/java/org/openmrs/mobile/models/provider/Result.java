
package org.openmrs.mobile.models.provider;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result implements Parcelable
{

    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("display")
    @Expose
    private String display;
    @SerializedName("person")
    @Expose
    private Person person;
    @SerializedName("identifier")
    @Expose
    private String identifier;
    @SerializedName("attributes")
    @Expose
    private List<Object> attributes = null;
    @SerializedName("retired")
    @Expose
    private Boolean retired;
    @SerializedName("links")
    @Expose
    private List<Link_> links = null;
    @SerializedName("resourceVersion")
    @Expose
    private String resourceVersion;
    public final static Parcelable.Creator<Result> CREATOR = new Creator<Result>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        public Result[] newArray(int size) {
            return (new Result[size]);
        }

    }
    ;

    protected Result(Parcel in) {
        this.uuid = ((String) in.readValue((String.class.getClassLoader())));
        this.display = ((String) in.readValue((String.class.getClassLoader())));
        this.person = ((Person) in.readValue((Person.class.getClassLoader())));
        this.identifier = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.attributes, (java.lang.Object.class.getClassLoader()));
        this.retired = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        in.readList(this.links, (org.openmrs.mobile.models.provider.Link_.class.getClassLoader()));
        this.resourceVersion = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Result() {
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

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public List<Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Object> attributes) {
        this.attributes = attributes;
    }

    public Boolean getRetired() {
        return retired;
    }

    public void setRetired(Boolean retired) {
        this.retired = retired;
    }

    public List<Link_> getLinks() {
        return links;
    }

    public void setLinks(List<Link_> links) {
        this.links = links;
    }

    public String getResourceVersion() {
        return resourceVersion;
    }

    public void setResourceVersion(String resourceVersion) {
        this.resourceVersion = resourceVersion;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(uuid);
        dest.writeValue(display);
        dest.writeValue(person);
        dest.writeValue(identifier);
        dest.writeList(attributes);
        dest.writeValue(retired);
        dest.writeList(links);
        dest.writeValue(resourceVersion);
    }

    public int describeContents() {
        return  0;
    }

}
