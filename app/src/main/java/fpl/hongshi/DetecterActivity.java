package fpl.hongshi;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKMatching;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.arcsoft.facetracking.AFT_FSDKEngine;
import com.arcsoft.facetracking.AFT_FSDKError;
import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.facetracking.AFT_FSDKVersion;
import com.guo.android_extend.java.AbsLoop;
import com.guo.android_extend.java.ExtByteArrayOutputStream;
import com.guo.android_extend.tools.CameraHelper;
import com.guo.android_extend.widget.CameraFrameData;
import com.guo.android_extend.widget.CameraGLSurfaceView;
import com.guo.android_extend.widget.CameraSurfaceView;
import com.guo.android_extend.widget.CameraSurfaceView.OnCameraListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 */

public class DetecterActivity extends Activity implements OnCameraListener, View.OnTouchListener, Camera.AutoFocusCallback {
    private final String TAG = this.getClass().getSimpleName();

    private int mWidth, mHeight, mFormat;
    private CameraSurfaceView mSurfaceView;
    private CameraGLSurfaceView mGLSurfaceView;
    private Camera mCamera;

    AFT_FSDKVersion version = new AFT_FSDKVersion();
    AFT_FSDKEngine engine = new AFT_FSDKEngine();
    List<AFT_FSDKFace> result = new ArrayList<>();

    byte[] mImageNV21 = null;
    FRAbsLoop mFRAbsLoop = null;
    AFT_FSDKFace mAFT_FSDKFace = null;
    Handler mHandler;

    Runnable hide = new Runnable() {
        @Override
        public void run() {
            mTextView.setAlpha(0.5f);
            mImageView.setImageAlpha(128);
        }
    };
    private Button btnChage;
    private int flag = 0;
    private int flag2 = 0;
    private MyDBHelper helper;
    private SQLiteDatabase db;
    private List<FaceRegist> mResgist;
    private long aTime;

    class FRAbsLoop extends AbsLoop {

        AFR_FSDKVersion version = new AFR_FSDKVersion();
        AFR_FSDKEngine engine = new AFR_FSDKEngine();
        AFR_FSDKFace result = new AFR_FSDKFace();
//        List<FaceDB.FaceRegist> mResgist = ((Application) DetecterActivity.this.getApplicationContext()).mFaceDB.mRegister;


        @Override
        public void setup() {
            AFR_FSDKError error = engine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
            Log.d(TAG, "AFR_FSDK_InitialEngine = " + error.getCode());
            error = engine.AFR_FSDK_GetVersion(version);
            Log.d(TAG, "FR=" + version.toString() + "," + error.getCode()); //(210, 178 - 478, 446), degree = 1　780, 2208 - 1942, 3370
        }

