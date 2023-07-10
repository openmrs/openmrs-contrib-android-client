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
package com.openmrs.android_sdk.library.databases

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.openmrs.android_sdk.library.OpenmrsAndroid
import com.openmrs.android_sdk.library.api.repository.FormRepository
import com.openmrs.android_sdk.library.dao.EncounterDAO
import com.openmrs.android_sdk.library.dao.EncounterRoomDAO
import com.openmrs.android_sdk.library.dao.ObservationDAO
import com.openmrs.android_sdk.library.dao.PatientDAO
import com.openmrs.android_sdk.library.databases.entities.*
import com.openmrs.android_sdk.library.di.entrypoints.RepositoryEntryPoint
import com.openmrs.android_sdk.library.models.*
import com.openmrs.android_sdk.utilities.ApplicationConstants
import com.openmrs.android_sdk.utilities.DateUtils
import com.openmrs.android_sdk.utilities.DateUtils.convertTime
import com.openmrs.android_sdk.utilities.execute
import dagger.hilt.android.EntryPointAccessors
import rx.Observable
import rx.schedulers.Schedulers
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.concurrent.Callable

object AppDatabaseHelper {
    val encounterRoomDAO: EncounterRoomDAO = AppDatabase.getDatabase(
        OpenmrsAndroid.getInstance()!!.applicationContext
    ).encounterRoomDAO()

    @JvmStatic
    fun convert(obs: Observation, encounterID: Long): ObservationEntity {
        val observationEntity = ObservationEntity()
        observationEntity.id = obs.id
        observationEntity.uuid = obs.uuid
        observationEntity.display = obs.display
        observationEntity.encounterKeyID = encounterID
        observationEntity.displayValue = obs.displayValue
        observationEntity.diagnosisOrder = obs.diagnosisOrder
        observationEntity.diagnosisList = obs.diagnosisList
        observationEntity.diagnosisCertainty = obs.diagnosisCertainty
        observationEntity.diagnosisNote = obs.diagnosisNote
        observationEntity.conceptuuid = obs.concept?.uuid
        return observationEntity
    }

    @JvmStatic
    fun convert(obs: ObservationEntity): Observation {

        val observation = Observation()

        val encounter = Encounter()
        encounter.uuid = encounterRoomDAO.getEncounterUuidByID(obs.encounterKeyID).toString()

        val person = Person()
        person.uuid = obs.patientUuid

        val concept = ConceptClass()
        concept.uuid = obs.conceptuuid

        observation.person = person
        observation.value = obs.displayValue
        observation.concept = concept
        observation.uuid = obs.uuid
        observation.display = obs.display
        observation.encounter = encounter
        observation.diagnosisOrder = obs.diagnosisOrder
        observation.diagnosisList = obs.diagnosisList
        observation.diagnosisCertainty = obs.diagnosisCertainty
        observation.diagnosisNote = obs.diagnosisNote
        observation.obsDatetime = obs.obsDateTime

        return observation
    }

    @JvmStatic
    fun convert(observationEntityList: List<ObservationEntity>): List<Observation> {
        val observationList: MutableList<Observation> = ArrayList()
        for (entity in observationEntityList) {
            val obs = Observation()
            obs.id = entity.id
            obs.encounterID = entity.encounterKeyID
            obs.uuid = entity.uuid
            obs.display = entity.display
            obs.displayValue = entity.displayValue
            obs.diagnosisOrder = entity.diagnosisOrder
            obs.diagnosisList = entity.diagnosisList
            obs.setDiagnosisCertanity(entity.diagnosisCertainty)
            obs.diagnosisNote = entity.diagnosisNote
            val concept = ConceptClass()
            concept.uuid = entity.conceptuuid
            obs.concept = concept
            observationList.add(obs)
        }
        return observationList
    }

