package com.example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Note
import com.example.data.NoteSticker
import com.example.ui.CatDrawingHelper
import com.example.ui.NotesViewModel
import android.util.Log
import androidx.activity.OnBackPressedCallback

class MainActivity : ComponentActivity() {

    private val viewModel: NotesViewModel by viewModels()
    val TAG = "CatNotesWidget"
    lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        mContext = applicationContext
        enableEdgeToEdge()

        // Handle initial launch from widget
        handleWidgetIntent(intent)

        setContent {
            val notes by viewModel.allNotes.collectAsStateWithLifecycle()
            val currentEditNote by viewModel.currentEditNote.collectAsStateWithLifecycle()
            val selectedStickerIndex by viewModel.selectedStickerIndex.collectAsStateWithLifecycle()

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
                CatBoardDashboard(
                    notes = notes,
                    onNoteClick = { note ->
                        var newIntent = Intent(mContext, EditNotesViewActivity::class.java)
                        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        newIntent.putExtra("NOTE_ID", note.id)
                        mContext.startActivity(newIntent)
                                  },
                    onAddNoteClick = { viewModel.createNewNote() },
                    onDeleteNote = { note -> viewModel.deleteNote(note) }
                )

            }
        }
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.i(TAG, "OnBackPressed")
                // Add your custom logic here (e.g., show a dialog)

                // If you want to fall back to the default back behavior:
                isEnabled = false // Disable this callback
                onBackPressedDispatcher.onBackPressed() // Retrigger the back event
            }
        }

        // Add the callback to the dispatcher
        onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onPause() {
        Log.i(TAG, "onPause")
        super.onPause()
        //viewModel.stopEditing(saveChanges = true)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleWidgetIntent(intent)
    }

    private fun handleWidgetIntent(intent: Intent?) {
        val noteId = intent?.getIntExtra("NOTE_ID", -1) ?: -1
        if (noteId != -1) {
            var newIntent = Intent(mContext, EditNotesViewActivity::class.java)
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            newIntent.putExtra("NOTE_ID", noteId)
            mContext.startActivity(newIntent)

        }
    }
}

// ==========================================
// 1. DASHBOARD OVERVIEW SCREEN (CAT BOARD)
// ==========================================

