package com.sofit.pdfScanner.utils;

import android.content.Context;
import android.util.Log;
import com.orhanobut.hawk.Hawk;

public class PrefsUtils {
    private Context context;

    public PrefsUtils(Context context) {
        this.context = context;
        initSharedPrefs(context);
    }

    public void initSharedPrefs(Context context) {
        Hawk.init(context).build();
    }

    public <T> void saveToPrefs(String key, T value) {
        Hawk.put(key, value);
    }

    public <T> T getFromPrefs(String key) {

        T value = Hawk.get(key);
        //  Log.d("Value", value.toString());
        return value;
    }

    public <T> T getFromPrefsWithDefault(String key, T defaultValue) {
        T value = Hawk.get(key, defaultValue);
        return value;
    }

    public void deleteAll() {
        Hawk.deleteAll();
        Log.d("HWK", String.valueOf(Hawk.deleteAll()));
    }

    public void deleteSingleValue(String key) {
        Hawk.delete(key);
    }

    public boolean checkKey(String key) {
        return Hawk.contains(key);
    }
}
