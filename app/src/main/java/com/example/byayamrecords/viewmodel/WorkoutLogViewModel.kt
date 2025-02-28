package com.example.byayamrecords.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.byayamrecords.model.WorkoutLog
import com.example.byayamrecords.repository.WorkoutLogRepository

class WorkoutLogViewModel(private val repository: WorkoutLogRepository) : ViewModel() {

    private val _products = MutableLiveData<WorkoutLog?>()
    val products: LiveData<WorkoutLog?> get() = _products

    private val _allProducts = MutableLiveData<List<WorkoutLog>?>()
    val allProducts: LiveData<List<WorkoutLog>?> get() = _allProducts

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> get() = _loadingState

    fun addLog(workoutLog: WorkoutLog, callback: (Boolean, String) -> Unit) {
        repository.addLog(workoutLog, callback)
    }

    fun updateLog(logId: String, data: MutableMap<String, Any>, callback: (Boolean, String) -> Unit) {
        repository.updateLog(logId, data){ success, message ->
            if (success) {
                getLogById(logId) // Fetch updated data after updating
            }
            callback(success, message)
        }
    }

    fun deleteLog(logId: String, callback: (Boolean, String) -> Unit) {
        repository.deleteLog(logId, callback)
    }

    fun getLogById(logId: String) {
        _loadingState.value = true
        repository.getLogById(logId) { product, success, _ ->
            if (success) {
                _products.value = null
                _products.value = product
            }
            _loadingState.value = false
        }
    }

    fun getAllLog() {
        _loadingState.value = true
        repository.getAllLog { products, success, _ ->
            if (success) {
                _allProducts.value = products
            } else {
                _allProducts.value = emptyList()
            }
            _loadingState.value = false
        }
    }
}