@Composable
fun CatBoardDashboard(
    notes: List<Note>,
    onNoteClick: (Note) -> Unit,
    onAddNoteClick: () -> Unit,
    onDeleteNote: (Note) -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Delightful Cat Header with Vibrant Palette Theme
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Logo Container (from theme)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .shadow(2.dp, CircleShape)
                            .background(Color(0xFFFF4D6D), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🐱", fontSize = 20.sp)
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "KittyNotes",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFBA1A5E),
                                fontFamily = FontFamily.Serif,
                                letterSpacing = (-0.5).sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            // Small decorative mini-bow
                            Canvas(modifier = Modifier.size(16.dp)) {
                                val half = size.minDimension / 2f
                                val center = Offset(half, half)
                                drawCircle(Color(0xFFFF4D6D), radius = half * 0.45f, center = Offset(half - half * 0.4f, half))
                                drawCircle(CatDrawingHelper.CharcoalDark, radius = half * 0.45f, center = Offset(half - half * 0.4f, half), style = Stroke(1.5f))
                                drawCircle(Color(0xFFFF4D6D), radius = half * 0.45f, center = Offset(half + half * 0.4f, half))
                                drawCircle(CatDrawingHelper.CharcoalDark, radius = half * 0.45f, center = Offset(half + half * 0.4f, half), style = Stroke(1.5f))
                                drawCircle(Color(0xFFFF4D6D), radius = half * 0.25f, center = center)
                                drawCircle(CatDrawingHelper.CharcoalDark, radius = half * 0.25f, center = center, style = Stroke(1.5f))
                            }
                        }
                        Text(
                            text = "HOMESCREEN WIDGETS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFBA1A5E).copy(alpha = 0.6f),
                            letterSpacing = 1.2.sp
                        )
                    }
                }
                // Settings style round action button
                IconButton(
                    onClick = {
                        // Soft interactive tap
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .shadow(1.dp, CircleShape)
                        .background(Color.White.copy(alpha = 0.8f), CircleShape)
                        .border(width = 1.dp, color = Color(0xFFFFC2D1), shape = CircleShape)
                ) {
                    Text("⚙️", fontSize = 16.sp)
                }
            }

            if (notes.isEmpty()) {
                // Empty State with an Adorable Kitty Illustration
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Sleepy Cat illustration
                        Canvas(modifier = Modifier.size(160.dp)) {
                            val w = size.width
                            val h = size.height
                            // head
                            drawCircle(Color.White, radius = 64f, center = Offset(w / 2, h / 2 + 10f))
                            drawCircle(CatDrawingHelper.CharcoalDark, radius = 64f, center = Offset(w / 2, h / 2 + 10f), style = Stroke(4f))

                            // ears
                            val leftEar = androidx.compose.ui.graphics.Path().apply {
                                moveTo(w / 2 - 50f, h / 2 - 35f)
                                lineTo(w / 2 - 70f, h / 2 - 95f)
                                lineTo(w / 2 - 20f, h / 2 - 52f)
                            }
                            drawPath(leftEar, Color.White)
                            drawPath(leftEar, CatDrawingHelper.CharcoalDark, style = Stroke(4f))

                            val rightEar = androidx.compose.ui.graphics.Path().apply {
                                moveTo(w / 2 + 50f, h / 2 - 35f)
                                lineTo(w / 2 + 70f, h / 2 - 95f)
                                lineTo(w / 2 + 20f, h / 2 - 52f)
                            }
                            drawPath(rightEar, Color.White)
                            drawPath(rightEar, CatDrawingHelper.CharcoalDark, style = Stroke(4f))

                            // ribbons on left ear
                            drawCircle(Color.Red, radius = 12f, center = Offset(w / 2 - 55f, h / 2 - 75f))
                            drawCircle(CatDrawingHelper.CharcoalDark, radius = 12f, center = Offset(w / 2 - 55f, h / 2 - 75f), style = Stroke(2.5f))

                            // sleeping eyes
                            drawArc(
                                CatDrawingHelper.CharcoalDark,
                                startAngle = 0f, sweepAngle = 180f, useCenter = false,
                                topLeft = Offset(w / 2 - 38f, h / 2), size = androidx.compose.ui.geometry.Size(22f, 12f),
                                style = Stroke(4f)
                            )
                            drawArc(
                                CatDrawingHelper.CharcoalDark,
                                startAngle = 0f, sweepAngle = 180f, useCenter = false,
                                topLeft = Offset(w / 2 + 16f, h / 2), size = androidx.compose.ui.geometry.Size(22f, 12f),
                                style = Stroke(4f)
                            )

                            // nose Button
                            drawCircle(Color(0xFFFFEA00), radius = 6f, center = Offset(w / 2, h / 2 + 16f))
                            drawCircle(CatDrawingHelper.CharcoalDark, radius = 6f, center = Offset(w / 2, h / 2 + 16f), style = Stroke(2f))

                            // whiskers
                            drawLine(CatDrawingHelper.CharcoalDark, Offset(w / 2 - 62f, h / 2 + 14f), Offset(w / 2 - 95f, h / 2 + 12f), 3.5f)
                            drawLine(CatDrawingHelper.CharcoalDark, Offset(w / 2 - 62f, h / 2 + 22f), Offset(w / 2 - 100f, h / 2 + 22f), 3.5f)
                            drawLine(CatDrawingHelper.CharcoalDark, Offset(w / 2 + 62f, h / 2 + 14f), Offset(w / 2 + 95f, h / 2 + 12f), 3.5f)
                            drawLine(CatDrawingHelper.CharcoalDark, Offset(w / 2 + 62f, h / 2 + 22f), Offset(w / 2 + 100f, h / 2 + 22f), 3.5f)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No notes on your cat board yet!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CatDrawingHelper.CharcoalDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap the cozy button below to doodle some sweet thoughts, stick kittens on them, and pin them to your homescreen!",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else {
                // Interactive Grid list of Notes
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(notes, key = { it.id }) { note ->
                        NoteBoardItem(
                            note = note,
                            onClick = { onNoteClick(note) },
                            onDelete = { onDeleteNote(note) }
                        )
                    }
                }
            }
        }

        // Custom paw floating action button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = 24.dp, end = 24.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingCatActionButton(onClick = onAddNoteClick)
        }
    }
}

