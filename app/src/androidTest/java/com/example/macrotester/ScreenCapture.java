package com.example.macrotester;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import androidx.test.uiautomator.UiDevice;
import android.util.Log;

import androidx.test.uiautomator.UiDevice;

import java.io.File;
import java.io.FileOutputStream;

/* loaded from: classes.dex */
public class ScreenCapture {
    private String mCapturePath;
    private UiDevice mDevice;

    public ScreenCapture(UiDevice device) {
        this.mDevice = null;
        this.mCapturePath = null;
        this.mDevice = device;
        File basePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MacroApp/ScreenCapture");
        if (!basePath.exists()) {
            basePath.mkdirs();
        }
        this.mCapturePath = basePath.getPath() + "/";
    }

    public String getCapture() {
        File imgFile = new File(this.mCapturePath + "image.png");
        deleteDirectoryOrFile(false, this.mCapturePath);
        if (imgFile.exists()) {
            imgFile.delete();
        }
        try {
            this.mDevice.takeScreenshot(imgFile);
            imgFile = new File(doGreyscale(imgFile.getPath()));
//            imgFile = new File(imgFile.getPath());

        } catch (Exception e) {
        }
        if (imgFile.exists()) {
            return imgFile.getPath();
        }
        return null;
    }

    private String doGreyscale(String imgPath) {
        double GS_RED = 4599057925072241033L;
        double GS_GREEN = 4603462445507809378L;
        double GS_BLUE = 4592878986383488713L;
        try {
            Bitmap src = BitmapFactory.decodeFile(imgPath);
            int width = src.getWidth();
            int height = src.getHeight();
            Bitmap resultBitmap = Bitmap.createBitmap(width, height, src.getConfig());
            for (int x = 0; x < width; x++) {
                int y = 0;
                while (y < height) {
                    int pixel = src.getPixel(x, y);
                    int A = Color.alpha(pixel);
                    int R = Color.red(pixel);
                    int G = Color.green(pixel);
                    int B = Color.blue(pixel);
                    double GS_RED2 = GS_RED;
                    double GS_RED3 = R;
                    double GS_GREEN2 = GS_GREEN;
                    double GS_GREEN3 = G;
                    double GS_BLUE2 = GS_BLUE;
                    double GS_BLUE3 = B;
                    int R2 = (int) ((GS_RED3 * 0.299d) + (GS_GREEN3 * 0.587d) + (GS_BLUE3 * 0.114d));
                    resultBitmap.setPixel(x, y, Color.argb(A, R2, R2, R2));
                    y++;
                    GS_RED = GS_RED2;
                    src = src;
                    GS_GREEN = GS_GREEN2;
                    GS_BLUE = GS_BLUE2;
                }
            }
            FileOutputStream fos = new FileOutputStream(imgPath);
            Bitmap resultBitmap2 = Bitmap.createScaledBitmap(resultBitmap, resultBitmap.getWidth(), resultBitmap.getHeight(), true);
            resultBitmap2.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            resultBitmap2.recycle();
            return imgPath;
        } catch (Exception e) {
            return null;
        }
    }

    private Bitmap setGrayscale(Bitmap img) {
        Bitmap bmap = img.copy(img.getConfig(), true);
        for (int i = 0; i < bmap.getWidth(); i++) {
            for (int j = 0; j < bmap.getHeight(); j++) {
                int c = bmap.getPixel(i, j);
                byte gray = (byte) ((Color.red(c) * 0.299d) + (Color.green(c) * 0.587d) + (Color.blue(c) * 0.114d));
                bmap.setPixel(i, j, Color.argb(255, (int) gray, (int) gray, (int) gray));
            }
        }
        return bmap;
    }

    private Bitmap removeNoise(Bitmap bmap) {
        for (int x = 0; x < bmap.getWidth(); x++) {
            for (int y = 0; y < bmap.getHeight(); y++) {
                int pixel = bmap.getPixel(x, y);
                if (Color.red(pixel) < 162 && Color.green(pixel) < 162 && Color.blue(pixel) < 162) {
                    bmap.setPixel(x, y, -16777216);
                }
            }
        }
        for (int x2 = 0; x2 < bmap.getWidth(); x2++) {
            for (int y2 = 0; y2 < bmap.getHeight(); y2++) {
                int pixel2 = bmap.getPixel(x2, y2);
                if (Color.red(pixel2) > 162 && Color.green(pixel2) > 162 && Color.blue(pixel2) > 162) {
                    bmap.setPixel(x2, y2, -1);
                }
            }
        }
        return bmap;
    }

    private void deleteDirectoryOrFile(boolean deleteRootDirectory, String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                File[] childFileList = file.listFiles();
                for (File childFile : childFileList) {
                    if (childFile.isDirectory()) {
                        deleteDirectoryOrFile(deleteRootDirectory, childFile.getAbsolutePath());
                    } else {
                        childFile.delete();
                    }
                }
                if (deleteRootDirectory) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            Log.e("ronnie", "DirectoryHelper DeleteDirectoryOrFile error " + e.toString());
        }
    }
}