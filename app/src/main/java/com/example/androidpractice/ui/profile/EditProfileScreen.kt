package com.example.androidpractice.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.content.Context
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import java.io.File

@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel,
    onDone: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val editProfile by viewModel.editProfile.collectAsState()
    val isImporting by viewModel.isImporting.collectAsState()

    var showPhotoDialog by remember { mutableStateOf(false) }
    var showCameraSettingsDialog by remember { mutableStateOf(false) }
    var showStorageSettingsDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    LaunchedEffect(Unit) {
        viewModel.initEdit()
    }

    // ── Запрос разрешения на хранилище при входе на экран ──────────────────
    // Если система не показала диалог (MIUI и др.) — предлагаем перейти в Настройки.
    // Если пользователь отказывается — выходим с экрана.
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) showStorageSettingsDialog = true
    }

    LaunchedEffect(Unit) {
        val alreadyGranted = ContextCompat.checkSelfPermission(context, storagePermission) ==
                PackageManager.PERMISSION_GRANTED
        if (!alreadyGranted) storagePermissionLauncher.launch(storagePermission)
    }

    // ── Камера ──────────────────────────────────────────────────────────────
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) tempCameraUri?.let { viewModel.updatePhotoUri(it.toString()) }
    }

    val launchCamera = {
        val uri = viewModel.createCameraUri()
        tempCameraUri = uri
        cameraLauncher.launch(uri)
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) launchCamera() else showCameraSettingsDialog = true
    }

    // ── Галерея ─────────────────────────────────────────────────────────────
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {}
            viewModel.updatePhotoUri(it.toString())
        }
    }

    // ── Пикер документа (резюме с устройства) ───────────────────────────────
    // Файл сразу копируется в локальное хранилище приложения — это позволяет
    // открывать его позже без зависимости от стороннего провайдера (Google Drive и т.д.)
    val documentPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.importResumeFile(context, it) }
    }

    // ── Диалог: Галерея или Камера ──────────────────────────────────────────
    if (showPhotoDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoDialog = false },
            title = { Text("Выбрать фото") },
            text = { Text("Откуда взять аватарку?") },
            confirmButton = {
                TextButton(onClick = {
                    showPhotoDialog = false
                    galleryLauncher.launch("image/*")
                }) { Text("Галерея") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPhotoDialog = false
                    val hasCameraPermission = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                    if (hasCameraPermission) {
                        launchCamera()
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }) { Text("Камера") }
            }
        )
    }

    // ── Диалог: направить в настройки, если камера заблокирована ────────────
    if (showCameraSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showCameraSettingsDialog = false },
            title = { Text("Нет доступа к камере") },
            text = { Text("Разрешение на использование камеры было отклонено. Откройте настройки приложения, чтобы выдать его вручную.") },
            confirmButton = {
                TextButton(onClick = {
                    showCameraSettingsDialog = false
                    context.startActivity(appSettingsIntent(context))
                }) { Text("Настройки") }
            },
            dismissButton = {
                TextButton(onClick = { showCameraSettingsDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    // ── Диалог: хранилище заблокировано — предложить настройки или выйти ────
    if (showStorageSettingsDialog) {
        AlertDialog(
            onDismissRequest = {},  // нельзя закрыть тапом мимо — требуется явный выбор
            title = { Text("Нет доступа к файлам") },
            text = { Text("Для редактирования профиля необходим доступ к файлам. Выдайте разрешение в настройках приложения или выйдите с экрана.") },
            confirmButton = {
                TextButton(onClick = {
                    showStorageSettingsDialog = false
                    context.startActivity(appSettingsIntent(context))
                }) { Text("Настройки") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showStorageSettingsDialog = false
                    onDone()
                }) { Text("Выйти") }
            }
        )
    }

    // ── UI ──────────────────────────────────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Редактирование профиля", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(24.dp))

        // Аватарка — нажатие открывает диалог выбора источника
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .clickable { showPhotoDialog = true },
            contentAlignment = Alignment.Center
        ) {
            if (editProfile.photoUri.isNotBlank()) {
                AsyncImage(
                    model = Uri.parse(editProfile.photoUri),
                    contentDescription = "Фото профиля",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Нет фото",
                    modifier = Modifier.size(80.dp),
                    tint = Color.Gray
                )
            }
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Изменить фото",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(28.dp)
                    .padding(2.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Нажмите для смены фото", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = editProfile.fullName,
            onValueChange = { viewModel.updateFullName(it) },
            label = { Text("ФИО") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = editProfile.position,
            onValueChange = { viewModel.updatePosition(it) },
            label = { Text("Должность") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Поле URL резюме и кнопка выбора локального файла
        OutlinedTextField(
            value = editProfile.resumeUrl,
            onValueChange = { viewModel.updateResumeUrl(it) },
            label = { Text("Ссылка на резюме (URL)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = editProfile.resumeLocalUri.isBlank()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = { documentPickerLauncher.launch("*/*") },
                enabled = !isImporting,
                modifier = Modifier.weight(1f)
            ) {
                if (isImporting) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Копирование...")
                } else {
                    Icon(Icons.Default.AttachFile, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Файл с устройства")
                }
            }

            if (editProfile.resumeLocalUri.isNotBlank()) {
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = { viewModel.clearResumeLocalUri() }) {
                    Text("Убрать", color = MaterialTheme.colorScheme.error)
                }
            }
        }

        if (editProfile.resumeLocalUri.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "📎 ${File(editProfile.resumeLocalUri).name}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.saveProfile(onDone) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Готово")
        }
    }
}

private fun appSettingsIntent(context: android.content.Context) =
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
