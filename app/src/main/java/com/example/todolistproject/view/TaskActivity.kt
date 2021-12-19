package com.example.todolistproject.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistproject.R
import com.example.todolistproject.model.Task
import com.example.todolistproject.view.adapter.TaskAdapter
import com.example.todolistproject.viewmodel.TaskViewModel
import java.util.*

class TaskActivity : AppCompatActivity() {

    private lateinit var rvTodoList: RecyclerView
    private lateinit var btnAddTask: Button
    private lateinit var mAdapter: TaskAdapter
    private lateinit var mViewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRecyclerView()
        initAddTaskButton()
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

        rvTodoList = findViewById(R.id.rv_todo_list)
        rvTodoList.run {
            layoutManager = LinearLayoutManager(this@TaskActivity)
            adapter = mAdapter
        }
    }

    private fun initAddTaskButton() {
        btnAddTask = findViewById(R.id.btn_add_task)
        btnAddTask.setOnClickListener {
            openAddTaskDialog()
        }
    }

    private fun initViewModel() {
        mViewModel =
            ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(
                TaskViewModel::class.java
            )
        mViewModel.getTaskList().observe(this, Observer {
            mAdapter.setTaskItems(it)
        })
    }

    private fun openAddTaskDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_task, null)
        val etTaskTitle: EditText = dialogView.findViewById(R.id.et_task_title)
        val etTaskDescription: EditText = dialogView.findViewById(R.id.et_task_description)

        val dialog =
            AlertDialog.Builder(this)
                .setTitle("Add task")
                .setView(dialogView)
                .setPositiveButton("Add") { _, _ ->
                    val title = etTaskTitle.text.toString()
                    val description = etTaskDescription.text.toString()
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
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_task, null)
        val etTaskTitle: EditText = dialogView.findViewById(R.id.et_task_title)
        val etTaskDescription: EditText = dialogView.findViewById(R.id.et_task_description)

        etTaskTitle.setText(task.title)
        etTaskDescription.setText(task.description)

        val dialog = AlertDialog.Builder(this).setTitle("Edit Task").setView(dialogView)
            .setPositiveButton("Okay") { _, _ ->
                val title = etTaskTitle.text.toString()
                val description = etTaskDescription.text.toString()

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