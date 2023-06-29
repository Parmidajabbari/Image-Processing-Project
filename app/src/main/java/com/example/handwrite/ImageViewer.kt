package com.example.handwrite

import android.app.Activity
import android.content.Context
import androidx.camera.core.VideoCapture
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import startRecording
import stopRecording
import java.io.File

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

@Composable
fun SelectionLayout(onSelectionChanged: (String) -> Unit) {
    var selectedButton by remember {
        mutableStateOf(UserSelection.Test1)
    }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xffF7F7F7))
            .padding(horizontal = 5.dp, vertical = 3.dp)
            .height(40.dp)
    ) {
        SelectionButton(layoutName = "Test1", isSelected = UserSelection.Test1 == selectedButton) {
            selectedButton = UserSelection.Test1
            onSelectionChanged("Test1")
        }
        SelectionButton(layoutName = "Test2", isSelected = UserSelection.Test2 == selectedButton) {
            selectedButton = UserSelection.Test2
            onSelectionChanged("Test2")
        }
        SelectionButton(layoutName = "Test3", isSelected = UserSelection.Test3 == selectedButton) {
            selectedButton = UserSelection.Test3
            onSelectionChanged("Test3")
        }
    }
}

@Composable
fun RowScope.SelectionButton(layoutName: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(onClick = { onClick() }, colors = ButtonDefaults.buttonColors(backgroundColor =
    if(isSelected) Color.White else Color(0xffF7F7F7)
    ),
        elevation = ButtonDefaults.elevation(defaultElevation = 0.dp, pressedElevation = 0.dp, disabledElevation = 0.dp),
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
    ) {
        Text(text = layoutName, color =  Color(0xff3D195B),
        )
    }
}

enum class UserSelection() {
    Test1,
    Test2,
    Test3,
}

@OptIn(ExperimentalAnimationApi::class)
//@Preview
@Composable
fun MainPage(activity : Activity, context: Context, videoCapture : VideoCapture, videoFile : File) {
    var videoCapture2: VideoCapture = videoCapture
    var isRecording = false
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Button(onClick = {
            if (!isRecording) {
                videoCapture2 = startRecording(context, activity, videoCapture, videoFile)
            }
            isRecording = true
        }) {
            Text(text = "Record")
        }
        Button(onClick = {
            if (isRecording) {
                stopRecording(videoCapture2)
            }
            isRecording = false
        }) {
            Text(text = "Stop")
        }
        var state by remember {
            mutableStateOf("Test1")
        }
        SelectionLayout() {
            state = it
        }
        AnimatedContent(
            targetState = state,
            transitionSpec = {
                slideInHorizontally() + fadeIn() with slideOutHorizontally() + fadeOut()
            }
        ) {
            when(state) {
                "Test1" -> {
                    TopMessage(message = "Please fill the following form.")
                    MyScreen(formName =  "ipform","fname")
                }
                "Test2" -> {
                    TopMessage(message = "Draw something you like.")
                    MyScreen(formName =  "ipform2","fname")
                }
                "Test3" -> {
                    ImageViewer()
                }
            }
        }
    }
}