package com.telefam.company.app.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Pure SVG Lucide vector icons for Linked Devices.
 * Zero emojis in buttons or interface components.
 */
object LinkedDevicesLucideIcons {

    val QrCode: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideQrCode",
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
                // Top-left square
                moveTo(3f, 3f)
                horizontalLineTo(9f)
                verticalLineTo(9f)
                horizontalLineTo(3f)
                close()
                moveTo(5f, 5f)
                horizontalLineTo(7f)
                verticalLineTo(7f)
                horizontalLineTo(5f)
                close()
                // Top-right square
                moveTo(15f, 3f)
                horizontalLineTo(21f)
                verticalLineTo(9f)
                horizontalLineTo(15f)
                close()
                moveTo(17f, 5f)
                horizontalLineTo(19f)
                verticalLineTo(7f)
                horizontalLineTo(17f)
                close()
                // Bottom-left square
                moveTo(3f, 15f)
                horizontalLineTo(9f)
                verticalLineTo(21f)
                horizontalLineTo(3f)
                close()
                moveTo(5f, 17f)
                horizontalLineTo(7f)
                verticalLineTo(19f)
                horizontalLineTo(5f)
                close()
                // Scattered modules
                moveTo(15f, 15f)
                horizontalLineTo(17f)
                verticalLineTo(17f)
                horizontalLineTo(15f)
                close()
                moveTo(19f, 15f)
                horizontalLineTo(21f)
                verticalLineTo(19f)
                horizontalLineTo(19f)
                close()
                moveTo(15f, 19f)
                horizontalLineTo(17f)
                verticalLineTo(21f)
                horizontalLineTo(15f)
                close()
                moveTo(19f, 19f)
                horizontalLineTo(21f)
                verticalLineTo(21f)
                horizontalLineTo(19f)
                close()
            }
        }.build()
    }

    val Scan: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideScan",
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
                moveTo(3f, 7f)
                verticalLineTo(5f)
                curveTo(3f, 3.9f, 3.9f, 3f, 5f, 3f)
                horizontalLineTo(7f)
                moveTo(17f, 3f)
                horizontalLineTo(19f)
                curveTo(20.1f, 3f, 21f, 3.9f, 21f, 5f)
                verticalLineTo(7f)
                moveTo(21f, 17f)
                verticalLineTo(19f)
                curveTo(21f, 20.1f, 20.1f, 21f, 19f, 21f)
                horizontalLineTo(17f)
                moveTo(7f, 21f)
                horizontalLineTo(5f)
                curveTo(3.9f, 21f, 3f, 20.1f, 3f, 19f)
                verticalLineTo(17f)
                moveTo(7f, 12f)
                horizontalLineTo(17f)
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
                moveTo(11f, 18f)
                horizontalLineTo(13f)
            }
        }.build()
    }

    val Tablet: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideTablet",
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
                moveTo(5f, 3f)
                horizontalLineTo(19f)
                curveTo(20.1f, 3f, 21f, 3.9f, 21f, 5f)
                verticalLineTo(19f)
                curveTo(21f, 20.1f, 20.1f, 21f, 19f, 21f)
                horizontalLineTo(5f)
                curveTo(3.9f, 21f, 3f, 20.1f, 3f, 19f)
                verticalLineTo(5f)
                curveTo(3f, 3.9f, 3.9f, 3f, 5f, 3f)
                close()
                moveTo(12f, 17f)
                horizontalLineTo(12.01f)
            }
        }.build()
    }

    val Laptop: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideLaptop",
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
                moveTo(4f, 6f)
                horizontalLineTo(20f)
                verticalLineTo(16f)
                horizontalLineTo(4f)
                close()
                moveTo(2f, 20f)
                horizontalLineTo(22f)
            }
        }.build()
    }

    val Monitor: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideMonitor",
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
                moveTo(4f, 4f)
                horizontalLineTo(20f)
                verticalLineTo(16f)
                horizontalLineTo(4f)
                close()
                moveTo(12f, 16f)
                verticalLineTo(20f)
                moveTo(8f, 20f)
                horizontalLineTo(16f)
            }
        }.build()
    }

    val Globe: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideGlobe",
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
                // Circle
                moveTo(12f, 2f)
                curveTo(17.52f, 2f, 22f, 6.48f, 22f, 12f)
                curveTo(22f, 17.52f, 17.52f, 22f, 12f, 22f)
                curveTo(6.48f, 22f, 2f, 17.52f, 2f, 12f)
                curveTo(2f, 6.48f, 6.48f, 2f, 12f, 2f)
                close()
                // Equator
                moveTo(2f, 12f)
                horizontalLineTo(22f)
                // Meridians
                moveTo(12f, 2f)
                curveTo(15f, 6f, 16.5f, 9f, 16.5f, 12f)
                curveTo(16.5f, 15f, 15f, 18f, 12f, 22f)
                moveTo(12f, 2f)
                curveTo(9f, 6f, 7.5f, 9f, 7.5f, 12f)
                curveTo(7.5f, 15f, 9f, 18f, 12f, 22f)
            }
        }.build()
    }

    val LogOut: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideLogOut",
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
                moveTo(9f, 21f)
                horizontalLineTo(5f)
                curveTo(3.9f, 21f, 3f, 20.1f, 3f, 19f)
                verticalLineTo(5f)
                curveTo(3f, 3.9f, 3.9f, 3f, 5f, 3f)
                horizontalLineTo(9f)
                moveTo(16f, 17f)
                lineTo(21f, 12f)
                lineTo(16f, 7f)
                moveTo(21f, 12f)
                horizontalLineTo(9f)
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

    val ShieldAlert: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideShieldAlert",
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
                moveTo(12f, 8f)
                verticalLineTo(12f)
                moveTo(12f, 16f)
                horizontalLineTo(12.01f)
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
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 2f)
                curveTo(17.52f, 2f, 22f, 6.48f, 22f, 12f)
                curveTo(22f, 17.52f, 17.52f, 22f, 12f, 22f)
                curveTo(6.48f, 22f, 2f, 17.52f, 2f, 12f)
                curveTo(2f, 6.48f, 6.48f, 2f, 12f, 2f)
                close()
                moveTo(12f, 6f)
                verticalLineTo(12f)
                lineTo(16f, 14f)
            }
        }.build()
    }

    val Copy: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideCopy",
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
                moveTo(9f, 9f)
                horizontalLineTo(20f)
                verticalLineTo(20f)
                horizontalLineTo(9f)
                close()
                moveTo(5f, 15f)
                horizontalLineTo(4f)
                curveTo(3.45f, 15f, 3f, 14.55f, 3f, 14f)
                verticalLineTo(4f)
                curveTo(3f, 3.45f, 3.45f, 3f, 4f, 3f)
                horizontalLineTo(14f)
                curveTo(14.55f, 3f, 15f, 3.45f, 15f, 4f)
                verticalLineTo(5f)
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
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(20f, 6f)
                lineTo(9f, 17f)
                lineTo(4f, 12f)
            }
        }.build()
    }

    val RefreshCw: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideRefreshCw",
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
                moveTo(3f, 12f)
                curveTo(3f, 7.03f, 7.03f, 3f, 12f, 3f)
                curveTo(15.7f, 3f, 18.88f, 5.25f, 20.24f, 8.5f)
                moveTo(21f, 3f)
                verticalLineTo(8.5f)
                horizontalLineTo(15.5f)
                moveTo(21f, 12f)
                curveTo(21f, 16.97f, 16.97f, 21f, 12f, 21f)
                curveTo(8.3f, 21f, 5.12f, 18.75f, 3.76f, 15.5f)
                moveTo(3f, 21f)
                verticalLineTo(15.5f)
                horizontalLineTo(8.5f)
            }
        }.build()
    }

    val KeyRound: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideKeyRound",
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
                moveTo(2f, 18f)
                lineTo(7f, 13f)
                moveTo(7f, 13f)
                curveTo(6.37f, 12.12f, 6f, 11.1f, 6f, 10f)
                curveTo(6f, 6.69f, 8.69f, 4f, 12f, 4f)
                curveTo(15.31f, 4f, 18f, 6.69f, 18f, 10f)
                curveTo(18f, 13.31f, 15.31f, 16f, 12f, 16f)
                curveTo(10.9f, 16f, 9.88f, 15.63f, 9f, 15f)
                lineTo(2f, 22f)
                verticalLineTo(18f)
                horizontalLineTo(6f)
                moveTo(12f, 10f)
                horizontalLineTo(12.01f)
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
                moveTo(20f, 10f)
                curveTo(20f, 16f, 12f, 22f, 12f, 22f)
                curveTo(12f, 22f, 4f, 16f, 4f, 10f)
                curveTo(4f, 5.58f, 7.58f, 2f, 12f, 2f)
                curveTo(16.42f, 2f, 20f, 5.58f, 20f, 10f)
                close()
                moveTo(12f, 10f)
                curveTo(13.1f, 10f, 14f, 9.1f, 14f, 8f)
                curveTo(14f, 6.9f, 13.1f, 6f, 12f, 6f)
                curveTo(10.9f, 6f, 10f, 6.9f, 10f, 8f)
                curveTo(10f, 9.1f, 10.9f, 10f, 12f, 10f)
                close()
            }
        }.build()
    }
}
