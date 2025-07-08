package com.example.weatherapp.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.db.fb.FBDatabase

class MainViewModelFactory(private val db : FBDatabase) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}