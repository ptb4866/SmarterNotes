package edu.ucla.cs.sourcecodes;


import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

//import com.androidbelieve.sourcecodes.R;

import java.io.IOException;
import java.util.List;

// ----------------------------------------------------------------------

/**
 * A simple wrapper around a Camera and a SurfaceView that renders a centered
 * preview of the Camera to the surface. We need to center the SurfaceView
 * because not all devices have cameras that support preview sizes at the same
 * aspect ratio as the device's display.
 */
public class Preview extends ViewGroup implements SurfaceHolder.Callback {

     private final String TAG = "Preview";
    int deviceWidth;
    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Camera.Size mPreviewSize;
    List<Camera.Size> mSupportedPreviewSizes;
    Camera mCamera;
    boolean mSurfaceCreated = false;
    boolean isPreviewing = false;
    public static boolean safeToTakePicture = false;


    Context context;
    private int mLayouts ;

    float mDist;

    Camera.Parameters parameters;


    Preview(Context context) {
        super(context);

        //RelativeLayout rl = (RelativeLayout)findViewById(R.id.surface_camera);


        mSurfaceView = new SurfaceView(context);

         addView(mSurfaceView);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
        if (mCamera != null) {
            mSupportedPreviewSizes = mCamera.getParameters()
                    .getSupportedPreviewSizes();
            if (mSurfaceCreated) requestLayout();
        }
    }

    public void switchCamera(Camera camera) {
        setCamera(camera);
        try {
            camera.setPreviewDisplay(mHolder);
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
    }

/*
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() > 0) {
            final View child = getChildAt(0);

            final int width = r - l;
            final int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
            }

            // Center the child SurfaceView within the parent.
            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height
                        / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = previewHeight * width
                        / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2, width,
                        (height + scaledChildHeight) / 2);
            }
        }
    }
*/

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        int curWidth, curHeight, curLeft, curTop, maxHeight;

        //get the available size of child view
        final int childLeft = this.getPaddingLeft();
        final int childTop = this.getPaddingTop();
        final int childRight = this.getMeasuredWidth() - this.getPaddingRight();
        final int childBottom = this.getMeasuredHeight() - this.getPaddingBottom();
        final int childWidth = childRight - childLeft;
        final int childHeight = childBottom - childTop;

        maxHeight = 0;
        curLeft = childLeft;
        curTop = childTop;


        View child = getChildAt(0);

        if (child.getVisibility() == GONE)
            return;

        //Get the maximum size of the child
        child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST));
        curWidth = child.getMeasuredWidth();
        curHeight = child.getMeasuredHeight();
        //wrap is reach to the end
        if (curLeft + curWidth >= childRight) {
            curLeft = childLeft;
            curTop += maxHeight;
            maxHeight = 0;
        }
        //do the layout
        child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight);
        //store the max height
        if (maxHeight < curHeight)
            maxHeight = curHeight;
        curLeft += curWidth;

    }




    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.
        final int width = resolveSize(getSuggestedMinimumWidth(),
                widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(),
                heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width,
                    height);
        }

        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);

            mCamera.setParameters(parameters);
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        try {


            Camera.Parameters camParam = mCamera.getParameters();
            Camera.Parameters params = mCamera.getParameters();
            String currentversion = android.os.Build.VERSION.SDK;
            Log.d("System out", "currentVersion " + currentversion);
            int currentInt = android.os.Build.VERSION.SDK_INT;
            Log.d("System out", "currentVersion " + currentInt);

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (currentInt != 7) {
                    mCamera.setDisplayOrientation(90);
                } else {
                    Log.d("System out", "Portrait " + currentInt);

                  //  params.setRotation(90);

                    mCamera.setParameters(params);
                }
            }
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (currentInt != 7) {
                    mCamera.setDisplayOrientation(0);
                } else {
                    Log.d("System out", "Landscape " + currentInt);
                    params.set("orientation", "landscape");
                    params.set("rotation", 90);
                    mCamera.setParameters(params);
                }
            }










            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
        if (mPreviewSize == null) requestLayout();
        mSurfaceCreated = true;
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
            mCamera.stopPreview();
            safeToTakePicture = false;
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }




    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        Log.d(TAG,"surfaceChanged");
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        parameters = mCamera.getParameters();
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);

        parameters.set("orientation", "portrait");
        requestLayout();

        mCamera.setParameters(parameters);
        mCamera.startPreview();
        safeToTakePicture = true;
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Get the pointer ID
        Camera.Parameters params = mCamera.getParameters();
        int action = event.getAction();



        if (event.getPointerCount() > 1) {
            // handle multi-touch events
            if (action == MotionEvent.ACTION_POINTER_DOWN) {
                mDist = getFingerSpacing(event);
            } else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported()) {
                mCamera.cancelAutoFocus();
                handleZoom(event, params);
            }
        } else {
            // handle single touch events
            if (action == MotionEvent.ACTION_UP) {
                handleFocus(event, params);
            }
        }
        return true;
    }

    private void handleZoom(MotionEvent event, Camera.Parameters params) {
        int maxZoom = params.getMaxZoom();
        int zoom = params.getZoom();
        float newDist = getFingerSpacing(event);
        if (newDist > mDist) {
            //zoom in
            if (zoom < maxZoom)
                zoom++;
        } else if (newDist < mDist) {
            //zoom out
            if (zoom > 0)
                zoom--;
        }
        mDist = newDist;
        params.setZoom(zoom);
        mCamera.setParameters(params);
    }

    public void handleFocus(MotionEvent event, Camera.Parameters params) {
        int pointerId = event.getPointerId(0);
        int pointerIndex = event.findPointerIndex(pointerId);
        // Get the pointer's current position
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);

        List<String> supportedFocusModes = params.getSupportedFocusModes();
        if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, Camera camera) {
                    // currently set to auto-focus on single touch
                }
            });
        }
    }

    /** Determine the space between the first two fingers */
    private float getFingerSpacing(MotionEvent event) {
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);


        //return FloatMath.sqrt(x * x + y * y);
        return (float) Math.sqrt(x * x + y * y);



    }

}
