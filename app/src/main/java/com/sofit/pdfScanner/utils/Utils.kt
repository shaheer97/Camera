package com.sofit.pdfScanner.utils

import android.content.Context
import android.widget.Toast

fun Context.showToast(string: String){
    Toast.makeText(this,string,Toast.LENGTH_SHORT).show()
}
fun Context.getAppName(): String = applicationInfo.loadLabel(packageManager).toString()
