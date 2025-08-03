package com.example.taskmaster.presentation

data class Task(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    var isDone: Boolean = false
)
