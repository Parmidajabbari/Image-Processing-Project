import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.camera.core.CameraXThreads.TAG
import androidx.camera.core.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File

@SuppressLint("RestrictedApi")
fun startRecording(context: Context, activity : Activity, videoCapture : VideoCapture, videoFile : File): VideoCapture {
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.CAMERA),
            0
        )
        Log.i(TAG, "request permissions")
    }

    videoCapture.startRecording(
        VideoCapture.OutputFileOptions.Builder(videoFile).build(),
        ContextCompat.getMainExecutor(context),
        object : VideoCapture.OnVideoSavedCallback {
            override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                Log.d(ContentValues.TAG, "Video saved: ${videoFile.absolutePath}")
            }

            override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                Log.e(ContentValues.TAG, "Video capture failed: $message", cause)
            }
        }
    )
    return videoCapture
}

@SuppressLint("RestrictedApi")
fun stopRecording(videoCapture: VideoCapture) {
    videoCapture.stopRecording()
}