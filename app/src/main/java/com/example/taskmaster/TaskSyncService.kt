package com.example.taskmaster

import android.util.Log
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TaskSyncService : WearableListenerService() {
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d("PHONE_SYNC", "onDataChanged meghívva (TaskSyncService)")
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                val path = dataItem.uri.path
                Log.d("PHONE_SYNC", "Beérkező path: $path")
                if (path == "/tasks") {
                    val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
                    val json = dataMap.getString("tasks_json")
                    if (json != null) {
                        val tasks: List<Task> = Gson().fromJson(
                            json, object : TypeToken<List<Task>>() {}.type
                        )
                        TaskStorage.saveTasks(this, tasks)
                        Log.d("PHONE_SYNC", "Feladatlista frissítve: ${tasks.size} elem")
                    }
                }
            }
        }
    }
}