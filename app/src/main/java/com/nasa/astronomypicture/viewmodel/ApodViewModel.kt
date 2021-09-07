package com.nasa.astronomypicture.viewmodel

import android.text.TextUtils
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.nasa.astronomypicture.R
import com.nasa.astronomypicture.model.ApodDataModel
import com.nasa.astronomypicture.model.NasaRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ApodViewModel(private val repository: NasaRepository) : ViewModel() {

    var currentApodDataModel : MutableLiveData<ApodDataModel> = MutableLiveData()

    var isFavourite = MutableLiveData<Boolean>()

    var isVideo = MutableLiveData<Boolean>()

    private var todaysApodDataModel : ApodDataModel? = null

    companion object {

        @JvmStatic
        @BindingAdapter("loadImage")
        fun loadImage(view: ImageView, url: String?) {
            if(!TextUtils.isEmpty(url)) {
                Glide.with(view.context).load(url)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(view)
            }
        }
    }

    private val statusMessage = MutableLiveData<Event<String>>()

    val message: LiveData<Event<String>>
        get() = statusMessage

    private fun updateCurrentApodDataModel(model : ApodDataModel){
        currentApodDataModel.value = model
        if(model.date.equals(getToday())){
            todaysApodDataModel = model
        }
        setFavourite()
        isVideo.value = currentApodDataModel.value?.media_type?.let { it.equals("video", true) }
    }

    fun toggleFavourite(){
        if(isFavourite.value == true){
            unCheckFavourite()
        }else{
            markFavourite()
        }
    }

    private fun setFavourite(){
        isFavourite.value = currentApodDataModel.value?.id?.let { it > 0 }
    }

    private fun markFavourite() = viewModelScope.launch {
        val row = currentApodDataModel.value?.let { repository.insert(it) }
        if (row != null && row > -1) {
            currentApodDataModel.value?.id = row
            getAllFavourites()
            statusMessage.value = Event("Added to favourites")
            setFavourite()
        } else {
            statusMessage.value = Event("Error Occurred")
        }
    }

    private fun unCheckFavourite() = viewModelScope.launch {
        val row = currentApodDataModel.value?.let { repository.removeFavourites(it) }
        if (row != null && row > 0) {
            currentApodDataModel.value?.id = -1
            getAllFavourites()
            setFavourite()
            statusMessage.value = Event("Removed from favourites")
        } else {
            statusMessage.value = Event("Error Occurred")
        }
    }

    fun getAllFavourites() = liveData {
        repository.favApodDataModel.collect {
            emit(it)
        }
    }

    fun clearAll() = viewModelScope.launch {
        val noOfRowsDeleted = repository.deleteAll()
        if (noOfRowsDeleted > 0) {
            getAllFavourites()
            statusMessage.value = Event("Removed all favourites")
        } else {
            statusMessage.value = Event("Error Occurred")
        }
    }

    fun getApodFromdb(date : String){
        viewModelScope.launch {
            repository.getApodOnDate(date).collect {
                if (it.isNotEmpty()) {
                    updateCurrentApodDataModel(it[0])
                }
            }
        }
    }

    fun getApodOnID(id : Long) {
        viewModelScope.launch {
            repository.getApodOnID(id).collect {
                if (it.isNotEmpty()) {
                    updateCurrentApodDataModel(it[0])
                }
            }
        }
    }

    fun getApod(date : String) {
        viewModelScope.launch {
            repository.getApodOnDate(date).collect {
                if (it.isEmpty()) {
                    getApodFromNetwork(date)
                } else {
                    updateCurrentApodDataModel(it[0])
                }
            }
        }
    }

    private fun getApodFromNetwork(date : String) {
        viewModelScope.launch{
            val response = repository.getApodFromService(date)
            if(response.isSuccessful){
                response.body()?.let {
                    updateCurrentApodDataModel(it)
                }
            }else{
                statusMessage.value = Event(response.message())
            }
        }
    }

    /*fun getApodFromNetwork() {
        viewModelScope.launch{
            val response = repository.getTodaysApodFromService()
            if(response.isSuccessful){
                response.body()?.let {
                    todaysApodDataModel = it
                    if(currentApodDataModel.value == null){
                        updateCurrentApodDataModel(it)
                    }
                }
            }else{
                statusMessage.value = Event(response.message())
            }
        }
    }*/

    fun getTodaysApod(){
        getApod(getToday())
    }

    fun getToday() : String{
        return SimpleDateFormat("yyyy-MM-dd").format(Date())
    }
}