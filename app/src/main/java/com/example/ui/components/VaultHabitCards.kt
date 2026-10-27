package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Habit
import com.example.data.model.HabitCategory
import com.example.ui.theme.CharcoalInk
import com.example.ui.theme.DeepPrimary
import com.example.ui.theme.InkSubtle
import com.example.ui.theme.LightPrimaryContainer
import com.example.ui.theme.SecondaryContainer
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.TertiaryContainer
import com.example.ui.theme.TertiaryFixed

/**
 * Reusable Habit Vault Cards for Active and Paused/Archived habits.
 * Separated to improve modularity and clean code architecture.
 */

@Composable
fun ActiveHabitCard(
  habit: Habit,
  onPause: () -> Unit,
  onArchive: () -> Unit,
  modifier: Modifier = Modifier
) {
  var menuExpanded by remember { mutableStateOf(false) }

  Row(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(SurfaceContainerLow)
      .border(1.dp, CharcoalInk.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
      .padding(10.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.weight(1f)
    ) {
      val iconBg = when (habit.category) {
        HabitCategory.WELLNESS -> TertiaryFixed
        HabitCategory.MIND -> SecondaryContainer
        HabitCategory.FITNESS -> LightPrimaryContainer
        else -> SurfaceContainerHigh
      }

      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(iconBg)
          .border(1.dp, CharcoalInk.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = habit.category.shortLabel.take(2).uppercase(),
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.ExtraBold,
          color = DeepPrimary
        )
      }

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = habit.title,
          style = MaterialTheme.typography.headlineSmall.copy(fontSize = 14.sp),
          color = CharcoalInk,
          fontWeight = FontWeight.Bold,
          maxLines = 1
        )
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.padding(top = 2.dp)
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(TertiaryContainer)
              .padding(horizontal = 6.dp, vertical = 1.dp)
          ) {
            Text(
              text = habit.category.shortLabel,
              style = MaterialTheme.typography.labelSmall,
              color = Color.White
            )
          }
          Text(
            text = habit.scheduleTime,
            style = MaterialTheme.typography.bodySmall,
            color = InkSubtle
          )
        }
      }
    }

    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(RoundedCornerShape(6.dp))
          .background(SurfaceContainer)
          .border(1.dp, CharcoalInk.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
          .clickable(onClick = onPause),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Default.Pause, contentDescription = "Pause", tint = CharcoalInk, modifier = Modifier.size(16.dp))
      }

      Box {
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(SurfaceContainer)
            .border(1.dp, CharcoalInk.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
            .clickable { menuExpanded = true },
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.MoreVert, contentDescription = "Opsi", tint = CharcoalInk, modifier = Modifier.size(16.dp))
        }

        DropdownMenu(
          expanded = menuExpanded,
          onDismissRequest = { menuExpanded = false }
        ) {
          DropdownMenuItem(
            text = { Text("Jeda Habit") },
            onClick = {
              onPause()
              menuExpanded = false
            }
          )
          DropdownMenuItem(
            text = { Text("Arsipkan Habit") },
            onClick = {
              onArchive()
              menuExpanded = false
            }
          )
        }
      }
    }
  }
}

@Composable
fun PausedHabitCard(
  habit: Habit,
  onResume: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(SurfaceContainerLow.copy(alpha = 0.7f))
      .border(1.dp, CharcoalInk.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
      .padding(10.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.weight(1f)
    ) {
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(SurfaceContainerHighest)
          .border(1.dp, CharcoalInk.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Default.SelfImprovement, contentDescription = null, tint = InkSubtle, modifier = Modifier.size(20.dp))
      }

      Column {
        Text(
          text = habit.title,
          style = MaterialTheme.typography.headlineSmall.copy(fontSize = 14.sp),
          color = CharcoalInk.copy(alpha = 0.75f),
          fontWeight = FontWeight.Bold
        )
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.padding(top = 2.dp)
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(SurfaceContainerHigh)
              .padding(horizontal = 6.dp, vertical = 1.dp)
          ) {
            Text(
              text = if (habit.isArchived) "Arsip" else "Jeda",
              style = MaterialTheme.typography.labelSmall,
              color = InkSubtle
            )
          }
          Text(
            text = habit.statusNote ?: "Tidak aktif",
            style = MaterialTheme.typography.bodySmall,
            color = InkSubtle
          )
        }
      }
    }

    Button(
      onClick = onResume,
      colors = ButtonDefaults.buttonColors(
        containerColor = SecondaryContainer,
        contentColor = DeepPrimary
      ),
      shape = RoundedCornerShape(6.dp),
      border = BorderStroke(1.dp, CharcoalInk.copy(alpha = 0.2f)),
      modifier = Modifier.height(32.dp)
    ) {
      Text(
        text = if (habit.isArchived) "Buka Arsip" else "Lanjutkan",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold
      )
    }
  }
}
