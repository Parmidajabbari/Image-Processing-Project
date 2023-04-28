package com.example.handwrite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirstPage()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FirstPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TopMessage(message = "Please fill the following form.")
        FieldMessage(message = "Write your first name.")
        MyScreen()
        FieldMessage(message = "Write your last name.")
        MyScreen()
        FieldMessage(message = "Write your age.")
        MyScreen()
        FieldMessage(message = "Write 'Apple'.")
        MyScreen()
        FieldMessage(message = "Write 'Mother'.")
        MyScreen()
        FieldMessage(message = "Write 'Sky'.")
        MyScreen()
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
    }
}

@Composable
fun TopMessage(message: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = message,
        fontSize = 20.sp,
        textAlign = TextAlign.Center,
    )

    Spacer(
        modifier = Modifier.padding(vertical = 10.dp)
    )
}

@Composable
fun FieldMessage(message: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = message,
        fontSize = 17.sp,
    )

    Spacer(
        modifier = Modifier.padding(vertical = 10.dp)
    )
}

@Composable
fun MyScreen() {
    var pathPoints by remember { mutableStateOf(emptyList<Pair<Float, Float>>()) }

    HandwritingInput(onSave = { newPoints ->
        pathPoints = newPoints
    })

    Button(onClick = {
    }) {
        Text("Save")
    }
}
