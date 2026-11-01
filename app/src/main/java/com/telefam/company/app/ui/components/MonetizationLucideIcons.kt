package com.telefam.company.app.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Lucide SVG Icons for Subscriptions, Wallet, Payouts, and Monetization screens.
 * Pure vector paths, zero emojis, zero runtime network dependencies.
 */
object MonetizationLucideIcons {

    val Crown: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideCrown",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(11.562f, 3.266f)
                arcTo(0.5f, 0.5f, 0f, false, true, 12.438f, 3.266f)
                lineTo(16f, 9f)
                lineTo(21.447f, 7.184f)
                arcTo(0.5f, 0.5f, 0f, false, true, 22.062f, 7.82f)
                lineTo(20f, 18f)
                arcTo(2f, 2f, 0f, false, true, 18f, 20f)
                lineTo(6f, 20f)
                arcTo(2f, 2f, 0f, false, true, 4f, 18f)
                lineTo(1.938f, 7.82f)
                arcTo(0.5f, 0.5f, 0f, false, true, 2.553f, 7.184f)
                lineTo(8f, 9f)
                close()
            }
        }.build()
    }

    val Wallet: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideWallet",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(19f, 7f)
                verticalLineTo(4f)
                arcTo(1f, 1f, 0f, false, false, 18f, 3f)
                horizontalLineTo(5f)
                arcTo(2f, 2f, 0f, false, false, 3f, 5f)
                verticalLineTo(19f)
                arcTo(2f, 2f, 0f, false, false, 5f, 21f)
                horizontalLineTo(19f)
                arcTo(2f, 2f, 0f, false, false, 21f, 19f)
                verticalLineTo(11f)
                arcTo(2f, 2f, 0f, false, false, 19f, 9f)
                horizontalLineTo(16f)
                arcTo(2f, 2f, 0f, false, false, 14f, 11f)
                verticalLineTo(15f)
                arcTo(2f, 2f, 0f, false, false, 16f, 17f)
                horizontalLineTo(21f)
            }
        }.build()
    }

    val Landmark: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideLandmark",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                // Pediment roof
                moveTo(2f, 8f)
                lineTo(12f, 2f)
                lineTo(22f, 8f)
                close()
                // Top lintel & bottom base
                moveTo(4f, 8f)
                horizontalLineTo(20f)
                moveTo(2f, 22f)
                horizontalLineTo(22f)
                moveTo(4f, 18f)
                horizontalLineTo(20f)
                // Columns
                moveTo(6f, 11f)
                verticalLineTo(18f)
                moveTo(10f, 11f)
                verticalLineTo(18f)
                moveTo(14f, 11f)
                verticalLineTo(18f)
                moveTo(18f, 11f)
                verticalLineTo(18f)
            }
        }.build()
    }

    val PlaySquare: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucidePlaySquare",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                // Rounded rect
                moveTo(5f, 3f)
                horizontalLineTo(19f)
                arcTo(2f, 2f, 0f, false, true, 21f, 5f)
                verticalLineTo(19f)
                arcTo(2f, 2f, 0f, false, true, 19f, 21f)
                horizontalLineTo(5f)
                arcTo(2f, 2f, 0f, false, true, 3f, 19f)
                verticalLineTo(5f)
                arcTo(2f, 2f, 0f, false, true, 5f, 3f)
                close()
                // Play triangle
                moveTo(10f, 8f)
                lineTo(16f, 12f)
                lineTo(10f, 16f)
                close()
            }
        }.build()
    }

    val Gift: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideGift",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                // Lid
                moveTo(3f, 8f)
                horizontalLineTo(21f)
                verticalLineTo(12f)
                horizontalLineTo(3f)
                close()
                // Box
                moveTo(4f, 12f)
                verticalLineTo(20f)
                arcTo(1f, 1f, 0f, false, false, 5f, 21f)
                horizontalLineTo(19f)
                arcTo(1f, 1f, 0f, false, false, 20f, 20f)
                verticalLineTo(12f)
                // Vertical ribbon
                moveTo(12f, 8f)
                verticalLineTo(21f)
                // Left ribbon loop
                moveTo(12f, 8f)
                arcTo(3f, 3f, 0f, false, false, 9f, 5f)
                arcTo(3f, 3f, 0f, false, false, 6f, 8f)
                close()
                // Right ribbon loop
                moveTo(12f, 8f)
                arcTo(3f, 3f, 0f, false, true, 15f, 5f)
                arcTo(3f, 3f, 0f, false, true, 18f, 8f)
                close()
            }
        }.build()
    }

    val Receipt: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideReceipt",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(4f, 2f)
                verticalLineTo(22f)
                lineTo(7f, 20f)
                lineTo(10f, 22f)
                lineTo(13f, 20f)
                lineTo(16f, 22f)
                lineTo(19f, 20f)
                lineTo(20f, 22f)
                verticalLineTo(2f)
                close()
                moveTo(8f, 7f)
                horizontalLineTo(16f)
                moveTo(8f, 11f)
                horizontalLineTo(16f)
                moveTo(8f, 15f)
                horizontalLineTo(12f)
            }
        }.build()
    }

    val Eye: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideEye",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(2f, 12f)
                curveTo(5f, 5f, 19f, 5f, 22f, 12f)
                curveTo(19f, 19f, 5f, 19f, 2f, 12f)
                close()
                moveTo(12f, 15f)
                arcTo(3f, 3f, 0f, true, false, 12f, 9f)
                arcTo(3f, 3f, 0f, false, false, 12f, 15f)
                close()
            }
        }.build()
    }

    val EyeOff: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideEyeOff",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(9.88f, 9.88f)
                arcTo(3f, 3f, 0f, true, false, 14.12f, 14.12f)
                moveTo(10.73f, 5.08f)
                arcTo(10.43f, 10.43f, 0f, false, true, 12f, 5f)
                curveTo(19f, 5f, 22f, 12f, 22f, 12f)
                arcTo(14.5f, 14.5f, 0f, false, true, 18.27f, 16.73f)
                moveTo(6.71f, 6.71f)
                arcTo(14.39f, 14.39f, 0f, false, false, 2f, 12f)
                curveTo(5f, 19f, 19f, 19f, 21.29f, 13.29f)
                moveTo(2f, 2f)
                lineTo(22f, 22f)
            }
        }.build()
    }

    val Star: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideStar",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 2f)
                lineTo(15.09f, 8.26f)
                lineTo(22f, 9.27f)
                lineTo(17f, 14.14f)
                lineTo(18.18f, 21.02f)
                lineTo(12f, 17.77f)
                lineTo(5.82f, 21.02f)
                lineTo(7f, 14.14f)
                lineTo(2f, 9.27f)
                lineTo(8.91f, 8.26f)
                close()
            }
        }.build()
    }

    val BarChart3: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideBarChart3",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(3f, 3f)
                verticalLineTo(21f)
                horizontalLineTo(21f)
                moveTo(18f, 17f)
                verticalLineTo(9f)
                moveTo(13f, 17f)
                verticalLineTo(5f)
                moveTo(8f, 17f)
                verticalLineTo(13f)
            }
        }.build()
    }

    val Users: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideUsers",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(16f, 21f)
                verticalLineTo(19f)
                arcTo(4f, 4f, 0f, false, false, 12f, 15f)
                horizontalLineTo(6f)
                arcTo(4f, 4f, 0f, false, false, 2f, 19f)
                verticalLineTo(21f)
                moveTo(9f, 11f)
                arcTo(4f, 4f, 0f, true, false, 9f, 3f)
                arcTo(4f, 4f, 0f, false, false, 9f, 11f)
                close()
                moveTo(22f, 21f)
                verticalLineTo(19f)
                arcTo(4f, 4f, 0f, false, false, 19f, 15.13f)
                moveTo(16f, 3.13f)
                arcTo(4f, 4f, 0f, false, true, 16f, 10.87f)
            }
        }.build()
    }

    val UserPlus: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideUserPlus",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(16f, 21f)
                verticalLineTo(19f)
                arcTo(4f, 4f, 0f, false, false, 12f, 15f)
                horizontalLineTo(5f)
                arcTo(4f, 4f, 0f, false, false, 1f, 19f)
                verticalLineTo(21f)
                moveTo(8.5f, 11f)
                arcTo(4f, 4f, 0f, true, false, 8.5f, 3f)
                arcTo(4f, 4f, 0f, false, false, 8.5f, 11f)
                close()
                moveTo(20f, 8f)
                verticalLineTo(14f)
                moveTo(23f, 11f)
                horizontalLineTo(17f)
            }
        }.build()
    }

    val UserX: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideUserX",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(16f, 21f)
                verticalLineTo(19f)
                arcTo(4f, 4f, 0f, false, false, 12f, 15f)
                horizontalLineTo(5f)
                arcTo(4f, 4f, 0f, false, false, 1f, 19f)
                verticalLineTo(21f)
                moveTo(8.5f, 11f)
                arcTo(4f, 4f, 0f, true, false, 8.5f, 3f)
                arcTo(4f, 4f, 0f, false, false, 8.5f, 11f)
                close()
                moveTo(18f, 8f)
                lineTo(22f, 12f)
                moveTo(22f, 8f)
                lineTo(18f, 12f)
            }
        }.build()
    }

    val CheckCircle: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideCheckCircle",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(22f, 11.08f)
                verticalLineTo(12f)
                arcTo(10f, 10f, 0f, true, true, 16.14f, 3.86f)
                moveTo(22f, 4f)
                lineTo(12f, 14.01f)
                lineTo(9f, 11.01f)
            }
        }.build()
    }

    val XCircle: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideXCircle",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 22f)
                arcTo(10f, 10f, 0f, true, false, 12f, 2f)
                arcTo(10f, 10f, 0f, false, false, 12f, 22f)
                close()
                moveTo(15f, 9f)
                lineTo(9f, 15f)
                moveTo(9f, 9f)
                lineTo(15f, 15f)
            }
        }.build()
    }

    val Clock: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideClock",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 22f)
                arcTo(10f, 10f, 0f, true, false, 12f, 2f)
                arcTo(10f, 10f, 0f, false, false, 12f, 22f)
                close()
                moveTo(12f, 6f)
                verticalLineTo(12f)
                lineTo(16f, 14f)
            }
        }.build()
    }

    val ShieldCheck: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideShieldCheck",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 22f)
                curveTo(12f, 22f, 20f, 18f, 20f, 12f)
                verticalLineTo(5f)
                lineTo(12f, 2f)
                lineTo(4f, 5f)
                verticalLineTo(12f)
                curveTo(4f, 18f, 12f, 22f, 12f, 22f)
                close()
                moveTo(9f, 12f)
                lineTo(11f, 14f)
                lineTo(15f, 10f)
            }
        }.build()
    }

    val HelpCircle: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideHelpCircle",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 22f)
                arcTo(10f, 10f, 0f, true, false, 12f, 2f)
                arcTo(10f, 10f, 0f, false, false, 12f, 22f)
                close()
                moveTo(9.09f, 9f)
                arcTo(3f, 3f, 0f, false, true, 14.83f, 10.5f)
                curveTo(14.83f, 12f, 12f, 12.75f, 12f, 14f)
                moveTo(12f, 17f)
                horizontalLineTo(12.01f)
            }
        }.build()
    }

    val Award: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideAward",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 15f)
                arcTo(7f, 7f, 0f, true, false, 12f, 1f)
                arcTo(7f, 7f, 0f, false, false, 12f, 15f)
                close()
                moveTo(8.21f, 13.89f)
                lineTo(7f, 23f)
                lineTo(12f, 20f)
                lineTo(17f, 23f)
                lineTo(15.79f, 13.88f)
            }
        }.build()
    }

    val Clapperboard: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideClapperboard",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(20.2f, 6f)
                lineTo(3f, 11f)
                lineTo(3.8f, 13f)
                lineTo(21f, 8f)
                close()
                moveTo(4f, 11f)
                verticalLineTo(19f)
                arcTo(2f, 2f, 0f, false, false, 6f, 21f)
                horizontalLineTo(18f)
                arcTo(2f, 2f, 0f, false, false, 20f, 19f)
                verticalLineTo(8.5f)
            }
        }.build()
    }

    val Heart: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideHeart",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(19f, 14f)
                curveTo(21f, 12f, 22f, 9.5f, 22f, 7f)
                arcTo(5f, 5f, 0f, false, false, 12f, 4.5f)
                arcTo(5f, 5f, 0f, false, false, 2f, 7f)
                curveTo(2f, 9.5f, 3f, 12f, 5f, 14f)
                lineTo(12f, 21f)
                close()
            }
        }.build()
    }

    val FileText: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideFileText",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(15f, 2f)
                horizontalLineTo(6f)
                arcTo(2f, 2f, 0f, false, false, 4f, 4f)
                verticalLineTo(20f)
                arcTo(2f, 2f, 0f, false, false, 6f, 22f)
                horizontalLineTo(18f)
                arcTo(2f, 2f, 0f, false, false, 20f, 20f)
                verticalLineTo(7f)
                close()
                moveTo(14f, 2f)
                verticalLineTo(8f)
                horizontalLineTo(20f)
                moveTo(10f, 13f)
                horizontalLineTo(14f)
                moveTo(10f, 17f)
                horizontalLineTo(14f)
            }
        }.build()
    }

    val BookOpen: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideBookOpen",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(2f, 3f)
                horizontalLineTo(8f)
                arcTo(4f, 4f, 0f, false, true, 12f, 7f)
                arcTo(4f, 4f, 0f, false, true, 16f, 3f)
                horizontalLineTo(22f)
                verticalLineTo(19f)
                horizontalLineTo(16f)
                arcTo(4f, 4f, 0f, false, false, 12f, 23f)
                arcTo(4f, 4f, 0f, false, false, 8f, 19f)
                horizontalLineTo(2f)
                close()
                moveTo(12f, 7f)
                verticalLineTo(23f)
            }
        }.build()
    }

    val ExternalLink: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideExternalLink",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(15f, 3f)
                horizontalLineTo(21f)
                verticalLineTo(9f)
                moveTo(10f, 14f)
                lineTo(21f, 3f)
                moveTo(18f, 13f)
                verticalLineTo(19f)
                arcTo(2f, 2f, 0f, false, true, 16f, 21f)
                horizontalLineTo(5f)
                arcTo(2f, 2f, 0f, false, true, 3f, 19f)
                verticalLineTo(8f)
                arcTo(2f, 2f, 0f, false, true, 5f, 6f)
                horizontalLineTo(11f)
            }
        }.build()
    }

    val Megaphone: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideMegaphone",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(11.6f, 16.8f)
                lineTo(18f, 21f)
                verticalLineTo(3f)
                lineTo(11.6f, 7.2f)
                horizontalLineTo(3f)
                verticalLineTo(16.8f)
                close()
                moveTo(6f, 17f)
                verticalLineTo(21f)
                arcTo(2f, 2f, 0f, false, false, 8f, 23f)
                horizontalLineTo(9f)
            }
        }.build()
    }

    val TrendingUp: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideTrendingUp",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(22f, 7f)
                lineTo(13.5f, 15.5f)
                lineTo(8.5f, 10.5f)
                lineTo(2f, 17f)
                moveTo(16f, 7f)
                horizontalLineTo(22f)
                verticalLineTo(13f)
            }
        }.build()
    }

    val DollarSign: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideDollarSign",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 2f)
                verticalLineTo(22f)
                moveTo(17f, 5f)
                horizontalLineTo(9.5f)
                arcTo(3.5f, 3.5f, 0f, false, false, 6f, 8.5f)
                arcTo(3.5f, 3.5f, 0f, false, false, 9.5f, 12f)
                horizontalLineTo(14.5f)
                arcTo(3.5f, 3.5f, 0f, false, true, 18f, 15.5f)
                arcTo(3.5f, 3.5f, 0f, false, true, 14.5f, 19f)
                horizontalLineTo(7f)
            }
        }.build()
    }
}
