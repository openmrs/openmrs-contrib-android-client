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
package org.openmrs.mobile.activities.providermanagerdashboard.addprovider

import org.openmrs.mobile.activities.BasePresenter
import org.openmrs.mobile.models.Person
import org.openmrs.mobile.models.PersonName
import org.openmrs.mobile.models.Provider

class AddProviderPresenter(private val addProviderView: AddProviderContract.View) : BasePresenter(), AddProviderContract.Presenter {
    /**
     * This fuction creates a new Person using the First Name and Last Name
     *
     * @param firstName
     * @param lastName
     * @return Person
     */
    override fun createPerson(firstName: String?, lastName: String?): Person {
        val person = Person()
        val personName = PersonName()
        personName.givenName = firstName
        personName.familyName = lastName
        person.uuid = null

        // this display gets used up by the recycler view for the name
        person.display = "$firstName $lastName"
        val names: MutableList<PersonName> = ArrayList()
        names.add(personName)
        person.names = names
        return person
    }

    /**
     * This function creates a new Provider from a Person (with first and last names)
     *
     * @param person
     * @param identifier
     * @return Provider
     */
    override fun createNewProvider(person: Person?, identifier: String?): Provider {
        val provider = Provider()
        provider.person = person
        provider.uuid = null
        provider.identifier = identifier
        provider.retired = false
        return provider
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
    override fun editExistingProvider(provider: Provider?, person: Person?, identifier: String?): Provider {
        provider?.person = person
        provider?.identifier = identifier
        return provider!!
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
    </Provider> */
    override fun getMatchingProviders(existingProviders: ArrayList<Provider?>?, currentProvider: Provider?): List<Provider> {
        val matchingProviders: MutableList<Provider> = ArrayList()
        for (provider in existingProviders!!) {
            val name = provider?.person!!.display!!.toLowerCase()
            val fName = currentProvider?.person!!.name.givenName!!.toLowerCase()
            val lName = currentProvider.person!!.name.familyName!!.toLowerCase()
            if (name.contains(fName) || name.contains(lName)) {
                matchingProviders.add(provider)
            }
        }
        return matchingProviders
    }

    override fun subscribe() {}

    init {
        addProviderView.setPresenter(this)
    }
}