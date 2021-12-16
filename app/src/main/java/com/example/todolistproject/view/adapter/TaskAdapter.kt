package com.example.todolistproject.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistproject.R
import com.example.todolistproject.model.Task
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(private val taskList: ArrayList<Task>) : RecyclerView.Adapter<TaskViewHolder>() {

    fun addItem(task: Task) {
        taskList.add(task)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.bind(task)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }
}

class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val tvTitle: TextView = view.findViewById(R.id.tv_title)
    val tvDescription: TextView = view.findViewById(R.id.tv_description)
    val tvDate: TextView = view.findViewById(R.id.tv_date)

    fun bind(task: Task) {
        tvTitle.text = task.title
        tvDescription.text = task.description
        tvDate.text = task.date.toDateString("yyyy.MM.dd HH:mm")
    }

    private fun Long.toDateString(format: String): String {
        val sdf = SimpleDateFormat(format)
        return sdf.format(Date(this))
    }
}