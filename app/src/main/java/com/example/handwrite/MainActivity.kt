package com.example.handwrite

import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

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
//            .fillMaxSize()
//            .verticalScroll(rememberScrollState())
    ) {
        TopMessage(message = "Please fill the following form.")
        FieldMessage(message = "Write your first name.")
        MyScreen("fname")
        FieldMessage(message = "Write your last name.")
        MyScreen("lname")
        FieldMessage(message = "Write your age.")
        MyScreen("age")
        FieldMessage(message = "Write 'Apple'.")
        MyScreen("apple")
        FieldMessage(message = "Write 'Mother'.")
        MyScreen("mother")
        FieldMessage(message = "Write 'Sky'.")
        MyScreen("sky")
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
fun MyScreen(rowName: String) {
    var pathPoints by remember { mutableStateOf(emptyList<Pair<Float, Float>>()) }

    HandwritingInput(onSave = { newPoints ->
        pathPoints = newPoints
    })

    Button(onClick = {
        val csvHeader = "X,Y,field, date"
        val dateTime = Date()
        val dateFormat = DateFormat.getDateTimeInstance()
        val formattedDateTime = dateFormat.format(dateTime)
        val csvData = pathPoints.joinToString("\n") { "${it.first},${it.second},${rowName},${formattedDateTime.toString()}" }
        val csvContent = "$csvHeader\n$csvData"
        try {
                val externalStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                // Create a new file in the external storage directory
                val csvFile = File(externalStorageDir, "/path_points.csv")
//              Write the CSV content to the file
                FileWriter(csvFile).use { writer ->
                    writer.append(csvContent)
                }
                println("Data saved to ${csvFile.absolutePath}")
            } catch (e: IOException) {
                e.printStackTrace()
            }
//        }
    }) {
        Text("Save")
    }
}
