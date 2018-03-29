package com.example.tom13.fooddemo.presenters;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.example.tom13.fooddemo.R;
import com.example.tom13.fooddemo.backgroundProcesses.CalorieEstimation;
import com.example.tom13.fooddemo.backgroundProcesses.UploadImage;
import com.example.tom13.fooddemo.calorieEstimation.CalorieEstimationFactory;
import com.example.tom13.fooddemo.foodLog.FoodLogImpl;
import com.example.tom13.fooddemo.host.HostFactory;
import com.example.tom13.fooddemo.image.Base64Image;
import com.example.tom13.fooddemo.image.ExifUtil;
import com.example.tom13.fooddemo.storage.DAO;
import com.example.tom13.fooddemo.storage.DAOFactory;
import com.example.tom13.fooddemo.views.CaptureImageActivity;
import com.example.tom13.fooddemo.views.MainActivity;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;

/**
 * Created by tom13 on 06/03/2018.
 * Presenter class to control the CaptureImageActivity View.
 */

public class CaptureImagePresenter {

    private Activity activity;
    private static final int CONTENT_REQUEST=1337;
    private File output=null;
    private String top1_prediction;
    private String calories;
    private Map<String, Runnable> imageCapture;
    private static final int GALLERY = 1;

    public CaptureImagePresenter(Activity activity, String medium) {
        this.activity = activity;
        this.imageCapture = new HashMap<>();
        imageCapture.put("camera", () -> takePicture());
        imageCapture.put("gallery", () -> gallery());

        imageCapture.get(medium).run();
    }

    private void gallery() {
        Intent i =new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        output = new File(dir, "CameraContentDemo.jpg");
        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
        dir.delete();

        activity.startActivityForResult(Intent.createChooser(i, "Select Picture"), GALLERY);
    }

    private void takePicture() {
        Intent i =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        output = new File(dir, "CameraContentDemo.jpg");
        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
        dir.delete();
        activity.startActivityForResult(i, CONTENT_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String[] galleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (EasyPermissions.hasPermissions(activity, galleryPermissions)) { }
        else {
            EasyPermissions.requestPermissions(activity, "Access for storage",
                    101, galleryPermissions);
        }
        if (requestCode == CONTENT_REQUEST || requestCode == GALLERY && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(output.getAbsolutePath());
            Bitmap orientedBitmap = ExifUtil.rotateBitmap(output.getAbsolutePath(), bitmap);
            Bitmap.createScaledBitmap(orientedBitmap, 300, 400, false);
            ImageView imageView= activity.findViewById(R.id.imageView);
            imageView.setImageBitmap(orientedBitmap);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }

    public void cancel() {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }

    public void sendImage() {
        UploadImage uploadImage = new UploadImage(new HostFactory().createHost(), new Base64Image(output).getBase64Image(), this);
        uploadImage.execute();
    }

    public void responseFromSever(String response) {
        String[] results = response.split(",");
        top1_prediction = results[0];
        getCalories();
    }

    public void getCalories() {
        CalorieEstimation calorieEstimation = (CalorieEstimation) new CalorieEstimationFactory(top1_prediction, this).getCalorieEstimator();
        calorieEstimation.execute();
    }

    public void updateUI(String results) {
        calories = results;
        CaptureImageActivity captureImageActivity = (CaptureImageActivity) activity;
        captureImageActivity.onResponse(top1_prediction, calories);
    }

    public void writeToLogs(String food, String calories) {
        DAO dao = new DAOFactory().getDAO(activity);
        dao.addFoodLog(new FoodLogImpl(0, food, Double.parseDouble(calories), new Date()));
        output.delete();
        goToMainActivity();
    }

    public void goToMainActivity() {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }

    public Activity getActivity(){
        return activity;
    }
}
