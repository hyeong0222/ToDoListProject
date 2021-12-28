package com.example.todolistproject.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.todolistproject.database.TaskDatabase
import com.example.todolistproject.database.dao.TaskDao
import com.example.todolistproject.model.Task
import io.reactivex.disposables.CompositeDisposable

class TaskRepository(application: Application) {

    private var mTaskDatabase: TaskDatabase
    private var mTaskDao: TaskDao
    private var mTask: LiveData<List<Task>>
    private var compositeDisposable: CompositeDisposable

    init {
        mTaskDatabase = TaskDatabase.getInstance(application)
        mTaskDao = mTaskDatabase.taskDao()
        mTask = mTaskDao.getTaskList()
        compositeDisposable = CompositeDisposable()
    }

    fun getTaskList(): LiveData<List<Task>> {
        return mTask
    }

    fun insertTask(task: Task) {
        mTaskDao.insertTask(task)
    }

    fun updateTask(task: Task) {
        mTaskDao.updateTask(task)
    }

    fun deleteTask(task: Task) {
        mTaskDao.deleteTask(task)
    }
}