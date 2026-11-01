package com.telefam.company.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Clean Lucide-standard vector icons for the Gifts screen top bar and categories matching Image 1.
 */
object LucideGiftCategoryIcons {

    val Wallet: ImageVector by lazy {
        ImageVector.Builder("GiftWallet", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color(0xFFE50914)), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(21f, 12f)
                lineTo(21f, 7f)
                curveTo(21f, 5.9f, 20.1f, 5f, 19f, 5f)
                lineTo(5f, 5f)
                curveTo(3.9f, 5f, 3f, 5.9f, 3f, 7f)
                lineTo(3f, 17f)
                curveTo(3f, 18.1f, 3.9f, 19f, 5f, 19f)
                lineTo(19f, 19f)
                curveTo(20.1f, 19f, 21f, 18.1f, 21f, 17f)
                close()
                moveTo(16f, 12f)
                lineTo(21f, 12f)
            }
        }.build()
    }

    val Coin: ImageVector by lazy {
        ImageVector.Builder("GiftCoin", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color(0xFFFFB800)), stroke = SolidColor(Color(0xFFD49B00)), strokeLineWidth = 1.5f) {
                moveTo(12f, 2f)
                arcTo(10f, 10f, 0f, true, true, 12f, 22f)
                arcTo(10f, 10f, 0f, true, true, 12f, 2f)
                close()
            }
            path(stroke = SolidColor(Color.White), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round) {
                moveTo(12f, 7f)
                lineTo(12f, 17f)
                moveTo(10f, 9f)
                lineTo(14f, 9f)
                moveTo(10f, 15f)
                lineTo(14f, 15f)
            }
        }.build()
    }

    val ForYou: ImageVector by lazy {
        ImageVector.Builder("GiftForYou", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color(0xFF555555)), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(12f, 2f)
                lineTo(14.5f, 9.5f)
                lineTo(22f, 12f)
                lineTo(14.5f, 14.5f)
                lineTo(12f, 22f)
                lineTo(9.5f, 14.5f)
                lineTo(2f, 12f)
                lineTo(9.5f, 9.5f)
                close()
            }
        }.build()
    }

    val Trending: ImageVector by lazy {
        ImageVector.Builder("GiftTrending", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color(0xFFFE2C55)), strokeLineWidth = 2.2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(23f, 6f)
                lineTo(13.5f, 15.5f)
                lineTo(8.5f, 10.5f)
                lineTo(1f, 18f)
                moveTo(17f, 6f)
                lineTo(23f, 6f)
                lineTo(23f, 12f)
            }
        }.build()
    }

    val Love: ImageVector by lazy {
        ImageVector.Builder("GiftLove", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color(0xFF555555)), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(19.5f, 12.572f)
                lineTo(12f, 20f)
                lineTo(4.5f, 12.572f)
                curveTo(2.5f, 10.5f, 2.5f, 7f, 4.5f, 5f)
                curveTo(6.5f, 3f, 10f, 3f, 12f, 5.5f)
                curveTo(14f, 3f, 17.5f, 3f, 19.5f, 5f)
                curveTo(21.5f, 7f, 21.5f, 10.5f, 19.5f, 12.572f)
                close()
            }
        }.build()
    }

    val Fun: ImageVector by lazy {
        ImageVector.Builder("GiftFun", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color(0xFF555555)), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(12f, 22f)
                arcTo(10f, 10f, 0f, true, true, 12f, 2f)
                arcTo(10f, 10f, 0f, true, true, 12f, 22f)
                moveTo(8f, 14f)
                curveTo(9f, 16f, 11f, 17f, 12f, 17f)
                curveTo(13f, 17f, 15f, 16f, 16f, 14f)
                moveTo(9f, 9f)
                lineTo(9.01f, 9f)
                moveTo(15f, 9f)
                lineTo(15.01f, 9f)
            }
        }.build()
    }

    val Luxury: ImageVector by lazy {
        ImageVector.Builder("GiftLuxury", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color(0xFF555555)), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(6f, 3f)
                lineTo(18f, 3f)
                lineTo(22f, 9f)
                lineTo(12f, 22f)
                lineTo(2f, 9f)
                close()
                moveTo(2f, 9f)
                lineTo(22f, 9f)
                moveTo(12f, 22f)
                lineTo(8.5f, 9f)
                moveTo(12f, 22f)
                lineTo(15.5f, 9f)
                moveTo(8.5f, 9f)
                lineTo(6f, 3f)
                moveTo(15.5f, 9f)
                lineTo(18f, 3f)
            }
        }.build()
    }

    val Support: ImageVector by lazy {
        ImageVector.Builder("GiftSupport", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color(0xFF555555)), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(18f, 11f)
                curveTo(19.1f, 11f, 20f, 11.9f, 20f, 13f)
                curveTo(20f, 17f, 16f, 21f, 11f, 21f)
                curveTo(7f, 21f, 4f, 18f, 4f, 14f)
                curveTo(4f, 11.8f, 5.8f, 10f, 8f, 10f)
                lineTo(12f, 10f)
                lineTo(12f, 4f)
                curveTo(12f, 2.9f, 12.9f, 2f, 14f, 2f)
                curveTo(15.1f, 2f, 16f, 2.9f, 16f, 4f)
                lineTo(16f, 11f)
                close()
            }
        }.build()
    }

    val Flame: ImageVector by lazy {
        ImageVector.Builder("GiftFlame", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color(0xFFFF3B30))) {
                moveTo(12f, 2f)
                curveTo(12f, 2f, 8f, 7f, 8f, 11f)
                curveTo(8f, 13.2f, 9.8f, 15f, 12f, 15f)
                curveTo(14.2f, 15f, 16f, 13.2f, 16f, 11f)
                curveTo(16f, 7f, 12f, 2f, 12f, 2f)
                close()
                moveTo(12f, 22f)
                curveTo(7f, 22f, 3f, 18f, 3f, 13f)
                curveTo(3f, 9.5f, 5.5f, 6.5f, 8.5f, 5f)
                curveTo(8.1f, 6.5f, 8f, 8.2f, 8.5f, 9.5f)
                curveTo(10f, 8f, 12.5f, 6.5f, 13.5f, 4f)
                curveTo(17f, 6.5f, 21f, 10.5f, 21f, 14.5f)
                curveTo(21f, 18.6f, 17f, 22f, 12f, 22f)
                close()
            }
        }.build()
    }
}

