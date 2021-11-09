package com.sofit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.sofit.adshelper.core.AdsHelper
import com.sofit.ocr.QRScanningActivity
import com.sofit.pdfScanner.R
import com.sofit.pdfScanner.activities.PdfScanningActivity
import com.sofit.realtimetext.RealTimeTextDetection
import com.sofit.utils.CustomViewDialog

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
             AdsHelper.showFacebookBanner(this@MainActivity, findViewById<View>(R.id.bannerAdContainer) as RelativeLayout)

    }


    fun scanClick(view: View) {
        startActivity((Intent(this@MainActivity, PdfScanningActivity::class.java)))
        AdsHelper.showFacebookInterstitial(this@MainActivity)


    }

    fun realTimeText(view: View) {
        startActivity(Intent(this@MainActivity, RealTimeTextDetection::class.java))
        AdsHelper.showFacebookInterstitial(this@MainActivity)

    }


    fun ocrClick(view: View) {
        startActivity(Intent(this@MainActivity, QRScanningActivity::class.java))
        AdsHelper.showFacebookInterstitial(this@MainActivity)


    }

    override fun onBackPressed() {
        if (!this@MainActivity.isFinishing) {
            val cDialog: CustomViewDialog = CustomViewDialog(this@MainActivity)
            cDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            cDialog.setCancelable(false)
            if (!cDialog.isShowing && !isFinishing) {
                cDialog.show()
            }
        }
        AdsHelper.showFacebookInterstitial(this@MainActivity)
    }
}