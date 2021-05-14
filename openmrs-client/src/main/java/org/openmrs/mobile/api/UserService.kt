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
package org.openmrs.mobile.api

import org.openmrs.mobile.application.OpenMRS
import org.openmrs.mobile.models.Results
import org.openmrs.mobile.models.User
import org.openmrs.mobile.utilities.ApplicationConstants
import org.openmrs.mobile.utilities.ToastUtil.error
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class UserService {
    fun updateUserInformation(username: String) {
        val restApi = RestServiceBuilder.createService(RestApi::class.java)
        val call = restApi.getUserInfo(username)
        call.enqueue(object : Callback<Results<User>> {
            override fun onResponse(call: Call<Results<User>>, response: Response<Results<User>>) {
                if (response.isSuccessful) {
                    val resultList = response.body()!!.results
                    var matchFound = false
                    if (resultList.isNotEmpty()) {
                        for (user in resultList) {
                            if (user.display!!.toUpperCase() == username.toUpperCase()) {
                                matchFound = true
                                fetchFullUserInformation(user.uuid)
                            }
                        }
                        if (!matchFound) {
                            //string resource and translation added "error_fetching_user_data_message"
                            error("Couldn't fetch user data")
                        }
                    }
                } else {
                    error(response.message())
                }
            }

            override fun onFailure(call: Call<Results<User>>, t: Throwable) {
                error(t.message!!)
            }
        })
    }

    private fun fetchFullUserInformation(uuid: String?) {
        val restApi = RestServiceBuilder.createService(RestApi::class.java)
        val call = restApi.getFullUserInfo(uuid)
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val userInfo: MutableMap<String, String?> = HashMap()
                    userInfo[ApplicationConstants.UserKeys.USER_PERSON_NAME] = response.body()!!.person!!.display
                    userInfo[ApplicationConstants.UserKeys.USER_UUID] = response.body()!!.person!!.uuid
                    OpenMRS.getInstance().setCurrentUserInformation(userInfo)
                } else {
                    error(response.message())
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                error(t.message!!)
            }
        })
    }
}