/**
 * Beautiful, vector-rendered gift artwork for all gifts in Image 1.
 * Guaranteed 100% SVG/Vector with zero emojis, rendering at ultra-high quality across all devices.
 */
@Composable
fun VirtualGiftVectorArtwork(
    giftId: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            when (giftId.lowercase()) {
                "galaxy" -> drawGalaxy(this)
                "rocket" -> drawRocket(this)
                "music_vibes" -> drawMusicVibes(this)
                "butterfly" -> drawButterfly(this)
                "fireworks" -> drawFireworks(this)
                "golden_lion" -> drawGoldenLion(this)
                "supercar" -> drawSupercar(this)
                "treasure_chest" -> drawTreasureChest(this)
                "hot_air_balloon" -> drawHotAirBalloon(this)
                "panda" -> drawPanda(this)
                "castle" -> drawCastle(this)
                "crystal_heart" -> drawCrystalHeart(this)
                "magic_wand" -> drawMagicWand(this)
                "rose_box", "rose" -> drawRoseBox(this)
                "crown" -> drawCrown(this)
                "eagle" -> drawEagle(this)
                "diamond_ring" -> drawDiamondRing(this)
                "yacht" -> drawYacht(this)
                "private_jet" -> drawPrivateJet(this)
                "golden_trophy" -> drawGoldenTrophy(this)
                else -> drawFallbackGift(this)
            }
        }
    }
}

