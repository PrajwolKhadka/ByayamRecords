package com.example.byayamrecords.repository

import com.example.byayamrecords.model.BMIRecord

interface BMIRepository {
    fun saveBMI(record: BMIRecord)
    fun deleteBMI(id: String)
    fun getAllBMIs(callback: (List<BMIRecord>) -> Unit)
}