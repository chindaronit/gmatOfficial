package com.gmat.ui.screens.merchant

import android.content.ContentValues
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gmat.R
import com.gmat.ui.components.CenterBar
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.gmat.navigation.NavRoutes
import com.gmat.ui.events.QRScannerEvents
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

fun saveQrToGallery(context: Context, bitmap: Bitmap) {
    val filename = "QRCode_${System.currentTimeMillis()}.png"
    val outputStream: OutputStream?

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }
        val imageUri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        outputStream = imageUri?.let { context.contentResolver.openOutputStream(it) }
    } else {
        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
        val image = File(imagesDir, filename)
        outputStream = FileOutputStream(image)
    }

    outputStream?.use {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        Toast.makeText(context, "QR Code saved to gallery", Toast.LENGTH_SHORT).show()
    } ?: run {
        Toast.makeText(context, "Error saving QR Code", Toast.LENGTH_SHORT).show()
    }
}

fun generateQrCode(text: String): Bitmap? {
    val qrCodeWriter = QRCodeWriter()
    return try {
        val bitMatrix: BitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 800, 800)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.Black.toArgb() else Color.White.toArgb())
            }
        }
        bitmap
    } catch (e: WriterException) {
        e.printStackTrace()
        null
    }
}


@Composable
fun UpgradedQR(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val qrCodeBitmap = remember { generateQrCode("upi://pay?pa=gpay-11249012205@okbizaxis&mc=5411&pn=Google%20Pay%20Merchant&oobe=fos123&qrst=stk&tr=1249012205&cu=INR&ver=01&mode=01&gstin=22AAAAA0000A1Z5") }
    val context = LocalContext.current
    Scaffold(
        topBar = {
            CenterBar(
                onClick = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.UpgradeQR.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                title = {
                    Text(
                        text = stringResource(id = R.string.upgrade_qr),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                })
        }
    ) { contentPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(top = 5.dp, bottom = 5.dp, start = 5.dp, end = 5.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            qrCodeBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .border(BorderStroke(1.dp, Color.Black)),
                    contentScale = ContentScale.FillWidth
                )
            }
            Spacer(modifier = modifier.height(20.dp))
            Text(
                text = "UPI ID: chinda@ybl",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp)
            )
            Spacer(modifier = modifier.weight(1f))
            Button(
                onClick = {
                    qrCodeBitmap?.let { bitmap ->
                        saveQrToGallery(context, bitmap)
                    }
                },
                modifier = modifier
                    .fillMaxWidth()
            ) {
                Text("Download")
            }
        }
    }
}