private fun drawGalaxy(scope: DrawScope) {
    with(scope) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension * 0.42f
        // Swirling glowing space disc
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFB5179E), Color(0xFF7209B7), Color(0xFF3F37C9), Color(0x0003071E)),
                center = center,
                radius = radius
            ),
            radius = radius,
            center = center
        )
        // Cosmic spiral arm
        drawArc(
            color = Color(0xFF4CC9F0),
            startAngle = 30f,
            sweepAngle = 260f,
            useCenter = false,
            topLeft = Offset(center.x - radius * 0.75f, center.y - radius * 0.4f),
            size = Size(radius * 1.5f, radius * 0.8f),
            style = Stroke(width = 5f, cap = StrokeCap.Round)
        )
        // Core star
        drawCircle(color = Color.White, radius = 6f, center = center)
        drawCircle(color = Color(0xFFFFD166), radius = 3.5f, center = center + Offset(12f, -10f))
        drawCircle(color = Color(0xFF4CC9F0), radius = 2.5f, center = center + Offset(-15f, 14f))
    }
}

private fun drawRocket(scope: DrawScope) {
    with(scope) {
        val w = size.width
        val h = size.height
        // Exhaust fire
        val firePath = Path().apply {
            moveTo(w * 0.42f, h * 0.72f)
            lineTo(w * 0.5f, h * 0.95f)
            lineTo(w * 0.58f, h * 0.72f)
            close()
        }
        drawPath(firePath, brush = Brush.verticalGradient(listOf(Color(0xFFFFB703), Color(0xFFE63946))))
        // Body
        val bodyPath = Path().apply {
            moveTo(w * 0.5f, h * 0.15f)
            cubicTo(w * 0.7f, h * 0.4f, w * 0.65f, h * 0.7f, w * 0.6f, h * 0.72f)
            lineTo(w * 0.4f, h * 0.72f)
            cubicTo(w * 0.35f, h * 0.7f, w * 0.3f, h * 0.4f, w * 0.5f, h * 0.15f)
            close()
        }
        drawPath(bodyPath, color = Color(0xFFE2E8F0))
        // Window
        drawCircle(color = Color(0xFF00B4D8), radius = w * 0.08f, center = Offset(w * 0.5f, h * 0.42f))
        drawCircle(color = Color.White, radius = w * 0.03f, center = Offset(w * 0.48f, h * 0.40f))
        // Fins
        drawPath(
            Path().apply {
                moveTo(w * 0.38f, h * 0.58f)
                lineTo(w * 0.22f, h * 0.74f)
                lineTo(w * 0.4f, h * 0.72f)
                close()
            },
            color = Color(0xFFE63946)
        )
        drawPath(
            Path().apply {
                moveTo(w * 0.62f, h * 0.58f)
                lineTo(w * 0.78f, h * 0.74f)
                lineTo(w * 0.6f, h * 0.72f)
                close()
            },
            color = Color(0xFFE63946)
        )
    }
}

private fun drawMusicVibes(scope: DrawScope) {
    with(scope) {
        val w = size.width
        val h = size.height
        // Headphone headband
        drawArc(
            color = Color(0xFF4361EE),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.22f, h * 0.18f),
            size = Size(w * 0.56f, h * 0.56f),
            style = Stroke(width = 8f, cap = StrokeCap.Round)
        )
        // Ear cushions
        drawRoundRect(
            color = Color(0xFFF72585),
            topLeft = Offset(w * 0.18f, h * 0.44f),
            size = Size(w * 0.14f, h * 0.30f),
            cornerRadius = CornerRadius(12f, 12f)
        )
        drawRoundRect(
            color = Color(0xFFF72585),
            topLeft = Offset(w * 0.68f, h * 0.44f),
            size = Size(w * 0.14f, h * 0.30f),
            cornerRadius = CornerRadius(12f, 12f)
        )
        // Floating notes
        drawCircle(color = Color(0xFF4CC9F0), radius = 6f, center = Offset(w * 0.35f, h * 0.3f))
        drawCircle(color = Color(0xFFFFB703), radius = 7f, center = Offset(w * 0.65f, h * 0.28f))
    }
}

