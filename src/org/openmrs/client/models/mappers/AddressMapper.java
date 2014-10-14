package org.openmrs.client.models.mappers;

import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.client.models.Address;

public class AddressMapper {

    private AddressMapper() {
    }

    public static Address parseAddress(JSONObject addressJSON) throws JSONException {
        Address address = new Address();
        address.setAddress1(addressJSON.getString("address1"));
        address.setAddress2(addressJSON.getString("address2"));
        address.setCityVillage(addressJSON.getString("cityVillage"));
        address.setCountry(addressJSON.getString("country"));
        address.setPostalCode(addressJSON.getString("postalCode"));
        address.setState(addressJSON.getString("stateProvince"));
        return address;
    }
}
