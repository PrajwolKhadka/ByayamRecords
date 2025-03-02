package com.example.byayamrecords.repository

import com.example.byayamrecords.model.WorkoutLog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class WorkoutLofRepoImpl() : WorkoutLogRepository {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val usersRef: DatabaseReference = database.reference.child("users")

    override fun addLog(workoutLog: WorkoutLog, callback: (Boolean, String) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val logId = usersRef.child(userId).child("products").push().key.toString()
            workoutLog.LogId = logId

            usersRef.child(userId).child("products").child(logId).setValue(workoutLog)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(true, "Log Added successfully")
                    } else {
                        callback(false, task.exception?.message ?: "Unknown error")
                    }
                }
        } else {
            callback(false, "User not authenticated")
        }
    }

    override fun updateLog(productId: String, data: MutableMap<String, Any>, callback: (Boolean, String) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            usersRef.child(userId).child("products").child(productId).updateChildren(data)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(true, "Log Updated successfully")
                    } else {
                        callback(false, task.exception?.message ?: "Unknown error")
                    }
                }
        } else {
            callback(false, "User not authenticated")
        }
    }

    override fun deleteLog(productId: String, callback: (Boolean, String) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            usersRef.child(userId).child("products").child(productId).removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(true, "Log Deleted successfully")
                    } else {
                        callback(false, task.exception?.message ?: "Unknown error")
                    }
                }
        } else {
            callback(false, "User not authenticated")
        }
    }

    override fun getLogById(productId: String, callback: (WorkoutLog?, Boolean, String) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            usersRef.child(userId).child("products").child(productId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val model = snapshot.getValue(WorkoutLog::class.java)
                            callback(model, true, "Log fetched successfully")
                        } else {
                            callback(null, false, "Log not found")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback(null, false, error.message)
                    }
                })
        } else {
            callback(null, false, "User not authenticated")
        }
    }

    override fun getAllLog(callback: (List<WorkoutLog>?, Boolean, String) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            usersRef.child(userId).child("products")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val products = mutableListOf<WorkoutLog>()
                        if (snapshot.exists()) {
                            for (eachProduct in snapshot.children) {
                                val data = eachProduct.getValue(WorkoutLog::class.java)
                                if (data != null) {
                                    products.add(data)
                                }
                            }
                        }
                        callback(products, true, "Logs fetched successfully")
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback(null, false, error.message)
                    }
                })
        } else {
            callback(null, false, "User not authenticated")
        }
    }
}