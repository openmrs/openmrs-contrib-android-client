package org.openmrs.mobile.activities.patientdashboard.charts

import androidx.lifecycle.SavedStateHandle
import com.openmrs.android_sdk.library.dao.VisitDAO
import com.openmrs.android_sdk.library.models.OperationType.PatientVisitsFetching
import com.openmrs.android_sdk.library.models.Visit
import com.openmrs.android_sdk.utilities.ApplicationConstants
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.openmrs.mobile.activities.BaseViewModel
import rx.android.schedulers.AndroidSchedulers
import javax.inject.Inject
import java.util.HashSet

@HiltViewModel
class PatientDashboardChartsViewModel @Inject constructor(
        private val visitDAO: VisitDAO,
        private val savedStateHandle: SavedStateHandle
) : BaseViewModel<JSONObject>() {

    private val patientId: String = savedStateHandle.get(PATIENT_ID_BUNDLE)!!

    fun fetchChartsData() {
        setLoading()
        addSubscription(visitDAO.getVisitsByPatientID(patientId.toLong())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { visits: List<Visit> -> setContent(getObservationListFromVisits(visits), PatientVisitsFetching) },
                        { setError(it) }
                ))
    }

    private fun getObservationListFromVisits(visits: List<Visit>): JSONObject {
        val displayableEncounterTypesArray = HashSet(ApplicationConstants.EncounterTypes.ENCOUNTER_TYPES_DISPLAYS.toList())
        val observationList = JSONObject()
        for (visit in visits) {
            val encounters = visit.encounters
            if (encounters.isNotEmpty()) {
                for (encounter in encounters) {
                    val datetime = encounter.encounterDate
                    val encounterTypeDisplay = encounter.encounterType!!.display
                    if (displayableEncounterTypesArray.contains(encounterTypeDisplay)) {
                        for (obs in encounter.observations) {
                            var observationLabel = obs.display
                            if (observationLabel!!.contains(":")) {
                                observationLabel = observationLabel.substring(0, observationLabel.indexOf(':'))
                            }
                            try {
                                if (observationList.has(observationLabel)) {
                                    val chartData: JSONObject? = observationList.getJSONObject(observationLabel)
                                    if (chartData!!.has(datetime)) {
                                        val obsValue: JSONArray? = chartData.getJSONArray(datetime)
                                        obsValue?.put(obs.displayValue)
                                        chartData.put(datetime, obsValue)
                                    } else {
                                        val obsValue = JSONArray()
                                        obsValue.put(obs.displayValue)
                                        chartData.put(datetime, obsValue)
                                    }
                                } else {
                                    val chartData = JSONObject()
                                    val obsValue = JSONArray()
                                    obsValue.put(obs.displayValue)
                                    chartData.put(datetime, obsValue)
                                    observationList.put(observationLabel, chartData)
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        }
        return observationList
    }

}
