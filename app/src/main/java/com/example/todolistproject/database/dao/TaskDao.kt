package com.example.todolistproject.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.todolistproject.model.Task

@Dao
interface TaskDao {

    @Query("SELECT * FROM Task ORDER BY date ASC")
    fun getTaskList(): LiveData<List<Task>>

    @Insert
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)
}