// Grid Note Card view with custom decorations and whiskers
@Composable
fun NoteBoardItem(
    note: Note,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete cute Note?") },
            text = { Text("Are you sure you want to toss away this adorable note?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Oops, Keep It!")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .shadow(
                elevation = 8.dp, 
                shape = RoundedCornerShape(28.dp), 
                spotColor = Color(0xFFFFC2D1), 
                ambientColor = Color(0xFFFFC2D1)
            )
            .clickable { onClick() }
    ) {
        // Draw matching Cat ear backplate
        Canvas(modifier = Modifier.fillMaxSize()) {
            val bgColor = when (note.themeId) {
                "hello_kitty" -> CatDrawingHelper.HelloKittyPink
                "cozy_neko" -> CatDrawingHelper.CozyNekoCream
                "sweet_strawberry" -> CatDrawingHelper.StrawberryPastelRed
                "mew_milk" -> CatDrawingHelper.MewMilkBlue
                else -> Color.White
            }
            drawRoundRect(
                color = bgColor,
                cornerRadius = CornerRadius(48f, 48f)
            )
            drawRoundRect(
                color = CatDrawingHelper.CharcoalDark,
                cornerRadius = CornerRadius(48f, 48f),
                style = Stroke(3.5f)
            )

            // Draw ears and Whiskers programmatically
            CatDrawingHelper.drawCatThemeBackground(this, note.themeId, size)
        }

        // Note Content Overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Decorative accent dot from theme
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFFFF4D6D), CircleShape)
                    )
                    Text(
                        text = note.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CatDrawingHelper.CharcoalDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.6f))
                        .clickable { showDeleteDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Delete Note",
                        tint = CatDrawingHelper.CharcoalDark,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = note.content,
                fontSize = 12.sp,
                color = CatDrawingHelper.CharcoalDark.copy(alpha = 0.85f),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp,
                modifier = Modifier.weight(1f)
            )
        }

        // Mini stickers display
        val stickers = note.getStickers()
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val cw = maxWidth
            val ch = maxHeight
            for (sticker in stickers) {
                val sx = sticker.relativeX * cw.value
                val sy = sticker.relativeY * ch.value
                val sSize = 25.dp * sticker.scale

                Box(
                    modifier = Modifier
                        .offset(x = sx.dp - (sSize / 2), y = sy.dp - (sSize / 2))
                        .size(sSize)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        withTransform({
                            rotate(sticker.rotation)
                        }) {
                            CatDrawingHelper.drawStickerComposable(this, sticker.stickerId, size.width)
                        }
                    }
                }
            }
        }
    }
}

// Paw Print Floating Action Button
@Composable
fun FloatingCatActionButton(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition()
    val bobbingAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .offset(y = bobbingAnim.dp)
            .shadow(
                elevation = 10.dp, 
                shape = RoundedCornerShape(18.dp), 
                spotColor = Color(0xFFFF4D6D), 
                ambientColor = Color(0xFFFF4D6D)
            )
            .size(64.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFFF4D6D))
            .clickable { onClick() }
            .testTag("add_note_button"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Little pink paw print inside base
            val padRadius = 14f
            drawCircle(Color.White, radius = padRadius, center = Offset(w / 2f, h / 2f + 5f))

            // 4 toes above
            val toeRadius = 7f
            drawCircle(Color.White, radius = toeRadius, center = Offset(w / 2f - 18f, h / 2f - 12f))
            drawCircle(Color.White, radius = toeRadius, center = Offset(w / 2f - 7f, h / 2f - 19f))
            drawCircle(Color.White, radius = toeRadius, center = Offset(w / 2f + 7f, h / 2f - 19f))
            drawCircle(Color.White, radius = toeRadius, center = Offset(w / 2f + 18f, h / 2f - 12f))
        }
    }
}

// ==========================================
// 2. NOTE CUSTOMIZER & STICKER CANVAS
// ==========================================

