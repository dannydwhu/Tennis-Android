package com.smartform.tennis.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.smartform.tennis.data.local.dao.*
import com.smartform.tennis.data.local.entity.*

/**
 * Room 数据库
 *
 * 本地数据存储，支持离线使用
 */
@Database(
    entities = [
        UserEntity::class,
        TrainingSessionEntity::class,
        ShotEntity::class,
        SensorDataEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun trainingSessionDao(): TrainingSessionDao
    abstract fun shotDao(): ShotDao
    abstract fun sensorDataDao(): SensorDataDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smartform_tennis.db"
                )
                    .fallbackToDestructiveMigration()  // 开发阶段使用
                    .build()
                    .also { instance = it }
            }
        }
    }
}
