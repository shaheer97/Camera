package com.sofit.pdfScanner.activities

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sofit.adshelper.core.AdsHelper
import com.sofit.pdfScanner.R
import com.sofit.pdfScanner.adapter.ScannedImagesAdapter
import com.sofit.pdfScanner.model.ScannedModel
import com.sofit.pdfScanner.utils.ConstantsClass
import com.sofit.pdfScanner.utils.PrefsUtils
import java.io.File
import java.util.*


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ScannedImagesActivity : AppCompatActivity() {
    private val imagesArrayList = ArrayList<ScannedModel>()
    private lateinit var adapter: ScannedImagesAdapter
    lateinit var prefsUtils: PrefsUtils
    lateinit var file: File

    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanned_images)
             AdsHelper.showFacebookBanner(this@ScannedImagesActivity, findViewById<View>(R.id.bannerAdContainer) as RelativeLayout)

        prefsUtils = PrefsUtils(this)
        showList()
        if (imagesArrayList.size==0){
            findViewById<TextView>(R.id.tool_tv).text=getString(R.string.no_data)
        }
    }


    private fun showList() {
        file = File(ConstantsClass.PATH_DIR_NAME_PDF)
        val listFile: Array<File>
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            Toast.makeText(applicationContext, getString(R.string.no_card), Toast.LENGTH_LONG).show()
        } else {
            if (!file.exists()) {
                file.mkdirs()
            }
        }

        if (file.isDirectory) {
            listFile = file.listFiles()
            for (absolutePath in listFile) {
                val lastModDate = Date(absolutePath.lastModified())
                Log.e("pathIs", absolutePath.toString())
                updateArrayList(ScannedModel(absolutePath.toString(), lastModDate.toString()))
            }
            setAdapter()
            adapter.notifyDataSetChanged()
        }
    }

    private fun updateArrayList(scannedModel: ScannedModel) {
        imagesArrayList.add(scannedModel)
    }

    override fun onPause() {
        super.onPause()
        Log.e("checksize", imagesArrayList.size.toString())
    }

    private fun setAdapter() {
        linearLayoutManager = LinearLayoutManager(this)
        val recyclerView: RecyclerView = findViewById(R.id.rvScannedImages)
        recyclerView.layoutManager = linearLayoutManager
        adapter = ScannedImagesAdapter(this@ScannedImagesActivity, imagesArrayList)
        recyclerView.adapter = adapter
//        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
//        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}
