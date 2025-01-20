package com.example.b2ccustomcamera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity

import androidx.activity.result.contract.ActivityResultContracts

import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {



    private val cameraPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {

            } else {

            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scanType = intent.getStringExtra(SCAN_TYPE_KEY)
        when (scanType) {
            SCAN_TYPE_PAN -> startPanScan()
            SCAN_TYPE_AADHAAR -> startAadhaarScan()
            SCAN_TYPE_CHEQUE -> startChequeScan()
        }

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) -> {

            }

            else -> {
                cameraPermissionRequest.launch(Manifest.permission.CAMERA)
            }
        }

    }


    companion object {
        const val SCAN_TYPE_KEY = "scan_type"
        const val SCAN_TYPE_PAN = "PAN"
        const val SCAN_TYPE_AADHAAR = "AADHAAR"
        const val SCAN_TYPE_CHEQUE = "CHEQUE"
    }

    private fun startPanScan() {
        val intent = Intent(this, CustomCamera::class.java).apply {
            putExtra(SCAN_TYPE_KEY, SCAN_TYPE_PAN)
        }
        this.startActivity(intent)
        finish()
    }

    private fun startAadhaarScan() {
        val intent = Intent(this, CustomCamera::class.java).apply {
            putExtra(SCAN_TYPE_KEY, SCAN_TYPE_AADHAAR)
        }
        this.startActivity(intent)
        finish()
    }

    private fun startChequeScan() {
        val intent = Intent(this, CustomCamera::class.java).apply {
            putExtra(SCAN_TYPE_KEY, SCAN_TYPE_CHEQUE)
        }
        this.startActivity(intent)
        finish()
    }
}




