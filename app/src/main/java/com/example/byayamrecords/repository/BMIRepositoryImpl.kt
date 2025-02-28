package com.example.byayamrecords.repository

import android.util.Log
import com.example.byayamrecords.model.BMIRecord
import com.google.firebase.database.*

class BMIRepositoryImpl : BMIRepository {
    private val db = FirebaseDatabase.getInstance().getReference("bmiRecords") // ✅ Use Realtime Database

    override fun saveBMI(record: BMIRecord) {
        val recordId = record.id.ifEmpty { db.push().key ?: return }
        val bmiData = hashMapOf(
            "id" to recordId,
            "bmi" to record.bmi,
            "weight" to record.weight,
            "height" to record.height,
            "status" to record.status,
            "date" to record.date
        )

        db.child(recordId).setValue(bmiData)
            .addOnSuccessListener {
                Log.d("Firebase", "BMI record saved successfully: $recordId")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error saving BMI record", e)
            }
    }


    override fun deleteBMI(id: String) {
        db.child(id).removeValue()
            .addOnSuccessListener {
                Log.d("Firebase", "BMI record deleted successfully: $id")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error deleting BMI record", e)
            }
    }

    override fun getAllBMIs(callback: (List<BMIRecord>) -> Unit) {
        db.addValueEventListener(object : ValueEventListener {
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
    }

}
