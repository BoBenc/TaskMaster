package com.example.taskmaster.presentation

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TaskSyncWear : WearableListenerService() {
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d("WEAR_SYNC", "Adatfogadása")
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                val path = dataItem.uri.path
                Log.d("WEAR_SYNC", "Path: $path")
                if(path == "/tasks") {
                    val json = DataMapItem.fromDataItem(dataItem).dataMap.getString("tasks_json")
                    if (json != null) {
                        val tasks: List<Task> = Gson().fromJson(json, object : TypeToken<List<Task>>() {}.type)
                        TaskStorageWear.saveTasks(this, tasks)
                        Log.d("WEAR_SYNC", "Adat fogadva")
                    }
                }
            }
        }
    }
    companion object {
        fun sendTasksToPhone(context: Context, tasks: List<Task>) {
            Log.d("WEAR_SYNC", "Adatküldés telefonra")
            val gson = Gson()
            val json = gson.toJson(tasks)
            val dataMapRequest = PutDataMapRequest.create("/tasks").apply {
                dataMap.putString("tasks_json", json)
                dataMap.putLong("timestamp", System.currentTimeMillis())
            }
            val request = dataMapRequest.asPutDataRequest()
            Wearable.getDataClient(context).putDataItem(request)
                .addOnSuccessListener { Log.d("WEAR_SYNC", "Sikeres küldés telefonra") }
                .addOnFailureListener { e -> Log.e("WEAR_SYNC", "Küldés sikertelen: ${e.message}") }
        }
    }
}