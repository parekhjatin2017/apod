package com.nasa.astronomypicture.model

import com.nasa.astronomypicture.model.api.ApodRestService
import com.nasa.astronomypicture.model.api.RetrofitInstance
import com.nasa.astronomypicture.model.db.ApodDao
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class NasaRepository(private val dao: ApodDao, private val apodService : ApodRestService) {

    val favApodDataModel = dao.getAllFavourites()

    suspend fun insert(model: ApodDataModel): Long {
        return dao.insertFavourites(model)
    }

    suspend fun removeFavourites(model: ApodDataModel): Int {
        return dao.removeFavourites(model)
    }

    suspend fun deleteAll(): Int {
        return dao.deleteAll()
    }

    fun getApodOnID(id: Long): Flow<List<ApodDataModel>> {
        return dao.getApodOnID(id)
    }

    fun getApodOnDate(date : String): Flow<List<ApodDataModel>> {
        return dao.getApodOnDate("%$date%")
    }

    suspend fun getApodFromService(date : String): Response<ApodDataModel> {
        return apodService.getApodData(RetrofitInstance.API_KEY, date)
    }

    suspend fun getTodaysApodFromService(): Response<ApodDataModel> {
        return apodService.getTodaysApodData(RetrofitInstance.API_KEY)
    }
}