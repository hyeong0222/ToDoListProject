package com.example.todolistproject.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistproject.R
import com.example.todolistproject.model.Task
import com.example.todolistproject.view.adapter.TaskAdapter
import java.util.*

class TaskActivity : AppCompatActivity() {

    private lateinit var rvTodoList: RecyclerView
    private lateinit var btnAddTask: Button
    private lateinit var mAdapter: TaskAdapter
    private val taskList = ArrayList<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRecyclerView()
        initAddTaskButton()
    }

    private fun initRecyclerView() {
        mAdapter = TaskAdapter(taskList)

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

                    val task = Task(title = title, description = description, date = date)
                    mAdapter.addItem(task)
                    mAdapter.notifyDataSetChanged()
                }
                .setNegativeButton("Cancel", null)
                .create()
        dialog.show()
    }
}