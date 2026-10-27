package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.UserProfile
import com.example.ui.theme.CharcoalInk
import com.example.ui.theme.InkSubtle
import com.example.ui.theme.PrimaryBlue

/**
 * Reusable Dialog components for Profile editing and Privacy information.
 */

@Composable
fun EditProfileDialog(
  profile: UserProfile,
  onDismiss: () -> Unit,
  onSave: (newName: String, newBio: String) -> Unit
) {
  var editName by remember { mutableStateOf(profile.name) }
  var editBio by remember { mutableStateOf(profile.bio) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Edit Profil", fontWeight = FontWeight.Bold, color = CharcoalInk) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = editName,
          onValueChange = { editName = it },
          label = { Text("Nama Tampilan") },
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
          if (editName.isNotBlank()) {
            onSave(editName.trim(), editBio.trim())
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
      ) {
        Text("Simpan")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Batal", color = InkSubtle)
      }
    }
  )
}

@Composable
fun PrivacyInfoDialog(
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Privasi & Penyimpanan Lokal", fontWeight = FontWeight.Bold) },
    text = {
      Text("Semua data kebiasaan, catatan tugas harian, dan streak Anda disimpan secara aman dan privat langsung di perangkat Anda tanpa data pihak ketiga.")
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
      ) {
        Text("Mengerti")
      }
    }
  )
}
