package com.example.androidpractice.ui.profile

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpractice.data.preferences.ProfileDataStore
import com.example.androidpractice.domain.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val profileDataStore = ProfileDataStore(application)

    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile

    private val _editProfile = MutableStateFlow(UserProfile())
    val editProfile: StateFlow<UserProfile> = _editProfile

    /** true пока файл резюме копируется в локальное хранилище */
    private val _isImporting = MutableStateFlow(false)
    val isImporting: StateFlow<Boolean> = _isImporting

    private val _downloadEvent = MutableSharedFlow<Long>(extraBufferCapacity = 1)
    val downloadEvent: SharedFlow<Long> = _downloadEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            profileDataStore.profile.collect { saved ->
                _profile.value = saved
            }
        }
    }

    fun initEdit() {
        _editProfile.value = _profile.value
    }

    fun updateFullName(name: String) {
        _editProfile.value = _editProfile.value.copy(fullName = name)
    }

    fun updatePosition(pos: String) {
        _editProfile.value = _editProfile.value.copy(position = pos)
    }

    fun updateResumeUrl(url: String) {
        _editProfile.value = _editProfile.value.copy(resumeUrl = url, resumeLocalUri = "")
    }

    fun clearResumeLocalUri() {
        _editProfile.value = _editProfile.value.copy(resumeLocalUri = "", resumeUrl = "")
    }

    fun updatePhotoUri(uri: String) {
        _editProfile.value = _editProfile.value.copy(photoUri = uri)
    }

    /**
     * Копирует выбранный файл (content:// URI от любого провайдера — Google Drive,
     * файловый менеджер и т.д.) в приватное хранилище приложения.
     * Сохраняет абсолютный путь к копии в editProfile.resumeLocalUri.
     */
    fun importResumeFile(context: Context, sourceUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _isImporting.value = true
            try {
                val fileName = getDisplayName(context, sourceUri)
                    ?: "resume_${System.currentTimeMillis()}"
                val resumeDir = File(context.filesDir, "resumes").also { it.mkdirs() }
                val destFile = File(resumeDir, fileName)

                context.contentResolver.openInputStream(sourceUri)?.use { input ->
                    destFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                withContext(Dispatchers.Main) {
                    _editProfile.value = _editProfile.value.copy(
                        resumeLocalUri = destFile.absolutePath,
                        resumeUrl = ""
                    )
                }
            } catch (_: Exception) {
                // Если копирование не удалось — ничего не меняем
            } finally {
                _isImporting.value = false
            }
        }
    }

    /** Открывает локальный файл через FileProvider и возвращает URI для Intent */
    fun getLocalResumeUri(context: Context): Uri? {
        val path = _profile.value.resumeLocalUri.ifBlank { return null }
        val file = File(path)
        if (!file.exists()) return null
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    fun saveProfile(onDone: () -> Unit) {
        viewModelScope.launch {
            profileDataStore.saveProfile(_editProfile.value)
            onDone()
        }
    }

    fun createCameraUri(): Uri {
        val app = getApplication<Application>()
        val photoFile = File(app.cacheDir, "camera/photo_${System.currentTimeMillis()}.jpg")
            .also { it.parentFile?.mkdirs() }
        return FileProvider.getUriForFile(app, "${app.packageName}.fileprovider", photoFile)
    }

    fun downloadResume() {
        val url = _profile.value.resumeUrl.trim()
        if (url.isBlank()) return
        val app = getApplication<Application>()
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Резюме")
            .setDescription("Загрузка...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "resume.pdf")
            .setMimeType("application/pdf")
        val dm = app.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        _downloadEvent.tryEmit(dm.enqueue(request))
    }

    private fun getDisplayName(context: Context, uri: Uri): String? =
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            if (idx >= 0) cursor.getString(idx) else null
        }
}
