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
        setFavourite()
        isVideo.value = currentApodDataModel.value?.media_type?.let { it.equals("video", true) }
    }

    fun toggleFavourite(){
        if(isFavourite.value == true){
            currentApodDataModel.value?.let { unCheckFavourite(it) }
        }else{
            currentApodDataModel.value?.let { markFavourite(it) }
        }
    }

    private fun setFavourite(){
        isFavourite.value = currentApodDataModel.value?.id?.let { it > 0 }
    }

    fun markFavourite(model : ApodDataModel) = viewModelScope.launch {
        val row = repository.insert(model)
        if (row != null && row > -1) {
            currentApodDataModel.value?.id = row
            getAllFavourites()
            statusMessage.value = Event("Added to favourites")
            setFavourite()
        } else {
            statusMessage.value = Event("Error Occurred")
        }
    }

    fun unCheckFavourite(model : ApodDataModel) = viewModelScope.launch {
        val row = repository.removeFavourites(model)
        if (row != null && row > 0) {
            model.id = 0
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
            currentApodDataModel.value?.id = 0
            setFavourite()
            statusMessage.value = Event("Removed all favourites")
        } else {
            statusMessage.value = Event("Error Occurred")
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
            val apod = repository.getApod(date)
            if(apod == null){
                statusMessage.value = Event("Error Occurred")
            }else{
                updateCurrentApodDataModel(apod)
            }
        }
    }
}