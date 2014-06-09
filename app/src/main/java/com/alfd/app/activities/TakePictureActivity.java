package com.alfd.app.activities;

import com.alfd.app.SC;
import com.alfd.app.activities.util.SystemUiHider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alfd.app.R;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class TakePictureActivity extends Activity implements SurfaceHolder.Callback {
    // a variable to store a reference to the Image View at the main.xml file
// private ImageView iv_image;
// a variable to store a reference to the Surface View at the main.xml file
    private SurfaceView surfaceView;

    // a bitmap to display the captured image
    private Bitmap bmp;
    FileOutputStream fo;

    // Camera variables
// a surface holder
    private SurfaceHolder sHolder;
    private Button takePictureButton;
    private LinearLayout actionPanelLayout;
    // a variable to control the camera
    private Camera camera;
    private Camera.PictureCallback pictureCallback;
    private String imageFullName;
    private Camera.Size imageSize;

    /**
     * Called when the activity is first created.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);

        // check if this device has a camera
        if (checkCameraHardware(getApplicationContext())) {

            Bundle extras = getIntent().getExtras();
            imageFullName = extras.getString(SC.IMAGE_FULL_NAME);


            surfaceView = (SurfaceView) findViewById(R.id.camera_preview);
            actionPanelLayout = (LinearLayout)findViewById(R.id.action_panel_layout);



            // Get a surface
            sHolder = surfaceView.getHolder();

            // add the callback interface methods defined below as the Surface
            // View
            // callbacks
            sHolder.addCallback(this);
            takePictureButton = (Button)findViewById(R.id.take_picture);

            takePictureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    camera.takePicture(null, null, pictureCallback);
                }
            });

            // tells Android that this surface will have its data constantly
            // replaced
            sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        } else {
            // display in long period of time
            Toast.makeText(getApplicationContext(),
                    "Your Device dosen't have a Camera !", Toast.LENGTH_LONG)
                    .show();
        }

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        FrameLayout.LayoutParams params  = (FrameLayout.LayoutParams)actionPanelLayout.getLayoutParams();
        int gravity = params.gravity;
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        }
        params.gravity = gravity;
        actionPanelLayout.setLayoutParams(params);


    }

    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            e.printStackTrace();
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        setCameraDisplayOrientation(0, camera);
        setCameraParams();
        camera.startPreview();
        // sets what code should be executed after the picture is taken
        pictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                camera.stopPreview();
                saveBitmap(data);
                camera.release();
                setResultAndFinish();
            }
        };


    }

    private void setResultAndFinish() {
        Intent i = getIntent();
        i.putExtra(SC.IMAGE_FULL_NAME, imageFullName);
        setResult(RESULT_OK, i);
        finish();
    }

    private void setCameraParams() {

        Camera.Parameters params =  camera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        imageSize = null;
        int minSize = 1600;
        for (Camera.Size size : params.getSupportedPictureSizes()) {
            if (imageSize ==null) {
                imageSize =size;
            }
            if (size.width>=minSize && size.height>=minSize) {
               imageSize = size;
            }
        }
        params.setPictureSize(imageSize.width, imageSize.height);
        camera.setParameters(params);
    }

    public void setCameraDisplayOrientation(int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private void saveBitmap(byte[] data) {



        int size  = Math.min(imageSize.width, imageSize.height);
        int x = 0;
        int y = 0;
        Bitmap croppedBmp = null;
        try {
            BitmapRegionDecoder bitmapDecoder = BitmapRegionDecoder.newInstance(data, 0, data.length, true);
            croppedBmp = bitmapDecoder.decodeRegion(new Rect(0, 0, size, size), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(imageFullName);
            croppedBmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                out.close();
            } catch(Throwable ignore) {}
        }


        Toast.makeText(getApplicationContext(),
                "Your Picture has been taken !", Toast.LENGTH_LONG)
                .show();
    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw the preview.

        camera = getCameraInstance();
        try {
            camera.setPreviewDisplay(holder);

        } catch (IOException exception) {
            destroyCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        destroyCamera();
    }

    private void destroyCamera() {
        if (camera == null) {
            return;
        }
        camera.release();
        camera = null;
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}

