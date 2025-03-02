package com.example.a35b_crud.utils


import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class ImageUtils(private val fragment: Fragment) {
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    fun registerActivity(callback: (Uri?) -> Unit) {
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)

                activityResultLauncher = fragment.registerForActivityResult(
                    ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    val imageData = result.data
                    if (result.resultCode == AppCompatActivity.RESULT_OK && imageData != null) {
                        val imageUri = imageData.data
                        callback(imageUri) // Return the selected image URI
                    } else {
                        Log.e("ImageUtils", "Image selection failed or cancelled")
                    }
                }

                permissionLauncher = fragment.registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->
                    if (isGranted) {
                        openGallery()
                    } else {
                        Log.e("ImageUtils", "Permission denied for gallery access")
                    }
                }
            }
        })
    }

    fun launchGallery(context: Context) {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(permission)
        } else {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        activityResultLauncher.launch(intent)
    }
}
