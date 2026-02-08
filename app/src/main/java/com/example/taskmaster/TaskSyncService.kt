package com.example.taskmaster

import android.content.Intent
import android.util.Log
import com.google.android.gms.wearable.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TaskSyncService : WearableListenerService() {
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d("PHONE_SYNC", "onDataChanged meghívva (TaskSyncService)")
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path == "/tasks") {
                Log.d("PHONE_SYNC", "/tasks path érkezett")
                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                val json = dataMap.getString("tasks_json")
                if (json != null) {
                    val tasks: List<Task> = Gson().fromJson(json, object : TypeToken<List<Task>>() {}.type)
                    TaskStorage.saveTasks(this, tasks)
                    Log.d("PHONE_SYNC", "💾 Mentve: ${tasks.size} feladat")

                    // Visszaküldés órára
                    //TaskSync.sendAllTaskToWatch(this, tasks)

                    val intent = Intent("com.example.taskmaster.UPDATE_TASKS")
                    intent.setPackage(packageName)
                    sendBroadcast(intent)
                }
            }
        }
    }
}