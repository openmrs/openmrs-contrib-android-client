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

package org.openmrs.mobile.activities.providermanagerdashboard.addprovider;

import org.jetbrains.annotations.NotNull;
import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.PersonName;
import org.openmrs.mobile.models.Provider;

import java.util.ArrayList;
import java.util.List;

public class AddProviderPresenter extends BasePresenter implements AddProviderContract.Presenter {

    @NotNull
    private final AddProviderContract.View addProviderView;

    public AddProviderPresenter(AddProviderContract.View view) {
        this.addProviderView = view;
        this.addProviderView.setPresenter(this);
    }

    /**
     * This fuction creates a new Person using the First Name and Last Name
     *
     * @param firstName
     * @param lastName
     * @return Person
     */
    @Override
    public Person createPerson(String firstName, String lastName) {
        Person person = new Person();

        PersonName personName = new PersonName();
        personName.setGivenName(firstName);
        personName.setFamilyName(lastName);

        List<PersonName> names = new ArrayList<>();
        names.add(personName);

        person.setNames(names);

        return person;
    }

    /**
     * This function creates a new Provider from a Person (with first and last names)
     *
     * @param person
     * @param identifier
     * @return Provider
     */
    @Override
    public Provider createNewProvider(Person person, String identifier) {
        Provider provider = new Provider();
        provider.setPerson(person);

        provider.setIdentifier(identifier);

        provider.setRetired(false);

        return provider;
    }

    /**
     * This function edits and existing Provider. It saves all information such as UUID.
     * Only edits the Person information
     *
     * @param provider
     * @param person
     * @param identifier
     * @return Provider
     */
    @Override
    public Provider editExistingProvider(Provider provider, Person person, String identifier) {
        provider.setPerson(person);
        provider.setIdentifier(identifier);

        return provider;
    }

    /**
     * This function finds the matching providers from existing providers list and returns
     * a list of matching ones.
     * Currently only first name and last name are checked. More fields can be added with more
     * attributes checking here.
     *
     * @param existingProviders
     * @param currentProvider
     * @return List<Provider>
     */
    @Override
    public List<Provider> getMatchingProviders(ArrayList<Provider> existingProviders, Provider currentProvider) {
        List<Provider> matchingProviders = new ArrayList<>();
        for (Provider provider : existingProviders) {
            String name = provider.getPerson().getDisplay().toLowerCase();
            String fName = currentProvider.getPerson().getName().getGivenName().toLowerCase();
            String lName = currentProvider.getPerson().getName().getFamilyName().toLowerCase();

            if (name.contains(fName) || name.contains(lName))
                matchingProviders.add(provider);
        }
        return matchingProviders;
    }

    @Override
    public void subscribe() {

    }
}
