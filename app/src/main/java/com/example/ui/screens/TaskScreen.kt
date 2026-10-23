package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyTask
import com.example.ui.VibeHabitViewModel
import com.example.ui.components.CartoonTaskIcon
import com.example.ui.components.VibePillBadge
import com.example.ui.components.VibeStickerBox
import com.example.ui.theme.CharcoalInk
import com.example.ui.theme.DeepPrimary
import com.example.ui.theme.InkSubtle
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryContainer
import com.example.ui.theme.SoftPastelCell
import com.example.ui.theme.SurfaceCanvas
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TertiaryContainer
import com.example.ui.theme.TertiaryFixed
import java.util.UUID

@Composable
fun TaskScreen(
  viewModel: VibeHabitViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val tasks by viewModel.tasks.collectAsState()

  var selectedFilter by remember { mutableStateOf("All") } // "All", "Priority", "Quick Win", "Routine"
  var newTaskText by remember { mutableStateOf("") }
  var showSquadSection by remember { mutableStateOf(false) }

  val filteredTasks = when (selectedFilter) {
    "Priority" -> tasks.filter { it.category == "Priority" }
    "Quick Win" -> tasks.filter { it.category == "Quick Win" }
    "Routine" -> tasks.filter { it.category == "Routine" }
    else -> tasks
  }

  val completedCount = tasks.count { it.isCompleted }
  val totalCount = tasks.size
  val progressPercent = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(SurfaceCanvas)
      .padding(horizontal = 16.dp, vertical = 12.dp)
      .testTag("task_screen"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {

    // 1. Task Progress Hero Bento
    item {
      VibeStickerBox(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = SurfaceWhite,
        shape = RoundedCornerShape(16.dp),
        offsetSize = 3.dp,
        borderWidth = 2.dp
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            VibePillBadge(
              text = "TASK VAULT",
              backgroundColor = SoftPastelCell,
              textColor = DeepPrimary,
              icon = {
                CartoonTaskIcon(isSelected = true, modifier = Modifier.size(16.dp))
              }
            )

            Text(
              text = "$completedCount of $totalCount Done",
              style = MaterialTheme.typography.labelMedium,
              color = PrimaryBlue,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Lock In Daily Tasks 🎯",
            style = MaterialTheme.typography.headlineMedium,
            color = CharcoalInk,
            fontWeight = FontWeight.ExtraBold
          )

          Text(
            text = "Crush your high-impact micro tasks alongside your atomic habits.",
            style = MaterialTheme.typography.bodySmall,
            color = InkSubtle,
            modifier = Modifier.padding(top = 2.dp)
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Progress Bar
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(14.dp)
              .clip(RoundedCornerShape(9999.dp))
              .background(Color(0xFFE5EBF5))
              .border(1.dp, CharcoalInk, RoundedCornerShape(9999.dp))
              .padding(2.dp)
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth(progressPercent.coerceIn(0f, 1f))
                .height(10.dp)
                .clip(RoundedCornerShape(9999.dp))
                .background(PrimaryBlue)
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "${(progressPercent * 100).toInt()}% Completion",
              style = MaterialTheme.typography.labelSmall,
              color = PrimaryBlue,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = if (completedCount == totalCount && totalCount > 0) "All Clear! 🚀" else "${totalCount - completedCount} tasks left",
              style = MaterialTheme.typography.labelSmall,
              color = CharcoalInk,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    // 2. Add New Task Quick Input Bar
    item {
      VibeStickerBox(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = SurfaceWhite,
        shape = RoundedCornerShape(14.dp),
        offsetSize = 2.dp,
        borderWidth = 1.5.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          BasicTextField(
            value = newTaskText,
            onValueChange = { newTaskText = it },
            textStyle = TextStyle(
              fontSize = 14.sp,
              fontWeight = FontWeight.Medium,
              color = CharcoalInk
            ),
            modifier = Modifier
              .weight(1f)
              .padding(horizontal = 10.dp)
              .testTag("new_task_input"),
            decorationBox = { innerTextField ->
              if (newTaskText.isEmpty()) {
                Text(
                  text = "Add quick task (e.g. Email prof, buy groceries)...",
                  style = MaterialTheme.typography.bodySmall,
                  color = InkSubtle
                )
              }
              innerTextField()
            }
          )

          Button(
            onClick = {
              if (newTaskText.isNotBlank()) {
                val newTask = DailyTask(
                  id = UUID.randomUUID().toString(),
                  title = newTaskText.trim(),
                  category = "Priority",
                  isCompleted = false,
                  timeEstimate = "10m",
                  emoji = "⚡"
                )
                viewModel.addTask(newTask)
                newTaskText = ""
                Toast.makeText(context, "Task added! 🎯", Toast.LENGTH_SHORT).show()
              }
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = PrimaryBlue,
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.height(38.dp)
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add", style = MaterialTheme.typography.labelMedium)
          }
        }
      }
    }

    // 3. Category Filter Tabs
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val filters = listOf("All", "Priority", "Quick Win", "Routine")
        filters.forEach { filter ->
          val isSelected = selectedFilter == filter
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(9999.dp))
              .background(if (isSelected) PrimaryBlue else Color.White)
              .border(1.5.dp, CharcoalInk, RoundedCornerShape(9999.dp))
              .clickable { selectedFilter = filter }
              .padding(horizontal = 14.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
          ) {
            val emoji = when (filter) {
              "Priority" -> "🎯 "
              "Quick Win" -> "⚡ "
              "Routine" -> "🧹 "
              else -> "📋 "
            }
            Text(
              text = "$emoji$filter",
              style = MaterialTheme.typography.labelMedium,
              color = if (isSelected) Color.White else CharcoalInk,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    // 4. Tasks List
    if (filteredTasks.isEmpty()) {
      item {
        VibeStickerBox(
          modifier = Modifier.fillMaxWidth(),
          backgroundColor = SurfaceWhite,
          shape = RoundedCornerShape(14.dp),
          offsetSize = 2.dp
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(text = "🎉", fontSize = 32.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "No tasks in this category!",
              style = MaterialTheme.typography.headlineSmall.copy(fontSize = 15.sp),
              color = CharcoalInk,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Add a quick task above or take a well-deserved breather.",
              style = MaterialTheme.typography.bodySmall,
              color = InkSubtle
            )
          }
        }
      }
    } else {
      items(filteredTasks, key = { it.id }) { task ->
        TaskItemCard(
          task = task,
          onToggle = {
            viewModel.toggleTask(task.id)
            val msg = if (!task.isCompleted) "Task completed! 🌟" else "Task unchecked"
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
          },
          onDelete = {
            viewModel.deleteTask(task.id)
            Toast.makeText(context, "Task removed", Toast.LENGTH_SHORT).show()
          }
        )
      }
    }

    // 5. Optional Squad Toggle Footer
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFFEBF0F7))
          .border(1.dp, Color(0xFFD8E0EB), RoundedCornerShape(12.dp))
          .clickable { showSquadSection = !showSquadSection }
          .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(Icons.Default.Groups, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
          Column {
            Text(
              text = "Squad Accountability Sprint",
              style = MaterialTheme.typography.labelLarge,
              color = CharcoalInk,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = if (showSquadSection) "Tap to collapse squad view" else "Tap to see squad leaderboard (384/500)",
              style = MaterialTheme.typography.bodySmall,
              color = InkSubtle
            )
          }
        }
        Text(
          text = if (showSquadSection) "▲" else "▼",
          fontSize = 14.sp,
          color = PrimaryBlue,
          fontWeight = FontWeight.Bold
        )
      }
    }

    if (showSquadSection) {
      item {
        SquadScreen(viewModel = viewModel, modifier = Modifier.fillMaxWidth().height(400.dp))
      }
    }

    item {
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
private fun TaskItemCard(
  task: DailyTask,
  onToggle: () -> Unit,
  onDelete: () -> Unit
) {
  val checkboxBg by animateColorAsState(
    targetValue = if (task.isCompleted) PrimaryBlue else Color(0xFFEFF3F8),
    label = "taskCheckColor"
  )

  VibeStickerBox(
    modifier = Modifier.fillMaxWidth(),
    backgroundColor = if (task.isCompleted) Color(0xFFF8FAFC) else SurfaceWhite,
    shape = RoundedCornerShape(12.dp),
    offsetSize = 2.dp,
    borderWidth = 1.5.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Checkbox button
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(checkboxBg)
            .border(1.5.dp, CharcoalInk, RoundedCornerShape(8.dp))
            .clickable(onClick = onToggle)
            .testTag("task_checkbox_${task.id}"),
          contentAlignment = Alignment.Center
        ) {
          if (task.isCompleted) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Done",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = task.title,
            style = MaterialTheme.typography.bodyLarge,
            color = if (task.isCompleted) InkSubtle else CharcoalInk,
            fontWeight = FontWeight.SemiBold,
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
          )

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(top = 2.dp)
          ) {
            // Category tag
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(
                  when (task.category) {
                    "Priority" -> Color(0xFFFED7AA)
                    "Quick Win" -> TertiaryFixed
                    else -> SecondaryContainer
                  }
                )
                .padding(horizontal = 6.dp, vertical = 1.dp)
            ) {
              Text(
                text = "${task.emoji} ${task.category}",
                style = MaterialTheme.typography.labelSmall,
                color = CharcoalInk,
                fontWeight = FontWeight.Bold
              )
            }

            // Estimate badge
            Text(
              text = "⏱ ${task.timeEstimate}",
              style = MaterialTheme.typography.labelSmall,
              color = InkSubtle
            )
          }
        }
      }

      // Delete action button
      Box(
        modifier = Modifier
          .size(30.dp)
          .clip(CircleShape)
          .clickable(onClick = onDelete)
          .padding(4.dp),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          Icons.Default.DeleteOutline,
          contentDescription = "Delete Task",
          tint = InkSubtle,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}
