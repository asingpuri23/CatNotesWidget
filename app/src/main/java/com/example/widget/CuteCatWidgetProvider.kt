package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.AppDatabase
import com.example.data.Note
import com.example.ui.CatDrawingHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CuteCatWidgetProvider : AppWidgetProvider() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val database = AppDatabase.getDatabase(context)

        scope.launch {
            // Retrieve notes sorted by updatedAt DESC
            val notes = database.noteDao().getAllNotes().first()
            val latestNote = notes.firstOrNull()

            for (widgetId in appWidgetIds) {
                updateWidget(context, appWidgetManager, widgetId, latestNote)
            }
        }
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        widgetId: Int,
        note: Note?
    ) {
        val views = RemoteViews(context.packageName, R.layout.cute_cat_widget)
        val bitmap = renderNoteToBitmap(context, note)
        views.setImageViewBitmap(R.id.widget_image, bitmap)

        // Setup pending intent to open app with active note ID
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            if (note != null) {
                putExtra("NOTE_ID", note.id)
            }
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            widgetId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

        appWidgetManager.updateAppWidget(widgetId, views)
    }

    private fun renderNoteToBitmap(context: Context, note: Note?): Bitmap {
        val size = 512
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paintFill = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        val paintStroke = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            color = 0xFF2C2C2C.toInt()
            strokeWidth = 4f
        }

        if (note == null) {
            // Draw an onboarding cat face
            paintFill.color = 0xFFFFE3EC.toInt() // pastel pink
            canvas.drawRoundRect(RectF(10f, 30f, size - 10f, size - 10f), 35f, 35f, paintFill)
            canvas.drawRoundRect(RectF(10f, 30f, size - 10f, size - 10f), 35f, 35f, paintStroke)

            // Hello Kitty style ears
            val earPath = android.graphics.Path()
            earPath.moveTo(size * 0.12f, 30f)
            earPath.quadTo(size * 0.18f, -10f, size * 0.32f, 30f)
            paintFill.color = 0xFFFFFFFF.toInt()
            canvas.drawPath(earPath, paintFill)
            canvas.drawPath(earPath, paintStroke)

            earPath.reset()
            earPath.moveTo(size * 0.68f, 30f)
            earPath.quadTo(size * 0.82f, -10f, size * 0.88f, 30f)
            canvas.drawPath(earPath, paintFill)
            canvas.drawPath(earPath, paintStroke)

            // Draw Bow
            CatDrawingHelper.drawStickerAndroid(canvas, "bow_red", size * 0.28f, 38f, 50f)

            // Sleepy eyes
            paintStroke.strokeWidth = 3f
            canvas.drawArc(RectF(size * 0.35f - 15f, size * 0.45f, size * 0.35f + 15f, size * 0.45f + 20f), 0f, 180f, false, paintStroke)
            canvas.drawArc(RectF(size * 0.65f - 15f, size * 0.45f, size * 0.65f + 15f, size * 0.45f + 20f), 0f, 180f, false, paintStroke)

            // Cute yellow nose
            paintFill.color = 0xFFFFEA00.toInt()
            canvas.drawCircle(size * 0.5f, size * 0.55f, 10f, paintFill)
            canvas.drawCircle(size * 0.5f, size * 0.55f, 10f, paintStroke)

            // Whiskers
            canvas.drawLine(15f, size * 0.5f, 65f, size * 0.51f, paintStroke)
            canvas.drawLine(15f, size * 0.54f, 70f, size * 0.54f, paintStroke)
            canvas.drawLine(15f, size * 0.58f, 65f, size * 0.57f, paintStroke)

            canvas.drawLine(size - 15f, size * 0.5f, size - 65f, size * 0.51f, paintStroke)
            canvas.drawLine(size - 15f, size * 0.54f, size - 70f, size * 0.54f, paintStroke)
            canvas.drawLine(size - 15f, size * 0.58f, size - 65f, size * 0.57f, paintStroke)

            // Onboarding Text
            val textPaint = TextPaint().apply {
                isAntiAlias = true
                color = 0xFF2C2C2C.toInt()
                textSize = 28f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("No cute notes yet!", size * 0.5f, size * 0.72f, textPaint)
            textPaint.textSize = 21f
            canvas.drawText("Tap to write a note", size * 0.5f, size * 0.79f, textPaint)
            canvas.drawText("& decorate with stickers!", size * 0.5f, size * 0.84f, textPaint)

            return bitmap
        }

        // Draw note theme background
        CatDrawingHelper.drawCatThemeBackgroundAndroid(canvas, note.themeId, size.toFloat(), size.toFloat())

        // Render Title
        val titlePaint = TextPaint().apply {
            isAntiAlias = true
            color = 0xFF2C2C2C.toInt()
            textSize = 34f
            strokeWidth = 3f
            style = Paint.Style.FILL
            isFakeBoldText = true
        }

        // Clip Title to available width
        var dispTitle = note.title
        if (dispTitle.length > 20) {
            dispTitle = dispTitle.substring(0, 18) + ".."
        }
        canvas.drawText(dispTitle, 45f, 105f, titlePaint)

        // Title bottom divider line
        val linePaint = Paint().apply {
            isAntiAlias = true
            color = CatDrawingHelper.getThemeAccentColor(note.themeId)
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }
        canvas.drawLine(40f, 125f, size - 40f, 125f, linePaint)

        // Draw Content using StaticLayout for automatic multi-line wrapping
        val contentPaint = TextPaint().apply {
            isAntiAlias = true
            color = 0xFF2C2C2C.toInt()
            textSize = 24f
        }

        val paddingX = 40
        val textWidth = size - (paddingX * 2)

        val staticLayout = StaticLayout.Builder.obtain(note.content, 0, note.content.length, contentPaint, textWidth)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(6f, 1f)
            .setMaxLines(9)
            .build()

        canvas.save()
        canvas.translate(paddingX.toFloat(), 150f)
        staticLayout.draw(canvas)
        canvas.restore()

        // Render Stickers in relative coordinates
        val stickers = note.getStickers()
        for (sticker in stickers) {
            val sx = sticker.relativeX * size
            val sy = sticker.relativeY * size
            val sSize = 65f * sticker.scale
            CatDrawingHelper.drawStickerAndroid(canvas, sticker.stickerId, sx, sy, sSize)
        }

        return bitmap
    }
}
