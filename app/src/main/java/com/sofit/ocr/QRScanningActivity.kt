package com.sofit.ocr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.sofit.adshelper.core.AdsHelper
import com.sofit.pdfScanner.R

class QRScanningActivity : AppCompatActivity() {

    lateinit var btnBarcode: ImageView
    lateinit var textView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_q_r_scanning)

            AdsHelper.loadFacebookInterstitial(  false)
            AdsHelper.showFacebookBanner(this@QRScanningActivity, findViewById<View>(R.id.bannerAdContainer) as RelativeLayout)

        btnBarcode = findViewById(R.id.button)
        textView = findViewById(R.id.textView)
        btnBarcode.setOnClickListener {
            val intentIntegrator = IntentIntegrator(this@QRScanningActivity)
            intentIntegrator.setBeepEnabled(true)
            intentIntegrator.setCameraId(0)
            intentIntegrator.setOrientationLocked(true)
            intentIntegrator.setPrompt("")
            intentIntegrator.setBarcodeImageEnabled(true)
            intentIntegrator.initiateScan()
            AdsHelper.showFacebookInterstitial(this@QRScanningActivity)

        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "cancelled", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("MainActivity", "Scanned")
                Toast.makeText(this, "Scanned -> " + result.contents, Toast.LENGTH_SHORT)
                    .show()
                textView.text = String.format("Scanned Result: %s", result)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AdsHelper.showFacebookInterstitial(this@QRScanningActivity)

    }
}
