package com.medical.data.database.dao

import androidx.room.*
import com.medical.data.tables.DataModel

@Dao
interface DataDao {

    @Query("SELECT * FROM data")
    fun getAllData(): List<DataModel>

    @Query("SELECT * FROM data WHERE id == :dataId")
    fun getDataById(dataId: Int): DataModel

    @Query("SELECT * FROM data WHERE cycleDate == :date")
    fun getDataByDate(date: String): List<DataModel>

    @Query("SELECT * FROM data WHERE cycleDateFormatted BETWEEN :startDate AND :endDate")
    fun getDataByDateRange(startDate: Long, endDate: Long): List<DataModel>

    @Query("SELECT * FROM data WHERE packageString LIKE :packageName AND cycleDateFormatted BETWEEN :startDate AND :endDate")
    fun getDataByPackage(packageName: String, startDate: Long, endDate: Long): List<DataModel>

    @Query("SELECT * FROM data WHERE autoclave == :autoclaveName AND cycleDateFormatted BETWEEN :startDate AND :endDate")
    fun getDataByAutoclave(autoclaveName: String, startDate: Long, endDate: Long): List<DataModel>

    @Query("SELECT * FROM data WHERE cycleUserId == :userId AND cycleDateFormatted BETWEEN :startDate AND :endDate")
    fun getDataByCycleUser(userId: String, startDate: Long, endDate: Long): List<DataModel>

    @Query("SELECT * FROM data WHERE biUserId == :userId AND cycleDateFormatted BETWEEN :startDate AND :endDate")
    fun getDataByBITestUser(userId: String, startDate: Long, endDate: Long): List<DataModel>

    @Query("SELECT * FROM data WHERE cycleDateFormatted BETWEEN :startDate AND :endDate AND (biResult == :status OR cycleTemperature LIKE :tempPressureStatus OR cyclePressure LIKE :tempPressureStatus)")
    fun getDataByStatus(
        status: String,
        tempPressureStatus: String,
        startDate: Long,
        endDate: Long
    ): List<DataModel>

    @Query("SELECT * FROM data WHERE packageString LIKE :barcode ")
    fun getDataByBarcode(barcode: String): List<DataModel>

    @Query("SELECT * FROM data WHERE cycleNumber = :cycleNumber ")
    fun getDataByCycleNumber(cycleNumber: String): List<DataModel>

    @Query("SELECT * FROM data WHERE cycleDate == :date OR biResult == :status OR biTestDate == :biDate")
    fun getDataByDateAndPendingBITest(date: String, status: String, biDate: String): List<DataModel>

    @Query("SELECT * FROM data WHERE cycleDate == :date AND biResult == :status")
    fun getDataByDateAndPendingBITest(date: String, status: String): List<DataModel>

    @Query("SELECT * FROM data WHERE cycleDate == :date AND autoclave == :autoclave AND biResult == :status")
    fun getDataByDateAndPassBITest(date: String, autoclave: String, status: String): List<DataModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addDate(dataModel: DataModel): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateData(vararg dataModel: DataModel)

    @Query("SELECT * FROM data WHERE cycleDateFormatted BETWEEN :startDate AND  :endDate")
    fun getDataToDelete(startDate: Long, endDate: Long): List<DataModel>

    @Delete
    fun deleteDate(dataModel: DataModel);
}