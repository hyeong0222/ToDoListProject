package com.example.todolistproject.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.todolistproject.R
import com.example.todolistproject.databinding.ActivityMainBinding
import com.example.todolistproject.view.fragment.TaskFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val taskFragment by lazy { TaskFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.fragment = taskFragment

        supportFragmentManager.beginTransaction().replace(binding.fragmentLayout.id, taskFragment).commit()
    }
}