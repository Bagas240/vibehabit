package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.DailyTask
import com.example.data.model.FrequencyType
import com.example.data.model.Habit
import com.example.data.model.HabitCategory
import com.example.data.model.NotificationPersona
import com.example.data.model.SquadMember
import com.example.data.model.Trophy
import com.example.data.model.UserProfile
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HabitRepository(context: Context) {

  private val prefs: SharedPreferences =
    (context.applicationContext ?: context).getSharedPreferences("vibe_habit_prefs", Context.MODE_PRIVATE)

  private val moshi: Moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .build()

  private val habitListType = Types.newParameterizedType(List::class.java, Habit::class.java)
  private val habitAdapter = moshi.adapter<List<Habit>>(habitListType)

  private val taskListType = Types.newParameterizedType(List::class.java, DailyTask::class.java)
  private val taskAdapter = moshi.adapter<List<DailyTask>>(taskListType)

  private val _isOnboarded = MutableStateFlow(prefs.getBoolean("key_onboarded", false))
  val isOnboarded: StateFlow<Boolean> = _isOnboarded.asStateFlow()

  private val _habits = MutableStateFlow<List<Habit>>(loadHabits())
  val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

  private val _tasks = MutableStateFlow<List<DailyTask>>(loadTasks())
  val tasks: StateFlow<List<DailyTask>> = _tasks.asStateFlow()

  private val _userProfile = MutableStateFlow(loadProfile())
  val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

  private val _trophies = MutableStateFlow<List<Trophy>>(loadTrophies())
  val trophies: StateFlow<List<Trophy>> = _trophies.asStateFlow()

  private val _squad = MutableStateFlow<List<SquadMember>>(loadSquad())
  val squad: StateFlow<List<SquadMember>> = _squad.asStateFlow()

  fun completeOnboarding(name: String) {
    val cleanName = name.trim().ifBlank { "Habit Hero" }
    val handle = "@" + cleanName.lowercase().replace(" ", "_")
    prefs.edit()
      .putBoolean("key_onboarded", true)
      .putString("user_name", cleanName)
      .putString("user_handle", handle)
      .apply()

    _isOnboarded.value = true
    _userProfile.update { it.copy(name = cleanName, handle = handle) }
    saveProfile(_userProfile.value)
  }

  fun toggleHabitToday(habitId: String) {
    _habits.update { list ->
      list.map { habit ->
        if (habit.id == habitId) {
          val newCompleted = !habit.isCompletedToday
          val newCount = if (newCompleted) habit.targetCount else 0
          val streakDelta = if (newCompleted) 1 else -1
          val updatedStreak = maxOf(0, habit.streakDays + streakDelta)
          val newMomentum = habit.weekMomentum.toMutableList()
          if (newMomentum.isNotEmpty()) {
            newMomentum[newMomentum.lastIndex] = newCompleted
          }
          habit.copy(
            isCompletedToday = newCompleted,
            currentCount = newCount,
            streakDays = updatedStreak,
            weekMomentum = newMomentum
          )
        } else habit
      }
    }
    saveHabits(_habits.value)
    recalculateProfileStats()
  }

  fun updateHabitProgress(habitId: String, delta: Int) {
    _habits.update { list ->
      list.map { habit ->
        if (habit.id == habitId) {
          val next = maxOf(0, habit.currentCount + delta)
          val isNowDone = next >= habit.targetCount && habit.targetCount > 0
          val newMomentum = habit.weekMomentum.toMutableList()
          if (newMomentum.isNotEmpty()) {
            newMomentum[newMomentum.lastIndex] = isNowDone
          }
          habit.copy(
            currentCount = next,
            isCompletedToday = isNowDone,
            weekMomentum = newMomentum
          )
        } else habit
      }
    }
    saveHabits(_habits.value)
    recalculateProfileStats()
  }

  fun toggleMomentumSquare(habitId: String, dayIndex: Int) {
    _habits.update { list ->
      list.map { habit ->
        if (habit.id == habitId && dayIndex in habit.weekMomentum.indices) {
          val nextMomentum = habit.weekMomentum.toMutableList()
          nextMomentum[dayIndex] = !nextMomentum[dayIndex]
          habit.copy(weekMomentum = nextMomentum)
        } else habit
      }
    }
    saveHabits(_habits.value)
  }

  fun addHabit(newHabit: Habit) {
    _habits.update { listOf(newHabit) + it }
    saveHabits(_habits.value)
    recalculateProfileStats()
  }

  fun pauseHabit(habitId: String) {
    _habits.update { list ->
      list.map { habit ->
        if (habit.id == habitId) {
          habit.copy(isPaused = true, statusNote = "Paused today")
        } else habit
      }
    }
    saveHabits(_habits.value)
  }

  fun resumeHabit(habitId: String) {
    _habits.update { list ->
      list.map { habit ->
        if (habit.id == habitId) {
          habit.copy(isPaused = false, isArchived = false, statusNote = null)
        } else habit
      }
    }
    saveHabits(_habits.value)
  }

  fun archiveHabit(habitId: String) {
    _habits.update { list ->
      list.map { habit ->
        if (habit.id == habitId) {
          habit.copy(isArchived = true, statusNote = "Archived")
        } else habit
      }
    }
    saveHabits(_habits.value)
  }

  fun toggleTask(taskId: String) {
    _tasks.update { list ->
      list.map { task ->
        if (task.id == taskId) {
          task.copy(isCompleted = !task.isCompleted)
        } else task
      }
    }
    saveTasks(_tasks.value)
  }

  fun addTask(task: DailyTask) {
    _tasks.update { listOf(task) + it }
    saveTasks(_tasks.value)
  }

  fun deleteTask(taskId: String) {
    _tasks.update { list -> list.filter { it.id != taskId } }
    saveTasks(_tasks.value)
  }

  fun setNotificationPersona(persona: NotificationPersona) {
    _userProfile.update { it.copy(persona = persona) }
    saveProfile(_userProfile.value)
  }

  fun setSoundHaptics(enabled: Boolean) {
    _userProfile.update { it.copy(soundHapticsEnabled = enabled) }
    saveProfile(_userProfile.value)
  }

  fun updateVibeSchedule(times: List<String>) {
    _userProfile.update { it.copy(vibeCheckTimes = times) }
    saveProfile(_userProfile.value)
  }

  fun updateProfileDetails(name: String, bio: String) {
    val cleanName = name.trim().ifBlank { _userProfile.value.name }
    val handle = "@" + cleanName.lowercase().replace(" ", "_")
    _userProfile.update { it.copy(name = cleanName, handle = handle, bio = bio) }
    saveProfile(_userProfile.value)
  }

  fun resetAllData() {
    prefs.edit().clear().apply()
    _isOnboarded.value = false
    _habits.value = emptyList()
    _tasks.value = emptyList()
    _userProfile.value = UserProfile(name = "Bestie", handle = "@bestie", habitsCrushed = 0, longestStreakDays = 0, focusHoursLogged = 0)
  }

  private fun recalculateProfileStats() {
    val completedCount = _habits.value.count { it.isCompletedToday && !it.isPaused && !it.isArchived }
    val maxStreak = _habits.value.maxOfOrNull { it.streakDays } ?: 0
    val totalCrushed = prefs.getInt("key_total_crushed", 0) + (if (completedCount > 0) 1 else 0)

    _userProfile.update { current ->
      current.copy(
        habitsCrushed = completedCount,
        longestStreakDays = maxStreak
      )
    }
    saveProfile(_userProfile.value)
  }

  // Persistent storage helpers
  private fun saveHabits(list: List<Habit>) {
    try {
      val json = habitAdapter.toJson(list)
      prefs.edit().putString("key_habits_json", json).apply()
    } catch (_: Exception) {}
  }

  private fun loadHabits(): List<Habit> {
    val json = prefs.getString("key_habits_json", null)
    if (!json.isNullOrBlank()) {
      try {
        return habitAdapter.fromJson(json) ?: emptyList()
      } catch (_: Exception) {}
    }
    return emptyList() // Clean start for real user tracking!
  }

  private fun saveTasks(list: List<DailyTask>) {
    try {
      val json = taskAdapter.toJson(list)
      prefs.edit().putString("key_tasks_json", json).apply()
    } catch (_: Exception) {}
  }

  private fun loadTasks(): List<DailyTask> {
    val json = prefs.getString("key_tasks_json", null)
    if (!json.isNullOrBlank()) {
      try {
        return taskAdapter.fromJson(json) ?: emptyList()
      } catch (_: Exception) {}
    }
    return emptyList() // Clean start for real user tracking!
  }

  private fun saveProfile(profile: UserProfile) {
    prefs.edit()
      .putString("user_name", profile.name)
      .putString("user_handle", profile.handle)
      .putString("user_bio", profile.bio)
      .putInt("user_level", profile.level)
      .putInt("user_crushed", profile.habitsCrushed)
      .putInt("user_streak", profile.longestStreakDays)
      .putBoolean("user_sound", profile.soundHapticsEnabled)
      .putString("user_persona", profile.persona.name)
      .apply()
  }

  private fun loadProfile(): UserProfile {
    val name = prefs.getString("user_name", "Bestie") ?: "Bestie"
    val handle = prefs.getString("user_handle", "@bestie") ?: "@bestie"
    val bio = prefs.getString("user_bio", "Building atomic habits one day at a time 🌿") ?: ""
    val level = prefs.getInt("user_level", 1)
    val crushed = prefs.getInt("user_crushed", 0)
    val streak = prefs.getInt("user_streak", 0)
    val sound = prefs.getBoolean("user_sound", true)
    val personaStr = prefs.getString("user_persona", "HYPE") ?: "HYPE"
    val persona = try { NotificationPersona.valueOf(personaStr) } catch (_: Exception) { NotificationPersona.HYPE }

    return UserProfile(
      name = name,
      handle = handle,
      titleBadge = "Habit Starter ⭐",
      bio = bio,
      level = level,
      habitsCrushed = crushed,
      focusHoursLogged = crushed * 2,
      longestStreakDays = streak,
      persona = persona,
      soundHapticsEnabled = sound
    )
  }

  private fun loadTrophies(): List<Trophy> {
    val habitsList = _habits?.value ?: emptyList()
    val tasksList = _tasks?.value ?: emptyList()
    val completedCount = habitsList.count { it.isCompletedToday }
    val maxStreak = habitsList.maxOfOrNull { it.streakDays } ?: 0
    val tasksDone = tasksList.count { it.isCompleted }

    return listOf(
      Trophy(
        id = "t1",
        title = "First Step 🌱",
        description = "Create and lock in your first habit.",
        iconType = "eco",
        isUnlocked = habitsList.isNotEmpty()
      ),
      Trophy(
        id = "t2",
        title = "Streak Starter 🔥",
        description = "Maintain an active streak of 3+ days.",
        iconType = "local_fire_department",
        isUnlocked = maxStreak >= 3,
        progressPercent = minOf(100, (maxStreak * 100) / 3),
        progressText = "$maxStreak/3 days"
      ),
      Trophy(
        id = "t3",
        title = "Lock In Master 🔒",
        description = "Complete all habits for the day.",
        iconType = "lock",
        isUnlocked = completedCount > 0 && completedCount == habitsList.size
      ),
      Trophy(
        id = "t4",
        title = "Task Slayer 🎯",
        description = "Complete 5 daily tasks in Task Vault.",
        iconType = "self_improvement",
        isUnlocked = tasksDone >= 5,
        progressPercent = minOf(100, (tasksDone * 100) / 5),
        progressText = "$tasksDone/5 tasks"
      )
    )
  }

  private fun loadSquad(): List<SquadMember> = listOf(
    SquadMember("s1", "You ⭐", "@you", 1, 0, 0, 0xFF3B68B5, "⭐"),
    SquadMember("s2", "Maya Chen", "@mayavibes", 2, 12, 4, 0xFF98B6A2, "🔥"),
    SquadMember("s3", "Liam Davies", "@liam_focus", 3, 9, 3, 0xFF8CA6DB, "⚡")
  )
}
