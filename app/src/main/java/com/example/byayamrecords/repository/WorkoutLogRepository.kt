package com.example.byayamrecords.repository

import com.example.byayamrecords.model.WorkoutLog


interface WorkoutLogRepository {

    fun addLog(
        workoutLog: WorkoutLog,
        callback: (Boolean, String) -> Unit
    )

   fun updateLog(
        LogId: String,
        data: MutableMap<String, Any>,
        callback: (Boolean, String) -> Unit
    )

    open fun deleteLog(
        productId: String,
        callback: (Boolean, String) -> Unit
    )


    open fun getLogById(
        productId: String,
        callback: (WorkoutLog?, Boolean, String)
        -> Unit
    )

    open fun getAllLog(callback: (List<WorkoutLog>?,
                                  Boolean, String) -> Unit)

}
