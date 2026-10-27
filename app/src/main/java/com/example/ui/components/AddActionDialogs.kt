package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyTask
import com.example.ui.theme.CharcoalInk
import com.example.ui.theme.InkSubtle
import com.example.ui.theme.PrimaryBlue
import java.util.UUID

/**
 * Clean dialog components for adding habits or quick daily tasks.
 * Extracted from MainActivity to ensure modularity and separation of concerns.
 */

@Composable
fun AddChoiceDialog(
  onDismiss: () -> Unit,
  onChooseHabit: () -> Unit,
  onChooseTask: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text("Apa yang ingin ditambahkan?", fontWeight = FontWeight.Bold, color = CharcoalInk)
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Option A: Tambah Habit
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFEFF6FF))
            .border(1.5.dp, PrimaryBlue, RoundedCornerShape(12.dp))
            .clickable {
              onDismiss()
              onChooseHabit()
            }
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(PrimaryBlue),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Eco, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
          }
          Column {
            Text("Habit Baru", fontWeight = FontWeight.Bold, color = CharcoalInk, fontSize = 15.sp)
            Text("Bangun kebiasaan harian & rutinitas", style = MaterialTheme.typography.bodySmall, color = InkSubtle)
          }
        }

        // Option B: Tambah Task
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFF7ED))
            .border(1.5.dp, Color(0xFFEA580C), RoundedCornerShape(12.dp))
            .clickable {
              onDismiss()
              onChooseTask()
            }
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(Color(0xFFEA580C)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Checklist, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
          }
          Column {
            Text("Task Baru", fontWeight = FontWeight.Bold, color = CharcoalInk, fontSize = 15.sp)
            Text("Catat tugas cepat (PR, belajar, dll)", style = MaterialTheme.typography.bodySmall, color = InkSubtle)
          }
        }
      }
    },
    confirmButton = {},
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Tutup", color = InkSubtle)
      }
    }
  )
}

@Composable
fun QuickAddTaskDialog(
  onDismiss: () -> Unit,
  onTaskCreated: (DailyTask) -> Unit
) {
  val context = LocalContext.current
  var taskTitle by remember { mutableStateOf("") }
  var taskCategory by remember { mutableStateOf("Priority") }
  var taskTime by remember { mutableStateOf("20m") }

  val categories = remember { listOf("Priority", "Belajar", "Quick Win", "Routine") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Tambah Task Baru", fontWeight = FontWeight.Bold, color = CharcoalInk) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
          value = taskTitle,
          onValueChange = { taskTitle = it },
          label = { Text("Nama Tugas (misal: Mengerjakan Tugas)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Text("Kategori:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          categories.forEach { cat ->
            val isSel = taskCategory == cat
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSel) PrimaryBlue else Color(0xFFF1F5F9))
                .clickable { taskCategory = cat }
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(
                text = cat,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSel) Color.White else CharcoalInk,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        OutlinedTextField(
          value = taskTime,
          onValueChange = { taskTime = it },
          label = { Text("Estimasi Waktu (misal: 15m, 30m, 1j)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (taskTitle.isNotBlank()) {
            val newTask = DailyTask(
              id = UUID.randomUUID().toString(),
              title = taskTitle.trim(),
              category = taskCategory,
              isCompleted = false,
              timeEstimate = taskTime.ifBlank { "20m" },
              emoji = ""
            )
            onTaskCreated(newTask)
            onDismiss()
            Toast.makeText(context, "Tugas berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
      ) {
        Text("Simpan Task")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Batal", color = InkSubtle)
      }
    }
  )
}
