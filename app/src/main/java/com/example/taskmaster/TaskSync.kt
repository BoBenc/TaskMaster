package com.example.taskmaster

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson

object TaskSync {
    fun sendAllTaskToWatch(context: Context, tasks: List<Task>) {
        if (tasks.isEmpty()) {
            Log.d("PHONE_SYNC", "Nincsenek feladatok az adatküldéshez")
            return
        }

        Log.d("PHONE_SYNC","Adatküldés órára")
        val gson = Gson()
        val json = gson.toJson(tasks)
        val dataMapRequest = PutDataMapRequest.create("/tasks").apply {
            dataMap.putString("tasks_json", json)
            dataMap.putLong("timestamp", System.currentTimeMillis())
        }
        val request = dataMapRequest.asPutDataRequest()
        Wearable.getDataClient(context).putDataItem(request).addOnSuccessListener {
            Log.d("PHONE_SYNC", "Adatküldés sikeres")
        }.addOnFailureListener { e ->
            Log.e("PHONE_SYNC", "Adatküldés sikertelen: ${e.message}")
        }
    }
}