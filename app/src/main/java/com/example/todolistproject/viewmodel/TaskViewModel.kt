package com.example.todolistproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolistproject.model.Task
import com.example.todolistproject.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(private val mTaskRepository: TaskRepository) : ViewModel() {

    private var mTaskList: LiveData<List<Task>> = mTaskRepository.getTaskList()

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