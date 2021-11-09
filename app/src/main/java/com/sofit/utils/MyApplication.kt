package com.sofit.utils

import android.app.Application
import android.os.StrictMode
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.orhanobut.hawk.Hawk
import com.sofit.adshelper.core.AdsHelper
import com.sofit.pdfScanner.BuildConfig
import com.sofit.pdfScanner.R

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        Hawk.init(this).build()
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        setAds()
    }

    fun setAds() {
        if (BuildConfig.DEBUG) {
            AdsHelper.Builder(context = applicationContext)
                .with(applicationContext)
                .isVerified(BuildConfig.DEBUG)
                .fbBannerId(applicationContext.getString(R.string.facebook_banner_test))
                .fbInterstitialID(applicationContext.getString(R.string.facebook_interstitial_test))
                .build()

        } else if (UtilsID.verifyInstallerId(applicationContext)) {
            AdsHelper.Builder(context = applicationContext)
                .with(applicationContext)
                .isVerified(BuildConfig.DEBUG)
                .fbBannerId(applicationContext.getString(R.string.facebook_banner))
                .fbInterstitialID(applicationContext.getString(R.string.facebook_interstitial))
                .build()
        }
    }

}