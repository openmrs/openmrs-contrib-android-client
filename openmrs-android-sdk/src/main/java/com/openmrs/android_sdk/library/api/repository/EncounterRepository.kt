package com.openmrs.android_sdk.library.api.repository

import android.util.Log
import com.openmrs.android_sdk.library.OpenmrsAndroid
import com.openmrs.android_sdk.library.dao.EncounterDAO
import com.openmrs.android_sdk.library.dao.EncounterRoomDAO
import com.openmrs.android_sdk.library.dao.PatientDAO
import com.openmrs.android_sdk.library.dao.VisitDAO
import com.openmrs.android_sdk.library.databases.AppDatabase
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper
import com.openmrs.android_sdk.library.models.Encountercreate
import com.openmrs.android_sdk.library.models.Visit
import com.openmrs.android_sdk.library.models.ResultType
import com.openmrs.android_sdk.library.models.Encounter
import com.openmrs.android_sdk.library.models.EncounterType
import com.openmrs.android_sdk.library.models.ConceptClass
import com.openmrs.android_sdk.library.models.Resource
import com.openmrs.android_sdk.library.databases.entities.StandaloneEncounterEntity
import com.openmrs.android_sdk.utilities.NetworkUtils
import com.openmrs.android_sdk.utilities.execute
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.Callable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncounterRepository @Inject constructor(
    val visitRepository: VisitRepository,
    val encounterDAO: EncounterDAO
) : BaseRepository() {

    val encounterRoomDAO: EncounterRoomDAO = AppDatabase.getDatabase(
        OpenmrsAndroid.getInstance()!!.applicationContext
    ).encounterRoomDAO()

    /**
     * Saves an encounter to local database and to server when online.
     *
     * @param encounterCreate  the Encountercreate object submit
     * @return ResultType of operation result: full success, local success, or error.
     */
    fun saveEncounter(encounterCreate: Encountercreate): Observable<ResultType> {
        return AppDatabaseHelper.createObservableIO(Callable {
            val patient = PatientDAO().findPatientByID(encounterCreate.patientId.toString())
            val activeVisit = VisitDAO().getActiveVisitByPatientId(encounterCreate.patientId!!).execute()
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
                            encounter.observations[i].concept = ConceptClass().apply {
                                uuid = encounterCreate.observations[i].concept!!
                            }
                        }

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
     * Updates an encounter to the server.
     *
     * @param uuid the UUID of the encounter to be updated.
     * @param encounterCreate  the Encountercreate object containing the updates, and must contain patient local id.
     */
    fun updateEncounter(uuid: String, encounterCreate: Encountercreate): Observable<Unit> {
        return AppDatabaseHelper.createObservableIO(Callable {
            if (!NetworkUtils.isOnline()) throw Exception("Must be online to update the encounter")

            restApi.updateEncounter(uuid, encounterCreate).execute().run {
                if (isSuccessful) {
                    // Update the visit linked to this encounter
                    val patient = PatientDAO().findPatientByID(encounterCreate.patientId.toString())
                    visitRepository.syncVisitsData(patient).execute()

                    return@Callable
                } else {
                    throw Exception("updateEncounter error: ${message()}")
                }
            }
        })
    }

    /**
     * Get all encounter resources of a patient from the server.
     *
     * @param uuid the UUID of the patient
     * @return Observable<List<Resource>>
     */
    fun getAllEncounterResourcesByPatientUuid(uuid: String): Observable<List<Resource>> {
        return AppDatabaseHelper.createObservableIO(Callable {
            if (!NetworkUtils.isOnline()) throw Exception("Must be online to fetch encounters")

            restApi.getAllEncountersForPatientByPatientUuid(uuid).execute().run {
                if (isSuccessful) {
                    return@Callable this.body()?.results!!
                } else {
                    throw Exception("Get Encounters error: ${message()}")
                }
            }
        })
    }

    /**
     * Get all encounters of a patient from the server
     * and save to local database.
     *
     * @param uuid the UUID of the patient
     * @return Observable<Encounter>
     */
    fun getAllEncountersByPatientUuidAndSaveLocally(uuid: String): Observable<List<Encounter>> {
        val encounterList: MutableList<Encounter> = mutableListOf()
        if (!NetworkUtils.isOnline()) throw Exception("Must be online to fetch encounters")

        restApi.getAllEncountersForPatientByPatientUuid(uuid).execute().run {
            if (isSuccessful && this.body() != null) {

                val encounterResources: List<Resource> = this.body()!!.results
                for (encounterResource in encounterResources) {
                    val encounter = getEncounterByUuid(encounterResource.uuid!!).execute()
                    encounterList.add(encounter)
                }
                encounterDAO.deleteAllStandaloneEncounters(uuid)      //delete previous list
                encounterDAO.saveStandaloneEncounters(encounterList) //save latest list
                return Observable.just(encounterList.toList())
            } else {
                throw Exception("Get Encounters error: ${message()}")
            }
        }
    }

    /**
     * Get all encounters of a patient from the server filtered by visit uuid
     *
     * @param visitUuid the UUID of the Visit
     * @return Observable<Resource>?
     */
    fun getAllEncounterResourcesByVisitUuid(visitUuid: String): Observable<Resource>? {
        var encounterResourceList: List<Resource> = mutableListOf()
        val fetchedVisit: Visit? = visitRepository.getVisit(visitUuid).execute()
        if(fetchedVisit != null) encounterResourceList = fetchedVisit.encounters

        return Observable.from(encounterResourceList)
    }

    /**
     * Get an Encounter from Encounter Uuid from the server
     *
     * @param uuid the UUID of the encounter
     * @return Observable<Encounter>
     */

    fun getEncounterByUuid(uuid: String): Observable<Encounter> {
        return AppDatabaseHelper.createObservableIO(Callable{
            if (!NetworkUtils.isOnline()) throw Exception("Must be online to fetch encounters")

            restApi.getEncounterByUuid(uuid).execute().run{
                if (isSuccessful && body() != null) {
                    return@Callable this.body()!!
                } else {
                    throw Exception("Get Encounters error: ${message()}")
                }
            }
        })
    }

    /**
     * Utility Function
     */

    fun saveLocallyIfNotExist(encounterList: List<Encounter>) {

        val existingStandaloneEncounters: List<StandaloneEncounterEntity> =
            encounterRoomDAO.getAllStandAloneEncounters().blockingGet()

        val encountersToInsert = mutableListOf<Encounter>()

        for (encounterToSave in encounterList) {
            var exists = false

            for (existingEncounter in existingStandaloneEncounters) {
                if (existingEncounter.uuid.equals(encounterToSave.uuid)) {
                    exists = true
                    break
                }
            }

            if (!exists) {
                encountersToInsert.add(encounterToSave)
            }
        }
        encounterDAO.saveStandaloneEncounters(encountersToInsert)
    }

    /**
     * Get all encounters of a patient from the server
     * filtered by the Encounter Type and save to local database.
     *
     * @param patient_uuid the UUID of the patient
     * @param encounterType_uuid the UUID of the EncounterType
     *
     * @return Observable<Encounter>
     */

    fun getAllEncountersByPatientUuidAndEncounterTypeAndSaveLocally(
        patient_uuid: String,
        encounterType_uuid: String
    ): Observable<List<Encounter>> {

        val encounterList: MutableList<Encounter> = mutableListOf()
        if (!NetworkUtils.isOnline()) throw Exception("Must be online to fetch encounters")

        restApi.getEncounterResourcesByEncounterType(patient_uuid, encounterType_uuid).execute().run {

                if (isSuccessful && this.body() != null) {

                    val encounterResources: List<Resource> = this.body()!!.results
                    for (encounterResource in encounterResources) {
                        val encounter = getEncounterByUuid(encounterResource.uuid!!).execute()
                        encounterList.add(encounter)
                    }

                    saveLocallyIfNotExist(encounterList)
                    return Observable.just(encounterList.toList())
                } else {
                    throw Exception("Get Encounters error: ${message()}")
                }
        }

    }

    /**
     * Get all encounters of a patient from the server
     * filtered by the Order Type and save to local database.
     *
     * @param patient_uuid the UUID of the patient
     * @param orderType_uuid the UUID of the EncounterType
     *
     * @return Observable<Encounter>
     */

    fun getAllEncountersByPatientUuidAndOrderTypeAndSaveLocally(
        patient_uuid: String,
        orderType_uuid: String
    ): Observable<List<Encounter>> {

        val encounterList: MutableList<Encounter> = mutableListOf()
        if (!NetworkUtils.isOnline()) throw Exception("Must be online to fetch encounters")

        restApi.getEncounterResourcesByOrderType(patient_uuid, orderType_uuid).execute()
            .run {

                if (isSuccessful && this.body() != null) {

                    val encounterResources: List<Resource> = this.body()!!.results
                    for (encounterResource in encounterResources) {
                        val encounter = getEncounterByUuid(encounterResource.uuid!!).execute()
                        encounterList.add(encounter)
                    }

                    saveLocallyIfNotExist(encounterList)
                    return Observable.just(encounterList.toList())
                } else {
                    throw Exception("Get Encounters error: ${message()}")
                }
            }

    }

    /**
     * Get all encounters of a patient from the server
     * filtered from the given Date and save to local database.
     *
     * @param patient_uuid the UUID of the patient
     * @param fromDate the String representation of Date in 'YYYY-MM-DD' format
     *
     * @return Observable<Encounter>
     */

    fun getAllEncountersByPatientUuidAndFromDateAndSaveLocally(
        patient_uuid: String,
        fromDate: String
    ): Observable<List<Encounter>> {

        val encounterList: MutableList<Encounter> = mutableListOf()
        if (!NetworkUtils.isOnline()) throw Exception("Must be online to fetch encounters")

        restApi.getEncounterResourcesFromDate(patient_uuid, fromDate).execute()
            .run {

                if (isSuccessful && this.body() != null) {

                    val encounterResources: List<Resource> = this.body()!!.results
                    for (encounterResource in encounterResources) {
                        val encounter = getEncounterByUuid(encounterResource.uuid!!).execute()
                        encounterList.add(encounter)
                    }

                    saveLocallyIfNotExist(encounterList)
                    return Observable.just(encounterList.toList())
                } else {
                    throw Exception("Get Encounters error: ${message()}")
                }
            }

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
