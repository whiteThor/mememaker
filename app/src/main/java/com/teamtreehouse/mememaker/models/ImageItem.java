package com.teamtreehouse.mememaker.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageItem {
    private Bitmap mBitmap;
    private String mText;

    public ImageItem(String filePath, String text) {
        mBitmap = BitmapFactory.decodeFile(filePath);
        mText = text;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public String getText() {
        return mText;
    }
}
