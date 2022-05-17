package com.example.todolistproject.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todolistproject.database.dao.TaskDao
import com.example.todolistproject.model.Task

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}