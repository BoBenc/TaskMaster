package com.example.taskmaster

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.taskmaster.ui.theme.TaskMasterTheme

class MainActivity : ComponentActivity() {
    private lateinit var items: ArrayList<String>
    private lateinit var itemsAdapter: ArrayAdapter<String>
    private lateinit var listViewItems: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        listViewItems = findViewById(R.id.taskListView)
        items = ArrayList()
        itemsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        listViewItems.adapter = itemsAdapter

        setUpListView()

        val btnAddButton: Button = findViewById(R.id.addButton)
        btnAddButton.setOnClickListener {
            val newItem: EditText = findViewById(R.id.taskEditText)
            val itemText = newItem.text.toString()
            if (itemText.isNotEmpty()) {
                items.add(itemText)
                itemsAdapter.notifyDataSetChanged()
                newItem.text.clear() // Beviteli mező törlése hozzáadás után
            }
        }
    }

    private fun setUpListView() {
        listViewItems.setOnItemClickListener { _, _, position, _ ->
            items.removeAt(position)
            itemsAdapter.notifyDataSetChanged()
            true
        }
    }
}