    @JvmStatic
    fun convert(encounter: Encounter, visitID: Long?): EncounterEntity {
        val encounterEntity = EncounterEntity()
        encounterEntity.id = encounter.id
        encounterEntity.display = encounter.display
        encounterEntity.uuid = encounter.uuid
        if (visitID != null) {
            encounterEntity.visitKeyId = visitID.toString()
        }
        encounterEntity.encounterDateTime = encounter.encounterDatetime.toString()
        encounterEntity.encounterType = encounter.encounterType!!.display
        encounterEntity.patientUuid = encounter.patient?.uuid
        encounterEntity.formUuid = encounter.formUuid
        if (null == encounter.location) {
            encounterEntity.locationUuid = null
        } else {
            encounterEntity.locationUuid = encounter.location!!.uuid
        }
        if (encounter.encounterProviders.isEmpty()) {
            encounterEntity.encounterProviderUuid = null
        } else {
            encounterEntity.encounterProviderUuid = encounter.encounterProviders[0].uuid
        }
        return encounterEntity
    }

    @JvmStatic
    fun convertToStandalone(encounter: Encounter): StandaloneEncounterEntity {
        return StandaloneEncounterEntity(
            display = encounter.display,
            uuid = encounter.uuid,
            encounterDateTime = encounter.encounterDate,
            encounterType = encounter.encounterType?.uuid,
            patientUuid = encounter.patientUUID,
            formUuid = encounter.formUuid,
            locationUuid = encounter.location?.uuid,
            encounterProviderUuid = if(encounter.encounterProviders.isEmpty()) null
                                    else encounter.encounterProviders[0].uuid,
            visitUuid = encounter.visit?.uuid
        )
    }

    @JvmStatic
    fun convert(entity: EncounterEntity): Encounter {
        val encounter = Encounter()
        if (null != entity.encounterType) {
            encounter.encounterType = EncounterType(entity.encounterType)
        }
        encounter.id = entity.id
        if (null != entity.visitKeyId) {
            encounter.visitID = entity.visitKeyId.toLong()
        }
        encounter.uuid = entity.uuid
        encounter.display = entity.display
        val dateTime = entity.encounterDateTime.toLong()
        encounter.setEncounterDatetime(convertTime(dateTime, DateUtils.OPEN_MRS_REQUEST_FORMAT))
        encounter.observations = ObservationDAO().findObservationByEncounterID(entity.id)
        encounter.patient = PatientDAO().findPatientByUUID(entity.patientUuid)
        val location: LocationEntity? = try {
            AppDatabase
                    .getDatabase(OpenmrsAndroid.getInstance()?.applicationContext)
                    .locationRoomDAO()
                    .findLocationByUUID(entity.locationUuid)
                    .blockingGet()
        } catch (e: Exception) {
            null
        }
        encounter.location = location
        val formRepository: FormRepository = EntryPointAccessors.fromApplication(
                OpenmrsAndroid.getInstance()!!.applicationContext,
                RepositoryEntryPoint::class.java
        ).provideFormRepository()
        encounter.form = formRepository.fetchFormByUuid(entity.formUuid).execute()
        return encounter
    }

    @JvmStatic
    fun convert(visitEntity: VisitEntity): Visit {
        val visit = Visit()
        visit.id = visitEntity.id
        visit.uuid = visitEntity.uuid
        visit.display = visitEntity.display
        visit.visitType = VisitType(visitEntity.visitType)
        try {
            val locationEntity = AppDatabase
                    .getDatabase(OpenmrsAndroid.getInstance()?.applicationContext)
                    .locationRoomDAO()
                    .findLocationByName(visitEntity.visitPlace)
                    .blockingGet()
            visit.location = locationEntity
        } catch (e: Exception) {
            visit.location = LocationEntity(visitEntity.visitPlace)
        }
        visit.startDatetime = visitEntity.startDate
        visit.stopDatetime = visitEntity.stopDate
        visit.encounters = EncounterDAO().findEncountersByVisitID(visitEntity.id)
        visit.patient = PatientDAO().findPatientByID(visitEntity.patientKeyID.toString())
        return visit
    }

