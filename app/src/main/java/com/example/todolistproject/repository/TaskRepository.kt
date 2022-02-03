package com.example.todolistproject.repository

import com.example.todolistproject.database.dao.TaskDao
import com.example.todolistproject.model.Task
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(private val mTaskDao: TaskDao) {

    fun getTaskList(): Flow<List<Task>> {
        return mTaskDao.getTaskList()
    }

    fun getIncompleteTaskList(): Flow<List<Task>> {
        return mTaskDao.getIncompleteTaskList()
    }

    fun getCompletedTaskList(): Flow<List<Task>> {
        return mTaskDao.getCompletedTaskList()
    }

    suspend fun insertTask(task: Task) {
        mTaskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        mTaskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        mTaskDao.deleteTask(task)
    }
}