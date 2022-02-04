package com.example.todolistproject.view

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.todolistproject.R
import com.example.todolistproject.databinding.ActivityMainBinding
import com.example.todolistproject.databinding.DialogAddTaskBinding
import com.example.todolistproject.model.Task
import com.example.todolistproject.view.adapter.TaskAdapter
import com.example.todolistproject.viewmodel.TaskViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalDate
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
        AndroidThreeTen.init(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.activity = this@TaskActivity
        binding.adapter = taskAdapter

        setListeners()
        observeViewModel()
    }

    private fun setListeners() {
        taskAdapter.listener = object : TaskAdapter.OnTaskItemClickListener {
            override fun onTaskItemClick(position: Int) {
                openModifyTaskDialog(taskAdapter.getItem(position))
            }

            override fun onTaskItemLongClick(position: Int) {
                openDeleteTaskDialog(taskAdapter.getItem(position))
            }

            override fun onTaskCompleteClick(position: Int) {
                setTaskCompleted(taskAdapter.getItem(position))
            }
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

        dialogViewBinding.startDateEditText.apply {
            inputType = InputType.TYPE_NULL
            setOnClickListener { openDatePickerDialog(dialogViewBinding.startDateEditText) }
        }

        dialogViewBinding.endDateEditText.apply {
            inputType = InputType.TYPE_NULL
            setOnClickListener { openDatePickerDialog(dialogViewBinding.endDateEditText) }
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
        val currentDate = LocalDate.now()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val selectedDate = LocalDate.of(year, month, day)
            editText.setText(selectedDate.toString())
        }

        DatePickerDialog(
            this,
            dateSetListener,
            currentDate.year,
            currentDate.monthValue,
            currentDate.dayOfMonth,
        ).show()
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