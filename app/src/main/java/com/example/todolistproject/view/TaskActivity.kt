package com.example.todolistproject.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistproject.R
import com.example.todolistproject.databinding.ActivityMainBinding
import com.example.todolistproject.databinding.DialogAddTaskBinding
import com.example.todolistproject.model.Task
import com.example.todolistproject.view.adapter.TaskAdapter
import com.example.todolistproject.viewmodel.TaskViewModel
import java.util.*

class TaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mAdapter: TaskAdapter
    private lateinit var mViewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.activity = this@TaskActivity

        initRecyclerView()
        initViewModel()
        observeViewModel()
    }

    private fun initRecyclerView() {
        mAdapter = TaskAdapter().apply {
            listener = object : TaskAdapter.OnTaskItemClickListener {
                override fun onTaskItemClick(position: Int) {
                    openModifyTaskDialog(getItem(position))
                }

                override fun onTaskItemLongClick(position: Int) {
                    openDeleteTaskDialog(getItem(position))
                }
            }
        }

        binding.rvTodoList.run {
            layoutManager = LinearLayoutManager(this@TaskActivity)
            adapter = mAdapter
        }
    }

    private fun initViewModel() {
        mViewModel =
            ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(
                TaskViewModel::class.java
            ).also {
                binding.viewModel = it
            }
    }

    private fun observeViewModel() {
        mViewModel.getTaskList().observe(this, Observer {
            binding.progressIndicator.visibility = View.GONE
            mAdapter.setTaskItems(it)
        })
    }

    fun openAddTaskDialog() {
        val dialogViewBinding = DialogAddTaskBinding.inflate(LayoutInflater.from(this))

        val dialog =
            AlertDialog.Builder(this).setTitle("Add task").setView(dialogViewBinding.root)
                .setPositiveButton("Add") { dialog, _ ->
                    addTask(dialogViewBinding)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .create()
        dialog.show()
    }

    private fun addTask(dialogViewBinding: DialogAddTaskBinding) {
        val title = dialogViewBinding.etTaskTitle.text.toString()
        val description = dialogViewBinding.etTaskDescription.text.toString()
        val date = Date().time

        val task =
            Task(id = null, title = title, description = description, date = date)
        mViewModel.insertTask(task)
    }

    private fun openModifyTaskDialog(task: Task) {
        val dialogViewBinding = DialogAddTaskBinding.inflate(LayoutInflater.from(this))
        dialogViewBinding.task = task

        val dialog = AlertDialog.Builder(this).setTitle("Edit Task").setView(dialogViewBinding.root)
            .setPositiveButton("Okay") { dialog, _ ->
                modifyTask(dialogViewBinding, task)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun modifyTask(dialogViewBinding: DialogAddTaskBinding, task: Task) {
        // We must deep copy the task object in order for the DiffUtil to recognize and compare the
        // new object with the old one.
        val modifiedTask = task.copy()
        modifiedTask.title = dialogViewBinding.etTaskTitle.text.toString()
        modifiedTask.description = dialogViewBinding.etTaskDescription.text.toString()
        modifiedTask.date = Date().time

        mViewModel.updateTask(modifiedTask)
    }

    private fun openDeleteTaskDialog(task: Task) {
        val dialog = AlertDialog.Builder(this).setTitle("Delete Task")
            .setMessage("Do you want to remove this task?").setPositiveButton("Yes") { _, _ ->
                mViewModel.deleteTask(task)
            }.setNegativeButton("No", null).create()
        dialog.show()
    }
}