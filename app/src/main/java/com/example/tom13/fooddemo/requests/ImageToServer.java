package com.example.tom13.fooddemo.requests;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.tom13.fooddemo.backgroundProcesses.UploadImage;
import com.example.tom13.fooddemo.host.Host;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by tom13 on 08/03/2018.
 */

public class ImageToServer {

    private File image;
    private String base64Image;

    public ImageToServer(File image) {
        this.image = image;
        base64Image = toBase64();
    }

    public void sendImage() {
        UploadImage uploadImage = (UploadImage) new UploadImage(new Host.HostBuilder("").build(), base64Image).execute();
    }

    public String toBase64() {
        Bitmap imageBitmap = BitmapFactory.decodeFile(image.getAbsolutePath());

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

}