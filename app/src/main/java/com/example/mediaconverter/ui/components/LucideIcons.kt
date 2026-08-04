package com.example.mediaconverter.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/** A small, local Lucide icon set so the UI has no extra icon-pack dependency. */
object LucideIcons {
    val Link = icon("Link") { moveTo(8f, 12f); lineTo(6f, 14f); lineTo(6f, 17f); lineTo(8f, 19f); lineTo(11f, 19f); lineTo(14f, 16f); moveTo(16f, 12f); lineTo(18f, 10f); lineTo(18f, 7f); lineTo(16f, 5f); lineTo(13f, 5f); lineTo(10f, 8f); moveTo(8f, 16f); lineTo(16f, 8f) }
    val Folder = icon("Folder") { moveTo(3f, 6f); lineTo(9f, 6f); lineTo(11f, 8f); lineTo(21f, 8f); verticalLineTo(19f); lineTo(3f, 19f); close() }
    val Play = icon("Play") { moveTo(5f, 3f); lineTo(19f, 12f); lineTo(5f, 21f); close() }
    val ListVideo = icon("ListVideo") { moveTo(8f, 6f); horizontalLineTo(21f); moveTo(8f, 12f); horizontalLineTo(21f); moveTo(8f, 18f); horizontalLineTo(21f); moveTo(3f, 6f); horizontalLineTo(3.1f); moveTo(3f, 12f); horizontalLineTo(3.1f); moveTo(3f, 18f); horizontalLineTo(3.1f) }
    val History = icon("History") { moveTo(3f, 12f); lineTo(6f, 9f); lineTo(9f, 12f); moveTo(6f, 9f); verticalLineTo(14f); moveTo(6f, 9f); lineTo(10f, 5f); lineTo(17f, 5f); lineTo(21f, 9f); lineTo(21f, 16f); lineTo(17f, 20f); lineTo(10f, 20f); lineTo(6f, 16f); moveTo(12f, 8f); verticalLineTo(13f); lineTo(16f, 15f) }
    val ChevronDown = icon("ChevronDown") { moveTo(6f, 9f); lineTo(12f, 15f); lineTo(18f, 9f) }
    val ArrowLeft = icon("ArrowLeft") { moveTo(19f, 12f); horizontalLineTo(5f); moveTo(12f, 19f); lineTo(5f, 12f); lineTo(12f, 5f) }
    val Sparkles = icon("Sparkles") { moveTo(12f, 3f); lineTo(14f, 10f); lineTo(21f, 12f); lineTo(14f, 14f); lineTo(12f, 21f); lineTo(10f, 14f); lineTo(3f, 12f); lineTo(10f, 10f); close(); moveTo(19f, 16f); verticalLineTo(22f); moveTo(16f, 19f); horizontalLineTo(22f) }
    val Check = icon("Check") { moveTo(5f, 12f); lineTo(9f, 16f); lineTo(19f, 6f) }
    val Trash = icon("Trash") { moveTo(4f, 7f); horizontalLineTo(20f); moveTo(10f, 11f); verticalLineTo(17f); moveTo(14f, 11f); verticalLineTo(17f); moveTo(6f, 7f); lineTo(7f, 21f); horizontalLineTo(17f); lineTo(18f, 7f); moveTo(9f, 7f); lineTo(10f, 3f); horizontalLineTo(14f); lineTo(15f, 7f) }

    private fun icon(name: String, block: androidx.compose.ui.graphics.vector.PathBuilder.() -> Unit): ImageVector =
        ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).apply {
            path(
                fill = SolidColor(Color.Transparent), stroke = SolidColor(Color.Black), strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round
            ) { block() }
        }.build()

}
