
package org.openmrs.mobile.models.retrofit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Form implements Serializable {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("uuid")
    @Expose
    private String uuid;

    @SerializedName("processor")
    @Expose
    private String processor;

    @SerializedName("pages")
    @Expose
    private List<Page> pages = new ArrayList<Page>();

    /**
     * 
     * @return
     *     The name
     */


    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * 
     * @param uuid
     *     The uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * 
     * @return
     *     The processor
     */
    public String getProcessor() {
        return processor;
    }

    /**
     * 
     * @param processor
     *     The processor
     */
    public void setProcessor(String processor) {
        this.processor = processor;
    }

    /**
     * 
     * @return
     *     The pages
     */
    public List<Page> getPages() {
        return pages;
    }

    /**
     * 
     * @param pages
     *     The pages
     */
    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

}
