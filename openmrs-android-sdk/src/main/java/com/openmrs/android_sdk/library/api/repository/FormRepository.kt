package com.openmrs.android_sdk.library.api.repository

import com.openmrs.android_sdk.library.databases.AppDatabaseHelper
import com.openmrs.android_sdk.library.databases.entities.FormResourceEntity
import com.openmrs.android_sdk.library.models.FormData
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton
import java.util.concurrent.Callable

@Singleton
class FormRepository @Inject constructor() : BaseRepository() {

    /**
     * Fetches forms as a list of resources.
     *
     * @return observable list of form resources
     */
    fun fetchFormResourceList(): Observable<List<FormResourceEntity>> {
        return AppDatabaseHelper.createObservableIO(Callable {
            return@Callable db.formResourceDAO().getFormResourceList()
        })
    }

    /**
     * Creates a form.
     *
     * @param uuid UUID of the form resource
     * @param formData form data that will be created
     * @return observable boolean true if operation is successful
     */
    fun createForm(uuid: String, formData: FormData): Observable<Boolean> {
        return AppDatabaseHelper.createObservableIO(Callable {
            restApi.formCreate(uuid, formData).execute().run {
                if (isSuccessful && body()!!.name == "json") return@run true
                else throw Exception("Error creating forms: ${message()}")
            }
        })
    }
}
