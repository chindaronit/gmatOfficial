package com.gmat.ui.events

sealed class QRScannerEvents {
    data object StartScanning: QRScannerEvents()
    data object ClearState: QRScannerEvents()
    data class AddQR(val qr:String):QRScannerEvents()
}
