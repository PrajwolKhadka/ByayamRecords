package com.example.byayamrecords.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.byayamrecords.model.WorkoutLog
import com.example.byayamrecords.repository.WorkoutLogRepository
import kotlinx.coroutines.launch

class WorkoutLogViewModel (val repository: WorkoutLogRepository) {
        fun addLog(workoutLog: WorkoutLog,
                       callback:(Boolean,String) -> Unit
        ){
            repository.addLog(workoutLog,callback)
        }

        fun updateLog(LogId:String,
                          data: MutableMap<String,Any>,
                          callback: (Boolean, String) -> Unit){
            repository.updateLog(LogId, data, callback)
        }

        fun deleteLog(LogId:String,
                          callback: (Boolean, String) -> Unit){
            repository.deleteLog(LogId, callback)
        }

        var _products = MutableLiveData<WorkoutLog?>()
        var products = MutableLiveData<WorkoutLog?>()
            get() = _products

        var _allProducts = MutableLiveData<List<WorkoutLog>?>()
        var allProducts = MutableLiveData<List<WorkoutLog>?>()
            get() = _allProducts

        fun getLogById(LogId:String){
            _loadingState.value = true
            repository.getLogById(LogId){
                    product,success,message->
                if(success){
                    _products.value = product
                }
                _loadingState.value = false
            }
        }

        var _loadingState = MutableLiveData<Boolean>()
        var loadingState = MutableLiveData<Boolean>()
            get() = _loadingState

    fun getAllLog() {
        _loadingState.value = true
        repository.getAllLog() { products, success, message ->
            if (success) {
                _allProducts.value = products
            } else {
                _allProducts.value = emptyList()
            }
            _loadingState.value = false
        }
    }

}
