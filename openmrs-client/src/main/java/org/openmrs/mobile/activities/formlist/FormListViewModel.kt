package org.openmrs.mobile.activities.formlist

import com.openmrs.android_sdk.library.OpenmrsAndroid
import com.openmrs.android_sdk.library.api.repository.FormRepository
import com.openmrs.android_sdk.library.dao.EncounterDAO
import com.openmrs.android_sdk.library.databases.entities.FormResourceEntity
import com.openmrs.android_sdk.library.models.FormData
import com.openmrs.android_sdk.utilities.execute
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONException
import org.json.JSONObject
import org.openmrs.mobile.activities.BaseViewModel
import rx.android.schedulers.AndroidSchedulers
import javax.inject.Inject
import java.io.IOException
import java.nio.charset.StandardCharsets

@HiltViewModel
class FormListViewModel @Inject constructor(
        private val encounterDAO: EncounterDAO,
        private val formRepository: FormRepository
) : BaseViewModel<Array<String>>() {

    private val formResourceList = mutableListOf<FormResourceEntity>()

    init {
        loadFormResourceList()
    }

    private fun loadFormResourceList() {
        setLoading()
        addSubscription(formRepository.fetchFormResourceList()
                .map {
                    for (formResource in it) {
                        var valueRefString: String? = null
                        for (resource in formResource.resources) {
                            if (resource.name == "json") valueRefString = resource.valueReference
                        }
                        if (!valueRefString.isNullOrBlank()) {
                            formResourceList.add(formResource)
                        } else {
                            // If no form fields provided, upload this form from local asset file
                            val formData = createFormDataFromAsset(formResource.name!!.toLowerCase())
                            formData?.let { formRepository.createForm(formResource.uuid!!, formData).execute() }
                        }
                    }
                    val size = formResourceList.size
                    val forms = ArrayList<String>(size)
                    for (i in 0 until size) forms += formResourceList[i].name!!

                    return@map forms.toTypedArray()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ setContent(it) }, { setError(it) })
        )
    }

    private fun createFormDataFromAsset(formName: String): FormData? {
        var formData: FormData? = null
        if (formName.contains("admission")) {
            formData = parseFormDataFromAsset("admission.json")
        } else if (formName.contains("vitals")) {
            formData = parseFormDataFromAsset("vitals1.json")
                    ?: parseFormDataFromAsset("vitals2.json")
        } else if (formName.contains("visit note")) {
            formData = parseFormDataFromAsset("visit_note.json")
        }
        return formData
    }

    private fun parseFormDataFromAsset(filename: String): FormData? {
        val json: String?
        json = try {
            val stream = OpenmrsAndroid.getInstance()!!.assets.open("forms/$filename")
            val buffer = ByteArray(stream.available())
            stream.read(buffer)
            stream.close()
            String(buffer, StandardCharsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        val obj: JSONObject?
        try {
            obj = JSONObject(json)
            val data = FormData()
            data.name = obj.getString("name")
            data.dataType = obj.getString("dataType")
            data.valueReference = obj.getString("valueReference")
            return data
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }

    inner class SelectedForm(private val position: Int) {
        var formName: String? = null
            private set
        var encounterName: String? = null
            private set
        var encounterType: String? = null
            private set
        var formFieldsJson: String? = null
            private set

        init {
            click()
        }

        private fun click() {
            formName = formResourceList[position].name
            encounterName = formName!!.split("\\(".toRegex()).toTypedArray()[0].trim { it <= ' ' }
            encounterType = encounterDAO.getEncounterTypeByFormName(encounterName!!)?.uuid
            formResourceList[position].resources.forEach {
                if (it.name == "json") formFieldsJson = it.valueReference
            }
        }
    }
}
