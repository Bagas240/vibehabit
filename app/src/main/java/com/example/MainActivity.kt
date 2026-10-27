package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppDestination
import com.example.ui.VibeHabitViewModel
import com.example.ui.components.AddChoiceDialog
import com.example.ui.components.QuickAddTaskDialog
import com.example.ui.components.VibeTopBar
import com.example.ui.navigation.VibeBottomNavigation
import com.example.ui.screens.AddHabitScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.OnboardingNameScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TaskScreen
import com.example.ui.screens.TodayScreen
import com.example.ui.theme.SurfaceCanvas
import com.example.ui.theme.VibeHabitTheme
import com.example.util.AppSecurityIntegrity

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    AppSecurityIntegrity.isPackageIntegrityValid(this)
    setContent {
      VibeHabitTheme {
        val habitViewModel: VibeHabitViewModel = viewModel()
        VibeHabitApp(viewModel = habitViewModel)
      }
    }
  }
}

@Composable
fun VibeHabitApp(
  viewModel: VibeHabitViewModel
) {
  val currentScreen by viewModel.currentScreen.collectAsState()

  var showAddChoiceDialog by remember { mutableStateOf(false) }
  var showAddTaskDialog by remember { mutableStateOf(false) }

  // Handle system back button for sub-screens
  BackHandler(
    enabled = currentScreen != AppDestination.Today &&
        currentScreen != AppDestination.Splash &&
        currentScreen != AppDestination.Onboarding
  ) {
    viewModel.navigateTo(AppDestination.Today)
  }

  val isFullScreen = currentScreen == AppDestination.Splash || currentScreen == AppDestination.Onboarding

  if (isFullScreen) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(SurfaceCanvas)
    ) {
      AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
          (scaleIn(
            animationSpec = spring(
              dampingRatio = Spring.DampingRatioMediumBouncy,
              stiffness = Spring.StiffnessMediumLow
            ),
            initialScale = 0.88f
          ) + fadeIn(animationSpec = tween(240))) togetherWith
          (scaleOut(
            animationSpec = spring(
              dampingRatio = Spring.DampingRatioNoBouncy,
              stiffness = Spring.StiffnessMediumLow
            ),
            targetScale = 1.08f
          ) + fadeOut(animationSpec = tween(180)))
        },
        label = "fullScreenTransition"
      ) { screen ->
        when (screen) {
          AppDestination.Splash -> SplashScreen(viewModel = viewModel)
          AppDestination.Onboarding -> OnboardingNameScreen(viewModel = viewModel)
          else -> Unit
        }
      }
    }
    return
  }

  val screenTitle = when (currentScreen) {
    AppDestination.Today -> "VibeHabit"
    AppDestination.Analytics -> "Analytics"
    AppDestination.Task -> "Tasks"
    AppDestination.You -> "Profile"
    AppDestination.AddHabit -> "Tambah Habit"
    else -> "VibeHabit"
  }

  val isAddScreen = currentScreen == AppDestination.AddHabit

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(SurfaceCanvas)
      .statusBarsPadding(),
    topBar = {
      VibeTopBar(
        currentScreenTitle = screenTitle,
        showBack = isAddScreen,
        onBackClick = { viewModel.navigateTo(AppDestination.Today) },
        onProfileClick = { viewModel.navigateTo(AppDestination.You) }
      )
    },
    bottomBar = {
      if (!isAddScreen) {
        VibeBottomNavigation(
          currentScreen = currentScreen,
          onNavigate = { destination -> viewModel.navigateTo(destination) },
          onAddClick = { showAddChoiceDialog = true }
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(SurfaceCanvas)
    ) {
      AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
          (scaleIn(
            animationSpec = spring(
              dampingRatio = Spring.DampingRatioMediumBouncy,
              stiffness = Spring.StiffnessMediumLow
            ),
            initialScale = 0.88f
          ) + fadeIn(animationSpec = tween(240))) togetherWith
          (scaleOut(
            animationSpec = spring(
              dampingRatio = Spring.DampingRatioNoBouncy,
              stiffness = Spring.StiffnessMediumLow
            ),
            targetScale = 1.08f
          ) + fadeOut(animationSpec = tween(180)))
        },
        label = "screenTransition"
      ) { screen ->
        when (screen) {
          AppDestination.Today -> {
            TodayScreen(
              viewModel = viewModel,
              onAddHabitClick = { viewModel.navigateTo(AppDestination.AddHabit) }
            )
          }
          AppDestination.Analytics -> {
            AnalyticsScreen(viewModel = viewModel)
          }
          AppDestination.Task -> {
            TaskScreen(viewModel = viewModel)
          }
          AppDestination.You -> {
            ProfileScreen(viewModel = viewModel)
          }
          AppDestination.AddHabit -> {
            AddHabitScreen(
              viewModel = viewModel,
              onNavigateBack = { viewModel.navigateTo(AppDestination.Today) }
            )
          }
          else -> Unit
        }
      }
    }
  }

  // 1. Central "+" Choice Dialog (Habit or Task)
  if (showAddChoiceDialog) {
    AddChoiceDialog(
      onDismiss = { showAddChoiceDialog = false },
      onChooseHabit = { viewModel.navigateTo(AppDestination.AddHabit) },
      onChooseTask = { showAddTaskDialog = true }
    )
  }

  // 2. Quick Add Task Dialog from "+" FAB
  if (showAddTaskDialog) {
    QuickAddTaskDialog(
      onDismiss = { showAddTaskDialog = false },
      onTaskCreated = { task -> viewModel.addTask(task) }
    )
  }
}
