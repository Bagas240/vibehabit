package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SquadMember
import com.example.ui.VibeHabitViewModel
import com.example.ui.components.VibePillBadge
import com.example.ui.components.VibeStickerBox
import com.example.ui.theme.CharcoalInk
import com.example.ui.theme.DeepPrimary
import com.example.ui.theme.InkSubtle
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SoftPastelCell
import com.example.ui.theme.SurfaceCanvas
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TertiaryFixed

@Composable
fun SquadScreen(
  viewModel: VibeHabitViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val squad by viewModel.squad.collectAsState()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(SurfaceCanvas)
      .padding(horizontal = 16.dp, vertical = 12.dp)
      .testTag("squad_screen"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {

    // 1. Squad Sprint Challenge Bento
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
              text = "SQUAD GOAL",
              backgroundColor = SoftPastelCell,
              textColor = DeepPrimary,
              icon = {
                Icon(Icons.Default.Groups, contentDescription = null, tint = DeepPrimary, modifier = Modifier.size(14.dp))
              }
            )
            Text(
              text = "Ends Sunday",
              style = MaterialTheme.typography.labelSmall,
              color = InkSubtle
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Squad Sprint: 500 Habits 🏃‍♂️",
            style = MaterialTheme.typography.headlineMedium,
            color = CharcoalInk,
            fontWeight = FontWeight.ExtraBold
          )

          Text(
            text = "Your squad has crushed 384 of 500 habits this week!",
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
                .fillMaxWidth(384f / 500f)
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
            Text(text = "384 Completed", style = MaterialTheme.typography.labelSmall, color = PrimaryBlue, fontWeight = FontWeight.Bold)
            Text(text = "77% Progress", style = MaterialTheme.typography.labelSmall, color = CharcoalInk, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // 2. Squad Leaderboard Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Squad Leaderboard",
          style = MaterialTheme.typography.headlineSmall,
          color = CharcoalInk,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Top 5 Squadies",
          style = MaterialTheme.typography.labelMedium,
          color = InkSubtle
        )
      }
    }

    // 3. Squad Members
    items(squad, key = { it.id }) { member ->
      SquadMemberRow(
        member = member,
        onSendNudge = {
          Toast.makeText(context, "Hype sent to ${member.name}! ⚡", Toast.LENGTH_SHORT).show()
        }
      )
    }

    item {
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
private fun SquadMemberRow(
  member: SquadMember,
  onSendNudge: () -> Unit
) {
  val isMe = member.id == "s1"
  VibeStickerBox(
    modifier = Modifier.fillMaxWidth(),
    backgroundColor = if (isMe) Color(0xFFF0F4FB) else SurfaceWhite,
    shape = RoundedCornerShape(14.dp),
    offsetSize = if (isMe) 2.5.dp else 1.5.dp,
    borderWidth = if (isMe) 2.dp else 1.5.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.weight(1f)
      ) {
        // Rank Badge
        Box(
          modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(
              when (member.rank) {
                1 -> Color(0xFFFDE68A)
                2 -> Color(0xFFE2E8F0)
                3 -> Color(0xFFFED7AA)
                else -> Color(0xFFE5EBF5)
              }
            )
            .border(1.dp, CharcoalInk, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "#${member.rank}",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = CharcoalInk
          )
        }

        // Avatar
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color(member.avatarBgHex))
            .border(1.5.dp, CharcoalInk, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(text = member.statusEmoji, fontSize = 20.sp)
        }

        Column {
          Text(
            text = member.name,
            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 14.sp),
            color = CharcoalInk,
            fontWeight = FontWeight.Bold
          )
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = "${member.habitsCompleted} habits",
              style = MaterialTheme.typography.bodySmall,
              color = InkSubtle
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(12.dp))
              Text(
                text = "${member.streakDays}d streak",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF92400E),
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      // Hype / Nudge button
      Button(
        onClick = onSendNudge,
        colors = ButtonDefaults.buttonColors(
          containerColor = if (isMe) PrimaryBlue else SurfaceContainerHigh,
          contentColor = if (isMe) Color.White else CharcoalInk
        ),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalInk.copy(alpha = 0.25f)),
        modifier = Modifier.height(34.dp)
      ) {
        Text(
          text = if (isMe) "You ⭐" else "⚡ Hype",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}
