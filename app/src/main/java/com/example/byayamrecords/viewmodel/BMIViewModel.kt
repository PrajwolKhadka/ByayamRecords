package com.example.byayamrecords.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.byayamrecords.model.BMIRecord
import com.example.byayamrecords.repository.BMIRepository
import com.example.byayamrecords.repository.BMIRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class BMIViewModel : ViewModel() {
    private val repository: BMIRepository = BMIRepositoryImpl()

    private val _bmiRecords = MutableLiveData<List<BMIRecord>>()
    val bmiRecords: LiveData<List<BMIRecord>> get() = _bmiRecords

    init {
        loadBMIs()
    }

    fun saveBMI(record: BMIRecord) {
        repository.saveBMI(record)
    }

    fun deleteBMI(id: String) {
        repository.deleteBMI(id)
    }

    private fun loadBMIs() {
        repository.getAllBMIs { records ->
            _bmiRecords.postValue(records)
        }
    }
}