        @Override
        public void loop() {
            if (mImageNV21 != null) {
                aTime=System.currentTimeMillis();
                Log.i(TAG, "loop: " + mImageNV21);
                AFR_FSDKError error = engine.AFR_FSDK_ExtractFRFeature(mImageNV21, mWidth, mHeight, AFR_FSDKEngine.CP_PAF_NV21, mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree(), result);
                Log.d(TAG, "Face=" + result.getFeatureData()[0] + "," + result.getFeatureData()[1] + "," + result.getFeatureData()[2] + "," + error.getCode());
//                AFR_FSDKMatching score = new AFR_FSDKMatching();
                float max = 0.0f;
                String name = null;
                Log.e(TAG, "loop: " + result.getFeatureData() + "->" + mResgist.size() + mResgist.get(0).getmName() + "->" + mResgist.size() + mResgist.get(0).getmFaceList());
//                for (FaceDB.FaceRegist fr : mResgist) {
//                    Log.i(TAG, "name: " + fr.mName + "->" + fr.mFaceList);
//                    for (AFR_FSDKFace face : fr.mFaceList) {
//                        Log.e(TAG, "face: " + face.getFeatureData()[0] + "->" + face.getFeatureData()[1] + "->" + face.getFeatureData()[2]);
//                        error = engine.AFR_FSDK_FacePairMatching(result, face, score);
//                        Log.d(TAG, "Score:" + score.getScore() + ", AFR_FSDK_FacePairMatching=" + error.getCode());
//                        if (max < score.getScore()) {
//                            max = score.getScore();
//                            name = fr.mName;
//                        }
//                    }
//                }
                for (int i=0;i<mResgist.size();i++){
                    for(int j=0;j<mResgist.get(i).getmFaceList().size();j++){
                        AFR_FSDKMatching score = new AFR_FSDKMatching();
                        engine.AFR_FSDK_FacePairMatching(result, mResgist.get(i).getmFaceList().get(j), score);
                        if (max < score.getScore()) {
                            max = score.getScore();
                            name = mResgist.get(j).getmName();
                            Log.i(TAG, "-----------: " + max + "->" + name+"i="+i+";j="+j);
                        }
                    }
                }
//                for (FaceRegist fr : mResgist) {
//                    Log.i(TAG, "name: " + fr.getmName() + "->" + fr.getmFaceList());
//                    for (AFR_FSDKFace face : fr.getmFaceList()) {
//                        Log.e(TAG, "face: " + face.getFeatureData()[0] + "->" + face.getFeatureData()[1] + "->" + face.getFeatureData()[2]);
//                        engine.AFR_FSDK_FacePairMatching(result, face, score);
//                        Log.d(TAG, "Score:" + score.getScore() + "name=" + fr.getmName());
//                        if (max < score.getScore()) {
//                            max = score.getScore();
//                            name = fr.getmName();
//                            Log.i(TAG, "-----------: " + max + "->" + name);
//                        }
//                    }
//                }

                //crop
                byte[] data = mImageNV21;
                YuvImage yuv = new YuvImage(data, ImageFormat.NV21, mWidth, mHeight, null);
                ExtByteArrayOutputStream ops = new ExtByteArrayOutputStream();
                yuv.compressToJpeg(mAFT_FSDKFace.getRect(), 80, ops);
                final Bitmap bmp = BitmapFactory.decodeByteArray(ops.getByteArray(), 0, ops.getByteArray().length);
                try {
                    ops.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.e("max", "loop: " + max);

                if (max > 0.6f) {
                    //fr success.
                    flag++;
                    final float max_score = max;
                    Log.d(TAG, "fit Score:" + max + ", NAME:" + name);
                    final String mNameShow = name;
                    mHandler.removeCallbacks(hide);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "验证通过: ----------------------" + flag);
//                            if (flag == 1) {
                                long hasTime = System.currentTimeMillis() - aTime;
                                mTextView.setAlpha(1.0f);
                                mTextView.setText(mNameShow);
                                mTextView.setTextColor(Color.RED);
                                mTextView1.setVisibility(View.VISIBLE);
                                mTextView1.setText("置信度：" + (float) ((int) (max_score * 1000)) / 1000.0);
                                mTextView1.setTextColor(Color.RED);
                                mImageView.setRotation(90);
                                mImageView.setImageAlpha(255);
                                mImageView.setImageBitmap(bmp);
                                Log.i(TAG, "run1111: 验证通过---"+"hasTime:"+hasTime+"ms");
                                SoundPlayUtils.play(1);
                                flag = -3;
//                            }
                        }
                    });
                } else {
                    flag2++;
                    final String mNameShow = "未识别";
                    DetecterActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "验证失败: ----------------------" + flag);
//                            if (flag2 == 3) {
                                mTextView.setAlpha(1.0f);
                                mTextView1.setVisibility(View.INVISIBLE);
                                mTextView.setText(mNameShow);
                                mTextView.setTextColor(Color.RED);
                                mImageView.setImageAlpha(255);
                                mImageView.setRotation(90);
                                mImageView.setImageBitmap(bmp);
                                Log.i(TAG, "run2222: 验证失败--------------------------------");
                                SoundPlayUtils.play(2);
                                flag2 = -2;
//                            }
                        }
                    });
                }
                mImageNV21 = null;
            }

        }

        @Override
        public void over() {
            AFR_FSDKError error = engine.AFR_FSDK_UninitialEngine();
            Log.d(TAG, "AFR_FSDK_UninitialEngine : " + error.getCode());
        }
    }

    private TextView mTextView;
    private TextView mTextView1;
    private ImageView mImageView;

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_camera);
        mGLSurfaceView = (CameraGLSurfaceView) findViewById(R.id.glsurfaceView);
        mGLSurfaceView.setOnTouchListener(this);
        mSurfaceView = (CameraSurfaceView) findViewById(R.id.surfaceView);
        mSurfaceView.setOnCameraListener(this);
        mSurfaceView.setupGLSurafceView(mGLSurfaceView, true, false, 90);
        mSurfaceView.debug_print_fps(true, false);

        //snap
        mTextView = (TextView) findViewById(R.id.textView);
        mTextView.setText("");
        mTextView1 = (TextView) findViewById(R.id.textView1);
        mTextView1.setText("");

        mImageView = (ImageView) findViewById(R.id.imageView);

        btnChage = (Button) findViewById(R.id.btn_chage);

        helper = new MyDBHelper(this);
        db = helper.getReadableDatabase();
        mResgist = helper.selectAll(db);
        Log.e(TAG, "onCreate: " + mResgist.size() + "-" + mResgist.get(0).getmName());

        mHandler = new Handler();
        mWidth = 1280;
        mHeight = 960;
        mFormat = ImageFormat.NV21;

        AFT_FSDKError err = engine.AFT_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.ft_key, AFT_FSDKEngine.AFT_OPF_0_HIGHER_EXT, 16, 5);
        Log.d(TAG, "AFT_FSDK_InitialFaceEngine =" + err.getCode());
        err = engine.AFT_FSDK_GetVersion(version);
        Log.d(TAG, "AFT_FSDK_GetVersion:" + version.toString() + "," + err.getCode());

        mFRAbsLoop = new FRAbsLoop();
        mFRAbsLoop.start();

        SoundPlayUtils.init(this);

