package com.openmrs.android_sdk.library.api.repository

import com.openmrs.android_sdk.library.dao.PatientDAO
import com.openmrs.android_sdk.library.dao.VisitDAO
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper
import com.openmrs.android_sdk.library.models.Encounter
import com.openmrs.android_sdk.library.models.EncounterType
import com.openmrs.android_sdk.library.models.Encountercreate
import com.openmrs.android_sdk.library.models.ResultType
import com.openmrs.android_sdk.utilities.NetworkUtils
import com.openmrs.android_sdk.utilities.execute
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton
import java.util.concurrent.Callable

@Singleton
class EncounterRepository @Inject constructor() : BaseRepository() {

    /**
     * Saves an encounter to local database and to server when online.
     *
     * @param encounterCreate  the Encountercreate object submit
     * @return ResultType of operation result: full success, local success, or error.
     */
    fun saveEncounter(encounterCreate: Encountercreate): Observable<ResultType> {
        return AppDatabaseHelper.createObservableIO(Callable {
            val patient = PatientDAO().findPatientByID(encounterCreate.patientId.toString())
            val activeVisit = VisitDAO().getActiveVisitByPatientId(encounterCreate.patientId).execute()
            if (patient == null || activeVisit == null || encounterCreate.synced) {
                return@Callable ResultType.EncounterSubmissionError
            }

            encounterCreate.visit = activeVisit.uuid
            val encId = encounterCreate.id
            if (encId == null || getEncounterCreateFromDB(encId).execute() == null) {
                encounterCreate.id = saveEncounterCreateToDB(encounterCreate).execute()
            }

            if (patient.isSynced && NetworkUtils.isOnline()) {
                restApi.createEncounter(encounterCreate).execute().run {
                    if (isSuccessful) {
                        val encounter: Encounter = body()!!
                        encounter.encounterType = EncounterType(encounterCreate.formname)
                        for (i in encounterCreate.observations.indices) {
                            encounter.observations[i].displayValue = encounterCreate.observations[i].value
                        }

                        // Update the visit linked to this encounter
                        activeVisit.encounters += encounter
                        VisitDAO().saveOrUpdate(activeVisit, encounterCreate.patientId!!).execute()

                        updateEncounterCreate(encounterCreate.apply { synced = true }).execute()

                        return@Callable ResultType.EncounterSubmissionSuccess
                    } else {
                        throw Exception("syncEncounter error: ${message()}")
                    }
                }
            } else {
                // Update patient locally
                patient.addEncounters(encounterCreate.id)
                PatientDAO().updatePatient(patient.id!!, patient)
                // EncounterService will run to upload the encounter when online
                return@Callable ResultType.EncounterSubmissionLocalSuccess
            }
        })
    }

    /**
     * Gets EncounterCreate object from database by its ID
     *
     * @param id id of the EncounterCreate to be fetched
     * @return EncounterCreate object found
     */
    fun getEncounterCreateFromDB(id: Long): Observable<Encountercreate?> {
        return AppDatabaseHelper.createObservableIO(Callable {
            return@Callable db.encounterCreateRoomDAO().getCreatedEncountersByID(id)
        })
    }

    /**
     * Saves Encountercreate object to database to be used to create an encounter in the server later.
     *
     * @param encounterCreate the EncounterCreate to be saved
     * @return the id of the entry saved to the database
     */
    fun saveEncounterCreateToDB(encounterCreate: Encountercreate): Observable<Long> {
        return AppDatabaseHelper.createObservableIO(Callable {
            return@Callable db.encounterCreateRoomDAO().addEncounterCreated(encounterCreate)
        })
    }

    /**
     * Updates an existing encounterCreate object in the database.
     *
     * @param encounterCreate the EncounterCreate to be updated
     * @return the number of updated rows in the database
     */
    fun updateEncounterCreate(encounterCreate: Encountercreate): Observable<Unit> {
        return AppDatabaseHelper.createObservableIO(Callable {
            return@Callable db.encounterCreateRoomDAO().updateExistingEncounter(encounterCreate)
        })
    }
}
