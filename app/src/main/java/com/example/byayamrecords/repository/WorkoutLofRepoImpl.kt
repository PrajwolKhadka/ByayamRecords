package com.example.byayamrecords.repository

import com.example.byayamrecords.model.WorkoutLog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class WorkoutLofRepoImpl : WorkoutLogRepository {
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref: DatabaseReference = database.reference.child("products")

    override fun addLog(
        workoutLog: WorkoutLog,
        callback: (Boolean, String) -> Unit
    ) {
        var id = ref.push().key.toString()
        workoutLog.LogId = id

        ref.child(id).setValue(workoutLog).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Log Added successfully")
            } else {
                callback(false, "${it.exception?.message}")

            }
        }

    }

    override fun updateLog(
        LogId: String,
        data: MutableMap<String, Any>,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(LogId).updateChildren(data).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Log Updated successfully")
            } else {
                callback(false, "${it.exception?.message}")

            }
        }
    }

    override fun deleteLog(LogId: String, callback: (Boolean, String) -> Unit) {
        ref.child(LogId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Log Deleted successfully")
            } else {
                callback(false, "${it.exception?.message}")

            }
        }
    }

    override fun getLogById(
        LogId: String,
        callback: (WorkoutLog?, Boolean, String) -> Unit
    ) {
        ref.child(LogId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var model = snapshot.getValue(WorkoutLog::class.java)
                    callback(model, true, "Log fetched successfully")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, false, error.message)
            }
        })
    }

    override fun getAllLog(callback: (List<WorkoutLog>?, Boolean, String) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var products = mutableListOf<WorkoutLog>()
                if (snapshot.exists()) {
                    for (eachProduct in snapshot.children) {
                        var data = eachProduct.getValue(WorkoutLog::class.java)
                        if (data != null) {
                            products.add(data)
                        }
                    }
                    callback(products, true, "product added succesfully")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, false, error.message)
            }
        })
    }
}