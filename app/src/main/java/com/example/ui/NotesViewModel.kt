package com.example.ui

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.widget.CuteCatWidgetProvider
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application) {


    private val db = AppDatabase.getDatabase(application)
    private val repository = NoteRepository(db.noteDao())

    val allNotes: StateFlow<List<Note>> = repository.allNotes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _currentEditNote = MutableStateFlow<Note?>(null)
    val currentEditNote: StateFlow<Note?> = _currentEditNote.asStateFlow()

    private val _selectedStickerIndex = MutableStateFlow<Int?>(null)
    val selectedStickerIndex: StateFlow<Int?> = _selectedStickerIndex.asStateFlow()

    // Create a new note
    fun createNewNote(themeId: String = "hello_kitty") {
        viewModelScope.launch {
            val note = Note(
                title = "New Note",
                content = "Double tap to write inside me !~",
                themeId = themeId,
                stickersJson = "[]"
            )
            val newId = repository.insert(note)
            val insertedNote = note.copy(id = newId.toInt())
            _currentEditNote.value = insertedNote
            _selectedStickerIndex.value = null
            notifyWidgets()
        }
    }

    // Set active note for editing
    fun startEditing(noteId: Int) {
        viewModelScope.launch {
            val note = repository.getNoteById(noteId)
            _currentEditNote.value = note
            _selectedStickerIndex.value = null
        }
    }

    fun stopEditing(saveChanges: Boolean = true) {
        if (saveChanges) {
            saveCurrentNote()
        } else {
            _currentEditNote.value = null
            _selectedStickerIndex.value = null
        }
    }

    // Update draft note title
    fun updateTitle(newTitle: String) {
        _currentEditNote.value = _currentEditNote.value?.copy(
            title = newTitle,
            updatedAt = System.currentTimeMillis()
        )
    }

    // Update draft note content
    fun updateContent(newContent: String) {
        _currentEditNote.value = _currentEditNote.value?.copy(
            content = newContent,
            updatedAt = System.currentTimeMillis()
        )
    }

    // Update draft note theme
    fun updateTheme(themeId: String) {
        _currentEditNote.value = _currentEditNote.value?.copy(
            themeId = themeId,
            updatedAt = System.currentTimeMillis()
        )
        // Auto-save theme change instantly
        saveCurrentNote()
    }

    // --- STICKER INTERACTIONS ---

    fun selectSticker(index: Int?) {
        _selectedStickerIndex.value = index
    }

    fun addSticker(stickerId: String) {
        val note = _currentEditNote.value ?: return
        val currentStickers = note.getStickers().toMutableList()

        // Place new sticker near the top center
        val newSticker = NoteSticker(
            stickerId = stickerId,
            relativeX = 0.5f,
            relativeY = 0.5f,
            scale = 1.0f,
            rotation = 0.0f
        )
        currentStickers.add(newSticker)

        val updatedJson = Note.createStickersJson(currentStickers)
        _currentEditNote.value = note.copy(
            stickersJson = updatedJson,
            updatedAt = System.currentTimeMillis()
        )
        _selectedStickerIndex.value = currentStickers.size - 1 // select the added sticker
        saveCurrentNote()
    }

    fun updateStickerPosition(index: Int, relX: Float, relY: Float) {
        val note = _currentEditNote.value ?: return
        val stickers = note.getStickers().toMutableList()
        if (index >= 0 && index < stickers.size) {
            val original = stickers[index]
            // Constraint within notepad bounds (slightly inside edges)
            val clampedX = relX.coerceIn(0.05f, 0.95f)
            val clampedY = relY.coerceIn(0.05f, 0.95f)
            stickers[index] = original.copy(relativeX = clampedX, relativeY = clampedY)

            _currentEditNote.value = note.copy(
                stickersJson = Note.createStickersJson(stickers),
                updatedAt = System.currentTimeMillis()
            )
        }
    }

    fun saveStickerPositions() {
        saveCurrentNote()
    }

    fun adjustStickerScale(index: Int, amount: Float) {
        val note = _currentEditNote.value ?: return
        val stickers = note.getStickers().toMutableList()
        if (index >= 0 && index < stickers.size) {
            val s = stickers[index]
            val newScale = (s.scale + amount).coerceIn(0.5f, 2.5f)
            stickers[index] = s.copy(scale = newScale)
            _currentEditNote.value = note.copy(
                stickersJson = Note.createStickersJson(stickers),
                updatedAt = System.currentTimeMillis()
            )
            saveCurrentNote()
        }
    }

    fun adjustStickerRotation(index: Int, angleOffset: Float) {
        val note = _currentEditNote.value ?: return
        val stickers = note.getStickers().toMutableList()
        if (index >= 0 && index < stickers.size) {
            val s = stickers[index]
            var newRot = (s.rotation + angleOffset) % 360f
            if (newRot < 0f) newRot += 360f
            stickers[index] = s.copy(rotation = newRot)
            _currentEditNote.value = note.copy(
                stickersJson = Note.createStickersJson(stickers),
                updatedAt = System.currentTimeMillis()
            )
            saveCurrentNote()
        }
    }

    fun removeSticker(index: Int) {
        val note = _currentEditNote.value ?: return
        val stickers = note.getStickers().toMutableList()
        if (index >= 0 && index < stickers.size) {
            stickers.removeAt(index)
            _currentEditNote.value = note.copy(
                stickersJson = Note.createStickersJson(stickers),
                updatedAt = System.currentTimeMillis()
            )
            _selectedStickerIndex.value = null
            saveCurrentNote()
        }
    }

    // Save notes
    fun saveCurrentNote() {
        val note = _currentEditNote.value ?: return
        viewModelScope.launch {
            repository.insert(note)
            notifyWidgets()
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.delete(note)
            if (_currentEditNote.value?.id == note.id) {
                _currentEditNote.value = null
                _selectedStickerIndex.value = null
            }
            notifyWidgets()
        }
    }

    // Tell the AppWidgetProvider to refresh layout
    private fun notifyWidgets() {
        val context = getApplication<Application>()
        val intent = Intent(context, CuteCatWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val component = ComponentName(context, CuteCatWidgetProvider::class.java)
        val ids = appWidgetManager.getAppWidgetIds(component)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        context.sendBroadcast(intent)
    }
}
