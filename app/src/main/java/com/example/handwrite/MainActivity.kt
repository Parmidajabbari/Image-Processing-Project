package com.example.handwrite

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.icu.text.AlphabeticIndex.Record
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.util.Size
import android.view.TextureView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.core.VideoCapture
import androidx.camera.core.VideoCapture.Builder
//import androidx.camera.core.ImageCapture
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.camera.video.VideoCapture
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
import androidx.camera.core.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.DateFormat
import java.util.Date
import androidx.camera.view.PreviewView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
class MainActivity : ComponentActivity() {

    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var videoCapture: VideoCapture<Recorder>
    private lateinit var camera: Camera

    //    @SuppressLint("RestrictedApi")
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create a file to save the captured vid  o.
        val outputDirectory = getOutputDirectory()
        val videoFile = File(
            outputDirectory,
            "${System.currentTimeMillis()}.mp4"
        )

        // Create a cameraSelector to select the camera to use.
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        // Create the Preview use case to display the camera preview.
        val preview = Preview.Builder()
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
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, videoCapture
                )
                // Start recording when the camera is bound.
                camera.startRecording(videoFile, ContextCompat.getMainExecutor(this), object : VideoCapture.OnVideoSavedCallback {
                    override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                        Log.d(TAG, "Video saved: ${videoFile.absolutePath}")
                    }

                    override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                        Log.e(TAG, "Video capture failed: $message", cause)
                    }
                })
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))

        setContent {
            FirstPage()
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

@Preview(showBackground = true)
@Composable
fun FirstPage() {
    MainPage()
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
