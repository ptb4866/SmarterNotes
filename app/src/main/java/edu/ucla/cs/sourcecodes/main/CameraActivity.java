package edu.ucla.cs.sourcecodes.main;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.support.annotation.BoolRes;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadFactory;

import edu.ucla.cs.sourcecodes.MicrophoneActivity;
import edu.ucla.cs.sourcecodes.notes.NoteActivity;
import edu.ucla.cs.sourcecodes.R;
;


public class CameraActivity extends  Activity{

    private String TAG = "CameraActivity.java";
    // You should have the trained data file in assets folder
    // You can get them at:
    // http://code.google.com/p/tesseract-ocr/downloads/list
    public static final String lang = "eng";
    public static CameraPreview mPreview;
    Camera mCamera;
    private ImageView microImage;
    private ImageView camImage;
    private ImageView noteImage;
    public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString();
    private ImageView mImageView;
    final int PIC_CROP = 1;
    private Boolean imageCropped = false;
    String _path = null;
    TextView notify;
    Bitmap selectedBitmap;

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        microImage = (ImageView)findViewById(R.id.microphone_id);
        camImage = (ImageView)findViewById(R.id.camera_id);
        noteImage = (ImageView)findViewById(R.id.notes_id);
        mImageView = (ImageView) findViewById(R.id.picture_view);
        notify = (TextView)findViewById(R.id.notification);


        createPath();

