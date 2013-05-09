package com.moziy.hollerback.activity;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.moziy.hollerback.R;
import com.moziy.hollerback.debug.LogUtil;
import com.moziy.hollerback.util.FileUtil;

public class HaveFunVideoActivity extends Activity implements
		SurfaceHolder.Callback {
	private SurfaceHolder surfaceHolder;
	private SurfaceView surfaceView;
	public MediaRecorder mrec = new MediaRecorder();

	File video;
	private Camera mCamera;
	boolean inPreview;

	boolean isPrepared;

	// All view crap

	TextView mTimer;
	Handler handler;

	int secondsPassed;

	View mTopView, mBottomView;

	ImageButton mRecordButton, mSendButton;

	private boolean isRecording = false;

	// All view crap

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		handler = new Handler();

		setContentView(R.layout.camera_surface);

		LogUtil.i("Video starting");

		try {
			mCamera = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
			LogUtil.e("Camera successfully opened");
		} catch (RuntimeException e) {
			LogUtil.e("Camera failed to open: " + e.getLocalizedMessage());
		}

		if (mCamera == null) {
			mCamera = Camera.open();
		}

		mCamera.getParameters().setRecordingHint(true);

		List<Size> tmpList = mCamera.getParameters().getSupportedPreviewSizes();

		for (Size size : tmpList) {
			LogUtil.i("Sizes ", size.width + " x " + size.height);
		}

		surfaceView = (SurfaceView) findViewById(R.id.surface_camera);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	}

	public void initializeView() {
		mTopView = findViewById(R.id.top_bar);
		mBottomView = findViewById(R.id.bottom_bar);
		mSendButton = (ImageButton) findViewById(R.id.send_button);

		// this 1.5 i guess assumes 640 x 480
		surfaceHolder.setFixedSize(getWindow().getWindowManager()
				.getDefaultDisplay().getWidth(), (int) (getWindow()
				.getWindowManager().getDefaultDisplay().getWidth() * 1.5));

		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mTopView
				.getLayoutParams();
		params.height = (getWindow().getWindowManager().getDefaultDisplay()
				.getHeight() - getWindow().getWindowManager()
				.getDefaultDisplay().getWidth()) / 2;
		mTopView.setLayoutParams(params);

		RelativeLayout.LayoutParams bottomParams = (RelativeLayout.LayoutParams) mBottomView
				.getLayoutParams();
		bottomParams.height = (getWindow().getWindowManager()
				.getDefaultDisplay().getHeight() - getWindow()
				.getWindowManager().getDefaultDisplay().getWidth()) / 2;
		mBottomView.setLayoutParams(bottomParams);

		mRecordButton = (ImageButton) findViewById(R.id.record_button);

		mTimer = (TextView) findViewById(R.id.timer);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			try {
				startRecording();
			} catch (Exception e) {
				String message = e.getMessage();
				Log.i(null, "Problem Start" + message);
				mrec.release();
			}
			break;

		case 1: // GoToAllNotes
			stopRecording();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void startRecording() throws IOException {

		mrec = new MediaRecorder(); // Works well
		mCamera.unlock();

		mrec.setCamera(mCamera);

		// mrec.setPreviewDisplay(surfaceHolder.getSurface());
		mrec.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		mrec.setAudioSource(MediaRecorder.AudioSource.MIC);

		mrec.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));

		mrec.setOrientationHint(90);

		// Supported Profiles

		mrec.setPreviewDisplay(surfaceHolder.getSurface());

		mrec.setOutputFile(FileUtil.generateRandomFileName() + ".extension");

		mrec.prepare();
		mrec.start();
	}

	protected void stopRecording() {
		mrec.stop();
		mrec.release();
		mCamera.release();
	}

	private void releaseMediaRecorder() {
		if (mrec != null) {
			mrec.reset(); // clear recorder configuration
			mrec.release(); // release the recorder object
			mrec = null;
			mCamera.lock(); // lock camera for later use
		}
	}

	// Call on application exit
	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		initPreview(0, 0);
		startPreview();
		Toast.makeText(this, "Camera Ready!", 1000).show();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (mCamera != null) {
			Parameters params = mCamera.getParameters();
			mCamera.setParameters(params);

		} else {
			Toast.makeText(getApplicationContext(), "Camera not available!",
					Toast.LENGTH_LONG).show();
			finish();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		mCamera.release();
	}

	private void initPreview(int width, int height) {
		if (mCamera != null && surfaceHolder.getSurface() != null) {
			try {
				mCamera.setPreviewDisplay(surfaceHolder);
			} catch (Throwable t) {
				Log.e("PreviewDemo-surfaceCallback",
						"Exception in setPreviewDisplay()", t);
			}

		}
	}

	private void startPreview() {
		if (mCamera != null) {
			mCamera.startPreview();
			inPreview = true;
		}
	}
}
