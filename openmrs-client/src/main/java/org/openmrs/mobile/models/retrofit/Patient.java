
package org.openmrs.mobile.models.retrofit;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Patient {

    @SerializedName("person")
    @Expose
    private Person person;
    @SerializedName("identifiers")
    @Expose
    private List<Identifier> identifiers = new ArrayList<Identifier>();

    /**
     * 
     * @return
     *     The person
     */
    public Person getPerson() {
        return person;
    }

    /**
     * 
     * @param person
     *     The person
     */
    public void setPerson(Person person) {
        this.person = person;
    }

    /**
     * 
     * @return
     *     The identifiers
     */
    public List<Identifier> getIdentifiers() {
        return identifiers;
    }

    /**
     * 
     * @param identifiers
     *     The identifiers
     */
    public void setIdentifiers(List<Identifier> identifiers) {
        this.identifiers = identifiers;
    }

}
