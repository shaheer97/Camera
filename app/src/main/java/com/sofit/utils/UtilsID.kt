package com.sofit.utils

import android.content.Context
import com.google.android.gms.common.util.CollectionUtils.listOf
import java.util.*

object UtilsID {
    fun verifyInstallerId(context: Context): Boolean {
        val validInstallers: List<String> =
            ArrayList(listOf("com.android.vending"))
        val installer = context.packageManager.getInstallerPackageName(context.packageName)
        return installer != null && validInstallers.contains(installer)
    }
}
