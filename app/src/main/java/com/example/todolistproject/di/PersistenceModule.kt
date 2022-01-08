package com.example.todolistproject.di

import android.app.Application
import androidx.room.Room
import com.example.todolistproject.database.dao.TaskDao
import com.example.todolistproject.database.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

    @Provides
    @Singleton
    fun provideTaskDatabase(
        application: Application,
    ): TaskDatabase {
        return Room.databaseBuilder(application, TaskDatabase::class.java, "Task.db").build()
    }

    @Provides
    @Singleton
    fun providesTaskDao(taskDatabase: TaskDatabase): TaskDao {
        return taskDatabase.taskDao()
    }
}