@Composable
fun NoteCustomizerScreen(
    note: Note,
    selectedStickerIndex: Int?,
    onBack: () -> Unit,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onThemeSelect: (String) -> Unit,
    onStickerSelect: (Int?) -> Unit,
    onAddSticker: (String) -> Unit,
    onUpdateStickerPos: (Int, Float, Float) -> Unit,
    onSaveStickerPos: () -> Unit,
    onStickerScale: (Int, Float) -> Unit,
    onStickerRotate: (Int, Float) -> Unit,
    onStickerRemove: (Int) -> Unit
) {
    var activeStickerTab by remember { mutableStateOf("bows") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // High-polish Customizer Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .shadow(2.dp, CircleShape)
                    .background(Color.White, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Save and Back",
                    tint = CatDrawingHelper.CharcoalDark
                )
            }

            Text(
                text = "Decorate Note",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = CatDrawingHelper.CharcoalDark,
                fontFamily = FontFamily.Serif
            )

            // Auto-saved confirmation stamp
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE8F5E9))
                    .border(1.dp, Color(0xFF81C784), RoundedCornerShape(16.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Saved",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Auto-Saved",
                        fontSize = 11.sp,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Interactive Note Editor & Sticker Field Canvas
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            // Container with onSizeChanged listeners
            var cardWidthPx by remember { mutableStateOf(1f) }
            var cardHeightPx by remember { mutableStateOf(1f) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .onSizeChanged { size ->
                        cardWidthPx = size.width.toFloat()
                        cardHeightPx = size.height.toFloat()
                    }
                    .shadow(12.dp, shape = RoundedCornerShape(26.dp))
                    .pointerInput(Unit) {
                        // Tapping background deselects sticker
                        detectDragGestures(
                            onDrag = { _, _ -> },
                            onDragStart = { onStickerSelect(null) }
                        )
                    }
            ) {
                // Background Paint for ear canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val bgColor = when (note.themeId) {
                        "hello_kitty" -> CatDrawingHelper.HelloKittyPink
                        "cozy_neko" -> CatDrawingHelper.CozyNekoCream
                        "sweet_strawberry" -> CatDrawingHelper.StrawberryPastelRed
                        "mew_milk" -> CatDrawingHelper.MewMilkBlue
                        else -> Color.White
                    }
                    // draw card base
                    drawRoundRect(
                        color = bgColor,
                        cornerRadius = CornerRadius(56f, 56f)
                    )
                    drawRoundRect(
                        color = CatDrawingHelper.CharcoalDark,
                        cornerRadius = CornerRadius(56f, 56f),
                        style = Stroke(3.8f)
                    )

                    // Draw ears & themes programmatically
                    CatDrawingHelper.drawCatThemeBackground(this, note.themeId, size)
                }

                // Interactive Note Inputs
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                ) {
                    // Title input field
                    TextField(
                        value = note.title,
                        onValueChange = onTitleChange,
                        placeholder = { Text("Title for notepad *~", fontWeight = FontWeight.Bold) },
                        textStyle = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = CatDrawingHelper.CharcoalDark,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Normal
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("note_title_input")
                    )

                    // Cute divider matching theme
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                            .height(2.5.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(CatDrawingHelper.getThemeAccentColor(note.themeId)),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // Notepad Content Edit area
                    TextField(
                        value = note.content,
                        onValueChange = onContentChange,
                        placeholder = { Text("Scribble your daily thoughts here ...", color = Color.Gray) },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = CatDrawingHelper.CharcoalDark,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .testTag("note_content_input")
                    )
                }

                // Stickers Overlay Layer
                val stickers = note.getStickers()
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val cw = maxWidth
                    val ch = maxHeight

                    stickers.forEachIndexed { idx, s ->
                        val sx = s.relativeX * cw.value
                        val sy = s.relativeY * ch.value
                        val stickSize = 50.dp * s.scale
                        val isSelected = selectedStickerIndex == idx

                        Box(
                            modifier = Modifier
                                .offset(
                                    x = sx.dp - (stickSize / 2),
                                    y = sy.dp - (stickSize / 2)
                                )
                                .size(stickSize)
                                .border(
                                    width = if (isSelected) 2.dp else 0.dp,
                                    color = if (isSelected) Color.Red else Color.Transparent,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .pointerInput(idx) {
                                    detectDragGestures(
                                        onDragStart = { onStickerSelect(idx) },
                                        onDragEnd = { onSaveStickerPos() },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            // Relative coordinate updates based on real pixel bounds
                                            val currentX = s.relativeX + (dragAmount.x / cardWidthPx)
                                            val currentY = s.relativeY + (dragAmount.y / cardHeightPx)
                                            onUpdateStickerPos(idx, currentX, currentY)
                                        }
                                    )
                                }
                                .clickable { onStickerSelect(idx) }
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                withTransform({
                                    rotate(s.rotation)
                                }) {
                                    CatDrawingHelper.drawStickerComposable(this, s.stickerId, size.width)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Control HUD (Shows up under canvas whenever a sticker is selected)
        AnimatedVisibility(
            visible = selectedStickerIndex != null,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 6.dp)
                    .shadow(4.dp, RoundedCornerShape(16.dp))
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Rotate left
                IconButton(onClick = { onStickerRotate(selectedStickerIndex!!, -15f) }) {
                    Text("↺", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                }
                // Rotate right
                IconButton(onClick = { onStickerRotate(selectedStickerIndex!!, 15f) }) {
                    Text("↻", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                }
                // Scale larger
                IconButton(onClick = { onStickerScale(selectedStickerIndex!!, 0.1f) }) {
                    Text("+", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                }
                // Scale smaller
                IconButton(onClick = { onStickerScale(selectedStickerIndex!!, -0.1f) }) {
                    Text("−", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                }
                // Delete sticker
                IconButton(onClick = { onStickerRemove(selectedStickerIndex!!) }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Trash sticker", tint = Color.Red)
                }
            }
        }

        // Customizer Controls & Editor Plate (Switch themes, unlock sticker packs)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(24.dp, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp), spotColor = Color(0xFFFFC2D1))
                .border(width = 1.5.dp, color = Color(0xFFFFC2D1), shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Theme selector label / switches
                Text(
                    text = "Select Kitty Theme Background:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val themes = listOf(
                        Triple("hello_kitty", "Kitty Pink", CatDrawingHelper.HelloKittyPink),
                        Triple("cozy_neko", "Calico Neko", CatDrawingHelper.CozyNekoCream),
                        Triple("sweet_strawberry", "Strawberry", CatDrawingHelper.StrawberryPastelRed),
                        Triple("mew_milk", "Mew Milk", CatDrawingHelper.MewMilkBlue)
                    )

                    themes.forEach { (tid, tname, color) ->
                        val isSelected = note.themeId == tid
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(color)
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) CatDrawingHelper.CharcoalDark else Color.LightGray,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onThemeSelect(tid) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tname,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CatDrawingHelper.CharcoalDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Divider line
                HorizontalDivider(modifier = Modifier.padding(bottom = 12.dp), color = Color(0xFFF3F3F3))

                // Picker tab layout for sticker packs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Customize Note Stickers:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val tabs = listOf(
                            "bows" to "Kitty Bows",
                            "neko" to "Playful Neko",
                            "treats" to "Sweet Treats"
                        )
                        tabs.forEach { (key, title) ->
                            val active = activeStickerTab == key
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (active) Color(0xFFFFF1F3) else Color.Transparent)
                                    .border(
                                        width = if (active) 1.5.dp else 0.dp,
                                        color = if (active) Color(0xFFFFC2D1) else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { activeStickerTab = key }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (active) Color(0xFFBA1A5E) else Color.Gray
                                )
                            }
                        }
                    }
                }

                // Sticker Selection Row
                val selectedPack = when (activeStickerTab) {
                    "bows" -> listOf("bow_pink", "bow_red", "cute_heart")
                    "neko" -> listOf("paw_pink", "fish_bone", "happy_kitty")
                    "treats" -> listOf("strawberry", "gold_star", "yarn_ball", "milk_bottle")
                    else -> emptyList()
                }

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    items(selectedPack) { stickerId ->
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFFFFC2D1))
                                .background(Color(0xFFFFF1F3), RoundedCornerShape(16.dp))
                                .border(1.5.dp, Color(0xFFFF4D6D), RoundedCornerShape(16.dp))
                                .clickable { onAddSticker(stickerId) }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                CatDrawingHelper.drawStickerComposable(this, stickerId, size.width)
                            }
                        }
                    }
                }
            }
        }
    }
}
