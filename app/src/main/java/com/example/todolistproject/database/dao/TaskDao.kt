package com.example.todolistproject.database.dao

import androidx.room.*
import com.example.todolistproject.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM Task ORDER BY endDate ASC")
    fun getTaskList(): Flow<List<Task>>

    @Query("SELECT * FROM Task WHERE isCompleted = 0 ORDER BY endDate ASC")
    fun getIncompleteTaskList(): Flow<List<Task>>

    @Query("SELECT * FROM Task WHERE isCompleted = 1 ORDER BY endDate ASC ")
    fun getCompletedTaskList(): Flow<List<Task>>

    @Insert
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)
}