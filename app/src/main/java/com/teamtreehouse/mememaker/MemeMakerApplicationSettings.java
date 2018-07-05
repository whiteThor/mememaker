package com.teamtreehouse.mememaker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.teamtreehouse.mememaker.utils.StorageType;

public class MemeMakerApplicationSettings {

    SharedPreferences mSharedPreferences;

    public MemeMakerApplicationSettings(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    }

    public String getStoragePreferences() {
        return mSharedPreferences.getString("Storage",StorageType.INTERNAL);
    }

    public void setSharedPreferences(String storageType) {
        mSharedPreferences.edit().putString("Storage",storageType).apply();

        SharedPreferences.Editor  editor = mSharedPreferences.edit();
        editor.putInt("MeaningOfLife", 42);
        int answer = mSharedPreferences.getInt("MeaningOfLife", 0);
    }
}