private fun drawButterfly(scope: DrawScope) {
    with(scope) {
        val w = size.width
        val h = size.height
        // Wings Left
        val leftWing = Path().apply {
            moveTo(w * 0.5f, h * 0.45f)
            cubicTo(w * 0.15f, h * 0.1f, w * 0.1f, h * 0.55f, w * 0.5f, h * 0.55f)
            cubicTo(w * 0.2f, h * 0.65f, w * 0.25f, h * 0.9f, w * 0.5f, h * 0.75f)
            close()
        }
        drawPath(leftWing, brush = Brush.linearGradient(listOf(Color(0xFF7209B7), Color(0xFF4CC9F0))))

        // Wings Right
        val rightWing = Path().apply {
            moveTo(w * 0.5f, h * 0.45f)
            cubicTo(w * 0.85f, h * 0.1f, w * 0.9f, h * 0.55f, w * 0.5f, h * 0.55f)
            cubicTo(w * 0.8f, h * 0.65f, w * 0.75f, h * 0.9f, w * 0.5f, h * 0.75f)
            close()
        }
        drawPath(rightWing, brush = Brush.linearGradient(listOf(Color(0xFF7209B7), Color(0xFF4CC9F0))))

        // Body
        drawRoundRect(
            color = Color(0xFF1B1B2F),
            topLeft = Offset(w * 0.47f, h * 0.35f),
            size = Size(w * 0.06f, h * 0.45f),
            cornerRadius = CornerRadius(6f, 6f)
        )
    }
}

private fun drawFireworks(scope: DrawScope) {
    with(scope) {
        val c = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension * 0.4f
        val colors = listOf(Color(0xFFFF007F), Color(0xFFFFB703), Color(0xFF00F5D4), Color(0xFF7B2CBF))
        for (i in 0 until 12) {
            val angle = Math.toRadians((i * 30).toDouble())
            val start = Offset(c.x + (r * 0.35f * Math.cos(angle)).toFloat(), c.y + (r * 0.35f * Math.sin(angle)).toFloat())
            val end = Offset(c.x + (r * Math.cos(angle)).toFloat(), c.y + (r * Math.sin(angle)).toFloat())
            drawLine(
                color = colors[i % colors.size],
                start = start,
                end = end,
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
            drawCircle(
                color = colors[(i + 1) % colors.size],
                radius = 4f,
                center = end
            )
        }
        drawCircle(color = Color.White, radius = 7f, center = c)
    }
}

private fun drawGoldenLion(scope: DrawScope) {
    with(scope) {
        val w = size.width
        val h = size.height
        // Golden lion head & mane
        drawCircle(
            brush = Brush.radialGradient(listOf(Color(0xFFFFD166), Color(0xFFE5A910), Color(0xFFB07D05))),
            radius = w * 0.34f,
            center = Offset(w * 0.5f, h * 0.46f)
        )
        // Snout
        drawRoundRect(
            color = Color(0xFFFFF3B0),
            topLeft = Offset(w * 0.41f, h * 0.45f),
            size = Size(w * 0.18f, h * 0.18f),
            cornerRadius = CornerRadius(10f, 10f)
        )
        // Nose
        drawCircle(color = Color(0xFF582F0E), radius = 6f, center = Offset(w * 0.5f, h * 0.49f))
        // Crown/mane rim
        drawCircle(color = Color(0xFFB07D05), radius = w * 0.34f, center = Offset(w * 0.5f, h * 0.46f), style = Stroke(width = 4f))
    }
}

private fun drawSupercar(scope: DrawScope) {
    with(scope) {
        val w = size.width
        val h = size.height
        val body = Path().apply {
            moveTo(w * 0.1f, h * 0.62f)
            lineTo(w * 0.28f, h * 0.48f)
            lineTo(w * 0.62f, h * 0.44f)
            lineTo(w * 0.88f, h * 0.56f)
            lineTo(w * 0.92f, h * 0.66f)
            lineTo(w * 0.1f, h * 0.66f)
            close()
        }
        drawPath(body, brush = Brush.linearGradient(listOf(Color(0xFF9B5DE5), Color(0xFFF15BB5))))
        // Windshield
        val window = Path().apply {
            moveTo(w * 0.32f, h * 0.49f)
            lineTo(w * 0.58f, h * 0.46f)
            lineTo(w * 0.64f, h * 0.54f)
            lineTo(w * 0.28f, h * 0.56f)
            close()
        }
        drawPath(window, color = Color(0xFF1E1E24))
        // Wheels
        drawCircle(color = Color(0xFF111111), radius = w * 0.08f, center = Offset(w * 0.26f, h * 0.66f))
        drawCircle(color = Color(0xFFE2E8F0), radius = w * 0.035f, center = Offset(w * 0.26f, h * 0.66f))
        drawCircle(color = Color(0xFF111111), radius = w * 0.08f, center = Offset(w * 0.74f, h * 0.66f))
        drawCircle(color = Color(0xFFE2E8F0), radius = w * 0.035f, center = Offset(w * 0.74f, h * 0.66f))
    }
}

private fun drawTreasureChest(scope: DrawScope) {
    with(scope) {
        val w = size.width
        val h = size.height
        // Base box
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFF8B5A2B), Color(0xFF5C3A21))),
            topLeft = Offset(w * 0.18f, h * 0.46f),
            size = Size(w * 0.64f, h * 0.34f),
            cornerRadius = CornerRadius(8f, 8f)
        )
        // Lid
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFFA0522D), Color(0xFF8B5A2B))),
            topLeft = Offset(w * 0.15f, h * 0.32f),
            size = Size(w * 0.70f, h * 0.18f),
            cornerRadius = CornerRadius(12f, 12f)
        )
        // Gold coins spilling
        drawCircle(color = Color(0xFFFFD700), radius = 10f, center = Offset(w * 0.42f, h * 0.45f))
        drawCircle(color = Color(0xFFFFB800), radius = 9f, center = Offset(w * 0.55f, h * 0.43f))
        drawCircle(color = Color(0xFFFFE169), radius = 8f, center = Offset(w * 0.49f, h * 0.40f))
        // Gold straps & lock
        drawRect(color = Color(0xFFFFD700), topLeft = Offset(w * 0.46f, h * 0.42f), size = Size(w * 0.08f, h * 0.12f))
    }
}

