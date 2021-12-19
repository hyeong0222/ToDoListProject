package com.example.todolistproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todolistproject.model.Task
import com.example.todolistproject.repository.TaskRepository

class TaskViewModel(application: Application): AndroidViewModel(application) {
    private val mTaskRepository: TaskRepository
    private var mTaskList: LiveData<List<Task>>

    init {
        mTaskRepository = TaskRepository(application)
        mTaskList = mTaskRepository.getTaskList()
    }

    fun insertTask(task: Task) {
        mTaskRepository.insertTask(task)
    }

    fun getTaskList(): LiveData<List<Task>> {
        return mTaskList
    }

    fun updateTask(task: Task) {
        mTaskRepository.updateTask(task)
    }

    fun deleteTask(task: Task) {
        mTaskRepository.deleteTask(task)
    }
}