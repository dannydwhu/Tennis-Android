package com.smartform.tennis.data.local.dao

import androidx.room.*
import com.smartform.tennis.data.local.entity.SensorDataEntity
import kotlinx.coroutines.flow.Flow

/**
 * 传感器原始数据数据访问对象
 */
@Dao
interface SensorDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: SensorDataEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(data: List<SensorDataEntity>)

    @Query("SELECT * FROM sensor_raw_data WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getBySessionId(sessionId: Long): Flow<List<SensorDataEntity>>

    @Query("SELECT * FROM sensor_raw_data WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    suspend fun getBySessionIdList(sessionId: Long): List<SensorDataEntity>

    @Query("SELECT * FROM sensor_raw_data WHERE sessionId = :sessionId AND timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp ASC")
    suspend fun getByTimeRange(sessionId: Long, startTime: Long, endTime: Long): List<SensorDataEntity>

    @Query("SELECT COUNT(*) FROM sensor_raw_data WHERE sessionId = :sessionId")
    suspend fun getCountBySessionId(sessionId: Long): Int

    @Query("DELETE FROM sensor_raw_data WHERE sessionId = :sessionId")
    suspend fun deleteBySessionId(sessionId: Long)

    @Query("DELETE FROM sensor_raw_data WHERE timestamp < :cutoffTime")
    suspend fun deleteOldData(cutoffTime: Long)

    @Query("SELECT * FROM sensor_raw_data WHERE isSynced = 0 LIMIT 1000")
    suspend fun getUnsyncedData(): List<SensorDataEntity>

    @Query("UPDATE sensor_raw_data SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Long)
}
