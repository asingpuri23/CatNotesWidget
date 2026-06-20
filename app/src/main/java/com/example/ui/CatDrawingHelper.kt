package com.example.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.cos
import kotlin.math.sin

object CatDrawingHelper {

    // --- COLOR CONSTANTS ---
    val HelloKittyPink = Color(0xFFFFE3EC)
    val HelloKittyDarkPink = Color(0xFFFF8DA1)
    val CozyNekoCream = Color(0xFFFFF7E6)
    val CalicoOrange = Color(0xFFFFB74D)
    val CalicoBrown = Color(0xFF8D6E63)
    val StrawberryPastelRed = Color(0xFFFFEBEE)
    val StrawberryRed = Color(0xFFFF5252)
    val StrawberryGreen = Color(0xFF81C784)
    val MewMilkSky = Color(0xFFE8F5E9) // soft pastel pastel mint/blue-green
    val MewMilkBlue = Color(0xFFE3F2FD)
    val MilkCapBlue = Color(0xFF90CAF9)
    val CharcoalDark = Color(0xFF2C2C2C)

    // Android Graphics Equivs for Widget Paint
    fun getThemeBgColor(themeId: String): Int {
        return when (themeId) {
            "hello_kitty" -> 0xFFFFE3EC.toInt()
            "cozy_neko" -> 0xFFFFF7E6.toInt()
            "sweet_strawberry" -> 0xFFFFEBEE.toInt()
            "mew_milk" -> 0xFFE3F2FD.toInt()
            else -> 0xFFFFE3EC.toInt()
        }
    }

    fun getThemeAccentColor(themeId: String): Int {
        return when (themeId) {
            "hello_kitty" -> 0xFFFF8DA1.toInt()
            "cozy_neko" -> 0xFFFFB74D.toInt()
            "sweet_strawberry" -> 0xFFFF5252.toInt()
            "mew_milk" -> 0xFF90CAF9.toInt()
            else -> 0xFFFF8DA1.toInt()
        }
    }

    // ==========================================
    // 1. JETPACK COMPOSE DRAWSCOPE RENDERING
    // ==========================================

    fun drawCatThemeBackground(drawScope: DrawScope, themeId: String, size: Size) {
        val width = size.width
        val height = size.height

        when (themeId) {
            "hello_kitty" -> {
                // Main Rounded Rect already drawn, let's draw Ears and Bow
                // Left Ear
                val leftEarPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(width * 0.1f, 0f)
                    quadraticTo(width * 0.15f, -height * 0.12f, width * 0.32f, -height * 0.01f)
                    lineTo(width * 0.3f, 0f)
                    close()
                }
                drawScope.drawPath(leftEarPath, Color.White)
                drawScope.drawPath(leftEarPath, CharcoalDark, style = Stroke(width = 4f))

                // Right Ear
                val rightEarPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(width * 0.7f, 0f)
                    quadraticTo(width * 0.85f, -height * 0.12f, width * 0.9f, 0f)
                    close()
                }
                drawScope.drawPath(rightEarPath, Color.White)
                drawScope.drawPath(rightEarPath, CharcoalDark, style = Stroke(width = 4f))

                // Left Ear Inside Pink
                val leftEarInside = androidx.compose.ui.graphics.Path().apply {
                    moveTo(width * 0.15f, 0f)
                    quadraticTo(width * 0.18f, -height * 0.07f, width * 0.26f, -height * 0.01f)
                    close()
                }
                drawScope.drawPath(leftEarInside, HelloKittyDarkPink)

                // Hello Kitty Red Bow near the left ear
                drawBow(drawScope, Offset(width * 0.28f, height * 0.02f), 35f)

                // Face decorations in the corners - whiskers on the sides
                // Left side whiskers
                drawScope.drawLine(CharcoalDark, Offset(10f, height * 0.4f), Offset(55f, height * 0.42f), strokeWidth = 4f)
                drawScope.drawLine(CharcoalDark, Offset(10f, height * 0.45f), Offset(60f, height * 0.45f), strokeWidth = 4f)
                drawScope.drawLine(CharcoalDark, Offset(10f, height * 0.5f), Offset(55f, height * 0.48f), strokeWidth = 4f)

                // Right side whiskers
                drawScope.drawLine(CharcoalDark, Offset(width - 10f, height * 0.4f), Offset(width - 55f, height * 0.42f), strokeWidth = 4f)
                drawScope.drawLine(CharcoalDark, Offset(width - 10f, height * 0.45f), Offset(width - 60f, height * 0.45f), strokeWidth = 4f)
                drawScope.drawLine(CharcoalDark, Offset(width - 10f, height * 0.5f), Offset(width - 55f, height * 0.48f), strokeWidth = 4f)

                // Little Hello Kitty outline at the bottom right
                val kittyStampX = width * 0.85f
                val kittyStampY = height * 0.88f
                drawScope.drawCircle(Color.White, radius = 24f, center = Offset(kittyStampX, kittyStampY))
                drawScope.drawCircle(CharcoalDark, radius = 24f, center = Offset(kittyStampX, kittyStampY), style = Stroke(width = 3f))
                // Tiny eyes & nose
                drawScope.drawCircle(CharcoalDark, radius = 2.5f, center = Offset(kittyStampX - 8f, kittyStampY - 2f))
                drawScope.drawCircle(CharcoalDark, radius = 2.5f, center = Offset(kittyStampX + 8f, kittyStampY - 2f))
                drawScope.drawCircle(Color(0xFFFFEA00), radius = 2f, center = Offset(kittyStampX, kittyStampY + 4f))
                // Tiny ribbon
                drawScope.drawCircle(Color.Red, radius = 4f, center = Offset(kittyStampX - 10f, kittyStampY - 14f))
            }
            "cozy_neko" -> {
                // Calico Cat theme - Cream bg, Orange and brown patches
                // Rounded patches in top-right and left corners
                // Left Ear
                val leftEarPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(width * 0.12f, 0f)
                    quadraticTo(width * 0.22f, -height * 0.1f, width * 0.35f, 0f)
                    close()
                }
                drawScope.drawPath(leftEarPath, CalicoBrown)
                drawScope.drawPath(leftEarPath, CharcoalDark, style = Stroke(width = 4f))

