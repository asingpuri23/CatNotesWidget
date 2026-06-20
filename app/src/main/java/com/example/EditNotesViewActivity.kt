package com.example

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.NotesViewModel
import kotlin.getValue

class EditNotesViewActivity : ComponentActivity() {
    val TAG = "CatNotesWidget"
    private val viewModel: NotesViewModel by viewModels()
    private var mCurrentEditNote = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "EditNotesViewActivity onCreate")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var noteId = intent.getIntExtra("NOTE_ID", 0)
        mCurrentEditNote = noteId
        viewModel.startEditing(mCurrentEditNote)
        setContent {
            val notes by viewModel.allNotes.collectAsStateWithLifecycle()
            val currentEditNote by viewModel.currentEditNote.collectAsStateWithLifecycle()
            val selectedStickerIndex by viewModel.selectedStickerIndex.collectAsStateWithLifecycle()

//            val callback = object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    Log.i(TAG, "OnBackPressed")
//                    // Add your custom logic here (e.g., show a dialog)
//
//                    // If you want to fall back to the default back behavior:
//                    isEnabled = false // Disable this callback
//                    onBackPressedDispatcher.onBackPressed() // Retrigger the back event
//                }
//            }
//
//            // Add the callback to the dispatcher
//            onBackPressedDispatcher.addCallback(this, callback)

            // Main layout wrapping
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFFFF1F3), // #FFF1F3 Vibrant Palette Base
                                Color(0xFFFFECEF), // Pastel Cream
                                Color(0xFFFFF1F3)  // #FFF1F3 Gradient Blend
                            )
                        )
                    )
            ) {
                if (currentEditNote != null) {
                    NoteCustomizerScreen(
                        note = currentEditNote!!,
                        selectedStickerIndex = selectedStickerIndex,
                        onBack = {
                            Log.i(TAG, "onBack")
                            viewModel.stopEditing(saveChanges = true)
                            onBackPressedDispatcher.onBackPressed()
                        },
                        onTitleChange = { viewModel.updateTitle(it) },
                        onContentChange = { viewModel.updateContent(it) },
                        onThemeSelect = { themeId -> viewModel.updateTheme(themeId) },
                        onStickerSelect = { idx -> viewModel.selectSticker(idx) },
                        onAddSticker = { stickerId -> viewModel.addSticker(stickerId) },
                        onUpdateStickerPos = { idx, x, y -> viewModel.updateStickerPosition(idx, x, y) },
                        onSaveStickerPos = { viewModel.saveStickerPositions() },
                        onStickerScale = { idx, amt -> viewModel.adjustStickerScale(idx, amt) },
                        onStickerRotate = { idx, rot -> viewModel.adjustStickerRotation(idx, rot) },
                        onStickerRemove = { idx -> viewModel.removeSticker(idx) }
                    )
                } else {
                    Greeting()
                }
            }
        }
    }

    override fun onPause() {
        Log.i(TAG, "onPause")
        super.onPause()
        viewModel.stopEditing(saveChanges = true)
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy")
        super.onDestroy()
        viewModel.stopEditing(saveChanges = true)
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF1F3), // #FFF1F3 Vibrant Palette Base
                        Color(0xFFFFECEF), // Pastel Cream
                        Color(0xFFFFF1F3)  // #FFF1F3 Gradient Blend
                    )
                )
            )
    ) {
        Text(
            text = "No note selected",
            modifier = modifier
        )
    }
}