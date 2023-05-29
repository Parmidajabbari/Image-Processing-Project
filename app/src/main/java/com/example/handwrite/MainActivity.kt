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
import java.io.File
import java.io.FileWriter
import java.io.IOException

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
    val filePath = "p1.csv"
    Column(
        modifier = Modifier
//            .fillMaxSize()
//            .verticalScroll(rememberScrollState())
    ) {
        TopMessage(message = "Please fill the following form.")
        FieldMessage(message = "Write your first name.")
        MyScreen(filePath, "fname")
        FieldMessage(message = "Write your last name.")
        MyScreen(filePath, "lname")
        FieldMessage(message = "Write your age.")
        MyScreen(filePath, "age")
        FieldMessage(message = "Write 'Apple'.")
        MyScreen(filePath, "apple")
        FieldMessage(message = "Write 'Mother'.")
        MyScreen(filePath, "mother")
        FieldMessage(message = "Write 'Sky'.")
        MyScreen(filePath, "sky")
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
fun MyScreen(filePath: String, rowName: String) {
    var pathPoints by remember { mutableStateOf(emptyList<Pair<Float, Float>>()) }

    HandwritingInput(onSave = { newPoints ->
        pathPoints = newPoints
    })

    Button(onClick = {
        val csvHeader = "X,Y"
        val csvData = pathPoints.joinToString("\n") { "${it.first},${it.second},${rowName}" }
        val csvContent = "$csvHeader\n$csvData"

        val csvFile = File("path_points.csv")

        try {
            // Create the file if it doesn't exist
            if (!csvFile.exists()) {
                csvFile.createNewFile()
            }

            // Write the CSV content to the file
            FileWriter(csvFile).use { writer ->
                writer.append(csvContent)
            }

            println("Data saved to ${csvFile.absolutePath}")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }) {
        Text("Save")
    }
}
