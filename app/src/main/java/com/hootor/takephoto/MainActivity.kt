package com.hootor.takephoto

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Camera
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.hootor.takephoto.databinding.ActivityMainBinding
import java.nio.channels.AsynchronousFileChannel.open
import java.nio.channels.DatagramChannel.open
import java.nio.channels.Pipe.open


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private val multiplePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),    // contract for requesting 1 permission
        ::onGotCameraPermissionResult
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        binding.takePhoto.setOnClickListener {
            multiplePermissionLauncher.launch(listOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE).toTypedArray())
        }


    }

    private fun onGotCameraPermissionResult(isGranted: Map<String, Boolean>) {
        var granted = true
        isGranted.forEach {
            if (granted){
                granted = it.value
            }
        }

        if (granted) {
//            launchScanning()
            Toast.makeText(this, "Даны все разрешения", Toast.LENGTH_SHORT).show()
        } else {
            // example of handling 'Deny & don't ask again' user choice
            if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                askUserForOpeningAppSettings()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun askUserForOpeningAppSettings() {
        val appSettingsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", this.packageName, null)
        )
        if (this.packageManager.resolveActivity(appSettingsIntent,
                PackageManager.MATCH_DEFAULT_ONLY) == null
        ) {
            Toast.makeText(this,
                "Permissions are denied forever",
                Toast.LENGTH_SHORT).show()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Permission denied")
                .setMessage("You have denied permissions forever. You can change your decision in the app settings.\n\nWould you like to open app settings?")
                .setPositiveButton("Open") { _, _ ->
                    startActivity(appSettingsIntent)
                }
                .create()
                .show()
        }
    }

}