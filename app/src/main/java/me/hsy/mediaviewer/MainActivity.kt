package me.hsy.mediaviewer

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.view.View
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION_CODES.M
import android.widget.Toast


class MainActivity : AppCompatActivity() {
    private var permissionBtn: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initBtn()

        permissionBtn = findViewById<Button>(R.id.permission_btn)
        permissionBtn?.setOnClickListener{
            if (!checkPermissionAllGranted(mPermissionsArrays)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(mPermissionsArrays, REQUEST_PERMISSION_CODE)
                }
            }
            else {
                Toast.makeText(this@MainActivity, "All permissions granted.",
                    Toast.LENGTH_SHORT).show()
            }

        }
    }
    private val mPermissionsArrays = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) // Permissions requested

    private fun checkPermissionAllGranted(permissions: Array<String>): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        for (permission in permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (i in permissions.indices) {
                if (grantResults.size > i && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,
                        "Granted permission: " + permissions[i], Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun initBtn(){
        open(R.id.image_btn, ImageActivity::class.java)
        open(R.id.video_btn, VideoActivity::class.java)
    }

    private fun open(buttonId: Int, clz: Class<*>) {
        findViewById<View>(buttonId).setOnClickListener { startActivity(Intent(this@MainActivity, clz)) }
    }

    companion object{
        private const val REQUEST_PERMISSION_CODE = 114
    }
}

