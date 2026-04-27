package com.example.vitagym.data.qr


import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.tasks.await

class QRCodeScanner {

    private val scanner = BarcodeScanning.getClient()

    /**
     * Scans an image for QR codes and returns the decoded value.
     * Returns null if no QR code is found or an error occurs.
     */
    suspend fun scanQRCode(image: InputImage): String? {
        return try {
            val barcodes = scanner.process(image).await()
            barcodes.firstOrNull { it.format == Barcode.FORMAT_QR_CODE }?.rawValue
        } catch (e: Exception) {
            null
        }
    }

    fun close() {
        scanner.close()
    }
}
