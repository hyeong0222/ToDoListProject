package com.example.todolistproject.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.todolistproject.database.TaskDatabase
import com.example.todolistproject.database.dao.TaskDao
import com.example.todolistproject.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(application: Application) {

    private var mTaskDatabase: TaskDatabase
    private var mTaskDao: TaskDao
    private var mTask: LiveData<List<Task>>

    init {
        mTaskDatabase = TaskDatabase.getInstance(application)
        mTaskDao = mTaskDatabase.taskDao()
        mTask = mTaskDao.getTaskList()
    }

    fun getTaskList(): LiveData<List<Task>> {
        return mTask
    }

    suspend fun insertTask(task: Task) {
        withContext(Dispatchers.IO) {
            mTaskDao.insertTask(task)
        }
    }

    suspend fun updateTask(task: Task) {
        withContext(Dispatchers.IO) {
            mTaskDao.updateTask(task)
        }
    }

    suspend fun deleteTask(task: Task) {
        withContext(Dispatchers.IO) {
            mTaskDao.deleteTask(task)
        }
    }
}