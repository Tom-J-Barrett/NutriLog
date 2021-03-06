package com.example.tom13.fooddemo.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by tom13 on 16/03/2018.
 * Converts image to base64 String.
 */

public class Base64Image {
    private File image;
    private String base64Image;

    public Base64Image(File image){
        this.image = image;
        base64Image = toBase64();
    }

    public String toBase64() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;

        Bitmap imageBitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), options);
        Bitmap.createScaledBitmap(imageBitmap, 300, 400, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public String getBase64Image(){
        return base64Image;
    }
}
