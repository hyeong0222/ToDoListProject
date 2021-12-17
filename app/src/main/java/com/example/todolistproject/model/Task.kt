package com.example.todolistproject.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Task")
data class Task(

    @PrimaryKey(autoGenerate = true)
    var id: Long?,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "description")
    var description: String? = "",

    @ColumnInfo(name = "date")
    var date: Long,
) {
    constructor(): this(null, "", "" , -1)
}
