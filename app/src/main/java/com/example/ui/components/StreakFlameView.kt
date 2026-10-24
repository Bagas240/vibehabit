package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CharcoalInk
import com.example.ui.theme.InkSubtle
import com.example.ui.theme.SurfaceWhite
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// Flame color stages by streak days
object FlameTheme {
  fun getColors(streakDays: Int, isActive: Boolean): Triple<Color, Color, Color> {
    if (!isActive || streakDays <= 0) {
      // Extinguished / Dormant ash gray
      return Triple(Color(0xFF8E8E93), Color(0xFF636366), Color(0xFF48484A))
    }
    return when {
      streakDays in 1..3 -> Triple(Color(0xFFFFB300), Color(0xFFFF6D00), Color(0xFFFF3D00)) // Warm Amber / Orange
      streakDays in 4..7 -> Triple(Color(0xFFFF5252), Color(0xFFFF1744), Color(0xFFD50000)) // Crimson Red
      streakDays in 8..14 -> Triple(Color(0xFFE040FB), Color(0xFFAA00FF), Color(0xFF6A1B9A)) // Mystical Purple
      streakDays in 15..29 -> Triple(Color(0xFF18FFFF), Color(0xFF00B0FF), Color(0xFF2979FF)) // Cosmic Electric Blue
      else -> Triple(Color(0xFFFFEA00), Color(0xFFFF9100), Color(0xFFFF3D00)) // Solar Gold (30+)
    }
  }

  fun getFlameName(streakDays: Int, isActive: Boolean): String {
    if (!isActive || streakDays <= 0) return "Padam (Mati Api)"
    return when {
      streakDays in 1..3 -> "Orange Ember Flame 🔥"
      streakDays in 4..7 -> "Crimson Blaze Flame 💥"
      streakDays in 8..14 -> "Mystic Violet Flame 💜"
      streakDays in 15..29 -> "Cosmic Cyan Flame ⚡"
      else -> "Prismatic Gold Flame 👑"
    }
  }
}

/**
 * Animated Flame Composable with dynamic streak colors, ignition burst,
 * and bold number centered in the flame.
 */
@Composable
fun StreakFlameView(
  streakDays: Int,
  isActive: Boolean,
  isIgniting: Boolean = false,
  flameSize: Dp = 100.dp,
  onClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val (topColor, midColor, botColor) = FlameTheme.getColors(streakDays, isActive)

  val animatedTopColor by animateColorAsState(targetValue = topColor, animationSpec = tween(600), label = "topColor")
  val animatedMidColor by animateColorAsState(targetValue = midColor, animationSpec = tween(600), label = "midColor")
  val animatedBotColor by animateColorAsState(targetValue = botColor, animationSpec = tween(600), label = "botColor")

  // Ignition pop scale
  val ignitionScale = remember { Animatable(1f) }
  val sparkAlpha = remember { Animatable(0f) }

  LaunchedEffect(isIgniting) {
    if (isIgniting) {
      sparkAlpha.snapTo(1f)
      ignitionScale.animateTo(
        targetValue = 1.32f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
      )
      ignitionScale.animateTo(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
      )
      sparkAlpha.animateTo(0f, animationSpec = tween(600))
    }
  }

  // Gentle organic flicker/breathing (lightweight, battery-efficient)
  val infiniteTransition = rememberInfiniteTransition(label = "flameAlive")
  val breatheScale by infiniteTransition.animateFloat(
    initialValue = 0.98f,
    targetValue = 1.03f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "breatheScale"
  )
  val tipSway by infiniteTransition.animateFloat(
    initialValue = -3f,
    targetValue = 3f,
    animationSpec = infiniteRepeatable(
      animation = tween(900, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "tipSway"
  )

  val totalScale = ignitionScale.value * (if (isActive) breatheScale else 1f)

  Box(
    modifier = modifier
      .size(flameSize)
      .scale(totalScale)
      .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
    contentAlignment = Alignment.Center
  ) {
    // 1. Canvas drawing of the stylized flame
    Canvas(modifier = Modifier.size(flameSize)) {
      val w = size.width
      val h = size.height

      // Ignition outer shockwave / aura glow
      if (sparkAlpha.value > 0.01f) {
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(animatedTopColor.copy(alpha = 0.5f * sparkAlpha.value), Color.Transparent),
            center = Offset(w / 2f, h * 0.55f),
            radius = w * 0.7f
          ),
          radius = w * 0.7f,
          center = Offset(w / 2f, h * 0.55f)
        )

        // Draw shooting sparks
        val sparkCount = 12
        for (i in 0 until sparkCount) {
          val angle = (i * 360f / sparkCount) * (Math.PI / 180f)
          val dist = (w * 0.48f) + (sparkAlpha.value * w * 0.25f)
          val sx = (w / 2f) + (dist * cos(angle)).toFloat()
          val sy = (h * 0.55f) + (dist * sin(angle)).toFloat()
          drawCircle(
            color = animatedTopColor.copy(alpha = sparkAlpha.value),
            radius = 3.5.dp.toPx(),
            center = Offset(sx, sy)
          )
        }
      }

      // Outer Flame Base Path
      val flamePath = Path()
      val cx = w / 2f
      val tipX = cx + (if (isActive) tipSway * 1.5f else 0f)

      flamePath.moveTo(tipX, h * 0.04f) // Flame tip

      // Right curvature
      flamePath.cubicTo(
        w * 0.88f, h * 0.28f,
        w * 0.98f, h * 0.65f,
        cx + w * 0.28f, h * 0.94f
      )

      // Bottom bowl curve
      flamePath.cubicTo(
        cx + w * 0.15f, h * 0.99f,
        cx - w * 0.15f, h * 0.99f,
        cx - w * 0.28f, h * 0.94f
      )

      // Left curvature back to tip
      flamePath.cubicTo(
        w * 0.02f, h * 0.65f,
        w * 0.12f, h * 0.28f,
        tipX, h * 0.04f
      )
      flamePath.close()

      // Gradient Fill for outer flame
      val outerBrush = Brush.verticalGradient(
        colors = listOf(animatedTopColor, animatedMidColor, animatedBotColor),
        startY = 0f,
        endY = h
      )
      drawPath(path = flamePath, brush = outerBrush)

      // Crisp cartoon border
      drawPath(
        path = flamePath,
        color = CharcoalInk,
        style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
      )

      // Inner Core Flame Path
      val innerPath = Path()
      val inTipX = cx + (if (isActive) tipSway * 0.8f else 0f)
      innerPath.moveTo(inTipX, h * 0.38f)
      innerPath.cubicTo(
        w * 0.72f, h * 0.52f,
        w * 0.78f, h * 0.75f,
        cx + w * 0.18f, h * 0.90f
      )
      innerPath.cubicTo(
        cx + w * 0.08f, h * 0.93f,
        cx - w * 0.08f, h * 0.93f,
        cx - w * 0.18f, h * 0.90f
      )
      innerPath.cubicTo(
        w * 0.22f, h * 0.75f,
        w * 0.28f, h * 0.52f,
        inTipX, h * 0.38f
      )
      innerPath.close()

      val innerColor = if (isActive && streakDays > 0) Color.White.copy(alpha = 0.55f) else Color(0xFFD1D5DB).copy(alpha = 0.4f)
      drawPath(path = innerPath, color = innerColor)
      drawPath(
        path = innerPath,
        color = CharcoalInk.copy(alpha = 0.35f),
        style = Stroke(width = 1.2.dp.toPx())
      )
    }

    // 2. Bold Streak Number in the Center of the Flame
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.padding(top = (flameSize * 0.22f))
    ) {
      Text(
        text = if (isActive && streakDays > 0) "$streakDays" else "0",
        fontSize = (flameSize.value * 0.32f).sp,
        fontWeight = FontWeight.Black,
        color = Color.White
      )
      Text(
        text = if (isActive && streakDays > 0) "DAY" else "DEAD",
        fontSize = (flameSize.value * 0.11f).sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color.White.copy(alpha = 0.92f),
        letterSpacing = 1.sp
      )
    }
  }
}

