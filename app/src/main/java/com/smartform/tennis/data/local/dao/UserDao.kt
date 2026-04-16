package com.smartform.tennis.data.local.dao

import androidx.room.*
import com.smartform.tennis.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * 用户数据访问对象
 */
@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Update
    suspend fun update(user: UserEntity)

    @Delete
    suspend fun delete(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getById(userId: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getByIdSync(userId: Long): UserEntity?

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getCurrentUser(): UserEntity?

    @Query("UPDATE users SET phone = :phone, nickname = :nickname, heightCm = :heightCm, weightKg = :weightKg, tennisLevel = :tennisLevel WHERE id = :userId")
    suspend fun updateProfile(userId: Long, phone: String?, nickname: String?, heightCm: Int?, weightKg: Double?, tennisLevel: String?)

    @Query("UPDATE users SET currentLevel = :level, experiencePoints = :exp WHERE id = :userId")
    suspend fun updateGrowth(userId: Long, level: Int, exp: Long)
}
