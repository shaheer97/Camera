package com.sofit.pdfScanner.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shockwave.pdfium.PdfiumCore
import com.sofit.pdfScanner.R
import com.sofit.pdfScanner.activities.PDFViewActivity
import com.sofit.pdfScanner.model.ScannedModel
import com.sofit.pdfScanner.utils.ConstantsClass
import com.sofit.pdfScanner.utils.PrefsUtils
import java.io.File
import java.io.IOException


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ScannedImagesAdapter(
    private val context: Context,
    private val dataset: ArrayList<ScannedModel>
) : RecyclerView.Adapter<ScannedImagesAdapter.MainViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ScannedImagesAdapter.MainViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.scanned_images_view, parent, false)
        return MainViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ScannedImagesAdapter.MainViewHolder, position: Int) {
        val item = dataset[position]
        holder.tvName.text = item.imagePath
        holder.tvPackage.text = item.scannedTime
        holder.prefsUtils = PrefsUtils(context)
        holder.imgDelete.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(context.getString(R.string.delete_message))

            builder.setPositiveButton(context.getString(R.string.yes)) { _, _ ->
                val target = File(item.imagePath)
                if (target.exists() && target.isFile && target.canWrite()) {
                    target.delete()
                    dataset.remove(dataset[position])
                    notifyDataSetChanged()
                }
            }

            builder.setNegativeButton(context.getString(R.string.no)) { _, _ ->

            }
            builder.show();

        })
        val fd: ParcelFileDescriptor = if (File(item.imagePath).exists()) ParcelFileDescriptor.open(
            File(item.imagePath),
            ParcelFileDescriptor.MODE_READ_WRITE
        ) else {
            return
        }
        val pageNum = 0
        val pdfiumCore = PdfiumCore(context)
        try {
            val pdfDocument: com.shockwave.pdfium.PdfDocument = pdfiumCore.newDocument(fd)
            pdfiumCore.openPage(pdfDocument, pageNum)
            val width: Int = pdfiumCore.getPageWidthPoint(pdfDocument, pageNum)
            val height: Int = pdfiumCore.getPageHeightPoint(pdfDocument, pageNum)

            // ARGB_8888 - best quality, high memory usage, higher possibility of OutOfMemoryError
            // RGB_565 - little worse quality, twice less memory usage
            val bitmap: Bitmap = Bitmap.createBitmap(
                width, height,
                Bitmap.Config.RGB_565
            )
            pdfiumCore.renderPageBitmap(pdfDocument, bitmap, pageNum, 0, 0, width, height)
            //if you need to render annotations and form fields, you can use
            //the same method above adding 'true' as last param

            Glide.with(context)
                .load(bitmap)
                .placeholder(R.drawable.ic_pdf)
                .into(holder.imgView)
            pdfiumCore.closeDocument(pdfDocument) // important!
        } catch (ex: IOException) {
            ex.printStackTrace()
            Toast.makeText(context, "failed", Toast.LENGTH_LONG).show()
        }

        holder.itemView.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, PDFViewActivity::class.java)
            intent.putExtra(ConstantsClass.FILE_PDF, item.imagePath)
            context.startActivity(intent)
        })

        holder.imgShare.setOnClickListener(View.OnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            val fileUri: Uri = Uri.parse(item.imagePath)
            sharingIntent.type = "*/*"
            sharingIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
            context.startActivity(
                Intent.createChooser(
                    sharingIntent,
                    context.getString(R.string.share_using)
                )
            )
        })


    }

    class MainViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        val tvName: TextView = v.findViewById(R.id.app_name)
        val tvPackage: TextView = v.findViewById(R.id.package_name)
        val imgView: ImageView = v.findViewById(R.id.app_icon)
        val imgDelete: ImageView = v.findViewById(R.id.imgDelete)
        val imgShare: ImageView = v.findViewById(R.id.imgShare)
        lateinit var prefsUtils: PrefsUtils
        override fun onClick(p0: View?) {}
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return dataset.size
    }

}