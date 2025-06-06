package com.amehran.barcodescanner.ui.theme.screens // Or your UI package

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.* // Keep this for remember, mutableStateOf, etc.
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
// NO 'import androidx.compose.runtime.getValue' needed if not using 'by' for this state
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import com.amehran.barcodescanner.presentation.scanner.BarcodeScanUiState
import com.amehran.barcodescanner.presentation.scanner.BarcodeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executor


@Composable
fun BarcodeScannerScreen( // Assuming this is your BarcodeCaptureScreen
    viewModel: BarcodeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // --- CHANGE IS HERE ---
    // Collect the state, but don't use 'by' delegation.
    // 'uiStateHolder' is now of type State<BarcodeScanUiState>
    val uiStateHolder: State<BarcodeScanUiState> =
        viewModel.uiState
    // --- END OF CHANGE ---


    var hasCameraPermission by remember { mutableStateOf(false) } // 'by' is fine for local remember states
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(key1 = true) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Scaffold { paddingValues ->
        Box(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()) {

            if (hasCameraPermission) {
                CameraView( // Assuming CameraView is defined as in previous examples
                    context = context,
                    lifecycleOwner = lifecycleOwner,
                    onImageCaptured = { bitmap ->
                        viewModel.processBarcodeScan(bitmap)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text("Camera permission is required.", modifier = Modifier.align(Alignment.Center))
            }

            // --- UI State Handling - USING .value ---
            // Access the actual state value using uiStateHolder.value
            when (val currentState = uiStateHolder.value) {
                is BarcodeScanUiState.Idle -> {
                    // Idle state UI
                }
                is BarcodeScanUiState.Scanning -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is BarcodeScanUiState.Success -> {
                    // No need to cast if 'currentState' is already smart-cast
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Barcode Found: ${currentState.barcodes.firstOrNull()?.displayValue ?: "N/A"}")
                        Button(onClick = { viewModel.clearScanResult() }) {
                            Text("Scan Another")
                        }
                    }
                }
                is BarcodeScanUiState.Error -> {
                    // No need to cast if 'currentState' is already smart-cast
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Error: ${currentState.message}")
                        Button(onClick = { viewModel.clearScanResult() }) {
                            Text("Try Again")
                        }
                    }
                }
            }
        }
    }
}


// --- CameraView and imageProxyToBitmap FROM PREVIOUS EXAMPLES WOULD GO HERE ---
// Make sure CameraView is defined as before. I'm omitting it for brevity here,
// but it's the same CameraView that takes onImageCaptured lambda.

@Composable
fun CameraView(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    onImageCaptured: (Bitmap?) -> Unit,
    modifier: Modifier = Modifier
) {
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) } // 'by' is fine here
    val coroutineScope = rememberCoroutineScope()

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            val executor = ContextCompat.getMainExecutor(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                imageCapture = ImageCapture.Builder()
                    .setTargetRotation(previewView.display.rotation)
                    .build()

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                } catch (exc: Exception) {
                    Log.e("CameraView", "Use case binding failed", exc)
                    onImageCaptured(null)
                }
            }, executor)
            previewView
        },
        modifier = modifier
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Button(
            onClick = {
                val localImageCapture = imageCapture ?: run {
                    Log.e("CameraView", "ImageCapture not initialized.")
                    onImageCaptured(null)
                    return@Button
                }
                val captureExecutor: Executor = Dispatchers.IO.asExecutor()
                localImageCapture.takePicture(
                    captureExecutor,
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            val bitmap = imageProxyToBitmap(image) // Defined below
                            image.close()
                            coroutineScope.launch(Dispatchers.Main) {
                                onImageCaptured(bitmap)
                            }
                        }
                        override fun onError(exception: ImageCaptureException) {
                            Log.e("CameraView", "Image capture failed: ${exception.message}", exception)
                            coroutineScope.launch(Dispatchers.Main) {
                                onImageCaptured(null)
                            }
                        }
                    }
                )
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Capture Barcode")
        }
    }
}

private fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
    if (image.format != ImageFormat.YUV_420_888) {
        Log.e("ImageUtil", "Unsupported image format: ${image.format}")
        // Attempt to convert from JPEG if that's the format, otherwise return null or throw
        if (image.format == ImageFormat.JPEG) {
            val buffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
        return null
    }

    val yBuffer = image.planes[0].buffer
    val uBuffer = image.planes[1].buffer
    val vBuffer = image.planes[2].buffer
    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()
    val nv21 = ByteArray(ySize + uSize + vSize)
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 90, out) // Quality 90
    val imageBytes = out.toByteArray()
    var bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

    val rotationDegrees = image.imageInfo.rotationDegrees
    if (rotationDegrees != 0) {
        val matrix = Matrix()
        matrix.postRotate(rotationDegrees.toFloat())
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    return bitmap
}