package com.example.todolistproject.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistproject.R
import com.example.todolistproject.databinding.ItemTaskBinding
import com.example.todolistproject.model.Task
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskAdapter @Inject constructor() : ListAdapter<Task, TaskViewHolder>(diffUtil) {

    interface OnTaskItemClickListener {
        fun onTaskItemClick(position: Int)
        fun onTaskItemLongClick(position: Int)
    }

    var listener: OnTaskItemClickListener? = null

    public override fun getItem(position: Int): Task = super.getItem(position)

    fun setTaskItems(list: List<Task>) = submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return DataBindingUtil.inflate<ItemTaskBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_task,
            parent,
            false
        ).let { TaskViewHolder(it, listener) }
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        (holder as? TaskViewHolder)?.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }
}

class TaskViewHolder(
    private val binding: ItemTaskBinding,
    listener: TaskAdapter.OnTaskItemClickListener?
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener { listener?.onTaskItemClick(bindingAdapterPosition) }

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