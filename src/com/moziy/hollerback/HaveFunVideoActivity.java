package com.moziy.hollerback;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.moziy.hollerback.debug.LogUtil;
import com.moziy.hollerback.util.HollerbackAppState;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.Toast;

public class HaveFunVideoActivity extends Activity implements
		SurfaceHolder.Callback {
	private SurfaceHolder surfaceHolder;
	private SurfaceView surfaceView;
	public MediaRecorder mrec = new MediaRecorder();
	private Button startRecording = null;
	// private Button stopRecording = null;
	File video;
	private Camera mCamera;
	boolean inPreview;

	boolean isPrepared;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_surface);
		Log.i(null, "Video starting");
		startRecording = (Button) findViewById(R.id.buttonstart);

		
		
		try {
			mCamera = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
			Log.e("Hollerback", "Camera successfully opened");
		} catch (RuntimeException e) {
			Log.e("Hollerback",
					"Camera failed to open: " + e.getLocalizedMessage());
		}

		if (mCamera == null) {
			mCamera = Camera.open();
		}

		mCamera.getParameters().setRecordingHint(true);

		List<Size> tmpList = mCamera.getParameters().getSupportedPreviewSizes();

		for (Size size : tmpList) {
			Log.i("Sizes ", size.width + " x " + size.height);
		}

		surfaceView = (SurfaceView) findViewById(R.id.surface_camera);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// try {
		// prepareForRecording();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "StartRecording");
		menu.add(0, 1, 0, "StopRecording");
		return super.onCreateOptionsMenu(menu);
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
			mrec.stop();
			mrec.release();
			mrec = null;
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
		mrec.setOutputFile("/sdcard/z0042.3gp");

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

			mCamera.setDisplayOrientation(90);
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
