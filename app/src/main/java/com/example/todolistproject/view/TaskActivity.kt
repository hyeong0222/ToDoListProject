package com.example.todolistproject.view

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistproject.R
import com.example.todolistproject.databinding.ActivityMainBinding
import com.example.todolistproject.databinding.DialogAddTaskBinding
import com.example.todolistproject.model.Task
import com.example.todolistproject.view.adapter.TaskAdapter
import com.example.todolistproject.viewmodel.TaskViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class TaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject lateinit var taskAdapter: TaskAdapter
    private val taskViewModel by viewModels<TaskViewModel>()
    private var currentFilter = TASK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.activity = this@TaskActivity

        initRecyclerView()
        observeViewModel()
    }

    private fun initRecyclerView() {
        taskAdapter?.apply {
            listener = object : TaskAdapter.OnTaskItemClickListener {
                override fun onTaskItemClick(position: Int) {
                    openModifyTaskDialog(getItem(position))
                }

                override fun onTaskItemLongClick(position: Int) {
                    openDeleteTaskDialog(getItem(position))
                }

                override fun onTaskCompleteClick(position: Int) {
                    setTaskCompleted(getItem(position))
                }
            }
        }

        binding.rvTodoList.apply {
            layoutManager = LinearLayoutManager(this@TaskActivity)
            adapter = taskAdapter
        }
    }

    private fun observeViewModel() {
        taskViewModel.taskList.observe(this) {
            taskAdapter.setTaskItems(it)
            showTaskList(true)
        }
    }

    fun openAddTaskDialog() {
        val dialogViewBinding = DialogAddTaskBinding.inflate(LayoutInflater.from(this))

        val dialog =
            AlertDialog.Builder(this).setTitle(getString(R.string.todo_add_task))
                .setView(dialogViewBinding.root)
                .setPositiveButton(getString(R.string.todo_add)) { _, _ ->
                    addTask(dialogViewBinding)
                }
                .setNegativeButton(getString(R.string.todo_cancel), null)
                .create()

        dialogViewBinding.startDateEditText.inputType = InputType.TYPE_NULL
        dialogViewBinding.startDateEditText.setOnClickListener {
            openDatePickerDialog(dialogViewBinding.startDateEditText)
        }
        dialogViewBinding.endDateEditText.inputType = InputType.TYPE_NULL
        dialogViewBinding.endDateEditText.setOnClickListener {
            openDatePickerDialog(dialogViewBinding.endDateEditText)
        }
        dialog.show()
    }

    private fun addTask(dialogViewBinding: DialogAddTaskBinding) {
        val title = dialogViewBinding.taskTitleEditText.text.toString()
        val description = dialogViewBinding.taskDescriptionEditText.text.toString()
        val startDate = dialogViewBinding.startDateEditText.text.toString()
        val endDate = dialogViewBinding.endDateEditText.text.toString()

        val task =
            Task(
                id = null,
                isCompleted = false,
                title = title,
                description = description,
                startDate = startDate,
                endDate = endDate
            )
        taskViewModel.insertTask(task)
    }

    private fun openModifyTaskDialog(task: Task) {
        val dialogViewBinding = DialogAddTaskBinding.inflate(LayoutInflater.from(this))
        dialogViewBinding.task = task

        val dialog = AlertDialog.Builder(this).setTitle(getString(R.string.todo_edit_task))
            .setView(dialogViewBinding.root)
            .setPositiveButton(getString(R.string.todo_okay)) { _, _ ->
                modifyTask(dialogViewBinding, task)
            }
            .setNegativeButton(getString(R.string.todo_cancel), null)
            .create()
        dialog.show()
    }

    private fun modifyTask(dialogViewBinding: DialogAddTaskBinding, task: Task) {
        // We must deep copy the task object in order for the DiffUtil to recognize and compare the
        // new object with the old one.
        val modifiedTask = task.copy()
        modifiedTask.apply {
            title = dialogViewBinding.taskTitleEditText.text.toString()
            description = dialogViewBinding.taskDescriptionEditText.text.toString()
            startDate = dialogViewBinding.startDateEditText.text.toString()
            endDate = dialogViewBinding.endDateEditText.text.toString()
        }

        taskViewModel.updateTask(modifiedTask)
    }

    private fun openDeleteTaskDialog(task: Task) {
        val dialog = AlertDialog.Builder(this).setTitle(getString(R.string.todo_delete_task))
            .setMessage(getString(R.string.todo_delete_task_confirmation))
            .setPositiveButton(getString(R.string.todo_yes)) { _, _ ->
                taskViewModel.deleteTask(task)
            }.setNegativeButton(getString(R.string.todo_no), null).create()
        dialog.show()
    }

    private fun setTaskCompleted(task: Task) {
        val isCompleted = task.isCompleted == true
        task.isCompleted = !isCompleted

        taskViewModel.updateTask(task)
    }

    private fun openDatePickerDialog(editText: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val date = Calendar.getInstance()
            date.set(Calendar.YEAR, year)
            date.set(Calendar.MONTH, month)
            date.set(Calendar.DAY_OF_YEAR, day)
            setDateText(editText, date)
        }

        DatePickerDialog(
            this,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun setDateText(editText: TextInputEditText, date: Calendar) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        editText.setText(sdf.format(date.time))
    }

    fun openListFilter() {
        val items = arrayOf(TASK, INCOMPLETE, COMPLETED)
        val checkedItem = items.indexOf(currentFilter)
        var tempFilter = currentFilter
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.todo_filter)
            .setNegativeButton(R.string.todo_cancel, null)
            .setPositiveButton(R.string.todo_okay) { _, _ ->
                currentFilter = tempFilter
                showTaskList(false)
                setFilteredList()
            }
            .setSingleChoiceItems(items, checkedItem) { _, index ->
                tempFilter = items[index]
            }
            .show()
    }

    private fun setFilteredList() {
        when (currentFilter) {
            TASK -> {
                taskViewModel.taskList.observe(this) {
                    taskAdapter.setTaskItems(it)
                }
            }
            INCOMPLETE -> {
                taskViewModel.incompleteTaskList.observe(this) {
                    taskAdapter.setTaskItems(it)
                }
            }
            COMPLETED -> {
                taskViewModel.completedTaskList.observe(this) {
                    taskAdapter.setTaskItems(it)
                }
            }
        }
        showTaskList(true)
    }

    private fun showTaskList(visible: Boolean) {
        binding.progressIndicator.isGone = visible
        binding.rvTodoList.isVisible = visible
    }

    companion object {
        const val TASK = "Tasks"
        const val INCOMPLETE = "Incomplete"
        const val COMPLETED = "Completed"
    }
}