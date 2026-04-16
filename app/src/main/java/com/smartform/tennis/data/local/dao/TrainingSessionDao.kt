package com.smartform.tennis.data.local.dao

import androidx.room.*
import com.smartform.tennis.data.local.entity.TrainingSessionEntity
import kotlinx.coroutines.flow.Flow

/**
 * 训练会话数据访问对象
 */
@Dao
interface TrainingSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: TrainingSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sessions: List<TrainingSessionEntity>)

    @Update
    suspend fun update(session: TrainingSessionEntity)

    @Delete
    suspend fun delete(session: TrainingSessionEntity)

    @Query("SELECT * FROM training_sessions WHERE userId = :userId ORDER BY startTime DESC")
    fun getByUserId(userId: Long): Flow<List<TrainingSessionEntity>>

    @Query("SELECT * FROM training_sessions WHERE userId = :userId ORDER BY startTime DESC")
    suspend fun getByUserIdList(userId: Long): List<TrainingSessionEntity>

    @Query("SELECT * FROM training_sessions WHERE id = :sessionId")
    suspend fun getById(sessionId: Long): TrainingSessionEntity?

    @Query("SELECT * FROM training_sessions WHERE userId = :userId AND startTime >= :startTime AND startTime <= :endTime ORDER BY startTime DESC")
    suspend fun getByTimeRange(userId: Long, startTime: Long, endTime: Long): List<TrainingSessionEntity>

    @Query("SELECT * FROM training_sessions WHERE isSynced = 0")
    suspend fun getUnsyncedSessions(): List<TrainingSessionEntity>

    @Query("UPDATE training_sessions SET isSynced = 1 WHERE id = :sessionId")
    suspend fun markAsSynced(sessionId: Long)

    @Query("DELETE FROM training_sessions WHERE userId = :userId")
    suspend fun deleteByUserId(userId: Long)

    @Query("SELECT COUNT(*) FROM training_sessions WHERE userId = :userId")
    suspend fun getCountByUserId(userId: Long): Int

    @Query("SELECT MAX(totalShots) FROM training_sessions WHERE userId = :userId")
    suspend fun getMaxTotalShots(userId: Long): Int?

    @Query("SELECT MAX(maxSpeed) FROM training_sessions WHERE userId = :userId")
    suspend fun getMaxSpeed(userId: Long): Double?
}
