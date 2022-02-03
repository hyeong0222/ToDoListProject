package com.example.todolistproject.viewmodel

import androidx.lifecycle.*
import com.example.todolistproject.model.Task
import com.example.todolistproject.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(private val mTaskRepository: TaskRepository) : ViewModel() {

    private val _taskList = mTaskRepository.getTaskList()
    private val _incompleteTaskList = mTaskRepository.getIncompleteTaskList()
    private val _completedTaskList = mTaskRepository.getCompletedTaskList()

    val taskList: LiveData<List<Task>> = _taskList.asLiveData()
    val incompleteTaskList: LiveData<List<Task>> = _incompleteTaskList.asLiveData()
    val completedTaskList: LiveData<List<Task>> = _completedTaskList.asLiveData()

    fun insertTask(task: Task) {
        viewModelScope.launch {
            mTaskRepository.insertTask(task)
        }
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