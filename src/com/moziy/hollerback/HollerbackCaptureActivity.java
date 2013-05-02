package com.moziy.hollerback;

import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

import com.moziy.hollerback.util.FileUtil;
import com.moziy.hollerback.view.CameraPreview;

public class HollerbackCaptureActivity extends Activity {

	Camera mCamera;
	CameraPreview mPreview;
	MediaRecorder mMediaRecorder;
	Button capture;

	boolean isRecording;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// capture
		capture = (Button) findViewById(R.id.button_capture);

		capture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!isRecording) {
					startVideoRecording();
					isRecording = true;
				} else {
					stopVideoRecording();
					isRecording = false;
				}
			}
		});

		// Create an instance of Camera
		mCamera = getCameraInstance();

		// Create our Preview view and set it as the content of our activity.
		mPreview = new CameraPreview(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);
		
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {

			c = Camera.open();

		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	public void prepareForVideoRecording() {

		if (mCamera == null) {
			return;
		}

		mMediaRecorder = new MediaRecorder();

		mCamera.unlock();
		mMediaRecorder.setCamera(mCamera);
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		// mMediaRecorder.setProfile(CamcorderProfile.get(front_faceid,
		// CamcorderProfile.QUALITY_480P));
		// this is back face only
		mMediaRecorder.setProfile(CamcorderProfile
				.get(CamcorderProfile.QUALITY_480P));

		mMediaRecorder.setOutputFile(FileUtil.getOutputMediaFile(
				FileUtil.MEDIA_TYPE_VIDEO).toString());

		mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
		mCamera.startPreview();

		try {
			mMediaRecorder.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public void startVideoRecording() {
		prepareForVideoRecording();
		mMediaRecorder.start();
	}

	public void stopVideoRecording() {
		mMediaRecorder.stop();
		mMediaRecorder.release();
		mCamera.lock();
	}

}
