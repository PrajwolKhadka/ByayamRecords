package com.example.byayamrecords.repository

import android.util.Log
import com.example.byayamrecords.model.BMIRecord
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class BMIRepositoryImpl : BMIRepository {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val usersRef: DatabaseReference = database.reference.child("users")

    override fun saveBMI(record: BMIRecord) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            Log.d("Firebase", "Saving BMI record for user: $userId")
            val recordId = record.id.ifEmpty { usersRef.child(userId).child("bmiRecords").push().key ?: return }
            Log.d("Firebase", "Generated record ID: $recordId")
            val bmiData = hashMapOf(
                "id" to recordId,
                "bmi" to record.bmi,
                "weight" to record.weight,
                "height" to record.height,
                "status" to record.status,
                "date" to record.date
            )

            usersRef.child(userId).child("bmiRecords").child(recordId).setValue(bmiData)
                .addOnSuccessListener {
                    Log.d("Firebase", "BMI record saved successfully: $recordId")
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Error saving BMI record", e)
                }
        } else {
            Log.e("Firebase", "User not authenticated")
        }
    }

    override fun deleteBMI(id: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            usersRef.child(userId).child("bmiRecords").child(id).removeValue()
                .addOnSuccessListener {
                    Log.d("Firebase", "BMI record deleted successfully: $id")
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Error deleting BMI record", e)
                }
        } else {
            Log.e("Firebase", "User not authenticated")
        }
    }

    override fun getAllBMIs(callback: (List<BMIRecord>) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            usersRef.child(userId).child("bmiRecords").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val bmiList = mutableListOf<BMIRecord>()
                    for (data in snapshot.children) {
                        val record = data.getValue(BMIRecord::class.java)?.copy(
                            weight = data.child("weight").getValue(Double::class.java) ?: 0.0,
                            height = data.child("height").getValue(Double::class.java) ?: 0.0
                        )
                        record?.let { bmiList.add(it) }
                    }
                    callback(bmiList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error fetching BMI records", error.toException())
                }
            })
        } else {
            Log.e("Firebase", "User not authenticated")
        }
    }
}