package com.example.taskmaster

import android.app.AlertDialog
import android.content.Context
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
import androidx.core.content.getSystemService
import com.example.taskmaster.ui.theme.TaskMasterTheme
import com.example.taskmaster.TaskStorage
import com.google.android.material.textfield.TextInputEditText

class MainActivity : ComponentActivity() {
    data class Task(val text: String, var isDone: Boolean = false)

    private lateinit var items: MutableList<Task>
    private lateinit var itemsAdapter: ArrayAdapter<Task>
    private lateinit var listViewItems: ListView
    private lateinit var taskSpeaker: TaskSpeaker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskSpeaker = TaskSpeaker(this) // TaskSpeaker inicializálása
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
            val newItem: TextInputEditText = findViewById(R.id.taskEditText)
            val itemText = newItem.text.toString()
            val imm = getSystemService<InputMethodManager>()
            if (itemText.isNotEmpty()) {
                items.add(Task(itemText))
                itemsAdapter.notifyDataSetChanged()
                TaskStorage.saveTasks(this, items) // Feladatok mentése
                newItem.text?.clear() // Beviteli mező törlése hozzáadás után
                newItem.clearFocus()
                imm?.hideSoftInputFromWindow(newItem.windowToken, 0)
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if(ev.action == MotionEvent.ACTION_DOWN) {
            currentFocus?.let { v ->
                if (v is EditText) {
                    val r = Rect().apply { v.getGlobalVisibleRect(this) }
                    if (!r.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                        v.clearFocus()
                        getSystemService<InputMethodManager>()?.hideSoftInputFromWindow(v.windowToken, 0)
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onDestroy() {
        taskSpeaker.shutdown()
        super.onDestroy()
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
            val speakerButton = view.findViewById<Button>(R.id.itemSpeaker)

            val task = tasks[position]
            textView.text = task.text

            if (task.isDone) {
                textView.paintFlags = textView.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                doneButton.text = context.getString(R.string.undo)
                doneButton.setBackgroundResource(R.drawable.custom_refresh_button)
            }
            else {
                textView.paintFlags = textView.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
                doneButton.text = context.getString(R.string.doneButton)
                doneButton.setBackgroundResource(R.drawable.custom_done_button)
            }

            doneButton.setOnClickListener {
                task.isDone = !task.isDone
                notifyDataSetChanged()
                TaskStorage.saveTasks(context, tasks) // Feladatok mentése
            }

//            delButton.setOnClickListener {
//                val normalText = "Biztosan törölni szeretnéd ezt a feladatot? \n \n"
//                val boldText = task.text
//
//                val spannable = SpannableString(normalText + boldText)
//                spannable.setSpan(
//                    StyleSpan(Typeface.BOLD),
//                    normalText.length,
//                    normalText.length + boldText.length,
//                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//                )
//
//                AlertDialog.Builder(context)
//                    .setTitle("Feladat törlése")
//                    .setMessage(spannable)
//                    .setPositiveButton("Igen") { dialog, _ -> onDelete(position) }
//                    .setNegativeButton("Mégsem") { dialog, _ -> dialog.dismiss() }
//                    .show()
//            }

            delButton.setOnClickListener {
                val dialogView = LayoutInflater.from(context).inflate(R.layout.custom_alert, null)
                val normalText = "Biztosan törölni szeretnéd ezt a feladatot? \n \n"
                val boldText = task.text
                val spannable = SpannableString(normalText + boldText)
                spannable.setSpan(
                    StyleSpan(Typeface.BOLD),
                    normalText.length,
                    normalText.length + boldText.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                val alertTextView = dialogView.findViewById<TextView>(R.id.alertText)
                alertTextView.text = spannable

                val alertDialog = AlertDialog.Builder(context)
                    .setView(dialogView)
                    .create()

                dialogView.findViewById<Button>(R.id.alertDeleteButton).setOnClickListener {
                    onDelete(position)
                    alertDialog.dismiss()
                }

                dialogView.findViewById<Button>(R.id.alertCancelButton).setOnClickListener {
                    alertDialog.dismiss()
                }

                alertDialog.show()
            }

            speakerButton.setOnClickListener {
                (context as? MainActivity)?.taskSpeaker?.speak(task.text)
            }

            return view
        }
    }
}