package com.moziy.hollerback;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OutputFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.moziy.hollerback.util.AppEnvironment;
import com.moziy.hollerback.util.FileUtil;
import com.moziy.hollerbacky.connection.TestHttpClient;

public class HollerbackCameraActivity extends Activity {

	private SurfaceView preview = null;
	private static SurfaceHolder previewHolder = null;
	private static Camera camera = null;
	private boolean inPreview = false;

	TextView mTimer;
	Handler handler;

	int VIDEO_SENT = 4;

	int secondsPassed;

	View mTopView, mBottomView;

	private String mFileDataPath;
	private String mFileDataName;

	public static String TAG = "VideoApp";

	ImageButton mRecordButton, mSendButton;

	private boolean isRecording = false;

	static MediaRecorder recorder;

	private AmazonS3Client s3Client = new AmazonS3Client(
			new BasicAWSCredentials(AppEnvironment.ACCESS_KEY_ID,
					AppEnvironment.SECRET_KEY));

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.custom_camera);

		handler = new Handler();

		mTopView = findViewById(R.id.top_bar);
		mBottomView = findViewById(R.id.bottom_bar);
		mSendButton = (ImageButton) findViewById(R.id.send_button);

		preview = (SurfaceView) findViewById(R.id.surface);

		previewHolder = preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		previewHolder.setFixedSize(getWindow().getWindowManager()
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

		mSendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new S3PutObjectTask().execute(mFileDataPath);
			}
		});

		mRecordButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isRecording) {
					// stop recording and release camera
					recorder.stop(); // stop the recording
					releaseMediaRecorder(); // release the MediaRecorder object
					camera.lock(); // take camera access back from MediaRecorder

					// inform the user that recording has stopped
					mRecordButton.setImageResource(R.drawable.record_button);
					isRecording = false;
					Toast.makeText(getApplicationContext(),
							"Saved to: " + mFileDataPath, 5000).show();

					if (mFileDataPath != null) {
						mRecordButton.setVisibility(View.GONE);
						mSendButton.setVisibility(View.VISIBLE);
					}
					handler.removeCallbacks(timeTask);
					secondsPassed = 0;

				} else {
					mSendButton.setVisibility(View.GONE);

					// initialize video camera
					if (prepareVideoRecorder()) {
						mTimer.setText("00:00");
						// Camera is available and unlocked, MediaRecorder is
						// prepared,
						// now you can start recording
						recorder.start();

						// inform the user that recording has started
						mRecordButton.setImageResource(R.drawable.stop_button);
						isRecording = true;
						handler.postDelayed(timeTask, 1000);
					} else {
						// prepare didn't work, release the camera
						releaseMediaRecorder();
						// inform user
					}
				}
			}
		});

		mTimer = (TextView) findViewById(R.id.timer);
	}

	Runnable timeTask = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			secondsPassed += 1;
			String seconds = secondsPassed < 10 ? "0"
					+ Integer.toString(secondsPassed) : Integer
					.toString(secondsPassed);
			mTimer.setText("00:" + seconds);
			handler.postDelayed(timeTask, 1000);
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		try {
			camera = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
			Log.e("Hollerback", "Camera successfully opened");
		} catch (RuntimeException e) {
			Log.e("Hollerback",
					"Camera failed to open: " + e.getLocalizedMessage());
		}

		if (camera == null) {
			camera = Camera.open();
		}
		previewHolder.addCallback(surfaceCallback);
	}

	@Override
	public void onPause() {
		if (inPreview) {
			camera.stopPreview();
		}

		camera.release();
		camera = null;
		inPreview = false;
		super.onPause();
	}

	private String getNewFileName() {
		mFileDataPath = FileUtil.getOutputMediaFile(FileUtil.MEDIA_TYPE_VIDEO)
				.toString();
		String[] temp = mFileDataPath.split("/");

		mFileDataName = temp[temp.length - 1];

		Toast.makeText(this, mFileDataName, 3000).show();

		return mFileDataPath;
	}

	private Camera.Size getBestPreviewSize(int width, int height,
			Camera.Parameters parameters) {
		Camera.Size result = null;
		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;
					if (newArea > resultArea) {
						result = size;
					}
				}
			}

		}
		// Toast.makeText(this, result.width + " x " + result.height,
		// 3000).show();
		return (result);
	}

	private boolean prepareVideoRecorder() {

		recorder = new MediaRecorder();

		// Step 1: Unlock and set camera to MediaRecorder
		camera.unlock();
		recorder.setCamera(camera);

		// Step 2: Set sources
		recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

		// recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		// recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

		// Step 3: Set a CamcorderProfile (requires API Level 8 or higher)

		recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));

		recorder.setOrientationHint(90);
		
		// Step 4: Set output file
		recorder.setOutputFile(getNewFileName());

		// Step 5: Set the preview output
		recorder.setPreviewDisplay(preview.getHolder().getSurface());

		// Step 6: Prepare configured MediaRecorder
		try {
			recorder.prepare();
		} catch (IllegalStateException e) {
			Log.d(TAG,
					"IllegalStateException preparing MediaRecorder: "
							+ e.getMessage());
			releaseMediaRecorder();
			return false;
		} catch (IOException e) {
			Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
			releaseMediaRecorder();
			return false;
		}
		return true;
	}

	private void releaseMediaRecorder() {

		if (recorder != null) {
			// recorder.reset(); // clear configuration (optional here)
			recorder.release();
			// recorder = null;
		}
	}

	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				camera.setPreviewDisplay(previewHolder);
				camera.setDisplayOrientation(90);
				MediaRecorder m = new MediaRecorder();
				m.setCamera(camera);
				m.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
				m.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			} catch (Throwable t) {
				Log.e("SurfaceCallback", "Exception in setPreviewDisplay()", t);
				Toast.makeText(HollerbackCameraActivity.this, t.getMessage(),
						Toast.LENGTH_LONG).show();
			}
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			Camera.Parameters parameters = camera.getParameters();
			Camera.Size size = getBestPreviewSize(width, height, parameters);

			if (size != null) {
				parameters.setPreviewSize(size.width, size.height);
				camera.setParameters(parameters);
				camera.setDisplayOrientation(90);
				camera.startPreview();
				inPreview = true;
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// no-op
			// if (camera != null) {
			// camera.stopPreview();
			// camera.setPreviewCallback(null);
			// camera.release();
			// camera = null;
			// }
		}
	};

	private class S3PutObjectTask extends AsyncTask<String, Void, S3TaskResult> {

		ProgressDialog dialog;

		protected void onPreExecute() {
			dialog = new ProgressDialog(HollerbackCameraActivity.this);
			dialog.setMessage("Uploading");
			dialog.setCancelable(false);
			dialog.show();
		}

		protected S3TaskResult doInBackground(String... paths) {

			if (paths == null || paths.length != 1) {
				return null;
			}

			S3TaskResult result = new S3TaskResult();

			// Put the image data into S3.
			try {
				s3Client.createBucket(AppEnvironment.getPictureBucket());

				// Content type is determined by file extension.
				PutObjectRequest por = new PutObjectRequest(
						AppEnvironment.getPictureBucket(), mFileDataName,
						new java.io.File(paths[0]));
				s3Client.putObject(por);
			} catch (Exception exception) {

				result.setErrorMessage(exception.getMessage());
			}

			return result;
		}

		protected void onPostExecute(S3TaskResult result) {

			dialog.dismiss();

			if (result.getErrorMessage() != null) {

				displayErrorAlert("Upload Failure", result.getErrorMessage());
			}

			if (result != null && result.getUri() != null) {

				Toast.makeText(getApplicationContext(),
						"Uploaded to: " + result.getUri().toString(),
						Toast.LENGTH_LONG).show();
				Toast.makeText(getApplicationContext(),
						"Uploaded to: " + result.getUri().getPath(),
						Toast.LENGTH_LONG).show();

			}

			new S3GeneratePresignedUrlTask().execute();
		}
	}

	private class S3GeneratePresignedUrlTask extends
			AsyncTask<Void, Void, S3TaskResult> {

		protected S3TaskResult doInBackground(Void... voids) {

			S3TaskResult result = new S3TaskResult();

			try {
				// Ensure that the image will be treated as such.
				ResponseHeaderOverrides override = new ResponseHeaderOverrides();
				override.setContentType("image/jpeg");

				// Generate the presigned URL.

				// Added an hour's worth of milliseconds to the current time.
				Date expirationDate = new Date(
						System.currentTimeMillis() + 3600000);
				GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(
						AppEnvironment.getPictureBucket(), mFileDataName);
				urlRequest.setExpiration(expirationDate);
				urlRequest.setResponseHeaders(override);

				URL url = s3Client.generatePresignedUrl(urlRequest);

				result.setUri(Uri.parse(url.toURI().toString()));

				updateTextView.obtainMessage(VIDEO_SENT).sendToTarget();

			} catch (Exception exception) {

				result.setErrorMessage(exception.getMessage());
			}

			return result;
		}

		protected void onPostExecute(S3TaskResult result) {

			if (result.getErrorMessage() != null) {

				displayErrorAlert("There was a failure",
						result.getErrorMessage());
			} else if (result.getUri() != null) {

				// Display in Browser.
				// startActivity(new Intent(Intent.ACTION_VIEW,
				// result.getUri()));

				Log.i("Upload", "Uploaded to: " + result.getUri().toString());
				Toast.makeText(getApplicationContext(),
						"Uploaded successfully", 2000).show();

				TestPostTask task = new TestPostTask();
				task.execute(new String[] { result.getUri().toString() });
			}
		}
	}

	private class S3TaskResult {
		String errorMessage = null;
		Uri uri = null;

		public String getErrorMessage() {
			return errorMessage;
		}

		public void setErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
		}

		public Uri getUri() {
			return uri;
		}

		public void setUri(Uri uri) {
			this.uri = uri;
		}
	}

	// Display an Alert message for an error or failure.
	protected void displayAlert(String title, String message) {

		AlertDialog.Builder confirm = new AlertDialog.Builder(this);
		confirm.setTitle(title);
		confirm.setMessage(message);

		confirm.setNegativeButton("Ok", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
			}
		});

		confirm.show().show();
	}

	protected void displayErrorAlert(String title, String message) {

		AlertDialog.Builder confirm = new AlertDialog.Builder(this);
		confirm.setTitle(title);
		confirm.setMessage(message);

		confirm.setNegativeButton("Ok", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

				HollerbackCameraActivity.this.finish();
			}
		});

		confirm.show().show();
	}

	private class TestPostTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {

			String response = TestHttpClient.postData(params[0], null);

			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				Toast.makeText(getApplicationContext(), result, 5000).show();
			}
		}
	}

	public final Handler updateTextView = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == VIDEO_SENT) {
				mRecordButton.setVisibility(View.VISIBLE);
				mSendButton.setVisibility(View.GONE);
			}
		}
	};
}