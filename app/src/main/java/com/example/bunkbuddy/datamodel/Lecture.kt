package com.example.bunkbuddy.datamodel

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(
    tableName = "lectures",
    foreignKeys = [ForeignKey(
        entity = Subject::class,
        parentColumns = ["id"],
        childColumns = ["subjectId"],
        onDelete = ForeignKey.CASCADE
    )]
)
@Parcelize
data class Lecture (
    var dayNumber: Int,
    var subject: Subject,
    var startTime: String,
    var endTime: String,
    @PrimaryKey(autoGenerate = true)
    var pid: Int,
    @ColumnInfo(name = "subjectId", index = true)
    var subjectId: Int
) : Parcelable