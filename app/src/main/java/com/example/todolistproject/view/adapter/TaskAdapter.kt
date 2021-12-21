package com.example.todolistproject.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistproject.R
import com.example.todolistproject.databinding.ItemTaskBinding
import com.example.todolistproject.model.Task
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter : RecyclerView.Adapter<TaskViewHolder>() {

    private var taskList: List<Task> = listOf()

    interface OnTaskItemClickListener {
        fun onTaskItemClick(position: Int)
        fun onTaskItemLongClick(position: Int)
    }

    var listener: OnTaskItemClickListener? = null

    fun getItem(position: Int): Task = taskList[position]

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
        return TaskViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.binding?.let {
            it.apply {
                tvTitle.text = task.title
                tvDescription.text = task.description
                tvDate.text = task.date.toDateString("yyyy.MM.dd HH:mm")
            }
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    private fun Long.toDateString(format: String): String {
        val sdf = SimpleDateFormat(format, Locale.ROOT)
        return sdf.format(Date(this))
    }
}

class TaskViewHolder(view: View, listener: TaskAdapter.OnTaskItemClickListener?) : RecyclerView.ViewHolder(view) {
    var binding: ItemTaskBinding? = DataBindingUtil.bind(view)

    init {
        view.setOnClickListener {
            listener?.onTaskItemClick(bindingAdapterPosition)
        }

        view.setOnLongClickListener {
            listener?.onTaskItemLongClick(bindingAdapterPosition)
            return@setOnLongClickListener true
        }
    }
}