                // Right Ear
                val rightEarPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(width * 0.65f, 0f)
                    quadraticTo(width * 0.78f, -height * 0.1f, width * 0.88f, 0f)
                    close()
                }
                drawScope.drawPath(rightEarPath, CalicoOrange)
                drawScope.drawPath(rightEarPath, CharcoalDark, style = Stroke(width = 4f))

                // Draw orange sleepy cat patches on the sides
                drawScope.drawArc(
                    color = CalicoOrange,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(width * 0.75f, height * 0.2f),
                    size = Size(width * 0.25f, height * 0.18f)
                )

                // Calico sleepy cat face sleeping in bottom-left
                val faceX = width * 0.18f
                val faceY = height * 0.88f
                // Sleeping curved eyes
                drawScope.drawArc(
                    color = CharcoalDark,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(faceX - 14f, faceY - 5f),
                    size = Size(10f, 6f),
                    style = Stroke(width = 3f)
                )
                drawScope.drawArc(
                    color = CharcoalDark,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(faceX + 4f, faceY - 5f),
                    size = Size(10f, 6f),
                    style = Stroke(width = 3f)
                )
                // Mouth anchor (y shape)
                drawScope.drawLine(CharcoalDark, Offset(faceX, faceY + 1f), Offset(faceX, faceY + 4f), strokeWidth = 3f)
                drawScope.drawArc(
                    color = CharcoalDark,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(faceX - 6f, faceY + 3f),
                    size = Size(6f, 5f),
                    style = Stroke(width = 3f)
                )
                drawScope.drawArc(
                    color = CharcoalDark,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(faceX, faceY + 3f),
                    size = Size(6f, 5f),
                    style = Stroke(width = 3f)
                )
                // Pink cheeks
                drawScope.drawCircle(Color(0xFFFF8DA1), radius = 5f, center = Offset(faceX - 22f, faceY + 1f))
                drawScope.drawCircle(Color(0xFFFF8DA1), radius = 5f, center = Offset(faceX + 22f, faceY + 1f))
            }
            "sweet_strawberry" -> {
                // Strawberry style - strawberry seeds on right side
                // Left Ear
                val leftEarPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(width * 0.12f, 0f)
                    quadraticTo(width * 0.22f, -height * 0.1f, width * 0.35f, 0f)
                    close()
                }
                drawScope.drawPath(leftEarPath, StrawberryRed)
                drawScope.drawPath(leftEarPath, CharcoalDark, style = Stroke(width = 4f))

                // Right Ear
                val rightEarPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(width * 0.65f, 0f)
                    quadraticTo(width * 0.78f, -height * 0.1f, width * 0.88f, 0f)
                    close()
                }
                drawScope.drawPath(rightEarPath, StrawberryRed)
                drawScope.drawPath(rightEarPath, CharcoalDark, style = Stroke(width = 4f))

                // Leaf clusters on top header (resembles strawberry top leaf)
                val leafPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(width * 0.4f, 0f)
                    quadraticTo(width * 0.5f, height * 0.07f, width * 0.6f, 0f)
                    quadraticTo(width * 0.5f, -height * 0.05f, width * 0.4f, 0f)
                }
                drawScope.drawPath(leafPath, StrawberryGreen)
                drawScope.drawPath(leafPath, CharcoalDark, style = Stroke(width = 3f))

                // Cute yellow strawberry seeds dotted vertically on the right
                val seeds = listOf(
                    Offset(width * 0.88f, height * 0.22f),
                    Offset(width * 0.92f, height * 0.35f),
                    Offset(width * 0.89f, height * 0.48f),
                    Offset(width * 0.93f, height * 0.6f)
                )
                for (seed in seeds) {
                    drawScope.drawOval(
                        Color(0xFFFFEB3B),
                        topLeft = Offset(seed.x - 3f, seed.y - 5f),
                        size = Size(6f, 10f)
                    )
                    drawScope.drawOval(
                        CharcoalDark,
                        topLeft = Offset(seed.x - 3f, seed.y - 5f),
                        size = Size(6f, 10f),
                        style = Stroke(width = 2f)
                    )
                }

                // Small Strawberry in bottom-left
                drawStrawberry(drawScope, Offset(width * 0.15f, height * 0.88f), 30f)
            }
            "mew_milk" -> {
                // Sky blue milk cat
                // Left Ear
                val leftEarPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(width * 0.12f, 0f)
                    quadraticTo(width * 0.22f, -height * 0.1f, width * 0.35f, 0f)
                    close()
                }
                drawScope.drawPath(leftEarPath, Color.White)
                drawScope.drawPath(leftEarPath, CharcoalDark, style = Stroke(width = 4f))

                // Right Ear
                val rightEarPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(width * 0.65f, 0f)
                    quadraticTo(width * 0.78f, -height * 0.1f, width * 0.88f, 0f)
                    close()
                }
                drawScope.drawPath(rightEarPath, Color.White)
                drawScope.drawPath(rightEarPath, CharcoalDark, style = Stroke(width = 4f))

                // Sky blue patch on ears
                val leftEarInside = androidx.compose.ui.graphics.Path().apply {
                    moveTo(width * 0.18f, 0f)
                    quadraticTo(width * 0.22f, -height * 0.06f, width * 0.3f, 0f)
                    close()
                }
                drawScope.drawPath(leftEarInside, MilkCapBlue)

                // Milk bubbles in bottom-right
                drawScope.drawCircle(Color.White, radius = 10f, center = Offset(width * 0.8f, height * 0.85f))
                drawScope.drawCircle(CharcoalDark, radius = 10f, center = Offset(width * 0.8f, height * 0.85f), style = Stroke(width = 3f))

                drawScope.drawCircle(Color.White, radius = 6f, center = Offset(width * 0.88f, height * 0.82f))
                drawScope.drawCircle(CharcoalDark, radius = 6f, center = Offset(width * 0.88f, height * 0.82f), style = Stroke(width = 2.5f))

                drawScope.drawCircle(Color.White, radius = 14f, center = Offset(width * 0.85f, height * 0.9f))
                drawScope.drawCircle(CharcoalDark, radius = 14f, center = Offset(width * 0.85f, height * 0.9f), style = Stroke(width = 3f))

                // Small milk carton drawn in bottom-left
                drawMilkCarton(drawScope, Offset(width * 0.15f, height * 0.86f), 24f)
            }
        }
    }

    private fun drawBow(drawScope: DrawScope, center: Offset, size: Float) {
        // Red ribbons centered at 'center' with size 'size'
        val halfSize = size / 2f
        // Left loop
        val leftPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(center.x, center.y)
            cubicTo(
                center.x - halfSize * 0.8f, center.y - halfSize * 1.2f,
                center.x - halfSize * 1.5f, center.y - halfSize * 0.5f,
                center.x - halfSize * 1.5f, center.y + halfSize * 0.2f
            )
            cubicTo(
                center.x - halfSize * 1.5f, center.y + halfSize * 0.8f,
                center.x - halfSize * 0.8f, center.y + halfSize * 1.2f,
                center.x, center.y
            )
        }
        drawScope.drawPath(leftPath, Color.Red)
        drawScope.drawPath(leftPath, CharcoalDark, style = Stroke(width = 3.5f))

        // Right loop
        val rightPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(center.x, center.y)
            cubicTo(
                center.x + halfSize * 0.8f, center.y - halfSize * 1.2f,
                center.x + halfSize * 1.5f, center.y - halfSize * 0.5f,
                center.x + halfSize * 1.5f, center.y + halfSize * 0.2f
            )
            cubicTo(
                center.x + halfSize * 1.5f, center.y + halfSize * 0.8f,
                center.x + halfSize * 0.8f, center.y + halfSize * 1.2f,
                center.x, center.y
            )
        }
        drawScope.drawPath(rightPath, Color.Red)
        drawScope.drawPath(rightPath, CharcoalDark, style = Stroke(width = 3.5f))

        // Left Ribbon tail
        val tailLeft = androidx.compose.ui.graphics.Path().apply {
            moveTo(center.x - halfSize * 0.2f, center.y + halfSize * 0.3f)
            lineTo(center.x - halfSize * 1.2f, center.y + halfSize * 1.6f)
            lineTo(center.x - halfSize * 0.6f, center.y + halfSize * 1.6f)
            close()
        }
        drawScope.drawPath(tailLeft, Color.Red)
        drawScope.drawPath(tailLeft, CharcoalDark, style = Stroke(width = 3f))

        // Right Ribbon tail
        val tailRight = androidx.compose.ui.graphics.Path().apply {
            moveTo(center.x + halfSize * 0.2f, center.y + halfSize * 0.3f)
            lineTo(center.x + halfSize * 1.2f, center.y + halfSize * 1.6f)
            lineTo(center.x + halfSize * 0.6f, center.y + halfSize * 1.6f)
            close()
        }
        drawScope.drawPath(tailRight, Color.Red)
        drawScope.drawPath(tailRight, CharcoalDark, style = Stroke(width = 3f))

        // Center knot
        drawScope.drawCircle(Color.Red, radius = halfSize * 0.5f, center = center)
        drawScope.drawCircle(CharcoalDark, radius = halfSize * 0.5f, center = center, style = Stroke(width = 3.5f))
    }

    private fun drawStrawberry(drawScope: DrawScope, center: Offset, size: Float) {
        val half = size / 2f
        // Heart-like berry base
        val berryPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(center.x, center.y - half)
            cubicTo(center.x + half * 1.2f, center.y - half * 1.2f, center.x + half * 1.2f, center.y + half * 0.3f, center.x, center.y + half)
            cubicTo(center.x - half * 1.2f, center.y + half * 0.3f, center.x - half * 1.2f, center.y - half * 1.2f, center.x, center.y - half)
        }
        drawScope.drawPath(berryPath, StrawberryRed)
        drawScope.drawPath(berryPath, CharcoalDark, style = Stroke(width = 3f))

        // Leaves on top
        drawScope.drawCircle(StrawberryGreen, radius = half * 0.4f, center = Offset(center.x, center.y - half))
        drawScope.drawCircle(CharcoalDark, radius = half * 0.4f, center = Offset(center.x, center.y - half), style = Stroke(width = 2.5f))

        // Small seeds (dots)
        val seeds = listOf(
            Offset(center.x - half * 0.4f, center.y - half * 0.2f),
            Offset(center.x + half * 0.4f, center.y - half * 0.2f),
            Offset(center.x, center.y + half * 0.1f),
            Offset(center.x - half * 0.2f, center.y + half * 0.4f),
            Offset(center.x + half * 0.2f, center.y + half * 0.4f)
        )
        for (seed in seeds) {
            drawScope.drawCircle(Color(0xFFFFEB3B), radius = 2f, center = seed)
        }
    }

    private fun drawMilkCarton(drawScope: DrawScope, center: Offset, size: Float) {
        val half = size / 2f
        // Milk carton rectangular body
        drawScope.drawRect(
            color = Color.White,
            topLeft = Offset(center.x - half, center.y - half * 0.4f),
            size = Size(size, size * 1.2f)
        )
        drawScope.drawRect(
            color = CharcoalDark,
            topLeft = Offset(center.x - half, center.y - half * 0.4f),
            size = Size(size, size * 1.2f),
            style = Stroke(width = 3f)
        )

        // Cartoon triangular top roof
        val roofPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(center.x - half, center.y - half * 0.4f)
            lineTo(center.x, center.y - half * 1.1f)
            lineTo(center.x + half, center.y - half * 0.4f)
            close()
        }
        drawScope.drawPath(roofPath, MilkCapBlue)
        drawScope.drawPath(roofPath, CharcoalDark, style = Stroke(width = 3f))

        // Cute face on the milk carton
        drawScope.drawCircle(CharcoalDark, radius = 2f, center = Offset(center.x - 5f, center.y + 4f))
        drawScope.drawCircle(CharcoalDark, radius = 2f, center = Offset(center.x + 5f, center.y + 4f))
        drawScope.drawArc(
            color = CharcoalDark,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(center.x - 2.5f, center.y + 5f),
            size = Size(5f, 4f),
            style = Stroke(width = 2f)
        )
    }

    fun drawStickerComposable(drawScope: DrawScope, stickerId: String, size: Float) {
        val center = Offset(0f, 0f) // The calling composable will be wrapped in transform
        when (stickerId) {
            "bow_pink" -> {
                val half = size / 2f
                // We will paint it pink instead of red
                // Center-left
                val leftPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(0f, 0f)
                    cubicTo(
                        -half * 0.8f, -half * 1.2f,
                        -half * 1.5f, -half * 0.5f,
                        -half * 1.5f, half * 0.2f
                    )
                    cubicTo(
                        -half * 1.5f, half * 0.8f,
                        -half * 0.8f, half * 1.2f,
                        0f, 0f
                    )
                }
                drawScope.drawPath(leftPath, HelloKittyDarkPink)
                drawScope.drawPath(leftPath, CharcoalDark, style = Stroke(width = 3.5f))

                // Right loop
                val rightPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(0f, 0f)
                    cubicTo(
                        half * 0.8f, -half * 1.2f,
                        half * 1.5f, -half * 0.5f,
                        half * 1.5f, half * 0.2f
                    )
                    cubicTo(
                        half * 1.5f, half * 0.8f,
                        half * 0.8f, half * 1.2f,
                        0f, 0f
                    )
                }
                drawScope.drawPath(rightPath, HelloKittyDarkPink)
                drawScope.drawPath(rightPath, CharcoalDark, style = Stroke(width = 3.5f))

                // Left Ribbon tail
                val tailLeft = androidx.compose.ui.graphics.Path().apply {
                    moveTo(-half * 0.2f, half * 0.3f)
                    lineTo(-half * 1.2f, half * 1.4f)
                    lineTo(-half * 0.6f, half * 1.4f)
                    close()
                }
                drawScope.drawPath(tailLeft, HelloKittyDarkPink)
                drawScope.drawPath(tailLeft, CharcoalDark, style = Stroke(width = 3f))

                // Right Ribbon tail
                val tailRight = androidx.compose.ui.graphics.Path().apply {
                    moveTo(half * 0.2f, half * 0.3f)
                    lineTo(half * 1.2f, half * 1.4f)
                    lineTo(half * 0.6f, half * 1.4f)
                    close()
                }
                drawScope.drawPath(tailRight, HelloKittyDarkPink)
                drawScope.drawPath(tailRight, CharcoalDark, style = Stroke(width = 3f))

                // Center
                drawScope.drawCircle(HelloKittyDarkPink, radius = half * 0.45f, center = center)
                drawScope.drawCircle(CharcoalDark, radius = half * 0.45f, center = center, style = Stroke(width = 3.5f))
            }
            "bow_red" -> {
                drawBow(drawScope, center, size)
            }
            "paw_pink" -> {
                // Main pad
                val mainYOffset = size * 0.15f
                drawScope.drawOval(
                    color = Color.White,
                    topLeft = Offset(-size * 0.4f, -size * 0.2f + mainYOffset),
                    size = Size(size * 0.8f, size * 0.6f)
                )
                drawScope.drawOval(
                    color = CharcoalDark,
                    topLeft = Offset(-size * 0.4f, -size * 0.2f + mainYOffset),
                    size = Size(size * 0.8f, size * 0.6f),
                    style = Stroke(width = 3f)
                )
                // Inside pink pad
                drawScope.drawOval(
                    color = HelloKittyDarkPink,
                    topLeft = Offset(-size * 0.28f, -size * 0.1f + mainYOffset),
                    size = Size(size * 0.56f, size * 0.42f)
                )

                // 4 toes
                val toeSize = size * 0.22f
                val toes = listOf(
                    Offset(-size * 0.35f, -size * 0.25f + mainYOffset),
                    Offset(-size * 0.12f, -size * 0.38f + mainYOffset),
                    Offset(size * 0.12f, -size * 0.38f + mainYOffset),
                    Offset(size * 0.35f, -size * 0.25f + mainYOffset)
                )
                for (toe in toes) {
                    drawScope.drawCircle(Color.White, radius = toeSize / 2f, center = toe)
                    drawScope.drawCircle(CharcoalDark, radius = toeSize / 2f, center = toe, style = Stroke(width = 2.5f))
                    // Pink inner
                    drawScope.drawCircle(HelloKittyDarkPink, radius = toeSize * 0.32f, center = toe)
                }
            }
            "fish_bone" -> {
                val len = size * 0.8f
                // fish head on left
                val headPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(-len * 0.5f, 0f)
                    lineTo(-len * 0.25f, -size * 0.25f)
                    lineTo(-len * 0.25f, size * 0.25f)
                    close()
                }
                drawScope.drawPath(headPath, CharcoalDark)

                // fish eye
                drawScope.drawCircle(Color.White, radius = 3f, center = Offset(-len * 0.4f, -2f))

                // spine
                drawScope.drawLine(CharcoalDark, Offset(-len * 0.25f, 0f), Offset(len * 0.4f, 0f), strokeWidth = 5f)

                // three bone ribs
                drawScope.drawLine(CharcoalDark, Offset(-len * 0.1f, -size * 0.2f), Offset(-len * 0.1f, size * 0.2f), strokeWidth = 4f)
                drawScope.drawLine(CharcoalDark, Offset(len * 0.05f, -size * 0.2f), Offset(len * 0.05f, size * 0.2f), strokeWidth = 4f)
                drawScope.drawLine(CharcoalDark, Offset(len * 0.2f, -size * 0.2f), Offset(len * 0.2f, size * 0.2f), strokeWidth = 4f)

                // fish tail on the right
                val tailPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(len * 0.4f, 0f)
                    lineTo(len * 0.55f, -size * 0.22f)
                    lineTo(len * 0.48f, 0f)
                    lineTo(len * 0.55f, size * 0.22f)
                    close()
                }
                drawScope.drawPath(tailPath, CharcoalDark)
            }
            "strawberry" -> {
                drawStrawberry(drawScope, center, size)
            }
            "cute_heart" -> {
                val half = size / 2f
                val hPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(0f, -half * 0.5f)
                    cubicTo(half * 0.7f, -half * 1.3f, half * 1.3f, -half * 0.3f, 0f, half * 0.9f)
                    cubicTo(-half * 1.3f, -half * 0.3f, -half * 0.7f, -half * 1.3f, 0f, -half * 0.5f)
                }
                drawScope.drawPath(hPath, StrawberryRed)
                drawScope.drawPath(hPath, CharcoalDark, style = Stroke(width = 3.5f))

                // Highlight gloss mark
                drawScope.drawCircle(Color.White, radius = size * 0.08f, center = Offset(-size * 0.18f, -size * 0.15f))
            }
            "gold_star" -> {
                val outerRadius = size * 0.5f
                val innerRadius = size * 0.2f
                val starPath = androidx.compose.ui.graphics.Path().apply {
                    var angle = -90f
                    val step = 360f / 10f
                    for (i in 0 until 10) {
                        val r = if (i % 2 == 0) outerRadius else innerRadius
                        val x = r * cos(Math.toRadians(angle.toDouble())).toFloat()
                        val y = r * sin(Math.toRadians(angle.toDouble())).toFloat()
                        if (i == 0) moveTo(x, y) else lineTo(x, y)
                        angle += step
                    }
                    close()
                }
                drawScope.drawPath(starPath, Color(0xFFFFD54F))
                drawScope.drawPath(starPath, CharcoalDark, style = Stroke(width = 3f))
            }
            "yarn_ball" -> {
                // Blue yarn ball
                drawScope.drawCircle(Color(0xFF81D4FA), radius = size * 0.42f, center = center)
                drawScope.drawCircle(CharcoalDark, radius = size * 0.42f, center = center, style = Stroke(width = 3.5f))

                // Scribble texture of wool wraps
                drawScope.drawArc(
                    color = CharcoalDark,
                    startAngle = 30f,
                    sweepAngle = 120f,
                    useCenter = false,
                    topLeft = Offset(-size * 0.3f, -size * 0.3f),
                    size = Size(size * 0.6f, size * 0.6f),
                    style = Stroke(width = 2.5f)
                )
                drawScope.drawArc(
                    color = CharcoalDark,
                    startAngle = 180f,
                    sweepAngle = 120f,
                    useCenter = false,
                    topLeft = Offset(-size * 0.35f, -size * 0.25f),
                    size = Size(size * 0.7f, size * 0.5f),
                    style = Stroke(width = 2.5f)
                )
                drawScope.drawLine(CharcoalDark, Offset(-size * 0.15f, -size * 0.15f), Offset(size * 0.2f, size * 0.2f), strokeWidth = 2.5f)

                // Loose string tail waving off
                val stringPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(size * 0.2f, size * 0.3f)
                    quadraticTo(size * 0.4f, size * 0.55f, size * 0.3f, size * 0.7f)
                    quadraticTo(size * 0.2f, size * 0.85f, size * 0.5f, size * 0.9f)
                }
                drawScope.drawPath(stringPath, Color(0xFF81D4FA), style = Stroke(width = 3f))
                drawScope.drawPath(stringPath, CharcoalDark, style = Stroke(width = 1.5f))
            }
            "happy_kitty" -> {
                // Small kitty head sticker
                drawScope.drawCircle(Color.White, radius = size * 0.42f, center = center)
                drawScope.drawCircle(CharcoalDark, radius = size * 0.42f, center = center, style = Stroke(width = 3f))

                // Ears
                val leftEar = androidx.compose.ui.graphics.Path().apply {
                    moveTo(-size * 0.35f, -size * 0.22f)
                    lineTo(-size * 0.43f, -size * 0.52f)
                    lineTo(-size * 0.15f, -size * 0.39f)
                }
                drawScope.drawPath(leftEar, Color.White)
                drawScope.drawPath(leftEar, CharcoalDark, style = Stroke(width = 3f))

                val rightEar = androidx.compose.ui.graphics.Path().apply {
                    moveTo(size * 0.35f, -size * 0.22f)
                    lineTo(size * 0.43f, -size * 0.52f)
                    lineTo(size * 0.15f, -size * 0.39f)
                }
                drawScope.drawPath(rightEar, Color.White)
                drawScope.drawPath(rightEar, CharcoalDark, style = Stroke(width = 3f))

                // Tiny eyes (winking left, open right)
                drawScope.drawArc(
                    color = CharcoalDark,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(-size * 0.23f, -size * 0.12f),
                    size = Size(size * 0.13f, size * 0.1f),
                    style = Stroke(width = 2.5f)
                )
                drawScope.drawCircle(CharcoalDark, radius = size * 0.04f, center = Offset(size * 0.15f, -size * 0.08f))

                // Hello Kitty Button nose
                drawScope.drawCircle(Color(0xFFFFEA00), radius = size * 0.04f, center = Offset(0f, 0.02f))
                drawScope.drawCircle(CharcoalDark, radius = size * 0.04f, center = Offset(0f, 0.02f), style = Stroke(width = 1.5f))

                // Tiny whiskers too!
                drawScope.drawLine(CharcoalDark, Offset(-size * 0.35f, 0f), Offset(-size * 0.52f, -size * 0.03f), strokeWidth = 2f)
                drawScope.drawLine(CharcoalDark, Offset(-size * 0.35f, size * 0.06f), Offset(-size * 0.52f, size * 0.08f), strokeWidth = 2f)
                drawScope.drawLine(CharcoalDark, Offset(size * 0.35f, 0f), Offset(size * 0.52f, -size * 0.03f), strokeWidth = 2f)
                drawScope.drawLine(CharcoalDark, Offset(size * 0.35f, size * 0.06f), Offset(size * 0.52f, size * 0.08f), strokeWidth = 2f)
            }
            "milk_bottle" -> {
                drawMilkCarton(drawScope, center, size)
            }
        }
    }

    // ==========================================
    // 2. ANDROID WIDGET BITMAP RENDERING (FOR HOME SCREEN WIDGETS)
    // ==========================================

    fun drawCatThemeBackgroundAndroid(canvas: Canvas, themeId: String, width: Float, height: Float) {
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

        // Draw general base note paper body
        paintFill.color = getThemeBgColor(themeId)
        val rectNote = RectF(8f, 25f, width - 8f, height - 8f)
        canvas.drawRoundRect(rectNote, 32f, 32f, paintFill)
        canvas.drawRoundRect(rectNote, 32f, 32f, paintStroke)

        when (themeId) {
            "hello_kitty" -> {
                // Draw white ears at top
                val earPath = Path()
                // Left ear
                earPath.reset()
                earPath.moveTo(width * 0.12f, 25f)
                earPath.quadTo(width * 0.18f, -height * 0.03f, width * 0.32f, 25f)
                paintFill.color = 0xFFFFFFFF.toInt()
                canvas.drawPath(earPath, paintFill)
                canvas.drawPath(earPath, paintStroke)

                // Left pink inside
                val leftPink = Path()
                leftPink.moveTo(width * 0.16f, 25f)
                leftPink.quadTo(width * 0.20f, height * 0.02f, width * 0.28f, 25f)
                paintFill.color = 0xFFFF8DA1.toInt()
                canvas.drawPath(leftPink, paintFill)

                // Right ear
                earPath.reset()
                earPath.moveTo(width * 0.68f, 25f)
                earPath.quadTo(width * 0.82f, -height * 0.03f, width * 0.88f, 25f)
                paintFill.color = 0xFFFFFFFF.toInt()
                canvas.drawPath(earPath, paintFill)
                canvas.drawPath(earPath, paintStroke)

                // Whiskers
                canvas.drawLine(15f, height * 0.4f, 55f, height * 0.42f, paintStroke)
                canvas.drawLine(15f, height * 0.45f, 60f, height * 0.45f, paintStroke)
                canvas.drawLine(15f, height * 0.5f, 55f, height * 0.48f, paintStroke)

                canvas.drawLine(width - 15f, height * 0.4f, width - 55f, height * 0.42f, paintStroke)
                canvas.drawLine(width - 15f, height * 0.45f, width - 60f, height * 0.45f, paintStroke)
                canvas.drawLine(width - 15f, height * 0.5f, width - 55f, height * 0.48f, paintStroke)

                // Hello Kitty stamp right bottom
                val stampX = width * 0.82f
                val stampY = height * 0.85f
                paintFill.color = 0xFFFFFFFF.toInt()
                canvas.drawCircle(stampX, stampY, 20f, paintFill)
                canvas.drawCircle(stampX, stampY, 20f, paintStroke)

                // Eyes and yellow nose
                paintFill.color = 0xFF2C2C2C.toInt()
                canvas.drawCircle(stampX - 7f, stampY - 2f, 2f, paintFill)
                canvas.drawCircle(stampX + 7f, stampY - 2f, 2f, paintFill)
                paintFill.color = 0xFFFFEA00.toInt() // Yellow
                canvas.drawCircle(stampX, stampY + 3f, 2f, paintFill)

                // Red bow stamp
                paintFill.color = 0xFFFF0000.toInt()
                canvas.drawCircle(stampX - 10f, stampY - 12f, 4f, paintFill)
            }
            "cozy_neko" -> {
                // Calico Cream Sleepy cat ears
                val earPath = Path()
                // Left brown ear
                earPath.moveTo(width * 0.12f, 25f)
                earPath.quadTo(width * 0.22f, -height * 0.02f, width * 0.35f, 25f)
                paintFill.color = 0xFF8D6E63.toInt() // Brown
                canvas.drawPath(earPath, paintFill)
                canvas.drawPath(earPath, paintStroke)

                // Right orange ear
                earPath.reset()
                earPath.moveTo(width * 0.65f, 25f)
                earPath.quadTo(width * 0.78f, -height * 0.02f, width * 0.88f, 25f)
                paintFill.color = 0xFFFFB74D.toInt() // Calico Orange
                canvas.drawPath(earPath, paintFill)
                canvas.drawPath(earPath, paintStroke)

                // Sleepy eyes in bottom right-ish
                val eyeX = width * 0.78f
                val eyeY = height * 0.85f
                paintStroke.strokeWidth = 3f
                canvas.drawArc(RectF(eyeX - 10f, eyeY - 4f, eyeX, eyeY + 2f), 0f, 180f, false, paintStroke)
                canvas.drawArc(RectF(eyeX + 4f, eyeY - 4f, eyeX + 14f, eyeY + 2f), 0f, 180f, false, paintStroke)

                // Little blush
                val paintBlush = Paint().apply {
                    isAntiAlias = true
                    style = Paint.Style.FILL
                    color = 0xFFFF8DA1.toInt()
                }
                canvas.drawCircle(eyeX - 15f, eyeY + 2f, 4f, paintBlush)
                canvas.drawCircle(eyeX + 20f, eyeY + 2f, 4f, paintBlush)
            }
            "sweet_strawberry" -> {
                // Strawberry style ears
                val earPath = Path()
                earPath.moveTo(width * 0.12f, 25f)
                earPath.quadTo(width * 0.22f, -height * 0.02f, width * 0.35f, 25f)
                paintFill.color = 0xFFFF5252.toInt() // Red
                canvas.drawPath(earPath, paintFill)
                canvas.drawPath(earPath, paintStroke)

                earPath.reset()
                earPath.moveTo(width * 0.65f, 25f)
                earPath.quadTo(width * 0.78f, -height * 0.02f, width * 0.88f, 25f)
                canvas.drawPath(earPath, paintFill)
                canvas.drawPath(earPath, paintStroke)

                // Strawberry leaf top
                val leaf = Path()
                leaf.moveTo(width * 0.42f, 25f)
                leaf.quadTo(width * 0.5f, height * 0.08f, width * 0.58f, 25f)
                leaf.quadTo(width * 0.5f, 20f, width * 0.42f, 25f)
                paintFill.color = 0xFF81C784.toInt() // Green
                canvas.drawPath(leaf, paintFill)
                canvas.drawPath(leaf, paintStroke)

                // Dotted seeds on the right side
                val y1 = height * 0.3f
                val y2 = height * 0.5f
                val y3 = height * 0.7f
                paintFill.color = 0xFFFFEB3B.toInt()
                canvas.drawOval(RectF(width - 24f, y1 - 4f, width - 18f, y1 + 4f), paintFill)
                canvas.drawOval(RectF(width - 24f, y1 - 4f, width - 18f, y1 + 4f), paintStroke)
                canvas.drawOval(RectF(width - 20f, y2 - 4f, width - 14f, y2 + 4f), paintFill)
                canvas.drawOval(RectF(width - 20f, y2 - 4f, width - 14f, y2 + 4f), paintStroke)
                canvas.drawOval(RectF(width - 26f, y3 - 4f, width - 20f, y3 + 4f), paintFill)
                canvas.drawOval(RectF(width - 26f, y3 - 4f, width - 20f, y3 + 4f), paintStroke)
            }
            "mew_milk" -> {
                // Mew milk blue ears
                val earPath = Path()
                earPath.moveTo(width * 0.12f, 25f)
                earPath.quadTo(width * 0.22f, -height * 0.02f, width * 0.35f, 25f)
                paintFill.color = 0xFFFFFFFF.toInt()
                canvas.drawPath(earPath, paintFill)
                canvas.drawPath(earPath, paintStroke)

                earPath.reset()
                earPath.moveTo(width * 0.65f, 25f)
                earPath.quadTo(width * 0.78f, -height * 0.02f, width * 0.88f, 25f)
                canvas.drawPath(earPath, paintFill)
                canvas.drawPath(earPath, paintStroke)

                // Inside blue patch
                val inside = Path()
                inside.moveTo(width * 0.16f, 25f)
                inside.quadTo(width * 0.21f, height * 0.03f, width * 0.30f, 25f)
                paintFill.color = 0xFF90CAF9.toInt()
                canvas.drawPath(inside, paintFill)

                // Bubbles bottom-right
                paintFill.color = 0xFFFFFFFF.toInt()
                canvas.drawCircle(width * 0.82f, height * 0.85f, 8f, paintFill)
                canvas.drawCircle(width * 0.82f, height * 0.85f, 8f, paintStroke)
                canvas.drawCircle(width * 0.88f, height * 0.82f, 5f, paintFill)
                canvas.drawCircle(width * 0.88f, height * 0.82f, 5f, paintStroke)
            }
        }
    }

    fun drawStickerAndroid(canvas: Canvas, stickerId: String, x: Float, y: Float, sz: Float) {
        val half = sz / 2f
        val paintFill = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        val paintStroke = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            color = 0xFF2C2C2C.toInt()
            strokeWidth = 3f
        }

        canvas.save()
        canvas.translate(x, y)

        when (stickerId) {
            "bow_pink" -> {
                // Draw Pink Bow
                paintFill.color = 0xFFFF8DA1.toInt()
                val leftLoop = Path().apply {
                    moveTo(0f, 0f)
                    cubicTo(-half * 0.8f, -half * 1.2f, -half * 1.5f, -half * 0.5f, -half * 1.5f, half * 0.2f)
                    cubicTo(-half * 1.5f, half * 0.8f, -half * 0.8f, half * 1.2f, 0f, 0f)
                }
                canvas.drawPath(leftLoop, paintFill)
                canvas.drawPath(leftLoop, paintStroke)

                val rightLoop = Path().apply {
                    moveTo(0f, 0f)
                    cubicTo(half * 0.8f, -half * 1.2f, half * 1.5f, -half * 0.5f, half * 1.5f, half * 0.2f)
                    cubicTo(half * 1.5f, half * 0.8f, half * 0.8f, half * 1.2f, 0f, 0f)
                }
                canvas.drawPath(rightLoop, paintFill)
                canvas.drawPath(rightLoop, paintStroke)

                // ribbon center
                canvas.drawCircle(0f, 0f, half * 0.45f, paintFill)
                canvas.drawCircle(0f, 0f, half * 0.45f, paintStroke)
            }
            "bow_red" -> {
                paintFill.color = 0xFFFF0000.toInt()
                val leftLoop = Path().apply {
                    moveTo(0f, 0f)
                    cubicTo(-half * 0.8f, -half * 1.2f, -half * 1.5f, -half * 0.5f, -half * 1.5f, half * 0.2f)
                    cubicTo(-half * 1.5f, half * 0.8f, -half * 0.8f, half * 1.2f, 0f, 0f)
                }
                canvas.drawPath(leftLoop, paintFill)
                canvas.drawPath(leftLoop, paintStroke)

                val rightLoop = Path().apply {
                    moveTo(0f, 0f)
                    cubicTo(half * 0.8f, -half * 1.2f, half * 1.5f, -half * 0.5f, half * 1.5f, half * 0.2f)
                    cubicTo(half * 1.5f, half * 0.8f, half * 0.8f, half * 1.2f, 0f, 0f)
                }
                canvas.drawPath(rightLoop, paintFill)
                canvas.drawPath(rightLoop, paintStroke)

                canvas.drawCircle(0f, 0f, half * 0.45f, paintFill)
                canvas.drawCircle(0f, 0f, half * 0.45f, paintStroke)
            }
            "paw_pink" -> {
                paintFill.color = 0xFFFFFFFF.toInt()
                canvas.drawOval(RectF(-half, -half * 0.5f, half, half * 0.7f), paintFill)
                canvas.drawOval(RectF(-half, -half * 0.5f, half, half * 0.7f), paintStroke)

                paintFill.color = 0xFFFF8DA1.toInt()
                canvas.drawOval(RectF(-half * 0.7f, -half * 0.3f, half * 0.7f, half * 0.5f), paintFill)

                // toes
                paintFill.color = 0xFFFFFFFF.toInt()
                canvas.drawCircle(-half * 0.8f, -half * 0.6f, half * 0.25f, paintFill)
                canvas.drawCircle(-half * 0.8f, -half * 0.6f, half * 0.25f, paintStroke)
                paintFill.color = 0xFFFF8DA1.toInt()
                canvas.drawCircle(-half * 0.8f, -half * 0.6f, half * 0.12f, paintFill)

                paintFill.color = 0xFFFFFFFF.toInt()
                canvas.drawCircle(-half * 0.25f, -half * 0.85f, half * 0.25f, paintFill)
                canvas.drawCircle(-half * 0.25f, -half * 0.85f, half * 0.25f, paintStroke)
                paintFill.color = 0xFFFF8DA1.toInt()
                canvas.drawCircle(-half * 0.25f, -half * 0.85f, half * 0.12f, paintFill)

                paintFill.color = 0xFFFFFFFF.toInt()
                canvas.drawCircle(half * 0.25f, -half * 0.85f, half * 0.25f, paintFill)
                canvas.drawCircle(half * 0.25f, -half * 0.85f, half * 0.25f, paintStroke)
                paintFill.color = 0xFFFF8DA1.toInt()
                canvas.drawCircle(half * 0.25f, -half * 0.85f, half * 0.12f, paintFill)

                paintFill.color = 0xFFFFFFFF.toInt()
                canvas.drawCircle(half * 0.8f, -half * 0.6f, half * 0.25f, paintFill)
                canvas.drawCircle(half * 0.8f, -half * 0.6f, half * 0.25f, paintStroke)
                paintFill.color = 0xFFFF8DA1.toInt()
                canvas.drawCircle(half * 0.8f, -half * 0.6f, half * 0.12f, paintFill)
            }
            "cute_heart" -> {
                paintFill.color = 0xFFFF5252.toInt()
                val hPath = Path().apply {
                    moveTo(0f, -half * 0.5f)
                    cubicTo(half * 0.7f, -half * 1.3f, half * 1.3f, -half * 0.3f, 0f, half * 0.9f)
                    cubicTo(-half * 1.3f, -half * 0.3f, -half * 0.7f, -half * 1.3f, 0f, -half * 0.5f)
                }
                canvas.drawPath(hPath, paintFill)
                canvas.drawPath(hPath, paintStroke)

                // gloss
                paintFill.color = 0xFFFFFFFF.toInt()
                canvas.drawCircle(-half * 0.35f, -half * 0.3f, sz * 0.08f, paintFill)
            }
            "strawberry" -> {
                // Heart base
                paintFill.color = 0xFFFF5252.toInt()
                val berry = Path().apply {
                    moveTo(0f, -half)
                    cubicTo(half * 1.2f, -half * 1.2f, half * 1.2f, half * 0.3f, 0f, half)
                    cubicTo(-half * 1.2f, half * 0.3f, -half * 1.2f, -half * 1.2f, 0f, -half)
                }
                canvas.drawPath(berry, paintFill)
                canvas.drawPath(berry, paintStroke)

                // green cap
                paintFill.color = 0xFF81C784.toInt()
                canvas.drawCircle(0f, -half, half * 0.4f, paintFill)
                canvas.drawCircle(0f, -half, half * 0.4f, paintStroke)

                // dots
                paintFill.color = 0xFFFFEB3B.toInt()
                canvas.drawCircle(-half * 0.4f, -half * 0.1f, 1.5f, paintFill)
                canvas.drawCircle(half * 0.4f, -half * 0.1f, 1.5f, paintFill)
                canvas.drawCircle(0f, half * 0.2f, 1.5f, paintFill)
            }
            "gold_star" -> {
                paintFill.color = 0xFFFFD54F.toInt()
                val star = Path()
                var angle = -90f
                val step = 360f / 10f
                val outerRadius = half
                val innerRadius = half * 0.4f
                for (i in 0 until 10) {
                    val r = if (i % 2 == 0) outerRadius else innerRadius
                    val sx = (r * cos(Math.toRadians(angle.toDouble()))).toFloat()
                    val sy = (r * sin(Math.toRadians(angle.toDouble()))).toFloat()
                    if (i == 0) star.moveTo(sx, sy) else star.lineTo(sx, sy)
                    angle += step
                }
                star.close()
                canvas.drawPath(star, paintFill)
                canvas.drawPath(star, paintStroke)
            }
            "happy_kitty" -> {
                paintFill.color = 0xFFFFFFFF.toInt()
                canvas.drawCircle(0f, 0f, half * 0.85f, paintFill)
                canvas.drawCircle(0f, 0f, half * 0.85f, paintStroke)

                // ears
                val left = Path().apply {
                    moveTo(-half * 0.7f, -half * 0.4f)
                    lineTo(-half * 0.9f, -half * 1.0f)
                    lineTo(-half * 0.3f, -half * 0.8f)
                    close()
                }
                canvas.drawPath(left, paintFill)
                canvas.drawPath(left, paintStroke)

                val right = Path().apply {
                    moveTo(half * 0.7f, -half * 0.4f)
                    lineTo(half * 0.9f, -half * 1.0f)
                    lineTo(half * 0.3f, -half * 0.8f)
                    close()
                }
                canvas.drawPath(right, paintFill)
                canvas.drawPath(right, paintStroke)

                // eyes (left curved sleep, right circle)
                paintStroke.strokeWidth = 2f
                canvas.drawArc(RectF(-half * 0.5f, -half * 0.2f, -half * 0.2f, 0f), 180f, 180f, false, paintStroke)
                paintFill.color = 0xFF2C2C2C.toInt()
                canvas.drawCircle(half * 0.3f, -half * 0.1f, half * 0.08f, paintFill)

                // yellow nose
                paintFill.color = 0xFFFFEA00.toInt()
                canvas.drawCircle(0f, half * 0.05f, half * 0.08f, paintFill)
                canvas.drawCircle(0f, half * 0.05f, half * 0.08f, paintStroke)
            }
            "fish_bone" -> {
                paintStroke.strokeWidth = 3f
                // line spine
                canvas.drawLine(-half * 0.8f, 0f, half * 0.8f, 0f, paintStroke)
                // ribs
                paintStroke.strokeWidth = 2.5f
                canvas.drawLine(-half * 0.2f, -half * 0.4f, -half * 0.2f, half * 0.4f, paintStroke)
                canvas.drawLine(half * 0.1f, -half * 0.4f, half * 0.1f, half * 0.4f, paintStroke)
                canvas.drawLine(half * 0.4f, -half * 0.4f, half * 0.4f, half * 0.4f, paintStroke)

                // head (triangle left)
                paintFill.color = 0xFF2C2C2C.toInt()
                val head = Path().apply {
                    moveTo(-half * 0.8f, 0f)
                    lineTo(-half * 0.4f, -half * 0.4f)
                    lineTo(-half * 0.4f, half * 0.4f)
                    close()
                }
                canvas.drawPath(head, paintFill)

                // tail
                val tail = Path().apply {
                    moveTo(half * 0.8f, 0f)
                    lineTo(half * 1.0f, -half * 0.4f)
                    lineTo(half * 1.0f, half * 0.4f)
                    close()
                }
                canvas.drawPath(tail, paintFill)
            }
        }
        canvas.restore()
    }
}