private fun drawHotAirBalloon(scope: DrawScope) {
    with(scope) {
        val w = size.width
        val h = size.height
        // Balloon
        val balloon = Path().apply {
            moveTo(w * 0.5f, h * 0.12f)
            cubicTo(w * 0.85f, h * 0.12f, w * 0.8f, h * 0.52f, w * 0.58f, h * 0.68f)
            lineTo(w * 0.42f, h * 0.68f)
            cubicTo(w * 0.2f, h * 0.52f, w * 0.15f, h * 0.12f, w * 0.5f, h * 0.12f)
            close()
        }
        drawPath(balloon, brush = Brush.linearGradient(listOf(Color(0xFFF72585), Color(0xFF7209B7), Color(0xFF4CC9F0))))
        // Basket
        drawRoundRect(
            color = Color(0xFF8B5A2B),
            topLeft = Offset(w * 0.42f, h * 0.78f),
            size = Size(w * 0.16f, h * 0.12f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        // Ropes
        drawLine(color = Color.Black, start = Offset(w * 0.44f, h * 0.68f), end = Offset(w * 0.44f, h * 0.78f), strokeWidth = 2f)
        drawLine(color = Color.Black, start = Offset(w * 0.56f, h * 0.68f), end = Offset(w * 0.56f, h * 0.78f), strokeWidth = 2f)
    }
}

private fun drawPanda(scope: DrawScope) {
    with(scope) {
        val w = size.width
        val h = size.height
        // Ears
        drawCircle(color = Color(0xFF1E1E24), radius = w * 0.10f, center = Offset(w * 0.32f, h * 0.32f))
        drawCircle(color = Color(0xFF1E1E24), radius = w * 0.10f, center = Offset(w * 0.68f, h * 0.32f))
        // Head
        drawCircle(color = Color.White, radius = w * 0.28f, center = Offset(w * 0.5f, h * 0.52f))
        drawCircle(color = Color(0xFFE2E8F0), radius = w * 0.28f, center = Offset(w * 0.5f, h * 0.52f), style = Stroke(width = 3f))
        // Eye patches
        drawOval(color = Color(0xFF1E1E24), topLeft = Offset(w * 0.33f, h * 0.45f), size = Size(w * 0.12f, h * 0.16f))
        drawOval(color = Color(0xFF1E1E24), topLeft = Offset(w * 0.55f, h * 0.45f), size = Size(w * 0.12f, h * 0.16f))
        // Eyes
        drawCircle(color = Color.White, radius = 4f, center = Offset(w * 0.39f, h * 0.51f))
        drawCircle(color = Color.White, radius = 4f, center = Offset(w * 0.61f, h * 0.51f))
        // Nose
        drawCircle(color = Color(0xFF1E1E24), radius = 6f, center = Offset(w * 0.5f, h * 0.60f))
    }
}

private fun drawCastle(scope: DrawScope) {
    with(scope) {
        val w = size.width
        val h = size.height
        // Central tower
        drawRect(color = Color(0xFF64B5F6), topLeft = Offset(w * 0.38f, h * 0.35f), size = Size(w * 0.24f, h * 0.45f))
        // Left tower
        drawRect(color = Color(0xFF42A5F5), topLeft = Offset(w * 0.20f, h * 0.45f), size = Size(w * 0.18f, h * 0.35f))
        // Right tower
        drawRect(color = Color(0xFF42A5F5), topLeft = Offset(w * 0.62f, h * 0.45f), size = Size(w * 0.18f, h * 0.35f))
        // Roof cones
        drawPath(Path().apply { moveTo(w * 0.5f, h * 0.15f); lineTo(w * 0.36f, h * 0.35f); lineTo(w * 0.64f, h * 0.35f); close() }, color = Color(0xFF1E88E5))
        drawPath(Path().apply { moveTo(w * 0.29f, h * 0.28f); lineTo(w * 0.18f, h * 0.45f); lineTo(w * 0.40f, h * 0.45f); close() }, color = Color(0xFF1565C0))
        drawPath(Path().apply { moveTo(w * 0.71f, h * 0.28f); lineTo(w * 0.60f, h * 0.45f); lineTo(w * 0.82f, h * 0.45f); close() }, color = Color(0xFF1565C0))
        // Gate
        drawRoundRect(color = Color(0xFF0D47A1), topLeft = Offset(w * 0.44f, h * 0.62f), size = Size(w * 0.12f, h * 0.18f), cornerRadius = CornerRadius(8f, 8f))
    }
}

private fun drawCrystalHeart(scope: DrawScope) {
    with(scope) {
        val w = size.width
        val h = size.height
        val heart = Path().apply {
            moveTo(w * 0.5f, h * 0.78f)
            lineTo(w * 0.20f, h * 0.42f)
            cubicTo(w * 0.10f, h * 0.22f, w * 0.35f, h * 0.12f, w * 0.5f, h * 0.32f)
            cubicTo(w * 0.65f, h * 0.12f, w * 0.90f, h * 0.22f, w * 0.80f, h * 0.42f)
            close()
        }
        drawPath(heart, brush = Brush.linearGradient(listOf(Color(0xFFFF4D6D), Color(0xFFFF758F), Color(0xFFFFB3C1))))
        // Facet lines for crystal look
        drawLine(color = Color.White.copy(alpha = 0.65f), start = Offset(w * 0.5f, h * 0.32f), end = Offset(w * 0.5f, h * 0.78f), strokeWidth = 3f)
        drawLine(color = Color.White.copy(alpha = 0.5f), start = Offset(w * 0.30f, h * 0.36f), end = Offset(w * 0.5f, h * 0.55f), strokeWidth = 2f)
        drawLine(color = Color.White.copy(alpha = 0.5f), start = Offset(w * 0.70f, h * 0.36f), end = Offset(w * 0.5f, h * 0.55f), strokeWidth = 2f)
    }
}

private fun drawMagicWand(scope: DrawScope) {
    with(scope) {
        val w = size.width
        val h = size.height
        // Wand shaft
        drawLine(
            brush = Brush.linearGradient(listOf(Color(0xFF7209B7), Color(0xFF3A0CA3))),
            start = Offset(w * 0.25f, h * 0.82f),
            end = Offset(w * 0.68f, h * 0.32f),
            strokeWidth = 10f,
            cap = StrokeCap.Round
        )
        // Star tip
        val starCenter = Offset(w * 0.72f, h * 0.26f)
        drawCircle(color = Color(0xFFFFD166), radius = w * 0.12f, center = starCenter)
        drawCircle(color = Color.White, radius = w * 0.05f, center = starCenter)
    }
}

private fun drawRoseBox(scope: DrawScope) {
    with(scope) {
        val w = size.width
        val h = size.height
        // Black luxury velvet box
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFF2B2B2B), Color(0xFF141414))),
            topLeft = Offset(w * 0.24f, h * 0.44f),
            size = Size(w * 0.52f, h * 0.38f),
            cornerRadius = CornerRadius(14f, 14f)
        )
        // Red ribbon
        drawRect(color = Color(0xFFD90429), topLeft = Offset(w * 0.47f, h * 0.44f), size = Size(w * 0.06f, h * 0.38f))
        // Roses bouquet
        drawCircle(color = Color(0xFFE63946), radius = 14f, center = Offset(w * 0.36f, h * 0.40f))
        drawCircle(color = Color(0xFFD90429), radius = 16f, center = Offset(w * 0.50f, h * 0.36f))
        drawCircle(color = Color(0xFFE63946), radius = 14f, center = Offset(w * 0.64f, h * 0.40f))
    }
}

