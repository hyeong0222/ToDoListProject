package com.example.todolistproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.todolistproject.model.Task
import com.example.todolistproject.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val mTaskRepository: TaskRepository
    private var mTaskList: LiveData<List<Task>>

    init {
        mTaskRepository = TaskRepository(application)
        mTaskList = mTaskRepository.getTaskList()
    }

    fun insertTask(task: Task) {
        viewModelScope.launch {
            mTaskRepository.insertTask(task)
        }
    }

    fun getTaskList(): LiveData<List<Task>> {
        return mTaskList
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            mTaskRepository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            mTaskRepository.deleteTask(task)
        }
    }
}