    @JvmStatic
    fun convert(visit: Visit): VisitEntity {
        val visitEntity = VisitEntity()
        visitEntity.id = visit.id
        visitEntity.uuid = visit.uuid
        visitEntity.patientKeyID = visit.patient.id!!
        visitEntity.visitType = visit.visitType.display
        visitEntity.visitPlace = visit.location.display
        visitEntity.isStartDate = visit.startDatetime
        visitEntity.stopDate = visit.stopDatetime
        return visitEntity
    }

    @JvmStatic
    fun convert(patientEntity: PatientEntity): Patient {
        val patient = Patient(patientEntity.id, patientEntity.encounters, null)
        patient.display = patientEntity.display
        patient.uuid = patientEntity.uuid
        val patientIdentifier = PatientIdentifier()
        patientIdentifier.identifier = patientEntity.identifier
        if (patient.identifiers == null) {
            patient.identifiers = ArrayList()
        }
        patient.identifiers.add(patientIdentifier)
        val personName = PersonName()
        personName.givenName = patientEntity.givenName
        personName.middleName = patientEntity.middleName
        personName.familyName = patientEntity.familyName
        patient.names.add(personName)
        patient.gender = patientEntity.gender
        patient.birthdate = patientEntity.birthDate
        val photoByteArray = patientEntity.photo
        if (photoByteArray != null) {
            patient.photo = byteArrayToBitmap(photoByteArray)
        }
        val personAddress = PersonAddress()
        personAddress.address1 = patientEntity.address_1
        personAddress.address2 = patientEntity.address_2
        personAddress.postalCode = patientEntity.postalCode
        personAddress.country = patientEntity.country
        personAddress.stateProvince = patientEntity.state
        personAddress.cityVillage = patientEntity.city
        patient.addresses.add(personAddress)
        if (patientEntity.causeOfDeath != null) {
            patient.causeOfDeath = Resource(ApplicationConstants.EMPTY_STRING, patientEntity.causeOfDeath, ArrayList(), 0)
        }
        patient.isDeceased = patientEntity.deceased == "true"
        return patient
    }

    @JvmStatic
    fun convert(patient: Patient): PatientEntity {
        val patientEntity = PatientEntity()
        patientEntity.display = patient.name.nameString
        patientEntity.uuid = patient.uuid
        patientEntity.isSynced = patient.isSynced
        if (patient.identifier != null) {
            patientEntity.identifier = patient.identifier.identifier
        } else {
            patientEntity.identifier = null
        }
        patientEntity.givenName = patient.name.givenName
        patientEntity.middleName = patient.name.middleName
        patientEntity.familyName = patient.name.familyName
        patientEntity.gender = patient.gender
        patientEntity.birthDate = patient.birthdate
        patientEntity.deathDate = null
        if (null != patient.causeOfDeath) {
            if (patient.causeOfDeath.display == null) {
                patientEntity.causeOfDeath = null
            } else {
                patientEntity.causeOfDeath = patient.causeOfDeath.display
            }
        } else {
            patientEntity.causeOfDeath = null
        }
        patientEntity.age = null
        if (patient.photo != null) {
            patientEntity.photo = bitmapToByteArray(patient.photo)
        } else {
            patientEntity.photo = null
        }
        if (null != patient.address) {
            patientEntity.address_1 = patient.address.address1
            patientEntity.address_2 = patient.address.address2
            patientEntity.postalCode = patient.address.postalCode
            patientEntity.country = patient.address.country
            patientEntity.state = patient.address.stateProvince
            patientEntity.city = patient.address.cityVillage
        }
        patientEntity.encounters = patient.encounters
        patientEntity.deceased = patient.isDeceased.toString()
        return patientEntity
    }

