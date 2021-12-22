package com.example.todolistproject.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
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
        Observable.just(tasks).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { DiffUtil.calculateDiff(TaskDiffCallback(taskList, tasks)) }
            .subscribe(
                {
                    this.taskList = tasks
                    it.dispatchUpdatesTo(this)
                }, {}
            )
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return DataBindingUtil.inflate<ItemTaskBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_task,
            parent,
            false
        ).let { TaskViewHolder(it, listener) }
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        (holder as? TaskViewHolder)?.bind(taskList.getOrNull(position) ?: return)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    private fun Long.toDateString(format: String): String {
        val sdf = SimpleDateFormat(format, Locale.ROOT)
        return sdf.format(Date(this))
    }
}

class TaskViewHolder(
    private val binding: ItemTaskBinding,
    listener: TaskAdapter.OnTaskItemClickListener?
) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            listener?.onTaskItemClick(bindingAdapterPosition)
        }

        itemView.setOnLongClickListener {
            listener?.onTaskItemLongClick(bindingAdapterPosition)
            return@setOnLongClickListener true
        }
    }

    fun bind(task: Task) {
        binding.task = task
        binding.executePendingBindings()
    }
}

@BindingAdapter("taskDate")
fun setTaskDate(tv: TextView, date: Long) {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    tv.text = sdf.format(date)
}