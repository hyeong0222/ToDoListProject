package com.example.todolistproject.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.todolistproject.model.Task

@Dao
interface TaskDao {

    @Query("SELECT * FROM Task ORDER BY date ASC")
    fun getTaskList(): LiveData<List<Task>>

    @Insert
    fun insertTask(task: Task)
}