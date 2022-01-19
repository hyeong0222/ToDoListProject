package com.example.todolistproject.view

import android.app.DatePickerDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.activity.viewModels
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

    @Inject lateinit var mAdapter: TaskAdapter
    private val mViewModel by viewModels<TaskViewModel>()
    private var currentFilter = TASK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.activity = this@TaskActivity

        initRecyclerView()
        observeViewModel()
    }

    private fun initRecyclerView() {
        mAdapter?.apply {
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

        binding.rvTodoList.run {
            layoutManager = LinearLayoutManager(this@TaskActivity)
            adapter = mAdapter
        }
    }

    private fun observeViewModel() {
        mViewModel.getTaskList().observe(this, {
            binding.progressIndicator.visibility = View.GONE
            mAdapter.setTaskItems(it)
        })
    }

    fun openAddTaskDialog() {
        val dialogViewBinding = DialogAddTaskBinding.inflate(LayoutInflater.from(this))

        val dialog =
            AlertDialog.Builder(this).setTitle(getString(R.string.todo_add_task))
                .setView(dialogViewBinding.root)
                .setPositiveButton(getString(R.string.todo_add)) { dialog, _ ->
                    addTask(dialogViewBinding)
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.todo_cancel)) { dialog, _ -> dialog.dismiss() }
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
        mViewModel.insertTask(task)
    }

    private fun openModifyTaskDialog(task: Task) {
        val dialogViewBinding = DialogAddTaskBinding.inflate(LayoutInflater.from(this))
        dialogViewBinding.task = task

        val dialog = AlertDialog.Builder(this).setTitle(getString(R.string.todo_edit_task))
            .setView(dialogViewBinding.root)
            .setPositiveButton(getString(R.string.todo_okay)) { dialog, _ ->
                modifyTask(dialogViewBinding, task)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.todo_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun modifyTask(dialogViewBinding: DialogAddTaskBinding, task: Task) {
        // We must deep copy the task object in order for the DiffUtil to recognize and compare the
        // new object with the old one.
        val modifiedTask = task.copy()
        modifiedTask.title = dialogViewBinding.taskTitleEditText.text.toString()
        modifiedTask.description = dialogViewBinding.taskDescriptionEditText.text.toString()
        modifiedTask.startDate = dialogViewBinding.startDateEditText.text.toString()
        modifiedTask.endDate = dialogViewBinding.endDateEditText.text.toString()

        mViewModel.updateTask(modifiedTask)
    }

    private fun openDeleteTaskDialog(task: Task) {
        val dialog = AlertDialog.Builder(this).setTitle(getString(R.string.todo_delete_task))
            .setMessage(getString(R.string.todo_delete_task_confirmation))
            .setPositiveButton(getString(R.string.todo_yes)) { _, _ ->
                mViewModel.deleteTask(task)
            }.setNegativeButton(getString(R.string.todo_no), null).create()
        dialog.show()
    }

    private fun setTaskCompleted(task: Task) {
        val isCompleted = task.isCompleted == true
        task.isCompleted = !isCompleted

        mViewModel.updateTask(task)
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
        MaterialAlertDialogBuilder(this)
            .setTitle("Filter")
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("OK") { dialog, _ ->
                binding.rvTodoList.visibility = View.GONE
                binding.progressIndicator.visibility = View.VISIBLE
                setFilteredList()
                dialog.dismiss()
            }
            .setSingleChoiceItems(items, checkedItem) { _, index ->
                currentFilter = items[index]
            }
            .show()
    }

    private fun setFilteredList() {
        when (currentFilter) {
            TASK -> {
                mViewModel.getTaskList().observe(this, {
                    mAdapter.setTaskItems(it)
                })
            }
            INCOMPLETE -> {
                mViewModel.getIncompleteTaskList().observe(this, {
                    mAdapter.setTaskItems(it)
                })
            }
            COMPLETED -> {
                mViewModel.getCompletedTaskList().observe(this, {
                    mAdapter.setTaskItems(it)
                })
            }
        }
        binding.progressIndicator.visibility = View.GONE
        binding.rvTodoList.visibility = View.VISIBLE
    }

    companion object {
        const val TASK = "Tasks"
        const val INCOMPLETE = "Incomplete"
        const val COMPLETED = "Completed"
    }
}