    @JvmStatic
    fun convert(allergy: Allergy, patientID: String?): AllergyEntity {
        val allergyEntity = AllergyEntity()
        allergyEntity.uuid = allergy.uuid
        allergyEntity.patientId = patientID
        allergyEntity.comment = allergy.comment
        if (allergy.severity != null) {
            allergyEntity.severityDisplay = allergy.severity!!.display
            allergyEntity.severityUUID = allergy.severity!!.uuid
        }
        allergyEntity.allergenDisplay = allergy.allergen!!.codedAllergen!!.display
        allergyEntity.allergenUUID = allergy.allergen!!.codedAllergen!!.uuid
        allergyEntity.allergenType = allergy.allergen!!.allergenType
        allergyEntity.allergyReactions = allergy.reactions
        return allergyEntity
    }

    @JvmStatic
    @JvmName("convertTo")
    fun convert(entities: List<AllergyEntity>): List<Allergy> {
        val allergies = ArrayList<Allergy>()
        for (allergyEntity in entities) {
            allergies.add(convert(allergyEntity))
        }
        return allergies
    }

    @JvmStatic
    fun convert(allergyEntity: AllergyEntity): Allergy {
        val allergy = Allergy()
        allergy.id = allergyEntity.id
        allergy.uuid = allergyEntity.uuid
        allergy.comment = allergyEntity.comment
        if (allergyEntity.allergyReactions != null) {
            allergy.reactions = allergyEntity.allergyReactions!!
        } else {
            allergy.reactions = ArrayList()
        }
        val allergen = Allergen()
        allergen.allergenType = allergyEntity.allergenType
        allergen.codedAllergen = Resource(allergyEntity.allergenUUID!!, allergyEntity.allergenDisplay!!, ArrayList(), 1)
        allergy.allergen = allergen
        if (allergyEntity.severityDisplay != null) {
            allergy.severity = Resource(allergyEntity.severityUUID!!, allergyEntity.severityDisplay!!, ArrayList(), 1)
        }
        return allergy
    }

    @JvmStatic
    fun convertToStandalone(observation: Observation): StandaloneObservationEntity {
        return StandaloneObservationEntity(
            uuid = observation.uuid,
            display = observation.display,
            encounterUuid = observation.encounter?.uuid,
            patientUuid = observation.person?.uuid,
            locationUuid = observation.location?.uuid,
            value = observation.value.toString(),
            status = observation.status,
            obsDateTime = observation.obsDatetime,
            interpretation = observation.interpretation,
            conceptuuid = observation.concept?.uuid,
            order = observation.order,
            comment = observation.comment
        )
    }

