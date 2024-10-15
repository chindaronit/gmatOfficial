package com.gmat

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.gmat.navigation.AppNavHost
import com.gmat.ui.theme.GmatTheme
import com.gmat.ui.viewModel.LeaderboardViewModel
import com.gmat.ui.viewModel.ScannerViewModel
import com.gmat.ui.viewModel.TransactionViewModel
import com.gmat.ui.viewModel.UserViewModel
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        // Initialize Firebase App Check with the Play Integrity provider
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )

        enableEdgeToEdge()
        installGoogleScanner()
        setContent {
            GmatTheme {
                val scannerViewModel: ScannerViewModel = hiltViewModel()
                val userViewModel: UserViewModel = hiltViewModel()
                val leaderboardViewModel: LeaderboardViewModel = hiltViewModel()
                val transactionViewModel: TransactionViewModel = hiltViewModel()
                transactionViewModel.initUserViewModel(this@MainActivity)
                AppNavHost(scannerViewModel=scannerViewModel, userViewModel = userViewModel, transactionViewModel = transactionViewModel, leaderboardViewModel = leaderboardViewModel)
            }
        }
    }

    private fun installGoogleScanner() {
        val moduleInstall = ModuleInstall.getClient(this)
        val moduleInstallRequest = ModuleInstallRequest.newBuilder()
            .addApi(GmsBarcodeScanning.getClient(this))
            .build()

        moduleInstall.installModules(moduleInstallRequest).addOnFailureListener {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }
}
