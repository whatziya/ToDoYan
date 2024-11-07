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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
                            AddTodoScreen()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(onAddClick : () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) { // Use Box to layer the button on top
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                "Мои дела",
                modifier = Modifier
                    .padding(top = 86.dp, start = 60.dp)
                    .background(MaterialTheme.colorScheme.background),
                style = Typography.displayLarge
            )
            Row(
                modifier = Modifier.padding(top = 5.dp, start = 60.dp)
            ) {
                Text(
                    "Выполнено - 5",
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface),
                    style = Typography.bodyLarge,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.weight(1f)) // Spacer to push the Image to the end
                Image(
                    painter = painterResource(R.drawable.ic_visible),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 18.dp) // Add end padding if needed
                )
            }

            LazyColumn(
                Modifier
                    .padding(top = 5.dp, start = 5.dp, end = 5.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.background)
            ) {
                items(todoList.size) {
                    ToDoContainer(todoList[it])
                }
            }
        }

        // Add the floating action button at the bottom end
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
fun AddTodoScreen() {
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
            checked = data.isCompleted, onCheckedChange = {
            }, enabled = !data.isCompleted, colors = if (data.isCompleted) {
                CheckboxColors(
                    checkedCheckmarkColor = Color.White,
                    uncheckedCheckmarkColor = Color.Transparent,
                    checkedBoxColor = ColorLightGreen,
                    uncheckedBoxColor = Color.Transparent,
                    disabledCheckedBoxColor = ColorLightGreen,
                    disabledUncheckedBoxColor = Color.Transparent,
                    disabledIndeterminateBoxColor = Color.Transparent,
                    checkedBorderColor = ColorLightGreen,
                    uncheckedBorderColor = Color.Transparent,
                    disabledBorderColor = ColorLightGreen,
                    disabledUncheckedBorderColor = Color.Transparent,
                    disabledIndeterminateBorderColor = Color.Transparent
                )
            } else if (data.importance == Importance.Low || data.importance == Importance.Medium) {
                CheckboxColors(
                    checkedCheckmarkColor = Color.Transparent,
                    uncheckedCheckmarkColor = Color.Transparent,
                    checkedBoxColor = Color.Transparent,
                    uncheckedBoxColor = Color.Transparent,
                    disabledCheckedBoxColor = Color.Transparent,
                    disabledUncheckedBoxColor = Color.Transparent,
                    disabledIndeterminateBoxColor = Color.Transparent,
                    checkedBorderColor = Color.Transparent,
                    uncheckedBorderColor = ColorLightGray,
                    disabledBorderColor = Color.Transparent,
                    disabledUncheckedBorderColor = Color.Transparent,
                    disabledIndeterminateBorderColor = Color.Transparent
                )
            } else {
                CheckboxColors(
                    checkedCheckmarkColor = Color.Transparent,
                    uncheckedCheckmarkColor = Color.Transparent,
                    checkedBoxColor = Color.Transparent,
                    uncheckedBoxColor = Color(0x99FF3B30),
                    disabledCheckedBoxColor = Color.Transparent,
                    disabledUncheckedBoxColor = Color.Transparent,
                    disabledIndeterminateBoxColor = Color.Transparent,
                    checkedBorderColor = Color.Transparent,
                    uncheckedBorderColor = ColorLightRed,
                    disabledBorderColor = Color.Transparent,
                    disabledUncheckedBorderColor = Color.Transparent,
                    disabledIndeterminateBorderColor = Color.Transparent
                )
            }
        )
        if (data.importance != Importance.Medium) {
            data.apply {
                Text(
                    if (importance == Importance.Low) "↓" else "!!",
                    style = Typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp // Adjust font size as needed
                    ),
                    color = if (importance == Importance.Low) ColorLightGray else ColorLightRed
                )
            }
        }

        Text(data.text, style = Typography.bodyLarge)

        Spacer(modifier = Modifier.weight(1f)) // Add a spacer to push Image to the end

        Image(
            painter = painterResource(R.drawable.ic_info),
            contentDescription = null
        )
    }


}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ToDoYanTheme {
        ToDoContainer(todoList[0])
    }
}