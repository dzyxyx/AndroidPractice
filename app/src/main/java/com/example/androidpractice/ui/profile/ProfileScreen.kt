package com.example.androidpractice.ui.profile

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onEdit: () -> Unit
) {
    val profile by viewModel.profile.collectAsState()
    val context = LocalContext.current

    var activeDownloadId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        viewModel.downloadEvent.collect { id ->
            activeDownloadId = id
        }
    }

    // BroadcastReceiver — открывает файл когда DownloadManager завершил загрузку по URL
    DisposableEffect(activeDownloadId) {
        val downloadId = activeDownloadId ?: return@DisposableEffect onDispose {}

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                val receivedId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
                if (receivedId != downloadId) return

                val dm = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val fileUri: Uri? = dm.getUriForDownloadedFile(downloadId)
                if (fileUri != null) {
                    openFile(ctx, fileUri)
                }
                activeDownloadId = null
            }
        }

        ContextCompat.registerReceiver(
            context, receiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        onDispose {
            try { context.unregisterReceiver(receiver) } catch (_: Exception) {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Аватарка
            if (profile.photoUri.isNotBlank()) {
                AsyncImage(
                    model = Uri.parse(profile.photoUri),
                    contentDescription = "Фото профиля",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Нет фото",
                        modifier = Modifier.size(80.dp),
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = profile.fullName.ifBlank { "Имя не указано" },
                style = MaterialTheme.typography.headlineSmall
            )

            if (profile.position.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = profile.position,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Локальный файл резюме — копия на устройстве, открывается через FileProvider
            if (profile.resumeLocalUri.isNotBlank()) {
                val fileName = java.io.File(profile.resumeLocalUri).name
                Text(
                    text = "📎 $fileName",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    val uri = viewModel.getLocalResumeUri(context)
                    if (uri != null) openFile(context, uri)
                }) {
                    Text("Открыть резюме")
                }
            }

            // Резюме по URL — скачивается через DownloadManager
            if (profile.resumeUrl.isNotBlank()) {
                Button(onClick = { viewModel.downloadResume() }) {
                    Text("Резюме")
                }
            }

            if (profile.resumeLocalUri.isBlank() && profile.resumeUrl.isBlank()) {
                Text(
                    text = "Ссылка на резюме не указана",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }

        FloatingActionButton(
            onClick = onEdit,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Редактировать")
        }
    }
}

private fun openFile(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, context.contentResolver.getType(uri) ?: "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        context.startActivity(Intent.createChooser(intent, "Открыть резюме"))
    } catch (_: Exception) {}
}
