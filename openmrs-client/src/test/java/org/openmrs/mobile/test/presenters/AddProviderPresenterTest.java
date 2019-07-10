/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.test.presenters;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.mobile.activities.providermanagerdashboard.addprovider.AddProviderContract;
import org.openmrs.mobile.activities.providermanagerdashboard.addprovider.AddProviderPresenter;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.test.ACUnitTestBase;

import java.util.ArrayList;
import java.util.List;

public class AddProviderPresenterTest extends ACUnitTestBase {

    @Mock
    private AddProviderContract.View view;
    @Mock
    private Person person;

    private AddProviderPresenter presenter;

    @Before
    public void setup() {
        presenter = new AddProviderPresenter(view);
    }

    @Test
    public void shouldCreateNewPerson() {
        String firstName = "firstname";
        String lastName = "lastname";

        Person person = presenter.createPerson(firstName, lastName);
        assert firstName.equals(person.getName().getGivenName());
        assert lastName.equals(person.getName().getFamilyName());
    }

    @Test
    public void shouldCreateNewProvider() {
        String identifier = "nurse";

        Provider newProvider = presenter.createNewProvider(person, identifier);
        assert person.equals(newProvider.getPerson());
        assert identifier.equals(newProvider.getIdentifier());
    }

    @Test
    public void shouldEditExistingProvider() {
        String identifier = "nurse";
        String uuid = "abcd";
        Provider provider = new Provider();
        provider.setIdentifier(identifier);
        provider.setUuid(uuid);

        Provider existingProvider = presenter.editExistingProvider(provider, person, identifier);
        assert existingProvider.getIdentifier().equals(identifier);
        assert existingProvider.getUuid().equals(uuid);

    }

    @Test
    public void shouldReturnMatchingProviders(){
        Provider providerA = createProvider("John","Smith","doc");
        Provider providerB = createProvider("Jake","Smith","doc");
        Provider providerC = createProvider("John","Doe","nurse");
        Provider providerD = createProvider("Joe","Smith","clerk");

        ArrayList<Provider> existingProviders = new ArrayList<>();
        existingProviders.add(providerA);
        existingProviders.add(providerB);
        existingProviders.add(providerC);
        existingProviders.add(providerD);

        Provider newProviderOne = createProvider("John","Will","clerk");
        Provider newProviderTwo = createProvider("Will","Smith","doc");

        List<Provider> matchingProvidersA = presenter.getMatchingProviders(existingProviders,newProviderOne);
        List<Provider> matchingProvidersB = presenter.getMatchingProviders(existingProviders,newProviderTwo);

        assert matchingProvidersA.size() == 2;
        assert matchingProvidersB.size() == 3;
    }

    private Provider createProvider(String fName, String lName, String identifier){
        Person person = presenter.createPerson(fName,lName);
        person.setDisplay(fName+" "+lName);
        return presenter.createNewProvider(person,identifier);
    }
}