private fun drawCrown(scope: DrawScope) {
    with(scope) {
        val w = size.width
        val h = size.height
        val crown = Path().apply {
            moveTo(w * 0.18f, h * 0.66f)
            lineTo(w * 0.18f, h * 0.36f)
            lineTo(w * 0.34f, h * 0.52f)
            lineTo(w * 0.50f, h * 0.24f)
            lineTo(w * 0.66f, h * 0.52f)
            lineTo(w * 0.82f, h * 0.36f)
            lineTo(w * 0.82f, h * 0.66f)
            close()
        }
        drawPath(crown, brush = Brush.verticalGradient(listOf(Color(0xFFFFD700), Color(0xFFFFA200))))
        // Jewels
        drawCircle(color = Color(0xFFE63946), radius = 5f, center = Offset(w * 0.18f, h * 0.34f))
        drawCircle(color = Color(0xFF4CC9F0), radius = 6f, center = Offset(w * 0.50f, h * 0.22f))
        drawCircle(color = Color(0xFFE63946), radius = 5f, center = Offset(w * 0.82f, h * 0.34f))
    }
}

private fun drawEagle(scope: DrawScope) {
    with(scope) {
        val w = size.width
        val h = size.height
        // Golden wings
        val wings = Path().apply {
            moveTo(w * 0.5f, h * 0.48f)
            lineTo(w * 0.12f, h * 0.28f)
            lineTo(w * 0.28f, h * 0.58f)
            lineTo(w * 0.5f, h * 0.68f)
            lineTo(w * 0.72f, h * 0.58f)
            lineTo(w * 0.88f, h * 0.28f)
            close()
        }
        drawPath(wings, brush = Brush.linearGradient(listOf(Color(0xFFFB8500), Color(0xFFFFB703))))
        // White head
        drawCircle(color = Color.White, radius = w * 0.12f, center = Offset(w * 0.5f, h * 0.38f))
        // Yellow beak
        drawPath(Path().apply { moveTo(w * 0.5f, h * 0.38f); lineTo(w * 0.58f, h * 0.42f); lineTo(w * 0.5f, h * 0.46f); close() }, color = Color(0xFFFFB703))
    }
}

