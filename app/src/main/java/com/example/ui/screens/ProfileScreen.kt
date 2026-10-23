package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Habit
import com.example.data.model.HabitCategory
import com.example.data.model.NotificationPersona
import com.example.ui.VibeHabitViewModel
import com.example.ui.components.VibeButton
import com.example.ui.components.VibePillBadge
import com.example.ui.components.VibeStickerBox
import com.example.ui.theme.CharcoalInk
import com.example.ui.theme.DeepPrimary
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.InkSubtle
import com.example.ui.theme.LightPrimaryContainer
import com.example.ui.theme.OnErrorContainer
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryContainer
import com.example.ui.theme.SurfaceCanvas
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TertiaryContainer
import com.example.ui.theme.TertiaryFixed

@Composable
fun ProfileScreen(
  viewModel: VibeHabitViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val profile by viewModel.userProfile.collectAsState()
  val activeHabits by viewModel.activeHabits.collectAsState()
  val pausedHabits by viewModel.pausedOrArchivedHabits.collectAsState()

  var habitVaultTab by remember { mutableStateOf("active") } // "active" or "paused"
  var showEditProfileDialog by remember { mutableStateOf(false) }
  var showExportDialog by remember { mutableStateOf(false) }
  var showPrivacyDialog by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(SurfaceCanvas)
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 12.dp)
      .testTag("profile_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {

    // 1. User Profile Hero Bento
    VibeStickerBox(
      modifier = Modifier.fillMaxWidth(),
      backgroundColor = SurfaceWhite,
      shape = RoundedCornerShape(16.dp),
      offsetSize = 2.dp
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.Top
        ) {
          // Cartoon Avatar Frame with Lv. 7 Badge
          Box(
            modifier = Modifier
              .size(76.dp)
              .testTag("avatar_container")
          ) {
            Box(
              modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(SecondaryContainer)
                .border(2.dp, CharcoalInk, CircleShape)
            ) {
              AsyncImage(
                model = profile.avatarUrl,
                contentDescription = "${profile.name} Avatar",
                modifier = Modifier.fillMaxSize()
              )
            }
            // Lv. 7 Badge
            Box(
              modifier = Modifier
                .align(Alignment.BottomEnd)
                .clip(RoundedCornerShape(9999.dp))
                .background(TertiaryFixed)
                .border(1.dp, CharcoalInk, RoundedCornerShape(9999.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "Lv. ${profile.level}",
                style = MaterialTheme.typography.labelSmall,
                color = CharcoalInk,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.width(14.dp))

          // Bio & Handle Details
          Column(modifier = Modifier.weight(1f)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = profile.name,
                style = MaterialTheme.typography.headlineSmall,
                color = CharcoalInk,
                fontWeight = FontWeight.Bold
              )
              VibePillBadge(
                text = profile.titleBadge,
                backgroundColor = LightPrimaryContainer,
                textColor = DeepPrimary
              )
            }

            Text(
              text = profile.handle,
              style = MaterialTheme.typography.labelMedium,
              color = InkSubtle,
              modifier = Modifier.padding(top = 2.dp)
            )

            Text(
              text = profile.bio,
              style = MaterialTheme.typography.bodySmall,
              color = CharcoalInk,
              modifier = Modifier.padding(top = 6.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Profile Actions
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Edit Profile
          VibeButton(
            text = "Edit Profile",
            onClick = { showEditProfileDialog = true },
            modifier = Modifier.weight(1f),
            backgroundColor = SurfaceContainerHigh,
            contentColor = CharcoalInk,
            icon = {
              Icon(Icons.Default.Edit, contentDescription = null, tint = CharcoalInk, modifier = Modifier.size(16.dp))
            },
            offsetSize = 0.dp,
            shape = RoundedCornerShape(10.dp),
            testTag = "edit_profile_btn"
          )

          // Share Card
          VibeButton(
            text = "Share Card",
            onClick = {
              Toast.makeText(context, "${profile.name}'s Vibe Card copied to share! 🚀", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.weight(1f),
            backgroundColor = PrimaryBlue,
            contentColor = Color.White,
            icon = {
              Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            },
            offsetSize = 0.dp,
            shape = RoundedCornerShape(10.dp),
            testTag = "share_card_btn"
          )
        }
      }
    }

    // 2. Lifetime Impact Stats (Flat Solid Metric Grid)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Metric 1: Habits Crushed
      VibeStickerBox(
        modifier = Modifier.weight(1f),
        backgroundColor = SurfaceWhite,
        shape = RoundedCornerShape(14.dp),
        offsetSize = 2.dp
      ) {
        Column(
          modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(SecondaryContainer)
              .border(1.dp, CharcoalInk.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.DoneAll, contentDescription = null, tint = DeepPrimary, modifier = Modifier.size(18.dp))
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "${profile.habitsCrushed}",
            style = MaterialTheme.typography.headlineSmall,
            color = CharcoalInk,
            fontWeight = FontWeight.ExtraBold
          )
          Text(
            text = "Habits Crushed",
            style = MaterialTheme.typography.labelSmall,
            color = InkSubtle
          )
        }
      }

      // Metric 2: Focus Logged
      VibeStickerBox(
        modifier = Modifier.weight(1f),
        backgroundColor = SurfaceWhite,
        shape = RoundedCornerShape(14.dp),
        offsetSize = 2.dp
      ) {
        Column(
          modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(TertiaryFixed)
              .border(1.dp, CharcoalInk.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.HourglassTop, contentDescription = null, tint = CharcoalInk, modifier = Modifier.size(18.dp))
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "${profile.focusHoursLogged} hrs",
            style = MaterialTheme.typography.headlineSmall,
            color = CharcoalInk,
            fontWeight = FontWeight.ExtraBold
          )
          Text(
            text = "Focus Logged",
            style = MaterialTheme.typography.labelSmall,
            color = InkSubtle
          )
        }
      }

      // Metric 3: Longest Streak
      VibeStickerBox(
        modifier = Modifier.weight(1f),
        backgroundColor = SurfaceWhite,
        shape = RoundedCornerShape(14.dp),
        offsetSize = 2.dp
      ) {
        Column(
          modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(LightPrimaryContainer)
              .border(1.dp, CharcoalInk.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "${profile.longestStreakDays} Days",
            style = MaterialTheme.typography.headlineSmall,
            color = CharcoalInk,
            fontWeight = FontWeight.ExtraBold
          )
          Text(
            text = "Longest Streak",
            style = MaterialTheme.typography.labelSmall,
            color = InkSubtle
          )
        }
      }
    }

    // 3. Habit Vault & Active Bank
    VibeStickerBox(
      modifier = Modifier.fillMaxWidth(),
      backgroundColor = SurfaceWhite,
      shape = RoundedCornerShape(16.dp),
      offsetSize = 2.dp
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Habit Vault",
            style = MaterialTheme.typography.headlineSmall,
            color = CharcoalInk,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "${activeHabits.size + pausedHabits.size} Total",
            style = MaterialTheme.typography.labelMedium,
            color = InkSubtle
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Segmented Tab Switcher
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceContainer)
            .border(1.dp, CharcoalInk.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
            .padding(4.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(if (habitVaultTab == "active") PrimaryBlue else Color.Transparent)
              .clickable { habitVaultTab = "active" }
              .padding(vertical = 8.dp)
              .testTag("tab_active_habits"),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Active (${activeHabits.size})",
              style = MaterialTheme.typography.labelMedium,
              color = if (habitVaultTab == "active") Color.White else InkSubtle,
              fontWeight = FontWeight.Bold
            )
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(if (habitVaultTab == "paused") PrimaryBlue else Color.Transparent)
              .clickable { habitVaultTab = "paused" }
              .padding(vertical = 8.dp)
              .testTag("tab_paused_habits"),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Paused / Archive (${pausedHabits.size})",
              style = MaterialTheme.typography.labelMedium,
              color = if (habitVaultTab == "paused") Color.White else InkSubtle,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Habit Items
        if (habitVaultTab == "active") {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            activeHabits.forEach { habit ->
              ActiveHabitCard(
                habit = habit,
                onPause = { viewModel.pauseHabit(habit.id) },
                onArchive = { viewModel.archiveHabit(habit.id) }
              )
            }
          }
        } else {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            pausedHabits.forEach { habit ->
              PausedHabitCard(
                habit = habit,
                onResume = { viewModel.resumeHabit(habit.id) }
              )
            }
          }
        }
      }
    }

    // 4. Vibe & Notification Reminders
    VibeStickerBox(
      modifier = Modifier.fillMaxWidth(),
      backgroundColor = SurfaceWhite,
      shape = RoundedCornerShape(16.dp),
      offsetSize = 2.dp
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            Icons.Default.NotificationsActive,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier.size(20.dp)
          )
          Text(
            text = "Vibe & Notification Reminders",
            style = MaterialTheme.typography.headlineSmall,
            color = CharcoalInk,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "Notification Persona",
          style = MaterialTheme.typography.labelMedium,
          color = InkSubtle
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Tone options grid
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Bestie Hype
          val isHype = profile.persona == NotificationPersona.HYPE
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(if (isHype) PrimaryBlue else SurfaceContainerHigh)
              .border(1.dp, CharcoalInk.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
              .clickable { viewModel.setPersona(NotificationPersona.HYPE) }
              .padding(10.dp)
              .testTag("persona_hype")
          ) {
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Bestie Hype ⚡",
                  style = MaterialTheme.typography.headlineSmall.copy(fontSize = 14.sp),
                  color = if (isHype) Color.White else CharcoalInk,
                  fontWeight = FontWeight.Bold
                )
                if (isHype) {
                  Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "\"Lock in! Don't let your streak slip!\"",
                style = MaterialTheme.typography.labelSmall,
                color = if (isHype) Color.White.copy(alpha = 0.9f) else InkSubtle
              )
            }
          }

          // Gentle Whisper
          val isWhisper = profile.persona == NotificationPersona.WHISPER
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(if (isWhisper) PrimaryBlue else SurfaceContainerHigh)
              .border(1.dp, CharcoalInk.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
              .clickable { viewModel.setPersona(NotificationPersona.WHISPER) }
              .padding(10.dp)
              .testTag("persona_whisper")
          ) {
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Gentle Whisper 🍃",
                  style = MaterialTheme.typography.headlineSmall.copy(fontSize = 14.sp),
                  color = if (isWhisper) Color.White else CharcoalInk,
                  fontWeight = FontWeight.Bold
                )
                if (isWhisper) {
                  Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "\"Take your time, breathe and reset.\"",
                style = MaterialTheme.typography.labelSmall,
                color = if (isWhisper) Color.White.copy(alpha = 0.9f) else InkSubtle
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Vibe Check Schedule Row
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceContainerLow)
            .border(1.dp, CharcoalInk.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
            .padding(12.dp),
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
                .background(SurfaceContainer)
                .border(1.dp, CharcoalInk.copy(alpha = 0.2f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "⏰", fontSize = 16.sp)
            }
            Column {
              Text(
                text = "Vibe Check Schedule",
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 14.sp),
                color = CharcoalInk,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Twice daily reminders",
                style = MaterialTheme.typography.bodySmall,
                color = InkSubtle
              )
            }
          }

          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            profile.vibeCheckTimes.forEach { time ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(SurfaceContainerHigh)
                  .border(1.dp, CharcoalInk.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = time,
                  style = MaterialTheme.typography.labelSmall,
                  color = CharcoalInk,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }
    }

    // 5. Preferences & Data Support
    VibeStickerBox(
      modifier = Modifier.fillMaxWidth(),
      backgroundColor = SurfaceWhite,
      shape = RoundedCornerShape(16.dp),
      offsetSize = 2.dp
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "Preferences & Data",
          style = MaterialTheme.typography.headlineSmall,
          color = CharcoalInk,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Item 1: Sound & Haptics
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(Icons.Default.Vibration, contentDescription = null, tint = InkSubtle, modifier = Modifier.size(20.dp))
            Text(
              text = "Sound & Haptics",
              style = MaterialTheme.typography.bodyLarge,
              color = CharcoalInk
            )
          }
          Switch(
            checked = profile.soundHapticsEnabled,
            onCheckedChange = { viewModel.setSoundHaptics(it) },
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color.White,
              checkedTrackColor = PrimaryBlue,
              uncheckedThumbColor = Color.White,
              uncheckedTrackColor = SurfaceContainerHigh
            )
          )
        }

        // Item 2: Theme
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(text = "🎨", fontSize = 18.sp)
            Text(
              text = profile.currentTheme,
              style = MaterialTheme.typography.bodyLarge,
              color = CharcoalInk
            )
          }
          VibePillBadge(
            text = "Default",
            backgroundColor = SecondaryContainer,
            textColor = DeepPrimary
          )
        }

        // Item 3: Export Habit Data (CSV)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { showExportDialog = true }
            .padding(vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(text = "📥", fontSize = 18.sp)
            Text(
              text = "Export Habit Data (CSV)",
              style = MaterialTheme.typography.bodyLarge,
              color = CharcoalInk
            )
          }
          Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = InkSubtle, modifier = Modifier.size(14.dp))
        }

        // Item 4: Privacy & Local Storage
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { showPrivacyDialog = true }
            .padding(vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(Icons.Default.Shield, contentDescription = null, tint = InkSubtle, modifier = Modifier.size(20.dp))
            Text(
              text = "Privacy & Local Storage",
              style = MaterialTheme.typography.bodyLarge,
              color = CharcoalInk
            )
          }
          Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = InkSubtle, modifier = Modifier.size(14.dp))
        }
      }
    }

    // 6. Log Out & Version Footer
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Button(
        onClick = {
          Toast.makeText(context, "Logged out of VibeHabit demo profile", Toast.LENGTH_SHORT).show()
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = SurfaceContainerHigh,
          contentColor = CharcoalInk
        ),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, CharcoalInk.copy(alpha = 0.3f))
      ) {
        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "Log Out", style = MaterialTheme.typography.labelLarge)
      }

      Text(
        text = "VibeHabit Version 2.4.0 (Build 384)",
        style = MaterialTheme.typography.labelSmall,
        color = InkSubtle
      )
    }
  }

  // Edit Profile Dialog
  if (showEditProfileDialog) {
    var editName by remember { mutableStateOf(profile.name) }
    var editBio by remember { mutableStateOf(profile.bio) }

    AlertDialog(
      onDismissRequest = { showEditProfileDialog = false },
      title = { Text("Edit Profile", fontWeight = FontWeight.Bold, color = CharcoalInk) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = editName,
            onValueChange = { editName = it },
            label = { Text("Display Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = editBio,
            onValueChange = { editBio = it },
            label = { Text("Bio") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.updateProfile(editName, editBio)
            showEditProfileDialog = false
            Toast.makeText(context, "Profile updated!", Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
          Text("Save")
        }
      },
      dismissButton = {
        TextButton(onClick = { showEditProfileDialog = false }) {
          Text("Cancel", color = InkSubtle)
        }
      }
    )
  }

  // Export CSV Dialog
  if (showExportDialog) {
    AlertDialog(
      onDismissRequest = { showExportDialog = false },
      title = { Text("Export Habit Data (CSV)", fontWeight = FontWeight.Bold) },
      text = {
        Text("Your habits, streaks, and check-in logs are formatted into standard CSV. Ready to export or back up locally.")
      },
      confirmButton = {
        Button(
          onClick = {
            showExportDialog = false
            Toast.makeText(context, "Habit records exported to CSV successfully!", Toast.LENGTH_LONG).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
          Text("Download CSV")
        }
      },
      dismissButton = {
        TextButton(onClick = { showExportDialog = false }) {
          Text("Close")
        }
      }
    )
  }

  // Privacy Dialog
  if (showPrivacyDialog) {
    AlertDialog(
      onDismissRequest = { showPrivacyDialog = false },
      title = { Text("Privacy & Local Storage", fontWeight = FontWeight.Bold) },
      text = {
        Text("VibeHabit operates completely private and secure. Your habit logs, streaks, and notifications remain strictly in your local vault.")
      },
      confirmButton = {
        Button(
          onClick = { showPrivacyDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
          Text("Got it")
        }
      }
    )
  }
}

@Composable
private fun ActiveHabitCard(
  habit: Habit,
  onPause: () -> Unit,
  onArchive: () -> Unit
) {
  var menuExpanded by remember { mutableStateOf(false) }

  Row(
    modifier = Modifier
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
      // Category Icon
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
        Text(text = habit.category.emoji, fontSize = 18.sp)
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
      // Pause button
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

      // More menu button
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
          Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = CharcoalInk, modifier = Modifier.size(16.dp))
        }

        DropdownMenu(
          expanded = menuExpanded,
          onDismissRequest = { menuExpanded = false }
        ) {
          DropdownMenuItem(
            text = { Text("Pause Habit") },
            onClick = {
              onPause()
              menuExpanded = false
            }
          )
          DropdownMenuItem(
            text = { Text("Archive Habit") },
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
private fun PausedHabitCard(
  habit: Habit,
  onResume: () -> Unit
) {
  Row(
    modifier = Modifier
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
              text = if (habit.isArchived) "Archived" else "Paused",
              style = MaterialTheme.typography.labelSmall,
              color = InkSubtle
            )
          }
          Text(
            text = habit.statusNote ?: "Inactive",
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
        text = if (habit.isArchived) "Unarchive" else "Resume",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold
      )
    }
  }
}
