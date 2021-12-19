package com.example.todolistproject.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistproject.R
import com.example.todolistproject.model.Task
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter() : RecyclerView.Adapter<TaskViewHolder>() {

    private var taskList: List<Task> = listOf()

    fun setTaskItems(tasks: List<Task>) {
        Observable.just(tasks).subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(Schedulers.io())
            .map { DiffUtil.calculateDiff(TaskDiffCallback(taskList, tasks)) }
            .subscribe(
                {
                    this.taskList = tasks
                    it.dispatchUpdatesTo(this)
                }, {}
            )
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

    private val tvTitle: TextView = view.findViewById(R.id.tv_title)
    private val tvDescription: TextView = view.findViewById(R.id.tv_description)
    private val tvDate: TextView = view.findViewById(R.id.tv_date)

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