private fun drawDiamondRing(scope: DrawScope) {
    with(scope) {
        val w = size.width
        val h = size.height
        // Platinum band
        drawCircle(
            color = Color(0xFFCBD5E1),
            radius = w * 0.24f,
            center = Offset(w * 0.5f, h * 0.56f),
            style = Stroke(width = 8f)
        )
        // Diamond gem
        val gem = Path().apply {
            moveTo(w * 0.38f, h * 0.30f)
            lineTo(w * 0.62f, h * 0.30f)
            lineTo(w * 0.68f, h * 0.38f)
            lineTo(w * 0.50f, h * 0.50f)
            lineTo(w * 0.32f, h * 0.38f)
            close()
        }
        drawPath(gem, brush = Brush.linearGradient(listOf(Color(0xFFE0F2FE), Color(0xFF38BDF8), Color(0xFF0284C7))))
        drawCircle(color = Color.White, radius = 4f, center = Offset(w * 0.48f, h * 0.34f))
    }
}

private fun drawYacht(scope: DrawScope) {
    with(scope) {
        val w = size.width
        val h = size.height
        // Hull
        val hull = Path().apply {
            moveTo(w * 0.12f, h * 0.64f)
            lineTo(w * 0.88f, h * 0.64f)
            lineTo(w * 0.74f, h * 0.78f)
            lineTo(w * 0.18f, h * 0.78f)
            close()
        }
        drawPath(hull, color = Color(0xFFF8FAFC))
        // Cabin
        drawRoundRect(color = Color(0xFF0284C7), topLeft = Offset(w * 0.26f, h * 0.46f), size = Size(w * 0.44f, h * 0.18f), cornerRadius = CornerRadius(6f, 6f))
        // Water
        drawLine(color = Color(0xFF0284C7), start = Offset(w * 0.05f, h * 0.82f), end = Offset(w * 0.95f, h * 0.82f), strokeWidth = 5f, cap = StrokeCap.Round)
    }
}