        // Add a listener to the Capture button
        microImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), MicrophoneActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setAction(Intent.ACTION_VIEW);
                startActivity(i);




            }
        });

        // Add a listener to the Capture button
        camImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mCamera.takePicture(null, null, mPicture);



            }
        });

        noteImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), NoteActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setAction(Intent.ACTION_VIEW);
                startActivity(i);



            }
        });




    }




    public void createPath() {
        String[] paths = new String[] { DATA_PATH, DATA_PATH + "/tessdata/" };
        for (String path : paths) {
            Log.d(TAG, "paths: " + path);
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                    return;
                } else {
                    Log.v(TAG, "Created directory " + path + " on sdcard");
                }
            }
        }
        // lang.traineddata file with the app (in assets folder)
        // You can get them at:
        // http://code.google.com/p/tesseract-ocr/downloads/list
        // This area needs work and optimization
        if (!(new File(DATA_PATH + "/tessdata/" + lang + ".traineddata")).exists()) {
            try {

                //check on this  - peter bankole
                AssetManager assetManager = getAssets();
                InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
                //GZIPInputStream gin = new GZIPInputStream(in);
                OutputStream out = new FileOutputStream(DATA_PATH
                        + "/tessdata/" + lang + ".traineddata");
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                //while ((lenf = gin.read(buff)) > 0) {
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                //gin.close();
                out.close();

                Log.v(TAG, "Copied " + lang + " traineddata");
            } catch (IOException e) {
                Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
            }
        }


    }



    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            //Remove Current Display
            microImage.setVisibility(View.GONE);
            camImage.setVisibility(View.GONE);
            noteImage.setVisibility(View.GONE);


            File pictureFile = getOutputMediaFile();
            //Force update the directory
            makeFileDiscoverable(pictureFile,getApplicationContext());

            if (pictureFile == null) {
                return;
            }


            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {

            } catch (IOException e) {
            }
            _path = pictureFile.getPath();

             Uri uri = Uri.fromFile(pictureFile);


            Bitmap bitmap = setPictureRotation(_path);
            ////Save to file again???
           // try
            //{
            //    FileOutputStream fos;
            //    fos = new FileOutputStream(_path);
            //    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            //}catch (FileNotFoundException e)
           // {
            //    e.getMessage();
           // }
            mImageView.setVisibility(View.VISIBLE);
            ((ImageView) mImageView.findViewById(R.id.picture_view)).setImageBitmap(bitmap);
            findViewById(R.id.camera_frag).setVisibility(View.INVISIBLE);
            performCrop(uri, bitmap);

        }
    };







    //HERE

    private Bitmap setPictureRotation(String path_to_picture) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;


        Bitmap bitmap = BitmapFactory.decodeFile(path_to_picture, options);


        ExifInterface ei = null;
        try {
            ei = new ExifInterface(path_to_picture);

            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return bitmap;

    }


    private void performCrop(Uri picUri, Bitmap bitmap) {

        try {

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();

            final Bitmap finalSelectedBitmap = bitmap;
            CameraActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String recognizedText = tess(finalSelectedBitmap);
                    displayWordsInNoteActivity(recognizedText);
                }
            });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == PIC_CROP) {
            if (data != null) {
                // get the returned data
                Bundle extras = data.getExtras();

                if (extras != null) {
                    // get the cropped bitmap
                    selectedBitmap = extras.getParcelable("data");

                    if (_path != null) {
                        selectedBitmap = setPictureRotation(_path);
                        mImageView.setImageBitmap(selectedBitmap);
                    }

                    AlertDialog dialog = new AlertDialog.Builder(CameraActivity.this)
                            .setMessage("Continue Extracting Text? ")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                    if (selectedBitmap != null) {

                                        final Bitmap finalSelectedBitmap = selectedBitmap;
                                        CameraActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                String recognizedText = tess(finalSelectedBitmap);
                                                displayWordsInNoteActivity(recognizedText);

                                            }
                                        });
                                    }

                                    dialog.dismiss();


                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                    dialog.dismiss();
                                }
                            })
                            .create();
                    dialog.show();

                }


            }
        }

    }

    void displayWordsInNoteActivity(String words) {


        final EditText edittext = new EditText(getApplicationContext());
        edittext.setTextColor(Color.BLACK);

        final String finalStr = words;
        AlertDialog dialog = new AlertDialog.Builder(CameraActivity.this)
                .setTitle("Add a new session ")
                .setMessage("Enter A Name ")
                .setView(edittext)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String textValue = edittext.getText().toString();


                        Intent i = new Intent( getApplicationContext(), NoteActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.setAction(Intent.ACTION_VIEW);
                        i.putExtra("addToNote", finalStr);
                        i.putExtra("sessionTitle",textValue);
                        Log.d(TAG, "starting Other Activity");
                        startActivity(i);


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        resetCameraView();

                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();



    }



    // Function used to extract text
    //returns the string of text
    private String tess(Bitmap bitmap) {

        Bitmap mBitmap;
        String recognizedText;

        // Convert to ARGB_8888, required by tess
        mBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        Log.v(TAG, "Before baseApi init");
        baseApi.init(DATA_PATH, lang);

        Log.v(TAG, "after  init");
        baseApi.setImage(mBitmap);
        Log.v(TAG, "setImage");
        recognizedText = baseApi.getUTF8Text();

        baseApi.end();
        // You now have the text in recognizedText var, you can do anything with it.
        // We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
        // so that garbage doesn't make it to the display.

        Log.v(TAG, "OCRED TEXT: " + recognizedText);

        if ( lang.equalsIgnoreCase("eng") ) {
            recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
        }

        recognizedText = recognizedText.trim();

        return recognizedText;

    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        //matrix.postRotate(angle);
        matrix.preRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, false);

        return retVal;
    }

    private static File getOutputMediaFile() {

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),"tessdata");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;


    }
    public void makeFileDiscoverable(File file, Context context){
        MediaScannerConnection.scanFile(context, new String[]{file.getPath()}, null, null);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(file)));
    }


    private void createCamera() {
        // Create an instance of Camera
        mCamera = getCameraInstance();
        // Setting the right parameters in the camera
        Camera.Parameters params = mCamera.getParameters();
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_frag);
        preview.addView(mPreview);
    }


    @SuppressWarnings("deprecation")
    private Point getDisplayWH() {

        Display display = this.getWindowManager().getDefaultDisplay();
        Point displayWH = new Point();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(displayWH);
            return displayWH;
        }
        displayWH.set(display.getWidth(), display.getHeight());
        return displayWH;
    }

    private Point calcCamPrevDimensions(Point disDim, Camera.Size camDim) {

        Point displayDim = disDim;
        Camera.Size cameraDim = camDim;

        double widthRatio = (double) displayDim.x / cameraDim.width;
        double heightRatio = (double) displayDim.y / cameraDim.height;

        // use ">" to zoom preview full screen
        if (widthRatio < heightRatio) {
            Point calcDimensions = new Point();
            calcDimensions.x = displayDim.x;
            calcDimensions.y = (displayDim.x * cameraDim.height) / cameraDim.width;
            return calcDimensions;
        }
        // use "<" to zoom preview full screen
        if (widthRatio > heightRatio) {
            Point calcDimensions = new Point();
            calcDimensions.x = (displayDim.y * cameraDim.width) / cameraDim.height;
            calcDimensions.y = displayDim.y;
            return calcDimensions;
        }
        return null;
    }
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            // attempt to get a Camera instance
            c = Camera.open();
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        // returns null if camera is unavailable
        return c;
    }

    private void resetCameraView() {
        //Remove Current Display
        microImage.setVisibility(View.VISIBLE);
        camImage.setVisibility(View.VISIBLE);
        noteImage.setVisibility(View.VISIBLE);
        findViewById(R.id.camera_frag).setVisibility(View.VISIBLE);

        notify.setVisibility(View.INVISIBLE);
        mImageView.setVisibility(View.INVISIBLE);


    }


    @Override
    public void onResume() {
        super.onResume();

        if (!checkCameraHardware(this)) {
            Intent i = new Intent(this, NoCamera.class);
            startActivity(i);
            finish();
        }

        resetCameraView();

        createCamera();



    }
    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();



        Log.d(TAG, "onPause");
        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.
        if (mCamera != null) {


            mCamera.release();
            mCamera = null;
        }

        // removing the inserted view - so when we come back to the app we
        // won't have the views on top of each other.
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_frag);
        preview.removeViewAt(0);

    }






}