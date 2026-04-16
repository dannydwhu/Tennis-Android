package com.smartform.tennis.data.local.dao

import androidx.room.*
import com.smartform.tennis.data.local.entity.ShotEntity
import kotlinx.coroutines.flow.Flow

/**
 * 击球记录数据访问对象
 */
@Dao
interface ShotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shot: ShotEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(shots: List<ShotEntity>)

    @Update
    suspend fun update(shot: ShotEntity)

    @Delete
    suspend fun delete(shot: ShotEntity)

    @Query("SELECT * FROM shots WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getBySessionId(sessionId: Long): Flow<List<ShotEntity>>

    @Query("SELECT * FROM shots WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    suspend fun getBySessionIdList(sessionId: Long): List<ShotEntity>

    @Query("SELECT * FROM shots WHERE userId = :userId ORDER BY timestamp DESC")
    fun getByUserId(userId: Long): Flow<List<ShotEntity>>

    @Query("SELECT * FROM shots WHERE userId = :userId AND shotType = :shotType ORDER BY timestamp DESC")
    fun getByUserIdAndType(userId: Long, shotType: String): Flow<List<ShotEntity>>

    @Query("SELECT * FROM shots WHERE userId = :userId ORDER BY maxSpeed DESC")
    suspend fun getTopBySpeed(userId: Long): List<ShotEntity>

    @Query("SELECT * FROM shots WHERE isSynced = 0")
    suspend fun getUnsyncedShots(): List<ShotEntity>

    @Query("UPDATE shots SET isSynced = 1 WHERE id = :shotId")
    suspend fun markAsSynced(shotId: Long)

    @Query("DELETE FROM shots WHERE sessionId = :sessionId")
    suspend fun deleteBySessionId(sessionId: Long)

    @Query("SELECT COUNT(*) FROM shots WHERE userId = :userId")
    suspend fun getCountByUserId(userId: Long): Int

    @Query("SELECT shotType, COUNT(*) as count FROM shots WHERE userId = :userId GROUP BY shotType")
    suspend fun getCountByType(userId: Long): List<Map<String, Any>>

    @Query("SELECT MAX(maxSpeed) FROM shots WHERE userId = :userId AND shotType = :shotType")
    suspend fun getMaxSpeedByType(userId: Long, shotType: String): Double?

    @Query("SELECT * FROM shots WHERE userId = :userId AND timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    suspend fun getByTimeRange(userId: Long, startTime: Long, endTime: Long): List<ShotEntity>
}
