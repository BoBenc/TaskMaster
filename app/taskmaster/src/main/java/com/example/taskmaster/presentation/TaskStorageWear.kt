package com.example.taskmaster.presentation

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object TaskStorageWear {
    fun saveTasks(context: Context, tasks: List<Task>) {
        val sharedPreferences = context.getSharedPreferences("task_storage_wear", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = gson.toJson(tasks)
        sharedPreferences.edit { putString("tasks", json) }
    }

    fun loadTasks(context: Context): MutableList<Task> {
        val prefs = context.getSharedPreferences("task_storage_wear", Context.MODE_PRIVATE)
        val json = prefs.getString("tasks", null)
        return if (json != null) {
            Gson().fromJson(json, object : TypeToken<MutableList<Task>>(){}.type)
        } else {
            mutableListOf()
        }
    }
}