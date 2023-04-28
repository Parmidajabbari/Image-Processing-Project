package com.example.handwrite

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HandwritingInput(onSave: (List<Pair<Float, Float>>) -> Unit) {
    val pathPoints = remember { mutableListOf<Pair<Float, Float>>() }

    Box(
        modifier = Modifier.fillMaxSize().height(100.dp).background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        var isTouchInProgress by remember { mutableStateOf(false) }
        var currentPath by remember { mutableStateOf(Path()) }

        Canvas(
            modifier = Modifier.fillMaxSize().pointerInteropFilter { event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        pathPoints.clear()
                        currentPath.reset()
                        currentPath.moveTo(event.x, event.y)
                        pathPoints.add(event.x to event.y)
                        isTouchInProgress = true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        currentPath.lineTo(event.x, event.y)
                        pathPoints.add(event.x to event.y)
                    }
                    MotionEvent.ACTION_UP -> {
                        isTouchInProgress = false
                        onSave(pathPoints.toList())
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        isTouchInProgress = false
                    }
                }
                true
            },
            onDraw = {
                val strokeWidth = 12f
                val color = Color.White
                drawPath(
                    path = currentPath,
                    brush = SolidColor(color),
                    alpha = color.alpha,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                        miter = 0f,
                        pathEffect = null,
                    ),
                    colorFilter = null
                )
            }
        )
    }
}
