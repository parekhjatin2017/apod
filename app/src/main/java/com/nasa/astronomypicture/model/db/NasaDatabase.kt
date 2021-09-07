package com.nasa.astronomypicture.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nasa.astronomypicture.model.ApodDataModel

@Database(entities = [ApodDataModel::class], version = 1)
abstract class NasaDatabase : RoomDatabase() {

    abstract val apodDao: ApodDao

    companion object {

        @Volatile
        private var INSTANCE: NasaDatabase? = null

        fun getInstance(context: Context): NasaDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        NasaDatabase::class.java,
                        "nasa_database"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}

