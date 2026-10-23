package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FrequencyType
import com.example.data.model.Habit
import com.example.data.model.HabitCategory
import com.example.ui.AppDestination
import com.example.ui.VibeHabitViewModel
import com.example.ui.components.VibeButton
import com.example.ui.components.VibePillBadge
import com.example.ui.components.VibeStickerBox
import com.example.ui.theme.CharcoalInk
import com.example.ui.theme.DeepPrimary
import com.example.ui.theme.InkMuted
import com.example.ui.theme.InkSubtle
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryContainer
import com.example.ui.theme.SoftPastelCell
import com.example.ui.theme.SurfaceCanvas
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun AddHabitScreen(
  viewModel: VibeHabitViewModel,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onNavigateBack() }

  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  var habitTitle by remember { mutableStateOf("Read 20 pages before bed 📖") }
  var selectedCategory by remember { mutableStateOf(HabitCategory.MIND) }
  var frequencyType by remember { mutableStateOf(FrequencyType.DAILY) }
  val activeDays = remember { mutableStateListOf(0, 1, 2, 3, 4, 5) } // Mon-Sat active
  var isNumericGoal by remember { mutableStateOf(true) }
  var goalCount by remember { mutableIntStateOf(20) }
  var goalUnit by remember { mutableStateOf("Pages") }
  var reminderTime by remember { mutableStateOf("21:30") }
  var isLockedInCelebration by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(SurfaceCanvas)
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 12.dp)
      .testTag("add_habit_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {

    // 1. Header / Mascot Inspiration Greeting Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(Color(0xFFEBF0F7))
        .border(1.dp, Color(0xFFD8E0EB), RoundedCornerShape(16.dp))
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          // Level 01 Pill
          VibePillBadge(
            text = "LEVEL 01",
            backgroundColor = Color(0xFFDCE5F2),
            textColor = DeepPrimary,
            icon = {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = DeepPrimary, modifier = Modifier.size(13.dp))
            }
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Start a New Habit ✨",
            style = MaterialTheme.typography.headlineMedium,
            color = CharcoalInk,
            fontWeight = FontWeight.Bold
          )

          Text(
            text = "Small moves, big main character energy.",
            style = MaterialTheme.typography.bodySmall,
            color = InkSubtle,
            modifier = Modifier.padding(top = 2.dp)
          )
        }

        // Mascot Stamp Visual
        Box(
          modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(PrimaryBlue)
            .border(2.dp, Color(0x332B4675), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Mascot Star",
            tint = Color.White,
            modifier = Modifier.size(34.dp)
          )
        }
      }
    }

    // 2. Habit Title Input Field
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Habit Title",
          style = MaterialTheme.typography.labelLarge,
          color = CharcoalInk,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Step 1 of 5",
          style = MaterialTheme.typography.labelSmall,
          color = InkSubtle
        )
      }

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(Color.White)
          .border(1.dp, Color(0xFFD8E0EB), RoundedCornerShape(12.dp))
          .padding(horizontal = 14.dp, vertical = 12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          BasicTextField(
            value = habitTitle,
            onValueChange = { habitTitle = it },
            textStyle = TextStyle(
              fontSize = 15.sp,
              fontWeight = FontWeight.Medium,
              color = CharcoalInk
            ),
            modifier = Modifier
              .weight(1f)
              .testTag("habit_title_input")
          )

          // Character Count Badge
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(Color(0xFFE2E8F2)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "${habitTitle.length}",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              color = CharcoalInk,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    // 3. Category & Mascot Icon Selector
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Pick your vibe category:",
          style = MaterialTheme.typography.labelLarge,
          color = CharcoalInk,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "5 vibes",
          style = MaterialTheme.typography.bodySmall,
          color = PrimaryBlue,
          fontWeight = FontWeight.Bold
        )
      }

      // Horizontal Scrollable Category Pills
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val categories = listOf(
          HabitCategory.MIND,
          HabitCategory.FITNESS,
          HabitCategory.HEALTH,
          HabitCategory.GLOW_UP,
          HabitCategory.DETOX
        )

        categories.forEach { category ->
          val isSelected = selectedCategory == category
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(9999.dp))
              .background(if (isSelected) PrimaryBlue else Color(0xFFE9EDF4))
              .border(1.dp, if (isSelected) Color.Transparent else Color(0xFFD4DDE8), RoundedCornerShape(9999.dp))
              .clickable { selectedCategory = category }
              .padding(horizontal = 14.dp, vertical = 8.dp)
              .testTag("category_pill_${category.name}"),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = category.emoji, fontSize = 14.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = category.displayName,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) Color.White else CharcoalInk,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }

    // 4. Frequency & Target Setter
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(Color(0xFFEBF0F7))
        .border(1.dp, Color(0xFFD8E0EB), RoundedCornerShape(16.dp))
        .padding(16.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "How often do we lock in?",
            style = MaterialTheme.typography.labelLarge,
            color = CharcoalInk,
            fontWeight = FontWeight.Bold
          )
          VibePillBadge(
            text = "STREAK READY",
            backgroundColor = Color(0xFFD7E2F0),
            textColor = DeepPrimary
          )
        }

        // Segmented Frequency Switcher (Daily, Specific Days, X times / wk)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFDBE3EF))
            .border(1.dp, Color(0xFFCCD7E6), RoundedCornerShape(8.dp))
            .padding(3.dp)
        ) {
          FrequencyType.values().forEach { freq ->
            val isSelected = frequencyType == freq
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) PrimaryBlue else Color.Transparent)
                .clickable { frequencyType = freq }
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = freq.label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) Color.White else InkSubtle,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        // Day of the Week Picker (M, T, W, T, F, S, S)
        val dayLetters = listOf("M", "T", "W", "T", "F", "S", "S")
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          dayLetters.forEachIndexed { index, letter ->
            val isDayActive = activeDays.contains(index)
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isDayActive) PrimaryBlue else Color(0xFFE0E7F1))
                .border(1.dp, if (isDayActive) Color.Transparent else Color(0xFFCDD7E5), CircleShape)
                .clickable {
                  if (isDayActive) activeDays.remove(index) else activeDays.add(index)
                },
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = letter,
                style = MaterialTheme.typography.labelMedium,
                color = if (isDayActive) Color.White else InkSubtle,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }

    // 5. Micro-Goal / Numeric Counter Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(Color.White)
        .border(1.dp, Color(0xFFD8E0EB), RoundedCornerShape(16.dp))
        .padding(16.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0xFFE2EAF5)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Tune, contentDescription = null, tint = DeepPrimary, modifier = Modifier.size(18.dp))
            }
            Column {
              Text(
                text = "Track quantity / numeric goal",
                style = MaterialTheme.typography.labelLarge,
                color = CharcoalInk,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Measure exact increments",
                style = MaterialTheme.typography.bodySmall,
                color = InkSubtle
              )
            }
          }

          Switch(
            checked = isNumericGoal,
            onCheckedChange = { isNumericGoal = it },
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color.White,
              checkedTrackColor = PrimaryBlue,
              uncheckedThumbColor = Color.White,
              uncheckedTrackColor = Color(0xFFCCD7E5)
            )
          )
        }

        // Counter Controls: − [ 20 Pages ] +
        if (isNumericGoal) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFFF1F4F8))
              .border(1.dp, Color(0xFFDCE3EC), RoundedCornerShape(10.dp))
              .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Minus Button
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFD0D8E4), RoundedCornerShape(8.dp))
                .clickable { if (goalCount > 1) goalCount-- },
              contentAlignment = Alignment.Center
            ) {
              Text(text = "−", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = CharcoalInk)
            }

            // Value + Unit
            Row(verticalAlignment = Alignment.Bottom) {
              Text(
                text = "$goalCount",
                style = MaterialTheme.typography.headlineLarge,
                color = CharcoalInk,
                fontWeight = FontWeight.ExtraBold
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = goalUnit,
                style = MaterialTheme.typography.labelLarge,
                color = PrimaryBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 2.dp)
              )
            }

            // Plus Button
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFD0D8E4), RoundedCornerShape(8.dp))
                .clickable { goalCount++ },
              contentAlignment = Alignment.Center
            ) {
              Text(text = "+", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = CharcoalInk)
            }
          }
        }
      }
    }

    // 6. Time & Reminder Settings
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(Color.White)
        .border(1.dp, Color(0xFFD8E0EB), RoundedCornerShape(16.dp))
        .padding(16.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Reminder Vibe",
            style = MaterialTheme.typography.labelLarge,
            color = CharcoalInk,
            fontWeight = FontWeight.Bold
          )
          VibePillBadge(
            text = "PUSH READY",
            backgroundColor = Color(0xFFDBE5F3),
            textColor = DeepPrimary
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Time Picker Pill Box
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFFEFF3F8))
              .border(1.dp, Color(0xFFD6DFE9), RoundedCornerShape(10.dp))
              .padding(horizontal = 14.dp, vertical = 10.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Schedule, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              BasicTextField(
                value = reminderTime,
                onValueChange = { reminderTime = it },
                textStyle = TextStyle(
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
                  color = CharcoalInk
                ),
                modifier = Modifier.width(50.dp)
              )
            }
          }

          // Mascot Microcopy Bubble
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFFF4F7FA))
              .border(1.dp, Color(0xFFDCE3EC), RoundedCornerShape(10.dp))
              .padding(horizontal = 12.dp, vertical = 10.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "🌙", fontSize = 16.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "\"Hey bestie, time to read!\"",
                style = MaterialTheme.typography.bodySmall,
                color = CharcoalInk,
                maxLines = 1
              )
            }
          }
        }
      }
    }

    // 7. Bottom Action Buttons Section
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 4.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Primary Chunky CTA Button ("Lock In This Habit 🚀")
      val buttonText = if (isLockedInCelebration) "LOCKED IN! 🔥" else "Lock In This Habit 🚀"
      val buttonColor by animateColorAsState(
        targetValue = if (isLockedInCelebration) DeepPrimary else PrimaryBlue,
        label = "lockinColor"
      )

      VibeButton(
        text = buttonText,
        onClick = {
          if (habitTitle.isBlank()) {
            Toast.makeText(context, "Please enter a habit title!", Toast.LENGTH_SHORT).show()
            return@VibeButton
          }

          val newHabit = Habit(
            id = UUID.randomUUID().toString(),
            title = habitTitle.trim(),
            category = selectedCategory,
            frequency = frequencyType,
            activeDays = activeDays.toList(),
            scheduleTime = "Daily • $reminderTime",
            isNumericGoal = isNumericGoal,
            targetCount = if (isNumericGoal) goalCount else 1,
            currentCount = 0,
            unit = goalUnit,
            isCompletedToday = false,
            streakDays = 1,
            weekMomentum = listOf(false, false, false, false, false, false, false)
          )

          viewModel.addHabit(newHabit)
          isLockedInCelebration = true
          Toast.makeText(context, "Habit locked in! Streak started! 🚀", Toast.LENGTH_SHORT).show()

          coroutineScope.launch {
            delay(1000)
            isLockedInCelebration = false
            onNavigateBack()
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp),
        backgroundColor = buttonColor,
        contentColor = Color.White,
        borderWidth = 2.dp,
        offsetSize = 3.dp,
        testTag = "lock_in_habit_btn"
      )

      // Secondary CTA Button ("Save to Drafts")
      Button(
        onClick = {
          Toast.makeText(context, "Saved to drafts! 📝", Toast.LENGTH_SHORT).show()
          onNavigateBack()
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFFDBE5F3),
          contentColor = Color(0xFF243A5E)
        ),
        shape = RoundedCornerShape(9999.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC7D5E7))
      ) {
        Text(
          text = "Save to Drafts",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}
