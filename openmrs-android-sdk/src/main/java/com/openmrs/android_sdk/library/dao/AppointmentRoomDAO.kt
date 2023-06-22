package com.openmrs.android_sdk.library.dao

import androidx.room.*
import com.openmrs.android_sdk.library.databases.entities.AppointmentEntity
import io.reactivex.Single

/**
 * The interface Appointment room dao.
 */
@Dao
interface AppointmentRoomDAO {

    /**
     * Add or update long.
     *
     * @param appointmentEntity the appointment entity
     * @return the long
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrUpdate(appointmentEntity: AppointmentEntity): Long

    /**
     * Add appointment
     *
     * @param apppointment the appointment entity
     * @return the long
     */
    @Insert
    fun addAppointment(appointmentEntity: AppointmentEntity): Long

    /**
     * Update appointment
     *
     * @param appointmentEntity the appointment entity
     * @return the int
     */
    @Update
    fun updateAppointment(appointmentEntity: AppointmentEntity): Int

    /**
     * Filter appointments based on appointment status
     *
     * @return the appointments
     */
    @Query("SELECT * FROM appointments WHERE status = :status")
    fun getAppointmentsWithStatus(status: String): Single<List<AppointmentEntity>>

    /**
     * Filter appointments based on appointment type
     *
     * @return the appointments
     */
    @Query("SELECT * FROM appointments WHERE type_display = :typeDisplay")
    fun getAppointmentsWithTypeDisplay(typeDisplay: String): Single<List<AppointmentEntity>>

    /**
     * Filter appointments for a given visit
     *
     * @return the appointments
     */
    @Query("SELECT * FROM appointments WHERE visit_uuid = :visitUuid")
    fun getAppointmentsForVisit(visitUuid: String): Single<List<AppointmentEntity>>

    /**
     * Get appointments for a patient
     *
     * @return the appointments
     */
    @Query("SELECT * FROM appointments WHERE patient_uuid = :patientUuid")
    fun getAppointmentsForPatient(patientUuid: String): Single<List<AppointmentEntity>>
}