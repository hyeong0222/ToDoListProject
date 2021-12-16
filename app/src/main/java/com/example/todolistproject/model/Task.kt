package com.example.todolistproject.model

data class Task(
    var title: String,
    var description: String? = "",
    var date: Long,
)
