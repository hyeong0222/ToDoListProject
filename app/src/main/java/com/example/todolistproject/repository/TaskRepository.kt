package com.example.todolistproject.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.todolistproject.database.TaskDatabase
import com.example.todolistproject.database.dao.TaskDao
import com.example.todolistproject.model.Task

class TaskRepository(application: Application) {

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

    fun insertTask(task: Task) {
        Thread(Runnable {
            mTaskDao.insertTask(task)
        }).start()
    }
}