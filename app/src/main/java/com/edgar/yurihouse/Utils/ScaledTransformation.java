package com.edgar.yurihouse.Utils;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

public class ScaledTransformation extends BitmapTransformation {

    private int width;
    private static final String TAG = "======================" + ScaledTransformation.class.getSimpleName();

    public ScaledTransformation(int width) {
        this.width = width;
    }

    @Override
    public Bitmap transform(BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        if (toTransform.getWidth() == outWidth && toTransform.getHeight() == outHeight) {
            return toTransform;
        }
        if (toTransform.getWidth() == 0) {
            return toTransform;
        }

        double aspectRatio = (double) toTransform.getHeight() / (double) toTransform.getWidth();
        int targetHeight = (int) (width * aspectRatio);

        if (targetHeight != 0 && width != 0) {
            return Bitmap.createScaledBitmap(toTransform, width, targetHeight, false);

        } else {
            return toTransform;
        }
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update("Scaled Transformation".getBytes());
    }

}
