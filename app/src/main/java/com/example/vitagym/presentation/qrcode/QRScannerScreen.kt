package com.example.vitagym.presentation.qrcode



import android.Manifest
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.vitagym.data.checkin.CheckInRepository
import com.example.vitagym.data.qr.QRCodeScanner
import com.example.vitagym.utils.CameraPermissionHelper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QRScannerScreen(
    onBackClick: () -> Unit,
    onCheckInSuccess: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val qrScanner = remember { QRCodeScanner() }
    val checkInRepository = remember { CheckInRepository() }
    val scope = rememberCoroutineScope()

    var isProcessing by remember { mutableStateOf(false) }
    var checkInState by remember { mutableStateOf<CheckInState>(CheckInState.Scanning) }

    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    LaunchedEffect(Unit) {
        if (!CameraPermissionHelper.hasCameraPermission(context)) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            qrScanner.close()
            cameraExecutor.shutdown()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (cameraPermissionState.hasPermission) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()

                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val imageAnalyzer = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also { analysis ->
                                analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                                    if (!isProcessing && checkInState is CheckInState.Scanning) {
                                        processImage(
                                            imageProxy = imageProxy,
                                            qrScanner = qrScanner,
                                            onQRDetected = { qrData ->
                                                isProcessing = true
                                                scope.launch {
                                                    handleCheckIn(
                                                        qrData = qrData,
                                                        repository = checkInRepository,
                                                        onStateChange = { checkInState = it },
                                                        onSuccess = onCheckInSuccess
                                                    )
                                                    isProcessing = false
                                                }
                                            }
                                        )
                                    } else {
                                        imageProxy.close()
                                    }
                                }
                            }

                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalyzer
                            )
                        } catch (e: Exception) {
                            Log.e("QRScanner", "Camera binding failed", e)
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )

            ScannerOverlay()

            when (val state = checkInState) {
                is CheckInState.Scanning -> {
                    InstructionCard(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 60.dp)
                    )
                }
                is CheckInState.Processing -> {
                    LoadingCard(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is CheckInState.Success -> {
                    SuccessCard(
                        gymName = state.gymName,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is CheckInState.Error -> {
                    ErrorCard(
                        message = state.message,
                        onRetry = { checkInState = CheckInState.Scanning },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Arrangement.Center
            ) {
                Text(
                    text = "Camera permission required to scan QR codes",
                    fontSize = 18.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { cameraPermissionState.launchPermissionRequest() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00E5FF)
                    )
                ) {
                    Text("Grant Permission")
                }
            }
        }

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Text("← Back", color = Color.White, fontSize = 18.sp)
        }
    }
}

private fun processImage(
    imageProxy: ImageProxy,
    qrScanner: QRCodeScanner,
    onQRDetected: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        kotlinx.coroutines.GlobalScope.launch {
            val qrData = qrScanner.scanQRCode(image)
            if (qrData != null) {
                onQRDetected(qrData)
            }
            imageProxy.close()
        }
    } else {
        imageProxy.close()
    }
}

private suspend fun handleCheckIn(
    qrData: String,
    repository: CheckInRepository,
    onStateChange: (CheckInState) -> Unit,
    onSuccess: () -> Unit
) {
    onStateChange(CheckInState.Processing)

    val parts = qrData.split("|")
    if (parts.size < 2) {
        onStateChange(CheckInState.Error("Invalid QR code format"))
        return
    }

    val gymId = parts[0]
    val gymName = parts[1]

    val result = repository.checkIn(gymId, gymName)

    result.onSuccess {
        onStateChange(CheckInState.Success(gymName))
        kotlinx.coroutines.delay(2000)
        onSuccess()
    }.onFailure { error ->
        onStateChange(CheckInState.Error(error.message ?: "Check-in failed"))
    }
}

@Composable
private fun BoxScope.ScannerOverlay() {
    Box(
        modifier = Modifier
            .size(280.dp)
            .align(Alignment.Center)
            .background(Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Color(0xFF00E5FF).copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp)
                )
        )
    }
}

@Composable
private fun InstructionCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E3F)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Scan QR Code to Check In",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00E5FF)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Position the QR code within the frame",
                fontSize = 14.sp,
                color = Color(0xFFB0B0B0)
            )
        }
    }
}

@Composable
private fun LoadingCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E3F)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Color(0xFF00E5FF),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Checking in...",
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun SuccessCard(gymName: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF00E676)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = Color.White,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Check-in Successful!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = gymName,
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ErrorCard(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFF5252)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Check-in Failed",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                fontSize = 16.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFFFF5252)
                )
            ) {
                Text("Try Again")
            }
        }
    }
}

sealed class CheckInState {
    object Scanning : CheckInState()
    object Processing : CheckInState()
    data class Success(val gymName: String) : CheckInState()
    data class Error(val message: String) : CheckInState()
}