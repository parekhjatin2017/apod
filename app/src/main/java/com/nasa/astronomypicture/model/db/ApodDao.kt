package com.nasa.astronomypicture.model.db

import androidx.room.*
import com.nasa.astronomypicture.model.ApodDataModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ApodDao {

    @Insert
    suspend fun insertFavourites(model: ApodDataModel) : Long

    @Delete
    suspend fun removeFavourites(model: ApodDataModel) : Int

    @Query("DELETE FROM fav_apod_table")
    suspend fun deleteAll() : Int

    @Query("SELECT * FROM fav_apod_table")
    fun getAllFavourites(): Flow<List<ApodDataModel>>

    @Query("SELECT * FROM fav_apod_table WHERE _id =:id ")
    fun getApodOnID(id: Long): Flow<List<ApodDataModel>>

    @Query("SELECT * FROM fav_apod_table WHERE date LIKE :date")
    fun getApodOnDate(date: String): Flow<List<ApodDataModel>>

}