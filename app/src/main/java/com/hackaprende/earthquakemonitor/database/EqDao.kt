package com.hackaprende.earthquakemonitor.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hackaprende.earthquakemonitor.Earthquake

@Dao
interface EqDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(eqList: MutableList<Earthquake>)

    @Query("SELECT * FROM eq_table")
    fun getEarthquake(): MutableList<Earthquake>

    @Query("SELECT * FROM eq_table order by magnitude ASC")
    fun getEarthquakeByMagnitude(): MutableList<Earthquake>
}