//        btnChage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setupCamera();
//            }
//        });
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mFRAbsLoop.shutdown();
        AFT_FSDKError err = engine.AFT_FSDK_UninitialFaceEngine();
        Log.d(TAG, "AFT_FSDK_UninitialFaceEngine =" + err.getCode());
    }

    @Override
    public Camera setupCamera() {
        // TODO Auto-generated method stub
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        Log.i(TAG, "setupCamera: ----------------------->" + findFrontFacingCameraID());

        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(mWidth, mHeight);
            parameters.setPreviewFormat(mFormat);

            for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
                Log.d(TAG, "SIZE:" + size.width + "x" + size.height);
            }
            for (Integer format : parameters.getSupportedPreviewFormats()) {
                Log.d(TAG, "FORMAT:" + format);
            }

            List<int[]> fps = parameters.getSupportedPreviewFpsRange();
            for (int[] count : fps) {
                Log.d(TAG, "T:");
                for (int data : count) {
                    Log.d(TAG, "V=" + data);
                }
            }
//            parameters.setPreviewFpsRange(15000, 30000);
//            parameters.setExposureCompensation(parameters.getMaxExposureCompensation());
//            parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
//            parameters.setAntibanding(Camera.Parameters.ANTIBANDING_AUTO);
//            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//            parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
//            parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
//            parameters.set("orientation", "portrait");
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mCamera != null) {
            mWidth = mCamera.getParameters().getPreviewSize().width;
            mHeight = mCamera.getParameters().getPreviewSize().height;
        }
        return mCamera;
    }

    public void setCameraDisplayOrientation(Activity activity,
                                            int cameraId, android.hardware.Camera camera) {
        try {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, info);
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0:
                    degrees = 0;
                    break;
                case Surface.ROTATION_90:
                    degrees = 90;
                    break;
                case Surface.ROTATION_180:
                    degrees = 180;
                    break;
                case Surface.ROTATION_270:
                    degrees = 270;
                    break;
            }

            int result;
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;  // compensate the mirror
            } else {  // back-facing
                result = (info.orientation - degrees + 360) % 360;
            }
            camera.setDisplayOrientation(result);
        } catch (Exception e) {
        }

    }

    private int findFrontFacingCameraID() {
        int cameraId = -1;
        // Search for the back facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Log.d(TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }


    @Override
    public void setupChanged(int format, int width, int height) {

    }

    @Override
    public boolean startPreviewLater() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Object onPreview(byte[] data, int width, int height, int format, long timestamp) {
        AFT_FSDKError err = engine.AFT_FSDK_FaceFeatureDetect(data, width, height, AFT_FSDKEngine.CP_PAF_NV21, result);
        Log.d(TAG, "AFT_FSDK_FaceFeatureDetect =" + err.getCode());
        Log.d(TAG, "Face=" + result.size());
        for (AFT_FSDKFace face : result) {
            Log.d(TAG, "Face:" + face.toString());
        }
        if (mImageNV21 == null) {
            if (!result.isEmpty()) {
                mAFT_FSDKFace = result.get(0).clone();
                mImageNV21 = data.clone();
            } else {
                mHandler.postDelayed(hide, 3000);
            }
        }
        //copy rects
        Rect[] rects = new Rect[result.size()];
        for (int i = 0; i < result.size(); i++) {
            rects[i] = new Rect(result.get(i).getRect());
        }
        //clear result.
        result.clear();
        //return the rects for render.
        return rects;
    }

    @Override
    public void onBeforeRender(CameraFrameData data) {

    }

    @Override
    public void onAfterRender(CameraFrameData data) {
        mGLSurfaceView.getGLES2Render().draw_rect((Rect[]) data.getParams(), Color.GREEN, 2);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        CameraHelper.touchFocus(mCamera, event, v, this);
        return false;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if (success) {
            Log.d(TAG, "Camera Focus SUCCESS!");
        }
    }
}
