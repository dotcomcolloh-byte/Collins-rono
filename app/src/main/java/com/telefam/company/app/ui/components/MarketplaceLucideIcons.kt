package com.telefam.company.app.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Lucide SVG Vector Icons for Telefam Marketplace.
 * All icons strictly use vector paths with zero emojis.
 */
object MarketplaceLucideIcons {

    val Store: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideStore",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(3f, 9f)
                lineTo(5f, 3f)
                lineTo(19f, 3f)
                lineTo(21f, 9f)
                moveTo(21f, 9f)
                verticalLineTo(20f)
                curveTo(21f, 20.55f, 20.55f, 21f, 20f, 21f)
                horizontalLineTo(4f)
                curveTo(3.45f, 21f, 3f, 20.55f, 3f, 20f)
                verticalLineTo(9f)
                moveTo(3f, 9f)
                curveTo(3f, 10.1f, 3.9f, 11f, 5f, 11f)
                curveTo(6.1f, 11f, 7f, 10.1f, 7f, 9f)
                curveTo(7f, 10.1f, 7.9f, 11f, 9f, 11f)
                curveTo(10.1f, 11f, 11f, 10.1f, 11f, 9f)
                curveTo(11f, 10.1f, 11.9f, 11f, 13f, 11f)
                curveTo(14.1f, 11f, 15f, 10.1f, 15f, 9f)
                curveTo(15f, 10.1f, 15.9f, 11f, 17f, 11f)
                curveTo(18.1f, 11f, 19f, 10.1f, 19f, 9f)
                curveTo(19f, 10.1f, 19.9f, 11f, 21f, 11f)
                moveTo(9f, 21f)
                verticalLineTo(14f)
                horizontalLineTo(15f)
                verticalLineTo(21f)
            }
        }.build()
    }

    val Car: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideCar",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(5f, 17f)
                horizontalLineTo(19f)
                moveTo(5f, 17f)
                curveTo(3.9f, 17f, 3f, 16.1f, 3f, 15f)
                verticalLineTo(11f)
                curveTo(3f, 10.2f, 3.5f, 9.5f, 4.2f, 9.2f)
                lineTo(6f, 5f)
                curveTo(6.4f, 4.4f, 7f, 4f, 7.7f, 4f)
                horizontalLineTo(16.3f)
                curveTo(17f, 4f, 17.6f, 4.4f, 18f, 5f)
                lineTo(19.8f, 9.2f)
                curveTo(20.5f, 9.5f, 21f, 10.2f, 21f, 11f)
                verticalLineTo(15f)
                curveTo(21f, 16.1f, 20.1f, 17f, 19f, 17f)
                moveTo(7f, 17f)
                verticalLineTo(19f)
                moveTo(17f, 17f)
                verticalLineTo(19f)
                moveTo(7f, 13f)
                horizontalLineTo(7.01f)
                moveTo(17f, 13f)
                horizontalLineTo(17.01f)
            }
        }.build()
    }

    val Home: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideHome",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(3f, 9f)
                lineTo(12f, 2f)
                lineTo(21f, 9f)
                verticalLineTo(20f)
                curveTo(21f, 20.6f, 20.6f, 21f, 20f, 21f)
                horizontalLineTo(4f)
                curveTo(3.4f, 21f, 3f, 20.6f, 3f, 20f)
                close()
                moveTo(9f, 21f)
                verticalLineTo(12f)
                horizontalLineTo(15f)
                verticalLineTo(21f)
            }
        }.build()
    }

    val Smartphone: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideSmartphone",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(7f, 2f)
                horizontalLineTo(17f)
                curveTo(18.1f, 2f, 19f, 2.9f, 19f, 4f)
                verticalLineTo(20f)
                curveTo(19f, 21.1f, 18.1f, 22f, 17f, 22f)
                horizontalLineTo(7f)
                curveTo(5.9f, 22f, 5f, 21.1f, 5f, 20f)
                verticalLineTo(4f)
                curveTo(5f, 2.9f, 5.9f, 2f, 7f, 2f)
                close()
                moveTo(12f, 18f)
                horizontalLineTo(12.01f)
            }
        }.build()
    }

    val Shirt: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideShirt",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(20.4f, 6.7f)
                lineTo(16.5f, 3.4f)
                curveTo(15.9f, 2.9f, 15.2f, 2.6f, 14.5f, 2.6f)
                horizontalLineTo(9.5f)
                curveTo(8.8f, 2.6f, 8.1f, 2.9f, 7.5f, 3.4f)
                lineTo(3.6f, 6.7f)
                curveTo(2.9f, 7.3f, 2.7f, 8.3f, 3.2f, 9.1f)
                lineTo(5f, 12f)
                lineTo(7f, 11f)
                verticalLineTo(20f)
                curveTo(7f, 20.6f, 7.4f, 21f, 8f, 21f)
                horizontalLineTo(16f)
                curveTo(16.6f, 21f, 17f, 20.6f, 17f, 20f)
                verticalLineTo(11f)
                lineTo(19f, 12f)
                lineTo(20.8f, 9.1f)
                curveTo(21.3f, 8.3f, 21.1f, 7.3f, 20.4f, 6.7f)
                close()
                moveTo(9f, 3f)
                curveTo(9f, 4.7f, 10.3f, 6f, 12f, 6f)
                curveTo(13.7f, 6f, 15f, 4.7f, 15f, 3f)
            }
        }.build()
    }

    val Armchair: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideArmchair",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(19f, 9f)
                verticalLineTo(6f)
                curveTo(19f, 4.9f, 18.1f, 4f, 17f, 4f)
                horizontalLineTo(7f)
                curveTo(5.9f, 4f, 5f, 4.9f, 5f, 6f)
                verticalLineTo(9f)
                moveTo(5f, 18f)
                verticalLineTo(20f)
                moveTo(19f, 18f)
                verticalLineTo(20f)
                moveTo(3f, 13f)
                curveTo(3f, 11.9f, 3.9f, 11f, 5f, 11f)
                curveTo(6.1f, 11f, 7f, 11.9f, 7f, 13f)
                verticalLineTo(16f)
                curveTo(7f, 17.1f, 6.1f, 18f, 5f, 18f)
                curveTo(3.9f, 18f, 3f, 17.1f, 3f, 16f)
                close()
                moveTo(17f, 13f)
                curveTo(17f, 11.9f, 17.9f, 11f, 19f, 11f)
                curveTo(20.1f, 11f, 21f, 11.9f, 21f, 13f)
                verticalLineTo(16f)
                curveTo(21f, 17.1f, 20.1f, 18f, 19f, 18f)
                curveTo(17.9f, 18f, 17f, 17.1f, 17f, 16f)
                close()
                moveTo(7f, 14f)
                horizontalLineTo(17f)
            }
        }.build()
    }

    val Sparkles: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideSparkles",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 3f)
                lineTo(14.5f, 8.5f)
                lineTo(20f, 11f)
                lineTo(14.5f, 13.5f)
                lineTo(12f, 19f)
                lineTo(9.5f, 13.5f)
                lineTo(4f, 11f)
                lineTo(9.5f, 8.5f)
                close()
                moveTo(19f, 3f)
                verticalLineTo(7f)
                moveTo(21f, 5f)
                horizontalLineTo(17f)
            }
        }.build()
    }

    val Dumbbell: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideDumbbell",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(6.5f, 6.5f)
                lineTo(17.5f, 17.5f)
                moveTo(6.5f, 6.5f)
                lineTo(4.5f, 8.5f)
                curveTo(3.7f, 9.3f, 2.5f, 9.3f, 1.7f, 8.5f)
                lineTo(1.5f, 8.3f)
                curveTo(0.7f, 7.5f, 0.7f, 6.3f, 1.5f, 5.5f)
                lineTo(5.5f, 1.5f)
                curveTo(6.3f, 0.7f, 7.5f, 0.7f, 8.3f, 1.5f)
                lineTo(8.5f, 1.7f)
                curveTo(9.3f, 2.5f, 9.3f, 3.7f, 8.5f, 4.5f)
                close()
                moveTo(17.5f, 17.5f)
                lineTo(19.5f, 15.5f)
                curveTo(20.3f, 14.7f, 21.5f, 14.7f, 22.3f, 15.5f)
                lineTo(22.5f, 15.7f)
                curveTo(23.3f, 16.5f, 23.3f, 17.7f, 22.5f, 18.5f)
                lineTo(18.5f, 22.5f)
                curveTo(17.7f, 23.3f, 16.5f, 23.3f, 15.7f, 22.5f)
                lineTo(15.5f, 22.3f)
                curveTo(14.7f, 21.5f, 14.7f, 20.3f, 15.5f, 19.5f)
                close()
            }
        }.build()
    }

    val Briefcase: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideBriefcase",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(4f, 7f)
                horizontalLineTo(20f)
                curveTo(21.1f, 7f, 22f, 7.9f, 22f, 9f)
                verticalLineTo(19f)
                curveTo(22f, 20.1f, 21.1f, 21f, 20f, 21f)
                horizontalLineTo(4f)
                curveTo(2.9f, 21f, 2f, 20.1f, 2f, 19f)
                verticalLineTo(9f)
                curveTo(2f, 7.9f, 2.9f, 7f, 4f, 7f)
                close()
                moveTo(16f, 7f)
                verticalLineTo(5f)
                curveTo(16f, 3.9f, 15.1f, 3f, 14f, 3f)
                horizontalLineTo(10f)
                curveTo(8.9f, 3f, 8f, 3.9f, 8f, 5f)
                verticalLineTo(7f)
                moveTo(2f, 13f)
                horizontalLineTo(22f)
            }
        }.build()
    }

    val Layers: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideLayers",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 2f)
                lineTo(2f, 7f)
                lineTo(12f, 12f)
                lineTo(22f, 7f)
                close()
                moveTo(2f, 12f)
                lineTo(12f, 17f)
                lineTo(22f, 12f)
                moveTo(2f, 17f)
                lineTo(12f, 22f)
                lineTo(22f, 17f)
            }
        }.build()
    }

    val Tag: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideTag",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12.5f, 2f)
                lineTo(21.5f, 11f)
                curveTo(22.2f, 11.7f, 22.2f, 12.8f, 21.5f, 13.5f)
                lineTo(13.5f, 21.5f)
                curveTo(12.8f, 22.2f, 11.7f, 22.2f, 11f, 21.5f)
                lineTo(2f, 12.5f)
                verticalLineTo(3.5f)
                curveTo(2f, 2.7f, 2.7f, 2f, 3.5f, 2f)
                close()
                moveTo(7f, 7f)
                horizontalLineTo(7.01f)
            }
        }.build()
    }

    val Search: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideSearch",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(11f, 19f)
                curveTo(15.42f, 19f, 19f, 15.42f, 19f, 11f)
                curveTo(19f, 6.58f, 15.42f, 3f, 11f, 3f)
                curveTo(6.58f, 3f, 3f, 6.58f, 3f, 11f)
                curveTo(3f, 15.42f, 6.58f, 19f, 11f, 19f)
                close()
                moveTo(21f, 21f)
                lineTo(16.65f, 16.65f)
            }
        }.build()
    }

    val Plus: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucidePlus",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 5f)
                lineTo(12f, 19f)
                moveTo(5f, 12f)
                lineTo(19f, 12f)
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
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(19f, 14f)
                curveTo(20.5f, 12.5f, 21.5f, 10.5f, 21.5f, 8f)
                curveTo(21.5f, 4.5f, 18.5f, 2.5f, 15.5f, 2.5f)
                curveTo(13.5f, 2.5f, 12.5f, 3.5f, 12f, 4.5f)
                curveTo(11.5f, 3.5f, 10.5f, 2.5f, 8.5f, 2.5f)
                curveTo(5.5f, 2.5f, 2.5f, 4.5f, 2.5f, 8f)
                curveTo(2.5f, 10.5f, 3.5f, 12.5f, 5f, 14f)
                lineTo(12f, 21f)
                close()
            }
        }.build()
    }

    val HeartFilled: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideHeartFilled",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFE50914)),
                stroke = SolidColor(Color(0xFFE50914)),
                strokeLineWidth = 1f
            ) {
                moveTo(19f, 14f)
                curveTo(20.5f, 12.5f, 21.5f, 10.5f, 21.5f, 8f)
                curveTo(21.5f, 4.5f, 18.5f, 2.5f, 15.5f, 2.5f)
                curveTo(13.5f, 2.5f, 12.5f, 3.5f, 12f, 4.5f)
                curveTo(11.5f, 3.5f, 10.5f, 2.5f, 8.5f, 2.5f)
                curveTo(5.5f, 2.5f, 2.5f, 4.5f, 2.5f, 8f)
                curveTo(2.5f, 10.5f, 3.5f, 12.5f, 5f, 14f)
                lineTo(12f, 21f)
                close()
            }
        }.build()
    }

    val Share2: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideShare2",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(18f, 5f)
                curveTo(19.65f, 5f, 21f, 3.65f, 21f, 2f)
                moveTo(6f, 12f)
                lineTo(18f, 5f)
                moveTo(6f, 12f)
                lineTo(18f, 19f)
                moveTo(6f, 14f)
                curveTo(7.1f, 14f, 8f, 13.1f, 8f, 12f)
                curveTo(8f, 10.9f, 7.1f, 10f, 6f, 10f)
                curveTo(4.9f, 10f, 4f, 10.9f, 4f, 12f)
                curveTo(4f, 13.1f, 4.9f, 14f, 6f, 14f)
                close()
                moveTo(18f, 7f)
                curveTo(19.1f, 7f, 20f, 6.1f, 20f, 5f)
                curveTo(20f, 3.9f, 19.1f, 3f, 18f, 3f)
                curveTo(16.9f, 3f, 16f, 3.9f, 16f, 5f)
                curveTo(16f, 6.1f, 16.9f, 7f, 18f, 7f)
                close()
                moveTo(18f, 21f)
                curveTo(19.1f, 21f, 20f, 20.1f, 20f, 19f)
                curveTo(20f, 17.9f, 19.1f, 17f, 18f, 17f)
                curveTo(16.9f, 17f, 16f, 17.9f, 16f, 19f)
                curveTo(16f, 20.1f, 16.9f, 21f, 18f, 21f)
                close()
            }
        }.build()
    }

    val MessageCircle: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideMessageCircle",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(21f, 11.5f)
                curveTo(21f, 16.2f, 16.9f, 20f, 12f, 20f)
                curveTo(10.4f, 20f, 8.9f, 19.6f, 7.6f, 18.8f)
                lineTo(3f, 20f)
                lineTo(4.4f, 15.8f)
                curveTo(3.5f, 14.5f, 3f, 13f, 3f, 11.5f)
                curveTo(3f, 6.8f, 7.1f, 3f, 12f, 3f)
                curveTo(16.9f, 3f, 21f, 6.8f, 21f, 11.5f)
                close()
            }
        }.build()
    }

    val MapPin: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideMapPin",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(21f, 10f)
                curveTo(21f, 17f, 12f, 23f, 12f, 23f)
                curveTo(12f, 23f, 3f, 17f, 3f, 10f)
                curveTo(3f, 5f, 7f, 1f, 12f, 1f)
                curveTo(17f, 1f, 21f, 5f, 21f, 10f)
                close()
                moveTo(12f, 13f)
                curveTo(13.65f, 13f, 15f, 11.65f, 15f, 10f)
                curveTo(15f, 8.35f, 13.65f, 7f, 12f, 7f)
                curveTo(10.35f, 7f, 9f, 8.35f, 9f, 10f)
                curveTo(9f, 11.65f, 10.35f, 13f, 12f, 13f)
                close()
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
                stroke = SolidColor(Color.Black),
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

    val AlertTriangle: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideAlertTriangle",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(10.29f, 3.86f)
                lineTo(1.82f, 18f)
                curveTo(1.64f, 18.3f, 1.55f, 18.65f, 1.55f, 19f)
                curveTo(1.55f, 20.1f, 2.45f, 21f, 3.55f, 21f)
                horizontalLineTo(20.45f)
                curveTo(20.8f, 21f, 21.15f, 20.91f, 21.45f, 20.73f)
                curveTo(22.37f, 20.18f, 22.68f, 18.98f, 22.18f, 18f)
                lineTo(13.71f, 3.86f)
                curveTo(13.25f, 3.03f, 12.19f, 2.76f, 11.36f, 3.22f)
                curveTo(10.93f, 3.46f, 10.58f, 3.81f, 10.29f, 3.86f)
                close()
                moveTo(12f, 9f)
                verticalLineTo(13f)
                moveTo(12f, 17f)
                horizontalLineTo(12.01f)
            }
        }.build()
    }

    val Camera: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideCamera",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(14.5f, 4f)
                horizontalLineTo(9.5f)
                lineTo(7.5f, 6f)
                horizontalLineTo(4f)
                curveTo(2.9f, 6f, 2f, 6.9f, 2f, 8f)
                verticalLineTo(18f)
                curveTo(2f, 19.1f, 2.9f, 20f, 4f, 20f)
                horizontalLineTo(20f)
                curveTo(21.1f, 20f, 22f, 19.1f, 22f, 18f)
                verticalLineTo(8f)
                curveTo(22f, 6.9f, 21.1f, 6f, 20f, 6f)
                horizontalLineTo(16.5f)
                close()
                moveTo(12f, 17f)
                curveTo(14.2f, 17f, 16f, 15.2f, 16f, 13f)
                curveTo(16f, 10.8f, 14.2f, 9f, 12f, 9f)
                curveTo(9.8f, 9f, 8f, 10.8f, 8f, 13f)
                curveTo(8f, 15.2f, 9.8f, 17f, 12f, 17f)
                close()
            }
        }.build()
    }

    val Trash2: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideTrash2",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(3f, 6f)
                horizontalLineTo(21f)
                moveTo(19f, 6f)
                verticalLineTo(20f)
                curveTo(19f, 21.1f, 18.1f, 22f, 17f, 22f)
                horizontalLineTo(7f)
                curveTo(5.9f, 22f, 5f, 21.1f, 5f, 20f)
                verticalLineTo(6f)
                moveTo(8f, 6f)
                verticalLineTo(4f)
                curveTo(8f, 2.9f, 8.9f, 2f, 10f, 2f)
                horizontalLineTo(14f)
                curveTo(15.1f, 2f, 16f, 2.9f, 16f, 4f)
                verticalLineTo(6f)
                moveTo(10f, 11f)
                verticalLineTo(17f)
                moveTo(14f, 11f)
                verticalLineTo(17f)
            }
        }.build()
    }

    val RotateCw: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideRotateCw",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(21f, 12f)
                curveTo(21f, 16.97f, 16.97f, 21f, 12f, 21f)
                curveTo(7.03f, 21f, 3f, 16.97f, 3f, 12f)
                curveTo(3f, 7.03f, 7.03f, 3f, 12f, 3f)
                curveTo(15.28f, 3f, 18.16f, 4.77f, 19.72f, 7.4f)
                moveTo(21f, 3f)
                verticalLineTo(8f)
                horizontalLineTo(16f)
            }
        }.build()
    }

    val Check: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideCheck",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(20f, 6f)
                lineTo(9f, 17f)
                lineTo(4f, 12f)
            }
        }.build()
    }

    val Truck: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideTruck",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(1f, 3f)
                horizontalLineTo(15f)
                verticalLineTo(16f)
                horizontalLineTo(1f)
                close()
                moveTo(15f, 8f)
                horizontalLineTo(19f)
                lineTo(23f, 12f)
                verticalLineTo(16f)
                horizontalLineTo(15f)
                moveTo(5.5f, 21f)
                curveTo(6.88f, 21f, 8f, 19.88f, 8f, 18.5f)
                curveTo(8f, 17.12f, 6.88f, 16f, 5.5f, 16f)
                curveTo(4.12f, 16f, 3f, 17.12f, 3f, 18.5f)
                curveTo(3f, 19.88f, 4.12f, 21f, 5.5f, 21f)
                close()
                moveTo(18.5f, 21f)
                curveTo(19.88f, 21f, 21f, 19.88f, 21f, 18.5f)
                curveTo(21f, 17.12f, 19.88f, 16f, 18.5f, 16f)
                curveTo(17.12f, 16f, 16f, 17.12f, 16f, 18.5f)
                curveTo(16f, 19.88f, 17.12f, 21f, 18.5f, 21f)
                close()
            }
        }.build()
    }

    val Package: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucidePackage",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(16.5f, 9.4f)
                lineTo(7.55f, 4.24f)
                moveTo(21f, 16f)
                verticalLineTo(8f)
                lineTo(12f, 3f)
                lineTo(3f, 8f)
                verticalLineTo(16f)
                lineTo(12f, 21f)
                close()
                moveTo(3.27f, 6.96f)
                lineTo(12f, 12.01f)
                lineTo(20.73f, 6.96f)
                moveTo(12f, 22.08f)
                verticalLineTo(12f)
            }
        }.build()
    }

    val X: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideX",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(18f, 6f)
                lineTo(6f, 18f)
                moveTo(6f, 6f)
                lineTo(18f, 18f)
            }
        }.build()
    }

    val ArrowLeft: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideArrowLeft",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(19f, 12f)
                lineTo(5f, 12f)
                moveTo(12f, 19f)
                lineTo(5f, 12f)
                lineTo(12f, 5f)
            }
        }.build()
    }
}
