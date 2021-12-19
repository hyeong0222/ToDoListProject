package com.example.todolistproject.view.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.todolistproject.model.Task

class TaskDiffCallback(private val oldTaskList: List<Task>, private val newTaskList: List<Task>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldTaskList.size

    override fun getNewListSize(): Int = newTaskList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldTaskList[oldItemPosition].id == newTaskList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldTaskList[oldItemPosition] == newTaskList[newItemPosition]
    }
}