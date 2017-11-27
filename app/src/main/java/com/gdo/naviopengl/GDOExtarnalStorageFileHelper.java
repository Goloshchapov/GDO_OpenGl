package com.gdo.naviopengl;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;


public class GDOExtarnalStorageFileHelper {
    public final static String APP_PATH = "/AppMinistryTest/";
    public final static String APP_GRAVATARS = "gravatars";
    public final static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH+APP_GRAVATARS;

    public static void saveBitmapToStorage(Bitmap bitmap, String filename) throws IOException {
       // String path = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH+APP_GRAVATARS;

        try {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            OutputStream outputStream = null;
            File file = new File(path, filename);
            file.createNewFile();
            outputStream = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (Exception e){
            Log.e("saveToExternalStorage()", e.getMessage());

        }


    }


    public static Bitmap loadBitmapFromExternalStorage(String filename){
        return BitmapFactory.decodeFile(path+"/"+filename);
    }

    public static boolean deleteFileFromExternalStorage(String filename){
        return new File(path, filename).delete();
    }

    public static String readTextFile(AssetManager assetManager, String fileName){
        StringBuilder sb = new StringBuilder();
        String readedString="";

        try {

            InputStream is =  assetManager.open(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            while ((readedString = bufferedReader.readLine())!=null) {
                sb.append(readedString);
            }
        bufferedReader.close();

        } catch (IOException e){
            Log.e("readTextFile exception", e.toString());
        }

        return sb.toString();
    }

}
