package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

class SignatureState {
    var paths by mutableStateOf<List<Path>>(emptyList())
    var currentPath by mutableStateOf<Path?>(null)
    var size by mutableStateOf(IntSize.Zero)

    fun clear() {
        paths = emptyList()
        currentPath = null
    }

    fun toBitmap(): Bitmap? {
        if (size.width == 0 || size.height == 0) return null
        val bitmap = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.WHITE) // maybe transparent? white is fine for PDF
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = 5f
            isAntiAlias = true
            strokeJoin = android.graphics.Paint.Join.ROUND
            strokeCap = android.graphics.Paint.Cap.ROUND
        }
        paths.forEach { path ->
            canvas.drawPath(path.asAndroidPath(), paint)
        }
        return bitmap
    }
}

@Composable
fun rememberSignatureState() = remember { SignatureState() }

@Composable
fun SignaturePad(
    state: SignatureState,
    modifier: Modifier = Modifier,
    strokeColor: Color = Color.Black,
    strokeWidth: Float = 5f
) {
    Canvas(
        modifier = modifier
            .onSizeChanged { state.size = it }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        state.currentPath = Path().apply { moveTo(offset.x, offset.y) }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        state.currentPath?.lineTo(change.position.x, change.position.y)
                    },
                    onDragEnd = {
                        state.currentPath?.let {
                            state.paths = state.paths + it
                            state.currentPath = null
                        }
                    },
                    onDragCancel = {
                        state.currentPath = null
                    }
                )
            }
    ) {
        state.paths.forEach { path ->
            drawPath(
                path = path,
                color = strokeColor,
                style = Stroke(width = strokeWidth)
            )
        }
        state.currentPath?.let { path ->
            drawPath(
                path = path,
                color = strokeColor,
                style = Stroke(width = strokeWidth)
            )
        }
    }
}