/**
 * Full Bento Streak Header Card: displays the flame illustration,
 * the streak level name, 24-hour status countdown/indicator, and ignition prompt.
 */
@Composable
fun StreakHeroCard(
  streakDays: Int,
  isActive: Boolean,
  hoursLeft: Int = 24,
  isIgniting: Boolean = false,
  onIgniteClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val flameName = FlameTheme.getFlameName(streakDays, isActive)
  val (topColor, _, _) = FlameTheme.getColors(streakDays, isActive)

  VibeStickerBox(
    modifier = modifier.fillMaxWidth(),
    backgroundColor = SurfaceWhite,
    shape = RoundedCornerShape(16.dp),
    offsetSize = 3.dp,
    borderWidth = 2.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Left side: Streak info details
      Column(modifier = Modifier.weight(1f)) {
        // Status Pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(if (isActive && streakDays > 0) topColor.copy(alpha = 0.18f) else Color(0xFFE5E7EB))
            .border(1.dp, CharcoalInk.copy(alpha = 0.25f), RoundedCornerShape(9999.dp))
            .padding(horizontal = 10.dp, vertical = 3.dp)
        ) {
          Text(
            text = if (isActive && streakDays > 0) "🔥 ACTIVE STREAK" else "❄️ STREAK EXTINCTION",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = if (isActive && streakDays > 0) CharcoalInk else Color(0xFF4B5563)
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = if (isActive && streakDays > 0) "Day $streakDays Streak" else "Streak Padam",
          style = MaterialTheme.typography.headlineMedium,
          color = CharcoalInk,
          fontWeight = FontWeight.ExtraBold
        )

        Text(
          text = flameName,
          style = MaterialTheme.typography.labelMedium,
          color = if (isActive && streakDays > 0) topColor else InkSubtle,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = if (isActive && streakDays > 0) {
            "Aktivitas dicatat hari ini. Selesaikan tugas atau habit untuk memperbesar apimu!"
          } else {
            "Sudah >24 jam tanpa aktivitas! Selesaikan habit atau task untuk menyalakan api kembali!"
          },
          style = MaterialTheme.typography.bodySmall,
          color = InkSubtle,
          lineHeight = 16.sp
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Right side: The Animated Flame View
      StreakFlameView(
        streakDays = streakDays,
        isActive = isActive,
        isIgniting = isIgniting,
        flameSize = 92.dp,
        onClick = onIgniteClick
      )
    }
  }
}
