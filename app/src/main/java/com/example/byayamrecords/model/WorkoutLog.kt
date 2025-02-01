package com.example.byayamrecords.model

import android.os.Parcel
import android.os.Parcelable

data class WorkoutLog(
    var LogId : String = "",
    var WorkoutName : String = "",
    var WorkoutSets : Int = 0,
    var WorkoutWeight : Int = 0,
    var WorkoutDesc: String ="",
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt() ?: 0,
        parcel.readInt() ?: 0,
        parcel.readString()?:""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(LogId)
        parcel.writeString(WorkoutName)
        parcel.writeInt(WorkoutSets)
        parcel.writeInt(WorkoutWeight)
        parcel.writeString(WorkoutDesc)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WorkoutLog> {
        override fun createFromParcel(parcel: Parcel): WorkoutLog {
            return WorkoutLog(parcel)
        }

        override fun newArray(size: Int): Array<WorkoutLog?> {
            return arrayOfNulls(size)
        }
    }
}