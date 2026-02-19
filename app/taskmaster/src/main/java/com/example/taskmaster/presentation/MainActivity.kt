package com.example.taskmaster.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*
import com.example.taskmaster.R
import com.example.taskmaster.presentation.theme.TaskMasterTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)
        setContent {
            TaskMasterTheme {
                SplashFlow(onShowMainScreen = { OnShowMainScreen() })
            }
        }
    }
}
enum class SplashState { TEXT, MAIN }

@Composable
fun SplashFlow(onShowMainScreen: @Composable () -> Unit) {
    var splashState by remember { mutableStateOf(SplashState.TEXT) }
    LaunchedEffect(splashState) {
        when (splashState) {
            SplashState.TEXT -> { delay(1200); splashState = SplashState.MAIN }
            SplashState.MAIN -> {}
        }
    }
    when (splashState) {
        SplashState.TEXT -> SplashText()
        SplashState.MAIN -> onShowMainScreen()
    }
}
@Composable
fun SplashText() {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background), contentAlignment = Alignment.Center) {
        Text(text = stringResource(id = R.string.app_name), color = MaterialTheme.colors.primary, style = MaterialTheme.typography.title2)
    }
}
@Composable
fun OnShowMainScreen() {
    val context = LocalContext.current
    var tasks by remember { mutableStateOf(emptyList<Task>()) }
    val listState = rememberScalingLazyListState()

    LaunchedEffect(true) {
        while (true) {
            tasks = TaskStorageWear.loadTasks(context) //
            delay(2000)
        }
    }

    fun syncData(newList: List<Task>) {
        tasks = newList
        TaskStorageWear.saveTasks(context, newList) //
        TaskSyncWear.sendTasksToPhone(context, newList) //
    }

    Scaffold(
        timeText = { TimeText() },
        positionIndicator = { PositionIndicator(scalingLazyListState = listState) }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            ScalingLazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(top = 40.dp, bottom = 65.dp, start = 10.dp, end = 10.dp)
            ) {
                items(tasks) { task ->
                    TaskRow(
                        task = task,
                        onToggle = { syncData(tasks.map { if (it.id == task.id) it.copy(isDone = !it.isDone) else it }) },
                        onDelete = { syncData(tasks.filter { it.id != task.id }) }
                    )
                }
            }
            Text(
                text = stringResource(id = R.string.authorText),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.background.copy(alpha = 0.8f))
                    .padding(bottom = 12.dp, top = 4.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.caption2
            )
        }
    }
}

@Composable
fun TaskRow(task: Task, onToggle: () -> Unit, onDelete: () -> Unit) {
    val buttonSize = 38.dp

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Text(
            text = task.text,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.body1.copy(
                textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None
            ),
            maxLines = 1
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = onToggle,
                modifier = Modifier.size(buttonSize),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (task.isDone) Color.Gray else Color(0xFF00C853)
                )
            ) {
                Icon(
                    painter = painterResource(
                        id = if (task.isDone) android.R.drawable.ic_menu_revert
                        else android.R.drawable.ic_input_add
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Button(
                onClick = onDelete,
                modifier = Modifier.size(buttonSize),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFB00020))
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_delete),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
            }
        }
    }
}