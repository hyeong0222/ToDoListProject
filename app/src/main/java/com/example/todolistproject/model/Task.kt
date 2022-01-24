package com.example.todolistproject.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Task")
data class Task(

    @PrimaryKey(autoGenerate = true)
    var id: Long?,

    @ColumnInfo(name = "isCompleted")
    var isCompleted: Boolean? = false,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "description")
    var description: String? = "",

    @ColumnInfo(name = "startDate")
    var startDate: String?,

    @ColumnInfo(name = "endDate")
    var endDate: String?,
)
