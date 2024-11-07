package com.whatziya.todoyan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.whatziya.todoyan.data.Importance
import com.whatziya.todoyan.data.TodoItem
import com.whatziya.todoyan.ui.theme.ColorLightBlue
import com.whatziya.todoyan.ui.theme.ColorLightGray
import com.whatziya.todoyan.ui.theme.ColorLightGreen
import com.whatziya.todoyan.ui.theme.ColorLightRed
import com.whatziya.todoyan.ui.theme.ToDoYanTheme
import com.whatziya.todoyan.ui.theme.Typography
import java.util.Date
import java.util.UUID
import kotlin.random.Random

class TodoItemsRepository {
    private val todoList = mutableStateListOf<TodoItem>()
    fun getTodoItems() = todoList
    fun addTodoItem(item: TodoItem) = todoList.add(item)
    fun removeTodoItem(item: TodoItem) = todoList.remove(item)
}

private val todoItemsRepository = TodoItemsRepository()
fun generateRandomTodoItems(): List<TodoItem> {
    val todoItems = mutableListOf<TodoItem>()

    val sampleTexts = listOf(
        "Buy groceries", "Finish homework", "Clean the house", "Prepare presentation",
        "Go to the gym", "Call mom", "Schedule dentist appointment", "Finish reading book",
        "Write blog post", "Update resume", "Plan weekend trip", "Organize workspace",
        "Pay bills", "Send email to boss", "Arrange meeting with team", "Study for exams",
        "Water the plants", "Buy birthday gift", "Complete project report", "Practice coding"
    )

    for (i in 0 until 20) {
        val id = UUID.randomUUID().toString()
        val text = sampleTexts[i % sampleTexts.size]
        val importance = when (Random.nextInt(3)) {
            0 -> Importance.Low
            1 -> Importance.Medium
            else -> Importance.High
        }
        val deadline = if (Random.nextBoolean()) Date(
            System.currentTimeMillis() + Random.nextLong(
                1,
                10
            ) * 24 * 60 * 60 * 1000
        ) else null
        val isCompleted = Random.nextBoolean()
        val createdAt =
            Date(System.currentTimeMillis() - Random.nextLong(1, 10) * 24 * 60 * 60 * 1000)
        val modifiedAt =
            Date(System.currentTimeMillis() - Random.nextLong(1, 5) * 24 * 60 * 60 * 1000)

        todoItems.add(
            TodoItem(
                id = id,
                text = text,
                importance = importance,
                deadline = deadline,
                isCompleted = isCompleted,
                createdAt = createdAt,
                modifiedAt = modifiedAt
            )
        )
    }

    return todoItems
}

private val todoList = generateRandomTodoItems()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoYanTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "main_screen",
                        Modifier.padding(innerPadding)
                    ) {
                        // Define the main screen route
                        composable("main_screen") {
                            MainScreen(
                                onAddClick = { navController.navigate("add_todo_screen") }
                            )
                        }
                        // Define the new screen route
                        composable("add_todo_screen") {
                            AddTodoScreen(onBack = { navController.navigateUp() })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(onAddClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                "Мои дела",
                modifier = Modifier.padding(top = 86.dp, start = 60.dp),
                style = Typography.displayLarge
            )
            Row(
                modifier = Modifier.padding(top = 5.dp, start = 60.dp)
            ) {
                Text("Выполнено - ${todoItemsRepository.getTodoItems().count { it.isCompleted }}", color = Color.LightGray, style = Typography.bodyLarge)
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(R.drawable.ic_visible),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 18.dp)
                )
            }
            LazyColumn(
                Modifier
                    .padding(top = 5.dp, start = 5.dp, end = 5.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
            ) {
                items(todoItemsRepository.getTodoItems().size) { index ->
                    ToDoContainer(todoItemsRepository.getTodoItems()[index])
                }
            }
        }
        FloatingActionButton(
            onClick = onAddClick,
            modifier = Modifier
                .clip(RoundedCornerShape(25.dp))
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = ColorLightBlue
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "Add"
            )
        }
    }
}

@Composable
fun AddTodoScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text("Add New Todo Item", style = Typography.displayLarge)
    }
}

@Composable
fun ToDoContainer(data: TodoItem, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = data.isCompleted,
            onCheckedChange = {},
            enabled = !data.isCompleted,
            colors = if (data.isCompleted) {
                CheckboxDefaults.colors(checkedColor = ColorLightGreen)
            } else if (data.importance == Importance.Low || data.importance == Importance.Medium) {
                CheckboxDefaults.colors(uncheckedColor = ColorLightGray)
            } else {
                CheckboxDefaults.colors(uncheckedColor = ColorLightRed)
            }
        )
        if (data.importance != Importance.Medium) {
            Text(
                if (data.importance == Importance.Low) "↓" else "!!",
                style = Typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                color = if (data.importance == Importance.Low) ColorLightGray else ColorLightRed
            )
        }
        Text(data.text, style = Typography.bodyLarge, maxLines = 3)
        Spacer(modifier = Modifier.weight(1f))
        Image(painter = painterResource(R.drawable.ic_info), contentDescription = null)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ToDoYanTheme {
        ToDoContainer(todoList[0])
    }
}