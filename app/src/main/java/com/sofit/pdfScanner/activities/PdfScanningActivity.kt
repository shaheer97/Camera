package com.sofit.pdfScanner.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.drawToBitmap
import com.kotlinpermissions.KotlinPermissions
import com.sofit.adshelper.core.AdsHelper
import com.sofit.documentscanner.ImageCropActivity
import com.sofit.documentscanner.helpers.ScannerConstants
import com.sofit.pdfScanner.R
import com.sofit.pdfScanner.utils.ConstantsClass
import com.sofit.pdfScanner.utils.PrefsUtils
import com.sofit.pdfScanner.utils.getAppName
import com.sofit.pdfScanner.utils.showToast
import kotlinx.android.synthetic.main.activity_pdf_scanning.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class PdfScanningActivity : AppCompatActivity() {
    lateinit var btnPick: RelativeLayout
    lateinit var btnSave: Button
    lateinit var imgBitmap: ImageView
    lateinit var mCurrentPhotoPath: String
    lateinit var prefsUtils: PrefsUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_scanning)
             AdsHelper.loadFacebookInterstitial(  false)
            AdsHelper.showFacebookBanner(this@PdfScanningActivity, findViewById<View>(R.id.bannerAdContainer) as RelativeLayout)

        prefsUtils = PrefsUtils(this)
        btnPick = findViewById(R.id.rl_pick_image)
        btnSave = findViewById(R.id.btnSave)
        imgBitmap = findViewById(R.id.imgBitmap)
        askPermission()
        btnSave.setOnClickListener(View.OnClickListener {
            Log.e("me", ScannerConstants.selectedImageBitmap.toString())
            applyStamp(ScannerConstants.selectedImageBitmap)
            pdfConverter(imgBitmap.drawToBitmap())
            btnSave.visibility = View.GONE
            btnPick.visibility = View.VISIBLE
            rl_show_pdf_file.visibility = View.VISIBLE
            imgBitmap.visibility = View.GONE
            AdsHelper.showFacebookInterstitial(this@PdfScanningActivity)

        })


        rl_show_pdf_file.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@PdfScanningActivity, ScannedImagesActivity::class.java))
            AdsHelper.showFacebookInterstitial(this@PdfScanningActivity)

        })
    }

    private fun applyStamp(btmap: Bitmap) {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        AdsHelper.showFacebookInterstitial(this@PdfScanningActivity)

    }

    fun pdfConverter(btmap: Bitmap) {
        val prefsUtils = PrefsUtils(this@PdfScanningActivity)
        val count = prefsUtils.getFromPrefsWithDefault(ConstantsClass.COUNTPDF, 1)
        val direct =
            File(Environment.getExternalStorageDirectory().path + ConstantsClass.DIR_NAME_PDF)

        if (!direct.exists()) {
            val wallpaperDirectory = File(ConstantsClass.PATH_DIR_NAME_PDF)
            wallpaperDirectory.mkdirs()
        }

        // Create a PdfDocument with a page of the same size as the image
        val document: PdfDocument = PdfDocument()
        val pageInfo: PdfDocument.PageInfo = PdfDocument.PageInfo.Builder(
            btmap.width,
            btmap.height,
            1
        ).create()
        val page: PdfDocument.Page = document.startPage(pageInfo)

        // Draw the bitmap onto the page
        val canvas: Canvas = page.canvas
        canvas.drawBitmap(btmap, 0f, 0f, null)
        document.finishPage(page)

        // Write the PDF file to a file
        val file = File(ConstantsClass.PATH_DIR_NAME_PDF, "${count}.pdf")
        prefsUtils.saveToPrefs(ConstantsClass.COUNTPDF, count + 1)
        if (file.exists()) {
            file.delete()
        }
        document.writeTo(FileOutputStream(file))
        document.close()
        showToast(getString(R.string.saved_message))
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1111 && resultCode == RESULT_OK && data != null) {
            var selectedImage = data.data
            var btimap: Bitmap? = null
            try {
                val inputStream = selectedImage?.let { contentResolver.openInputStream(it) }
                btimap = BitmapFactory.decodeStream(inputStream)
                ScannerConstants.selectedImageBitmap = btimap
                startActivityForResult(
                    Intent(this@PdfScanningActivity, ImageCropActivity::class.java),
                    1234
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (requestCode == 1231 && resultCode == Activity.RESULT_OK) {
            ScannerConstants.selectedImageBitmap = MediaStore.Images.Media.getBitmap(
                this.contentResolver,
                Uri.parse(mCurrentPhotoPath)
            )
            startActivityForResult(
                Intent(this@PdfScanningActivity, ImageCropActivity::class.java),
                1234
            )
        } else if (requestCode == 1234 && resultCode == Activity.RESULT_OK) {
            if (ScannerConstants.selectedImageBitmap != null) {
                ScannerConstants.selectedImageBitmap.apply {
                    imgBitmap.setImageBitmap(drawText())
                }
                setBW(imgBitmap)
                imgBitmap.visibility = View.VISIBLE
                btnPick.visibility = View.GONE
                btnSave.visibility = View.VISIBLE
               // btnShowScannedFiles.visibility = View.GONE
                rl_show_pdf_file.visibility = View.GONE
            } else
                showToast(getString(R.string.not_ok))
        }
    }


    fun Bitmap.drawText(
        text: String = getAppName(),
        textSize: Float = 44f,
        color: Int = Color.YELLOW
    ): Bitmap? {
        val bitmap = copy(config, true)
        val canvas = Canvas(bitmap)

        Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            this.color = color
            this.textSize = textSize
            typeface = ResourcesCompat.getFont(this@PdfScanningActivity, R.font.londrina_shadow)
            setShadowLayer(1f, 1f, 1f, Color.YELLOW)
            canvas.drawText(text, 5f, height - 20f, this)
        }
        return bitmap
    }

    private fun setBW(mainImage: ImageView) {
        val colorMatrix = floatArrayOf(
            // grey
            0f, 0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f, 0f,
            0f, 0f, 1f, 1f, 0f
            // full b&w
//            80f, 80f, 80f, 0f, -123f * 223f,
//            80f, 80f, 80f, 0f, -123f * 223f,
//            80f, 80f, 80f, 0f, -123f * 223f,
//            0f, 0f, 0f, 1f, 0f

        )
        val colorFilter: ColorFilter = ColorMatrixColorFilter(colorMatrix)
        mainImage.colorFilter = colorFilter
    }

    fun askPermission() {
        if (
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            KotlinPermissions.with(this)
                .permissions(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
                .onAccepted { permissions ->
                    setView()
                }
                .onDenied { permissions ->
                    askPermission()
                }
                .onForeverDenied { permissions ->
                    Toast.makeText(
                        this@PdfScanningActivity,
                        getString(R.string.perm_message),
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
                .ask()
        } else {
            setView()
        }
    }

    fun setView() {
        btnPick.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this@PdfScanningActivity)
            builder.setTitle(getAppName())
            builder.setMessage(getString(R.string.choose_image))
            builder.setPositiveButton(getString(R.string.gallery)) { dialog, which ->
                dialog.dismiss()
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 1111)
            }
            builder.setNegativeButton(getString(R.string.camera)) { dialog, which ->
                dialog.dismiss()
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (cameraIntent.resolveActivity(packageManager) != null) {
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                    } catch (ex: IOException) {
                     }
                    if (photoFile != null) {
                        val builder = StrictMode.VmPolicy.Builder()
                        StrictMode.setVmPolicy(builder.build())
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
                        startActivityForResult(cameraIntent, 1231)
                    }
                }
            }
            builder.setNeutralButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
            AdsHelper.showFacebookInterstitial(this@PdfScanningActivity)

        })
    }

    @SuppressLint("SimpleDateFormat")
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        )
        val image = File.createTempFile(
            imageFileName, // prefix
            ".jpg", // suffix
            storageDir      // directory
        )
        mCurrentPhotoPath = "file:" + image.absolutePath
        return image
    }


}
