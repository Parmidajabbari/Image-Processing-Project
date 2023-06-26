package com.example.handwrite

import android.widget.ImageView
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Preview(showBackground = true)
@Composable
fun ImageViewer() {
    Box() {
        SeeResults()
        DisplayImages()
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DisplayImages() {
    val context = LocalContext.current
    val images = listOf("ipimage1", "ipimage2", "ipimage3", "ipimage4", "ipimage5", "ipimage6")
    var currentImageIndex by remember { mutableStateOf(0) }
    val timerState = remember { mutableStateOf(TimerState.INACTIVE) }

    LaunchedEffect(Unit) {
        timerState.value = TimerState.ACTIVE
    }

    AnimatedContent(
        targetState = currentImageIndex,
        transitionSpec = {
            fadeIn() with fadeOut()
        }
    ) { targetIndex ->
        val imageResource = context.resources.getIdentifier(
            images[targetIndex],
            "drawable",
            context.packageName,
        )
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = imageResource),
            contentDescription = null,
        )
    }

    LaunchedEffect(timerState.value) {
        var j = 0
        if (timerState.value == TimerState.ACTIVE) {
            while (j != 5) {
                delay(1000L)
                currentImageIndex = (currentImageIndex + 1) % images.size
                j++
            }
        }
    }
}

enum class TimerState {
    INACTIVE,
    ACTIVE
}

@Composable
fun SeeResults() {
    Button(
        modifier = Modifier
            .padding(vertical = 250.dp)
            .fillMaxWidth()
            .height(200.dp),
        onClick = { /*TODO*/ },
    ) {
        Text(text = "Tap here to see results!")
    }
}

//@Composable
//fun DisplayImage(imagePath: String, duration: Int) {
//    val context = LocalContext.current
//
//    val animatedProgress = remember { Animatable(1f) }
//
//    LaunchedEffect(animatedProgress) {
//        animatedProgress.animateTo(0.5f,
//            animationSpec = tween(
//                durationMillis = duration,
//                delayMillis = duration
//            ))
//    }
//
//    Box() {
//        val imageResource = context.resources.getIdentifier(
//            imagePath,
//            "drawable",
//            context.packageName,
//        )
//        Image(
//            painter = painterResource(id = imageResource),
//            modifier = Modifier
//                .graphicsLayer {
//                    scaleY = animatedProgress.value;
//                    scaleX = animatedProgress.value
//                }
//                .fillMaxSize(),
//            contentDescription = "background_image",
//            contentScale = ContentScale.FillBounds
//        )
//    }
//}