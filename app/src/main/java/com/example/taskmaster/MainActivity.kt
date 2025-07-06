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
import com.example.taskmaster.TaskStorage

class MainActivity : ComponentActivity() {
    data class Task(val text: String, var isDone: Boolean = false)

    private lateinit var items: MutableList<Task>
    private lateinit var itemsAdapter: ArrayAdapter<Task>
    private lateinit var listViewItems: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        listViewItems = findViewById(R.id.taskListView)
        items = TaskStorage.loadTasks(this) // Feladatok betöltése
        itemsAdapter = TaskAdapter(this, items) { position ->
            items.removeAt(position)
            itemsAdapter.notifyDataSetChanged()
            TaskStorage.saveTasks(this, items) // Feladatok mentése
        }
        listViewItems.adapter = itemsAdapter

        setUpListView()

        val btnAddButton: Button = findViewById(R.id.addButton)
        btnAddButton.setOnClickListener {
            val newItem: EditText = findViewById(R.id.taskEditText)
            val itemText = newItem.text.toString()
            if (itemText.isNotEmpty()) {
                items.add(Task(itemText))
                itemsAdapter.notifyDataSetChanged()
                TaskStorage.saveTasks(this, items) // Feladatok mentése
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

    class TaskAdapter(context: Context, private val tasks: MutableList<Task>, private val onDelete: (Int) -> Unit) : ArrayAdapter<Task>(context, 0, tasks) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
            val textView = view.findViewById<TextView>(R.id.itemTextView)
            val doneButton = view.findViewById<Button>(R.id.doneButton)
            val delButton = view.findViewById<Button>(R.id.delButton)

            val task = tasks[position]
            textView.text = task.text

            if (task.isDone) {
                textView.paintFlags = textView.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                doneButton.text = context.getString(R.string.undo)
            }
            else {
                textView.paintFlags = textView.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
                doneButton.text = context.getString(R.string.doneButton)
            }

            doneButton.setOnClickListener {
                task.isDone = !task.isDone
                notifyDataSetChanged()
                TaskStorage.saveTasks(context, tasks) // Feladatok mentése
            }

            delButton.setOnClickListener {
                onDelete(position)
            }

            return view
        }
    }
}