package com.example.todolistproject.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.todolistproject.model.Task

@Dao
interface TaskDao {

    @Query("SELECT * FROM Task ORDER BY endDate ASC")
    fun getTaskList(): LiveData<List<Task>>

    @Query("SELECT * FROM Task WHERE isCompleted = 0 ORDER BY endDate ASC")
    fun getIncompleteTaskList(): LiveData<List<Task>>

    @Query("SELECT * FROM Task WHERE isCompleted = 1 ORDER BY endDate ASC ")
    fun getCompletedTaskList(): LiveData<List<Task>>

    @Insert
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)
}