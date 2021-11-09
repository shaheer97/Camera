package com.sofit.realtimetext

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Vibrator
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.sofit.pdfScanner.R
import java.io.IOException

class RealTimeTextDetection : AppCompatActivity() {

    lateinit var cameraSourceLast: CameraSource
    lateinit var cameraViewLast: SurfaceView
    lateinit var editTextLast: EditText

    val REQUESTPERMISSIONID = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_time_text_detection)
        cameraViewLast = findViewById<SurfaceView>(R.id.surfaceView_myLast)
        editTextLast = findViewById<EditText>(R.id.et_myLast)
        val textRecognizer = TextRecognizer.Builder(
            applicationContext
        ).build()
        if (!textRecognizer.isOperational) {
            Toast.makeText(this, "Non Operational case", Toast.LENGTH_SHORT).show()
        } else {
            cameraSourceLast = CameraSource.Builder(applicationContext, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(2.0f)
                .setRequestedPreviewSize(1280, 1080)
                .setAutoFocusEnabled(true)
                .build()
            cameraViewLast.getHolder().addCallback(object : SurfaceHolder.Callback {
                @SuppressLint("MissingPermission")
                override fun surfaceCreated(holder: SurfaceHolder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(
                                applicationContext,
                                Manifest.permission.CAMERA
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return
                        }
                        cameraSourceLast.start(cameraViewLast.getHolder())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun surfaceChanged(
                    holder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int
                ) {
                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                    cameraSourceLast.stop()
                }
            })
            textRecognizer.setProcessor(object : Detector.Processor<TextBlock> {
                override fun release() {}
                override fun receiveDetections(detections: Detections<TextBlock>) {
                    val items = detections.detectedItems
                    if (items.size() != 0) {
                        editTextLast.post(Runnable {
                            val stringBuilder = StringBuilder()
                            for (i in 0 until items.size()) {
                                val item = items.valueAt(i)
                                stringBuilder.append(item.value)
                                stringBuilder.append("\n")
                            }
                            editTextLast.setText(stringBuilder.toString())
                        })
                    }
                }
            })
        }
    }

    fun shareClick(view: View?) {
        val mymessage: String = editTextLast.getText().toString()
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_TEXT, mymessage)
        startActivity(Intent.createChooser(sharingIntent, "Share using"))
    }


    @SuppressLint("MissingPermission")
    fun stopDetection(view: View?) {
        val btnshare = findViewById<ImageView>(R.id.share_img)
        val btnstop = findViewById<Button>(R.id.btnstopdetection)
        cameraViewLast.setVisibility(View.INVISIBLE)
        val v = getSystemService(VIBRATOR_SERVICE) as Vibrator
        v.vibrate(400)
        btnshare.visibility = View.VISIBLE
        btnstop.visibility = View.GONE
    }
}