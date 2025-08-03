package com.example.taskmaster.presentation

import android.util.Log
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
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
}