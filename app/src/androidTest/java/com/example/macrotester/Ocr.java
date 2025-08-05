package com.example.macrotester;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import androidx.test.uiautomator.UiDevice;

import android.util.Log;
import com.googlecode.tesseract.android.ResultIterator;
import com.googlecode.tesseract.android.TessBaseAPI;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import UtilGroup.BuildConfig;
import UtilGroup.EventHistory;

/* loaded from: classes.dex */
public class Ocr {
    private Context mContext;
    private String mDataPath;
    private UiDevice mDevice;
    private EventHistory mEventHistory;
    private TessBaseAPI mTess;

    public Ocr(UiDevice device, Context context, EventHistory eventHistory) {
        this.mDevice = null;
        this.mDataPath = BuildConfig.FLAVOR;
        try {
            this.mDevice = device;
            this.mContext = context;
            this.mEventHistory = eventHistory;

            this.mDataPath = this.mContext.getFilesDir() + "/tesseract/";
            this.mDataPath = this.mDataPath.replace(BuildConfig.APPLICATION_ID, "com.example.macrotester");
            checkFile(new File(this.mDataPath + "tessdata/"), "eng");
            checkFile(new File(this.mDataPath + "tessdata/"), "kor");
            this.mTess = new TessBaseAPI();
            // 기본 영어
            this.mTess.init(this.mDataPath, "eng");
            this.mTess.setVariable("tessedit_char_whitelist", "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ1234567890',.?&;/-> ");
            this.mTess.setVariable("tessedit_char_blacklist", "|");
        } catch (Exception e) {
            Log.e("jkseo", "OCR Error : " + e.toString());
        }
    }

    public void TesssReInit(String lang){
        this.mTess.init(this.mDataPath, lang);
    }
    public int[] getSearchKeyword(String imgPath, String keyword)  throws Exception{
        Throwable th;
        Bitmap img = null;
        String ocrText = null;
        try {
            try {
                img = BitmapFactory.decodeFile(imgPath);
                int orgW = img.getWidth();
                int orgH = img.getHeight();
                this.mTess.setImage(img);
                Log.e("jkseo", "ORG W:H : " + orgW + ":" + orgH + "    imgRect W:H : " + this.mTess.getThresholdedImage().getWidth() + ":" + this.mTess.getThresholdedImage().getHeight());
                int tmpX = (orgW == this.mTess.getThresholdedImage().getWidth() || orgH == this.mTess.getThresholdedImage().getHeight()) ? 1 : this.mTess.getThresholdedImage().getWidth() / 1;
                ocrText = this.mTess.getUTF8Text();
                boolean searchResult = false;

                String replaceKeyword1 = keyword;
                String replaceKeyword2 = keyword;
                String replaceKeyword3 = keyword;

                // TODO replaceKeyword 를 포함하는지 확인해본다.

//                replaceKeyword = replaceKeyword.replace(".", "");
//                replaceKeyword = replaceKeyword.replace(":", "z");

                replaceKeyword1 =replaceKeyword1.replace("l", "I");
                replaceKeyword2 =replaceKeyword2.replace("I", "l");
                replaceKeyword3 =replaceKeyword3.replace("l", "|");


                try {
                    ResultIterator iterator = this.mTess.getResultIterator();
                    iterator.begin();
                    do {
                        String text = iterator.getUTF8Text(3);

                        Rect rect = iterator.getBoundingRect(3);

                        if(text.contains(keyword) || text.contains(replaceKeyword1) ||
                                text.contains(replaceKeyword2) || text.contains(replaceKeyword3))
                        {
                            searchResult = true;
                        }
                        else
                        {
                            searchResult = false;
                        }

                        if(!searchResult)
                            continue;

                        int left = rect.left / tmpX;
                        int top = rect.top / tmpX;
                        int right = rect.right / tmpX;
                        int bottom = rect.bottom / tmpX;
                        int centerX = (right + left) / 2;
                        try {
                            try {
                                int centerY = (bottom + top) / 2;
                                Log.e("jkseo", "words : " + text + "    BoundingRect " + rect.left + ":" + rect.top + ":" + rect.right + ":" + rect.bottom + "     RealPosition " + left + ":" + top + ":" + right + ":" + bottom + "   Center : " + centerX + ":" + centerY);
                                try {
                                    this.mTess.clear();
                                } catch (Exception e) {
                                }
                                try {
                                    iterator.delete();
                                } catch (Exception e2) {
                                }
                                int[] iArr = {centerX, centerY};
                                try {
                                    img.recycle();
                                } catch (Exception e3) {
                                }
                                return iArr;
                            } catch (Throwable th2) {
                                th = th2;
                                try {
                                    img.recycle();
                                } catch (Exception e4) {
                                }
                                throw th;
                            }
                        } catch (Exception e5) {
                            ocrText = ocrText;
                            img.recycle();
                            this.mEventHistory.writeEventHistory("Ocr getSearchKeyword : " + ocrText);
                            return new int[]{-1, -1};
                        }
                    } while (iterator.next(3));

//                    img.recycle();
                } catch (Exception e6) {
                } catch (Throwable th3) {
                    th = th3;
                }
            } catch (Exception e7) {
            } catch (Throwable th4) {
                th = th4;
            }
        } catch (Exception e8) {
        }
        this.mEventHistory.writeEventHistory("Ocr getSearchKeyword : " + ocrText);
        return new int[]{-1, -1};
    }

    private void copyFiles(String lang) {
        try {
            String filepath = this.mDataPath + "/tessdata/" + lang + ".traineddata";
            AssetManager assetManager = this.mContext.getAssets();
            InputStream inputStream = assetManager.open("tessdata/" + lang + ".traineddata");
            OutputStream outputStream = new FileOutputStream(filepath);
            byte[] buffer = new byte[1024];
            while (true) {
                int read = inputStream.read(buffer);
                if (read != -1) {
                    outputStream.write(buffer, 0, read);
                } else {
                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkFile(File dir, String lang) {
        if (!dir.exists() && dir.mkdirs()) {
            copyFiles(lang);
        }
        if (dir.exists()) {
            String datafilePath = this.mDataPath + "/tessdata/" + lang + ".traineddata";
            File datafile = new File(datafilePath);
            if (!datafile.exists()) {
                copyFiles(lang);
            }
        }
    }
}