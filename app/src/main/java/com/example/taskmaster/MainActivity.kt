package com.example.taskmaster

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
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
        itemsAdapter = TaskAdapter(this, items) { position ->
            items.removeAt(position)
            itemsAdapter.notifyDataSetChanged()
        }
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

    class TaskAdapter(context: Context, private val  tasks: MutableList<String>, private val onDelete: (Int) -> Unit) : ArrayAdapter<String>(context, 0, tasks) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
            val textView = view.findViewById<TextView>(R.id.itemTextView)
            val delButton = view.findViewById<Button>(R.id.delButton)

            textView.text = tasks[position]
            delButton.setOnClickListener {
                onDelete(position)
            }

            return view
        }
    }
}