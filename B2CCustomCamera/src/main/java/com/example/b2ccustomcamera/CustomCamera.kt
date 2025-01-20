package com.example.b2ccustomcamera

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.example.b2ccustomcamera.MainActivity.Companion.SCAN_TYPE_AADHAAR
import com.example.b2ccustomcamera.MainActivity.Companion.SCAN_TYPE_CHEQUE
import com.example.b2ccustomcamera.MainActivity.Companion.SCAN_TYPE_KEY
import com.example.b2ccustomcamera.MainActivity.Companion.SCAN_TYPE_PAN
import com.example.b2ccustomcamera.ui.theme.DummyAndroidSDKTheme
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import org.json.JSONObject
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

var scanType=""
class CustomCamera : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         scanType = intent.getStringExtra(SCAN_TYPE_KEY).toString()
        enableEdgeToEdge()
        setContent {
            CustomCameraScreen()
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CustomCameraScreen(modifier: Modifier = Modifier) {
    DummyAndroidSDKTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Body(modifier = Modifier.padding(innerPadding))

        }
    }
}

@Composable
fun Body(modifier: Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.LightGray.copy(alpha = 0.2f))
    ) {
        Spacer(modifier = Modifier.padding(12.dp))
        Text("Custom Camera", color = Color.White)
        Spacer(modifier = Modifier.padding(12.dp))
        CameraPreviewScreen()
    }
}


@Composable
fun CameraPreviewScreen() {
    var recognizedText = remember { mutableStateOf<String?>(null) }
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val preview = androidx.camera.core.Preview.Builder().build()
    val previewView = remember {
        PreviewView(context)
    }
    val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()
    }
    // State to hold the captured image URI
    val capturedImageUri = remember { mutableStateOf<Uri?>(null) }
    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, preview, imageCapture)
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    Box( modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.padding(12.dp))
        AndroidView(
            { previewView },
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .aspectRatio(4f / 3f)
                .align(Alignment.Center)
                .clip(RoundedCornerShape(10.dp))

        )
        if(capturedImageUri.value==null){
            Button(onClick = {
                captureImage(
                    imageCapture,
                    context,
                    capturedImageUri,
                    recognizedText
                )
            }, modifier = Modifier.align(Alignment.BottomCenter)) {
                Text(text = "Capture Image")
            }
        }else{
            Button(onClick = {
                println("Scan Button is pressed")
                val inputStream =
                    capturedImageUri.value?.let { context.contentResolver.openInputStream(it) }

                val bitmap = inputStream.use { BitmapFactory.decodeStream(it) }
                processImage(bitmap, context, recognizedText)
            }, modifier = Modifier.align(Alignment.BottomCenter)) {
                Text(text = "Scan")
            }
        }

        capturedImageUri.value?.let { uri ->
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = rememberAsyncImagePainter(model = uri),
                    contentDescription = "Captured Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(24.dp)
                        .align(Alignment.Center)
                        .height(previewView.height.dp)
                        .width(previewView.width.dp)
                        .aspectRatio(4f / 3f)
                        .clip(RoundedCornerShape(10.dp)).border(2.dp, Color.Black)

                )
                Icon(
                    imageVector = Icons.Filled.Close,
                    modifier = Modifier.size(48.dp).align(Alignment.TopEnd).clickable {
                        capturedImageUri.value = null
                        recognizedText.value = null },
                    contentDescription = "Close Icon",
                    tint = Color.Red
                )

            }

        }
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }


private fun captureImage(
    imageCapture: ImageCapture,
    context: Context,
    capturedImageUri: MutableState<Uri?>,
    recognizedText: MutableState<String?>,
) {
    val uniqueName = "CameraxImage_${System.currentTimeMillis()}.jpeg"
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, uniqueName)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
        }
    }
    val outputOptions = ImageCapture.OutputFileOptions
        .Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues,
        )
        .build()

    // Ensure directory exists
    val directory = File("/storage/emulated/0/Pictures/CameraX-Image/")
    if (!directory.exists()) {
        directory.mkdirs()
    }

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {

                capturedImageUri.value = outputFileResults.savedUri
            }

            override fun onError(exception: ImageCaptureException) {
                println("Failed $exception")
                exception.printStackTrace()
            }

        })
}

private fun processImage(
    bitmap: Bitmap,
    context: Context,
    recognizedText: MutableState<String?>,
) {


    val recognizer: TextRecognizer =
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    val image = InputImage.fromBitmap(bitmap, 0)

    recognizer.process(image)

        .addOnSuccessListener { visionText ->

            recognizedText.value = visionText.text
            val jsonOutput = parseOCRToJSON( recognizedText.value!!)
            println("Recognized Text is  ${recognizedText.value}")
            println("Recognized Text in JSON Format  $jsonOutput")

        }

        .addOnFailureListener { e ->
            recognizedText.value = "Error: ${e.message}"
            println("Recognized Text is  ${recognizedText.value}")

        }

}



fun parseOCRToJSON(ocrText: String): String {
    val documentType: String
    val data = JSONObject()


    if(scanType==SCAN_TYPE_PAN){
            documentType = "PAN"

            val lines = ocrText.split("\n").map { it.trim() }

            data.put("name", lines.getOrNull(2) ?: "")
            data.put("fatherName", lines.getOrNull(3) ?: "")
            data.put("dateOfBirth", lines.find { it.matches(Regex("\\d{2}/\\d{2}/\\d{4}")) } ?: "")
            data.put("panNumber", lines.find { it.matches(Regex("[A-Z]{5}[0-9]{4}[A-Z]")) } ?: "")
            data.put("issueDate", lines.find { it.matches(Regex("\\d{2}/\\d{2}/\\d{4}")) } ?: "")
            data.put("signature", lines.getOrNull(lines.size - 1) ?: "")
        }else if(scanType==SCAN_TYPE_AADHAAR) {
            documentType = "Aadhaar"

            val lines = ocrText.split("\n").map { it.trim() }

            data.put("name", lines.getOrNull(1) ?: "")
            data.put("aadhaarNumber", lines.find { it.matches(Regex("\\d{4} \\d{4} \\d{4}")) } ?: "")
            data.put("dobOrYearOfBirth", lines.find { it.matches(Regex("\\d{2}/\\d{2}/\\d{4}|\\d{4}")) } ?: "")
        }else if(scanType==SCAN_TYPE_CHEQUE){
            documentType = "Cheque"

            val lines = ocrText.split("\n").map { it.trim() }

            data.put("bankName", lines.getOrNull(0) ?: "")
            data.put("accountNumber", lines.find { it.matches(Regex("\\d{9,18}")) } ?: "")
            data.put("ifscCode", lines.find { it.matches(Regex("[A-Z]{4}0[A-Z0-9]{6}")) } ?: "")
            data.put("chequeNumber", lines.find { it.matches(Regex("\\d{6}")) } ?: "")
        }
        else{
            documentType = "Unknown"
            data.put("rawData", ocrText)
    }

    return JSONObject()
        .put("documentType", documentType)
        .put("data", data)
        .put("responseType", "zzb")
        .toString(4) // Pretty print JSON with 4 spaces
}


