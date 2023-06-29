package com.example.handwrite

//import androidx.camera.core.ImageCapture
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.camera.video.VideoCapture
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Size
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.VideoCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.view.PreviewView
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.DateFormat
import java.util.Date

class MainActivity : ComponentActivity() {

    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var videoCapture: VideoCapture
    private lateinit var camera: Camera

    //    @SuppressLint("RestrictedApi")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create a file to save the captured vid  o.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) {
            // Permission has been granted, you can write to external storage
            // ...
        } else {
            // Permission has not been granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                0
            )
        }
        val outputDirectory = getOutputDirectory()
        val videoFile = File(
            outputDirectory,
            "${System.currentTimeMillis()}.mp4"
        )

        // Create a cameraSelector to select the camera to use.
        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

        val previewView = PreviewView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // Create the Preview use case to display the camera preview.
        val preview = androidx.camera.core.Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

        // Create the VideoCapture use case to capture the video.
        @SuppressLint("RestrictedApi")
        videoCapture = VideoCapture.Builder()
            .build()
        // Bind the camera use cases to the lifecycle of this activity.
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                0
            )
        }


        cameraProvider = cameraProviderFuture.get()
        cameraProvider.unbindAll()
        camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, videoCapture
                )
        setContent {
            FirstPage(this, this, videoCapture, videoFile)
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

}


fun getResolutions(selector: CameraSelector,
                   provider:ProcessCameraProvider
): Map<Quality, Size> {
    return selector.filter(provider.availableCameraInfos).firstOrNull()
        ?.let { camInfo ->
            QualitySelector.getSupportedQualities(camInfo)
                .associateWith { quality ->
                    QualitySelector.getResolution(camInfo, quality)!!
                }
        } ?: emptyMap()
}
@OptIn(ExperimentalAnimationApi::class)
//@Preview(showBackground = true)
@Composable
fun FirstPage(activity : Activity, context: Context, videoCapture : VideoCapture, videoFile : File) {
    MainPage(activity, context, videoCapture, videoFile)
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
fun MyScreen(formName: String, rowName: String) {
    var pathPoints by remember { mutableStateOf(emptyList<Pair<Float, Float>>()) }

    HandwritingInput(formName = formName, onSave = { newPoints ->
        pathPoints = newPoints
    })

    Button(onClick = {
        val csvHeader = "X, Y, field, date"
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

