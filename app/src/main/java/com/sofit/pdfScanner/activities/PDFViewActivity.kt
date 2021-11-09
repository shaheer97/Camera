package com.sofit.pdfScanner.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView
import com.sofit.pdfScanner.R
import com.sofit.pdfScanner.utils.ConstantsClass
import java.io.File


class PDFViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_p_d_f_view)

        val pdfView = findViewById<View>(R.id.pdfView) as PDFView
        val path:String= intent.getStringExtra(ConstantsClass.FILE_PDF).toString()
        pdfView.fromFile(File(path)).load()
    }
}