package com.example.todolistproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolistproject.model.Task
import com.example.todolistproject.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(private val mTaskRepository: TaskRepository) : ViewModel() {

    private val _taskList = MutableLiveData<List<Task>>()
    private val _incompleteTaskList = MutableLiveData<List<Task>>()
    private val _completedTaskList = MutableLiveData<List<Task>>()

    val taskList: LiveData<List<Task>> = _taskList
    val incompleteTaskList: LiveData<List<Task>> = _incompleteTaskList
    val completedTaskList: LiveData<List<Task>> = _completedTaskList

    init {
        getTaskList()
        getIncompleteTaskList()
        getCompletedTaskList()
    }

    fun insertTask(task: Task) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { mTaskRepository.insertTask(task) }
        }
    }

    private fun getTaskList() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { _taskList.postValue(mTaskRepository.getTaskList()) }
        }
    }

    private fun getIncompleteTaskList() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { _incompleteTaskList.postValue(mTaskRepository.getIncompleteTaskList()) }
        }
    }

    private fun getCompletedTaskList() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { _completedTaskList.postValue(mTaskRepository.getCompletedTaskList()) }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { mTaskRepository.updateTask(task) }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { mTaskRepository.deleteTask(task) }
        }
    }
}