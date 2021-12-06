package me.hsy.embeddedcam

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import android.media.CamcorderProfile
import android.media.MediaMetadataRetriever
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import android.widget.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private var shutterBtn: ImageButton? = null
    private var flipcamBtn: ImageButton? = null
    private var mSurfaceView: SurfaceView? = null
    private var mSurfaceHolder: SurfaceHolder? = null

    private var mCamera: Camera? = null
    private var mMediaRecorder: MediaRecorder? = null
    private var mIsRecording = false

    private var inputFilename: EditText? = null
    private var lastImage: ImageView? = null
    private var currentCameraId: Int? = null
    private var infoPanel: TextView? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // request permissions
        if (!checkPermissionAllGranted(mPermissionsArrays)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(mPermissionsArrays, REQUEST_PERMISSIONS)
            }
        }
        else {
            Toast.makeText(this@MainActivity, "All permissions granted.",
                Toast.LENGTH_SHORT).show()
        }

        // init the views
        mSurfaceView = findViewById(R.id.surface_view)
        shutterBtn = findViewById(R.id.shutter_btn)
        flipcamBtn = findViewById(R.id.flipcam_btn)
        lastImage = findViewById(R.id.last_image)
        inputFilename = findViewById(R.id.input_filename)
        infoPanel = findViewById(R.id.info_panel)

        startCamera(Camera.CameraInfo.CAMERA_FACING_BACK)
        showLastPic()

        // shutter button functionality
        shutterBtn?.setOnClickListener{
            if (mIsRecording) {
                stopRecordVideo()
                showLastPic()
            }
            else {
                takePicture()
            }
        }
        shutterBtn?.setOnLongClickListener {
            if (!mIsRecording) {
                recordVideo()
            }
            true
        }

        shutterBtn?.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP && mIsRecording) {
                stopRecordVideo()
                showLastPic()
                return@setOnTouchListener true
            }
            return@setOnTouchListener false
        }

        flipcamBtn?.setOnClickListener {
            flipCamera()
        }

    }

    private fun startCamera(cameraNumber: Int) {
        try {
            mCamera = Camera.open(cameraNumber)
            currentCameraId = cameraNumber
            setCameraDisplayOrientation()
        } catch (e: Exception) {
            // error
        }
        mSurfaceHolder = mSurfaceView!!.holder
        mSurfaceHolder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    mCamera?.setPreviewDisplay(holder)
                    mCamera?.startPreview()
                } catch (e: IOException) {
                    // error
                }
            }

            override fun surfaceChanged(holder: SurfaceHolder, i: Int, i1: Int, i2: Int) {
                try {
                    mCamera!!.stopPreview()
                } catch (e: Exception) {
                    // error
                }
                try {
                    mCamera!!.setPreviewDisplay(holder)
                    mCamera!!.startPreview()
                } catch (e: Exception) {
                    //error
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {}
        })
    }

    private fun setCameraDisplayOrientation() {
        val rotation = windowManager.defaultDisplay.rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees   = 0
            Surface.ROTATION_90 -> degrees  = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(currentCameraId!!, info)
        val result = (info.orientation - degrees + 360) % 360
        mCamera!!.setDisplayOrientation(result)
    }

    private fun showLastPic(){
        val pictureDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        var latestFile: File? = null
        var latestCreateTime: Long = -1
        var currentCreateTime: File? = null
        for(i in pictureDirectory!!.listFiles()){
            if(i.lastModified() > latestCreateTime){
                latestCreateTime = i.lastModified()
                latestFile = i
            }
        }

        var bitmap:Bitmap = Bitmap.createBitmap(128,128, Bitmap.Config.ARGB_8888)

        if(latestFile != null){
            val fileName: String = latestFile?.name
            // image file
            if (fileName.contains("IMG_")){
                bitmap = decodeBitmapFromFile(latestFile.absolutePath, 128, 128)
                bitmap = rotateImage(bitmap, 90f)

                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeFile(latestFile.absolutePath, options)
                val width = options.outWidth
                val height = options.outHeight
                infoPanel?.text = "Last Image: ${latestFile.name}\nResolution: ${height}x${width}"
            }
            // video file
            else if (fileName.contains("VID_")){
                bitmap = getFrameInVideo(latestFile.absolutePath)
                val width = bitmap.width
                val height = bitmap.height
                bitmap = rotateImage(bitmap, 90f)
                infoPanel?.text = "Last Video: ${latestFile.name}\nResolution: ${height}x${width}"
            }
        }
        lastImage?.setImageBitmap(bitmap)
    }


    // rotate the picture to correctly display the thumbnail
    private fun rotateImage(image: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.setRotate(degree)
        val width = image.width
        val height = image.height
        return Bitmap.createBitmap(image, 0, 0, width, height, matrix, true)
    }


    private fun getFrameInVideo(path: String): Bitmap {
        var bitmap:Bitmap? = null
        var retriever: MediaMetadataRetriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(path)
            bitmap= retriever.frameAtTime!!
        } catch (e: IllegalArgumentException) {
            // error
        } catch (e: RuntimeException) {
            // error
        } finally {
            try {
                retriever.release()
            } catch (e: RuntimeException) {
                e// error
            }
        }
        return bitmap!!
    }

    private fun recordVideo() {
        Log.d("@=>", "[Video] record start")
        if(mIsRecording){
            return
        }
        if (prepareVideoRecorder()) {
            mMediaRecorder!!.start()
            mIsRecording = true
        }
        else {
            releaseMediaRecorder()
        }
    }

    private fun takePicture() {
        Log.d("@=>", "[Picture] taken")
        mCamera!!.takePicture(null, null, Camera.PictureCallback { bytes, camera ->
            val pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE) ?: return@PictureCallback
            try {
                val fos = FileOutputStream(pictureFile)
                fos.write(bytes)
                fos.close()
            } catch (e: FileNotFoundException) {
                //error
            } catch (e: IOException) {
                //error
            }
            mCamera!!.startPreview()
            showLastPic()
        })
    }

    private fun stopRecordVideo() {
        Log.d("@=>", "[Video] record stop")
        if (mIsRecording) {
            mMediaRecorder!!.stop()
            releaseMediaRecorder()
            mCamera!!.lock()
            mIsRecording = false
            Toast.makeText(this,
                "Video Record Finished", Toast.LENGTH_LONG).show()
            showLastPic()
        }
    }

    private fun getOutputMediaFile(type: Int): File? {
        val mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (!mediaStorageDir!!.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val input = "_" + inputFilename?.text.toString()
//        infoPanel?.text = "IMG_" + "${input}_$timeStamp" + ".jpg"
        return if (type == MEDIA_TYPE_IMAGE) {
            File(mediaStorageDir.path + File.separator + "IMG" + "${input}_$timeStamp" + ".jpg")

        } else if (type == MEDIA_TYPE_VIDEO) {
            File(mediaStorageDir.path + File.separator + "VID" + "${input}_$timeStamp" + ".mp4")
        } else {
            return null
        }
    }

    private fun prepareVideoRecorder(): Boolean {
        mMediaRecorder = MediaRecorder()
        mCamera!!.unlock()
        mMediaRecorder!!.setCamera(mCamera)
        mMediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
        mMediaRecorder!!.setVideoSource(MediaRecorder.VideoSource.CAMERA)
        mMediaRecorder!!.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH))
        mMediaRecorder!!.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString()) // Save
        mMediaRecorder!!.setPreviewDisplay(mSurfaceHolder!!.surface)
        try {
            mMediaRecorder!!.prepare()
        } catch (e: IllegalStateException) {
            releaseMediaRecorder()
            return false
        } catch (e: IOException) {
            releaseMediaRecorder()
            return false
        }
        return true
    }

    private fun flipCamera(){
        releaseCamera()
        val cameraNumber = (currentCameraId!! + 1) % 2
        startCamera(cameraNumber)
        mCamera?.setPreviewDisplay(mSurfaceView?.holder);
        mCamera?.startPreview();
    }

    private fun releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder!!.reset()
            mMediaRecorder!!.release()
            mMediaRecorder = null
            mCamera!!.lock()
        }
    }

    private fun releaseCamera() {
        if (mCamera != null) {
            mCamera!!.release()
            mCamera = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaRecorder()
        releaseCamera()
    }

    private fun decodeBitmapFromFile(path: String, reqWidth: Int, reqHeight: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(path, options)
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }




    private val mPermissionsArrays = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS) {
            for (i in permissions.indices) {
                if (grantResults.size > i && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,
                        "Granted permission: " + permissions[i], Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object{
        const val REQUEST_PERMISSIONS = 1
        const val MEDIA_TYPE_IMAGE = 2
        const val MEDIA_TYPE_VIDEO = 3
    }
}