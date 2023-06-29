package com.example.handwrite

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HandwritingInput(formName: String, onSave: (List<Pair<Float, Float>>) -> Unit) {
    var paths by remember { mutableStateOf(emptyList<Path>()) }
    var currentPath by remember { mutableStateOf(Path()) }
    val pathPoints = remember { mutableListOf<Pair<Float, Float>>() }
    val context = LocalContext.current
    val imageResource = context.resources.getIdentifier(
        formName,
        "drawable",
        context.packageName,
    )

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Box() {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = imageResource),
                contentDescription = null,
            )
            var isTouchInProgress by remember { mutableStateOf(false) }
            Canvas (
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
                    .pointerInteropFilter { event ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                currentPath = Path().apply {
                                    moveTo(event.x, event.y)
                                }
                                isTouchInProgress = true
                            }

                            MotionEvent.ACTION_MOVE -> {
                                currentPath.lineTo(event.x, event.y)
                                pathPoints.add(event.x to event.y)
                            }

                            MotionEvent.ACTION_UP -> {
                                isTouchInProgress = false
                                onSave(pathPoints.toList())
                                paths += currentPath
                                currentPath = Path()
                            }

                            MotionEvent.ACTION_CANCEL -> {
                                isTouchInProgress = false
                            }
                        }
                        true
                    }
            ) {
                paths.forEach { path ->
                    drawPath(path, Color.Black, style = Stroke(width = 5.dp.toPx()))
                }
                drawPath(currentPath, Color.Black, style = Stroke(width = 5.dp.toPx()))
            }
        }
    }
}
