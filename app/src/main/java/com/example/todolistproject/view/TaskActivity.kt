package com.example.todolistproject.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
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

    private val dialogBinding: DialogAddTaskBinding by lazy {
        DialogAddTaskBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.activity = this@TaskActivity

        initRecyclerView()
        initViewModel()
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
            )
        mViewModel.getTaskList().observe(this, {
            mAdapter.setTaskItems(it)
        })
    }

    fun openAddTaskDialog() {
        val dialog =
            AlertDialog.Builder(this).setTitle("Add task").setView(dialogBinding.root)
                .setPositiveButton("Add") { _, _ ->
                    val title = dialogBinding.etTaskTitle.text.toString()
                    val description = dialogBinding.etTaskDescription.text.toString()
                    val date = Date().time

                    val task =
                        Task(id = null, title = title, description = description, date = date)
                    mViewModel.insertTask(task)
                }
                .setNegativeButton("Cancel", null)
                .create()
        dialog.show()
    }

    private fun openModifyTaskDialog(task: Task) {
        val dialog = AlertDialog.Builder(this).setTitle("Edit Task").setView(dialogBinding.root)
            .setPositiveButton("Okay") { _, _ ->
                val title = dialogBinding.etTaskTitle.text.toString()
                val description = dialogBinding.etTaskDescription.text.toString()

                task.title = title
                task.description = description
                mViewModel.updateTask(task)
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

    private fun openDeleteTaskDialog(task: Task) {
        val dialog = AlertDialog.Builder(this).setTitle("Delete Task")
            .setMessage("Do you want to remove this task?").setPositiveButton("Yes") { _, _ ->
                mViewModel.deleteTask(task)
            }.setNegativeButton("No", null).create()
        dialog.show()
    }
}