private fun drawPrivateJet(scope: DrawScope) {
    with(scope) {
        val w = size.width
        val h = size.height
        // Fuselage
        val body = Path().apply {
            moveTo(w * 0.15f, h * 0.52f)
            lineTo(w * 0.85f, h * 0.44f)
            lineTo(w * 0.78f, h * 0.56f)
            lineTo(w * 0.15f, h * 0.54f)
            close()
        }
        drawPath(body, color = Color(0xFFE2E8F0))
        // Wings
        val wing = Path().apply {
            moveTo(w * 0.45f, h * 0.48f)
            lineTo(w * 0.28f, h * 0.28f)
            lineTo(w * 0.38f, h * 0.48f)
            close()
        }
        drawPath(wing, color = Color(0xFF0284C7))
        // Tail
        val tail = Path().apply {
            moveTo(w * 0.16f, h * 0.53f)
            lineTo(w * 0.10f, h * 0.36f)
            lineTo(w * 0.22f, h * 0.53f)
            close()
        }
        drawPath(tail, color = Color(0xFF0369A1))
    }
}

private fun drawGoldenTrophy(scope: DrawScope) {
    with(scope) {
        val w = size.width
        val h = size.height
        // Cup body
        val cup = Path().apply {
            moveTo(w * 0.28f, h * 0.26f)
            lineTo(w * 0.72f, h * 0.26f)
            lineTo(w * 0.65f, h * 0.58f)
            cubicTo(w * 0.58f, h * 0.68f, w * 0.42f, h * 0.68f, w * 0.35f, h * 0.58f)
            close()
        }
        drawPath(cup, brush = Brush.verticalGradient(listOf(Color(0xFFFFD700), Color(0xFFFFA200))))
        // Handles
        drawArc(color = Color(0xFFFFD700), startAngle = 90f, sweepAngle = 180f, useCenter = false, topLeft = Offset(w * 0.18f, h * 0.30f), size = Size(w * 0.18f, h * 0.22f), style = Stroke(4f))
        drawArc(color = Color(0xFFFFD700), startAngle = 270f, sweepAngle = 180f, useCenter = false, topLeft = Offset(w * 0.64f, h * 0.30f), size = Size(w * 0.18f, h * 0.22f), style = Stroke(4f))
        // Stem and base
        drawRect(color = Color(0xFFE5A910), topLeft = Offset(w * 0.46f, h * 0.66f), size = Size(w * 0.08f, h * 0.12f))
        drawRoundRect(color = Color(0xFF333333), topLeft = Offset(w * 0.32f, h * 0.76f), size = Size(w * 0.36f, h * 0.10f), cornerRadius = CornerRadius(4f, 4f))
    }
}

private fun drawFallbackGift(scope: DrawScope) {
    with(scope) {
        val c = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension * 0.35f
        drawCircle(brush = Brush.radialGradient(listOf(Color(0xFFFFD700), Color(0xFFE63946))), radius = r, center = c)
        drawCircle(color = Color.White, radius = r * 0.4f, center = c)
    }
}
