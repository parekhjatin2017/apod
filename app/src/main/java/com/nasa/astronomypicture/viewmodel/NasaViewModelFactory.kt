package com.nasa.astronomypicture.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nasa.astronomypicture.model.NasaRepository

class NasaViewModelFactory(
        private val repository: NasaRepository
        ):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
     if(modelClass.isAssignableFrom(ApodViewModel::class.java)){
         return ApodViewModel(repository) as T
     }
        throw IllegalArgumentException("Unknown View Model class")
    }

}