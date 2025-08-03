/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.taskmaster.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
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
                SplashFlow (
                    onShowMainScreen = { OnShowMainScreen() }
                )
            }
        }
    }
}

enum class SplashState {ICON, TEXT, MAIN}

@Composable
fun SplashFlow(onShowMainScreen: @Composable () -> Unit) {
    var splashState by remember { mutableStateOf(SplashState.ICON) }
    LaunchedEffect(splashState) {
        when (splashState) {
            SplashState.ICON -> {
                delay(1200)
                splashState = SplashState.TEXT
            }
            SplashState.TEXT -> {
                delay(1200)
                splashState = SplashState.MAIN
            }
            SplashState.MAIN -> {}
        }
    }

    when (splashState) {
        SplashState.ICON -> SplashIcon()
        SplashState.TEXT -> SplashText()
        SplashState.MAIN -> onShowMainScreen()
    }
}

@Composable
fun SplashIcon() {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Splash Icon",
            modifier = Modifier.size(64.dp)
        )
    }
}

@Composable
fun SplashText() {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.title2,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun OnShowMainScreen() {
    val context = LocalContext.current
    var tasks by remember { mutableStateOf(emptyList<Task>()) }

    LaunchedEffect(true) {
        while (true) {
            tasks = TaskStorageWear.loadTasks(context)
            delay(2000)
        }
    }

    fun toggleTaskDone(taskId: String) {
        tasks = tasks.map { task ->
            if (task.id == taskId) task.copy(isDone = !task.isDone) else task
        }
        TaskStorageWear.saveTasks(context, tasks)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 50.dp)
        ) {
            items(tasks) { task ->
                TaskRow(
                    task = task,
                    onToggleDone = { toggleTaskDone(task.id) }
                )
            }
        }

        Text(
            text = stringResource(id = R.string.authorText),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(vertical = 20.dp),
            style = MaterialTheme.typography.body2
        )
    }
}

@Composable
fun TaskRow(
    task: Task,
    onToggleDone: () -> Unit
) {
    androidx.compose.foundation.layout.Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 6.dp, bottom = 6.dp)
    ) {
        Text(
            text = task.text,
            modifier = Modifier
                .weight(1f)
                .padding(end = 10.dp),
            style = MaterialTheme.typography.body1.copy(
                textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None
            )
        )

        if (!task.isDone) {
            DoneButton(onClick = onToggleDone)
        } else {
            RefreshButton(onClick = onToggleDone)
        }
    }
}