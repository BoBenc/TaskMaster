package com.example.taskmaster;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import androidx.core.content.edit

object TaskStorage {
    fun saveTasks(context: Context, tasks: List<Task>) {
        val sharedPreferences = context.getSharedPreferences("task_storage", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = gson.toJson(tasks)
        sharedPreferences.edit { putString("tasks", json) }
    }

    fun loadTasks(context: Context): MutableList<Task> {
        val sharedPreferences = context.getSharedPreferences("task_storage", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("tasks", null)
        return if (json != null) {
            gson.fromJson(json, object : TypeToken<MutableList<Task>>() {}.type)
        } else {
            mutableListOf()
        }
    }
}