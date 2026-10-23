package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.HabitRepository
import com.example.data.model.DailyTask
import com.example.data.model.Habit
import com.example.data.model.HabitCategory
import com.example.data.model.NotificationPersona
import com.example.data.model.SquadMember
import com.example.data.model.Trophy
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

sealed class AppDestination {
  data object Splash : AppDestination()
  data object Onboarding : AppDestination()
  data object Today : AppDestination()
  data object Analytics : AppDestination()
  data object AddHabit : AppDestination()
  data object Task : AppDestination()
  data object You : AppDestination()
}

class VibeHabitViewModel(
  application: Application
) : AndroidViewModel(application) {

  private val repository = HabitRepository(application)

  val isOnboarded: StateFlow<Boolean> = repository.isOnboarded
  val userProfile: StateFlow<UserProfile> = repository.userProfile
  val trophies: StateFlow<List<Trophy>> = repository.trophies
  val squad: StateFlow<List<SquadMember>> = repository.squad
  val tasks: StateFlow<List<DailyTask>> = repository.tasks

  private val _selectedCategory = MutableStateFlow<HabitCategory?>(null)
  val selectedCategory: StateFlow<HabitCategory?> = _selectedCategory.asStateFlow()

  // Starts at Splash
  private val _currentScreen = MutableStateFlow<AppDestination>(AppDestination.Splash)
  val currentScreen: StateFlow<AppDestination> = _currentScreen.asStateFlow()

  val activeHabits: StateFlow<List<Habit>> = combine(repository.habits, _selectedCategory) { habits, category ->
    habits.filter { !it.isPaused && !it.isArchived }
      .filter { category == null || it.category == category }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val pausedOrArchivedHabits: StateFlow<List<Habit>> = repository.habits
    .combine(_selectedCategory) { habits, _ ->
      habits.filter { it.isPaused || it.isArchived }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allActiveHabitsRaw: StateFlow<List<Habit>> = repository.habits
    .combine(_selectedCategory) { habits, _ ->
      habits.filter { !it.isPaused && !it.isArchived }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun navigateTo(destination: AppDestination) {
    _currentScreen.value = destination
  }

  fun onSplashCompleted() {
    if (isOnboarded.value) {
      _currentScreen.value = AppDestination.Today
    } else {
      _currentScreen.value = AppDestination.Onboarding
    }
  }

  fun completeOnboarding(name: String) {
    repository.completeOnboarding(name)
    _currentScreen.value = AppDestination.Today
  }

  fun setCategoryFilter(category: HabitCategory?) {
    _selectedCategory.value = category
  }

  fun toggleHabit(habitId: String) {
    repository.toggleHabitToday(habitId)
  }

  fun incrementProgress(habitId: String) {
    repository.updateHabitProgress(habitId, +1)
  }

  fun decrementProgress(habitId: String) {
    repository.updateHabitProgress(habitId, -1)
  }

  fun toggleMomentum(habitId: String, dayIndex: Int) {
    repository.toggleMomentumSquare(habitId, dayIndex)
  }

  fun addHabit(habit: Habit) {
    repository.addHabit(habit)
  }

  fun pauseHabit(habitId: String) {
    repository.pauseHabit(habitId)
  }

  fun resumeHabit(habitId: String) {
    repository.resumeHabit(habitId)
  }

  fun archiveHabit(habitId: String) {
    repository.archiveHabit(habitId)
  }

  fun setPersona(persona: NotificationPersona) {
    repository.setNotificationPersona(persona)
  }

  fun setSoundHaptics(enabled: Boolean) {
    repository.setSoundHaptics(enabled)
  }

  fun updateVibeTimes(times: List<String>) {
    repository.updateVibeSchedule(times)
  }

  fun updateProfile(name: String, bio: String) {
    repository.updateProfileDetails(name, bio)
  }

  fun toggleTask(taskId: String) {
    repository.toggleTask(taskId)
  }

  fun addTask(task: DailyTask) {
    repository.addTask(task)
  }

  fun deleteTask(taskId: String) {
    repository.deleteTask(taskId)
  }

  fun resetAllData() {
    repository.resetAllData()
    _currentScreen.value = AppDestination.Onboarding
  }
}