    private fun bitmapToByteArray(image: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 0, outputStream)
        return outputStream.toByteArray()
    }

    private fun byteArrayToBitmap(imageByteArray: ByteArray): Bitmap {
        val inputStream = ByteArrayInputStream(imageByteArray)
        return BitmapFactory.decodeStream(inputStream)
    }

    @JvmStatic
    fun <T> createObservableIO(func: Callable<T>?): Observable<T> {
        return Observable.fromCallable(func)
                .subscribeOn(Schedulers.io())
    }

    fun convert(appointment: Appointment): AppointmentEntity {

        val timeSlot = appointment.timeSlot
        val appointmentBlock = appointment.timeSlot?.appointmentBlock
        val patient = appointment.patient
        val visit = appointment.visit
        val appointmentType = appointment.appointmentType

        val appointmentProviderEntity  = AppointmentProviderEntity()
        appointmentProviderEntity.display = appointmentBlock?.provider?.display
        appointmentProviderEntity.uuid = appointmentBlock?.provider?.uuid

        val appointmentLocationEntity = AppointmentLocationEntity()
        appointmentLocationEntity.display = appointmentBlock?.location?.display
        appointmentLocationEntity.uuid = appointmentBlock?.location?.uuid

        val appointmentBlockEntity = AppointmentBlockEntity()
        val blockTypesObjectList = appointmentBlock?.types // List received from server
        val typeStringList = mutableListOf<String>() // Entity requires this list
        for(type in blockTypesObjectList!!) {
            typeStringList.add(type.display!!)
        }
        appointmentBlockEntity.uuid = appointmentBlock.uuid
        appointmentBlockEntity.display = appointmentBlock.display
        appointmentBlockEntity.startDate = appointmentBlock.startDate
        appointmentBlockEntity.endDate = appointmentBlock.endDate
        appointmentBlockEntity.types = typeStringList
        appointmentBlockEntity.provider = appointmentProviderEntity
        appointmentBlockEntity.location = appointmentLocationEntity

        val timeSlotEntity = TimeSlotEntity()
        timeSlotEntity.uuid = appointment.timeSlot?.uuid
        timeSlotEntity.display = appointment.timeSlot?.display
        timeSlotEntity.startDate = timeSlot?.startDate
        timeSlotEntity.endDate = timeSlot?.endDate
        timeSlotEntity.appointmentBlock = appointmentBlockEntity

        val appointmentPatientEntity = AppointmentPatientEntity()
        appointmentPatientEntity.display = patient?.name?.nameString
        appointmentPatientEntity.uuid = patient?.uuid

        val appointmentVisitEntity = AppointmentVisitEntity()
        appointmentVisitEntity.uuid = visit?.uuid
        appointmentVisitEntity.display = visit?.display

        val appointmentTypeEntity = AppointmentTypeEntity()
        appointmentTypeEntity.uuid = appointmentType?.uuid
        appointmentTypeEntity.display = appointmentType?.display
        appointmentTypeEntity.description = appointmentType?.description
        appointmentTypeEntity.duration = appointmentType?.duration
        appointmentTypeEntity.confidential = appointmentType?.confidential!!

        val appointmentEntity = AppointmentEntity()

        appointmentEntity.uuid = appointment.uuid
        appointmentEntity.display = appointment.display
        appointmentEntity.status = appointment.status
        appointmentEntity.reason = appointment.reason
        appointmentEntity.timeSlot = timeSlotEntity
        appointmentEntity.patient = appointmentPatientEntity
        appointmentEntity.visit = appointmentVisitEntity

        return appointmentEntity
    }

    fun convert(orderGet: OrderGet): OrderEntity {
        val orderEntity = OrderEntity()
        orderEntity.uuid = orderGet.uuid
        orderEntity.display = orderGet.display
        orderEntity.action = orderGet.action
        orderEntity.accessionNumber = orderGet.accessionNumber
        orderEntity.autoExpireDate = orderGet.autoExpireDate
        orderEntity.careSettingName = orderGet.careSettingName
        orderEntity.conceptUuid = orderGet.conceptUuid
        orderEntity.dateActivated = orderGet.dateActivated
        orderEntity.dateStopped = orderGet.dateStopped
        orderEntity.doseUnits = orderGet.doseUnits
        orderEntity.urgency = orderGet.urgency
        orderEntity.orderer.display = orderGet.orderer.display
        orderEntity.orderer.uuid = orderGet.orderer.uuid
        orderEntity.dosingType = orderGet.dosingType
        orderEntity.drug = orderGet.drug
        orderEntity.fulfillerStatus = orderGet.fulfillerStatus
        orderEntity.specimenSource = orderGet.specimenSource
        orderEntity.instructions = orderGet.instructions
        orderEntity.type = orderGet.type
        orderEntity.orderType.uuid = orderGet.orderType.uuid
        orderEntity.orderType.display = orderGet.orderType.display
        orderEntity.quantity = orderGet.quantity
        orderEntity.dosingInstructions = orderGet.dosingInstructions
        orderEntity.encounterUuid = orderGet.encounterUuid
        orderEntity.fulfillerComment = orderGet.fulfillerComment
        orderEntity.scheduledDate = orderGet.scheduledDate
        orderEntity.numberOfRepeats = orderGet.numberOfRepeats
        orderEntity.orderReason = orderGet.orderReason
        orderEntity.duration = orderGet.duration
        orderEntity.orderNumber = orderGet.orderNumber

        return orderEntity
    }
}
