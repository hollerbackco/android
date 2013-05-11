package com.moziy.hollerback.activity;

import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OutputFormat;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.moziy.hollerback.R;
import com.moziy.hollerback.communication.IABIntent;
import com.moziy.hollerback.debug.LogUtil;
import com.moziy.hollerback.helper.S3RequestHelper;
import com.moziy.hollerback.util.CameraUtil;
import com.moziy.hollerback.util.FileUtil;
import com.moziy.hollerback.util.ImageUtil;
import com.moziy.hollerbacky.connection.RequestCallbacks.OnS3UploadListener;

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

	float targetPreviewWidth;
	float targetPreviewHeight;
	String targetExtension;

	// Preview shit
	private View mPreviewParentView;
	private VideoView mPreviewVideoView;
	private ImageButton mPreviewPlayBtn, mPreviewDeleteBtn;
	private ImageView mImagePreview;

	S3RequestHelper mS3RequestHelper;

	private String mConversationId;

	public static HollerbackCameraActivity sInstance;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.custom_camera);

		sInstance = this;

		if (getIntent().hasExtra(IABIntent.PARAM_ID)) {
			mConversationId = getIntent().getStringExtra(IABIntent.PARAM_ID);
		}

		handler = new Handler();
		mS3RequestHelper = new S3RequestHelper();

		mTopView = findViewById(R.id.top_bar);
		mBottomView = findViewById(R.id.bottom_bar);
		mSendButton = (ImageButton) findViewById(R.id.send_button);

		mPreviewParentView = findViewById(R.id.rl_video_preview);
		mPreviewVideoView = (VideoView) findViewById(R.id.vv_video_preview);
		mPreviewPlayBtn = (ImageButton) findViewById(R.id.ib_play_btn);
		mPreviewDeleteBtn = (ImageButton) findViewById(R.id.ib_delete_btn);
		mImagePreview = (ImageView) findViewById(R.id.iv_video_preview);

		preview = (SurfaceView) findViewById(R.id.surface);

		previewHolder = preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		targetPreviewWidth = 480;
		targetPreviewHeight = 320;

		// this 1.5 i guess assumes 640 x 480
		previewHolder
				.setFixedSize(
						getWindow().getWindowManager().getDefaultDisplay()
								.getWidth(),
						(int) (getWindow().getWindowManager()
								.getDefaultDisplay().getWidth() * (targetPreviewWidth / targetPreviewHeight)));

		RelativeLayout.LayoutParams mImagePreviewParams = (RelativeLayout.LayoutParams) mImagePreview
				.getLayoutParams();
		mImagePreviewParams.height = (int) (getWindow().getWindowManager()
				.getDefaultDisplay().getWidth() * (targetPreviewWidth / targetPreviewHeight));

		mImagePreview.setLayoutParams(mImagePreviewParams);

		// RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
		// mTopView
		// .getLayoutParams();
		// params.height = (getWindow().getWindowManager().getDefaultDisplay()
		// .getHeight() - getWindow().getWindowManager()
		// .getDefaultDisplay().getWidth()) / 2;
		// mTopView.setLayoutParams(params);
		//
		// RelativeLayout.LayoutParams bottomParams =
		// (RelativeLayout.LayoutParams) mBottomView
		// .getLayoutParams();
		// bottomParams.height = (getWindow().getWindowManager()
		// .getDefaultDisplay().getHeight() - getWindow()
		// .getWindowManager().getDefaultDisplay().getWidth()) / 2;
		// mBottomView.setLayoutParams(bottomParams);

		mRecordButton = (ImageButton) findViewById(R.id.record_button);

		mSendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mS3RequestHelper.uploadNewVideo(mConversationId, mFileDataName,
						FileUtil.getImageUploadName(mFileDataName),
						mOnS3UploadListener);
			}
		});

		mRecordButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isRecording) {
					stopRecording();
				} else {
					startRecording();
				}
			}
		});

		mTimer = (TextView) findViewById(R.id.timer);
	}

	OnS3UploadListener mOnS3UploadListener = new OnS3UploadListener() {

		@Override
		public void onStart() {

		}

		@Override
		public int onProgress(long progress) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int onComplete() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void onS3Upload(boolean success) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onS3Url(String url, boolean success) {
			// TODO Auto-generated method stub

		}
	};

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
		// if (inPreview) {
		// camera.stopPreview();
		// }
		//
		// camera.release();
		// camera = null;
		// inPreview = false;
		super.onPause();
	}

	private void startRecording() {
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

	private void stopRecording() {
		// stop recording and release camera
		recorder.stop(); // stop the recording
		releaseMediaRecorder(); // release the MediaRecorder object
		camera.lock(); // take camera access back from MediaRecorder

		// inform the user that recording has stopped
		mRecordButton.setImageResource(R.drawable.record_button);
		isRecording = false;
		Toast.makeText(getApplicationContext(), "Saved to: " + mFileDataPath,
				5000).show();

		if (mFileDataPath != null) {
			mRecordButton.setVisibility(View.GONE);
			mSendButton.setVisibility(View.VISIBLE);
		}
		handler.removeCallbacks(timeTask);
		secondsPassed = 0;

		displayPreview();
		ImageUtil.generateThumbnail(mFileDataName);

	}

	public void displayPreview() {

		if (inPreview) {
			camera.stopPreview();
		}

		mImagePreview.setVisibility(View.VISIBLE);

		preview.setVisibility(View.GONE);

		inPreview = false;

		mPreviewParentView.setVisibility(View.VISIBLE);
		mPreviewVideoView.setVisibility(View.VISIBLE);
		mPreviewPlayBtn.setVisibility(View.VISIBLE);
		mPreviewDeleteBtn.setVisibility(View.VISIBLE);

		Bitmap bmThumbnail = ThumbnailUtils.createVideoThumbnail(
				FileUtil.getLocalFile(mFileDataName),
				Thumbnails.FULL_SCREEN_KIND);
		mImagePreview.setBackgroundDrawable(new BitmapDrawable(bmThumbnail));

		mPreviewVideoView.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				mPreviewPlayBtn.setVisibility(View.VISIBLE);
			}
		});

		mPreviewDeleteBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hidePreview();
			}
		});

		mPreviewPlayBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				playVideo(mFileDataName);
				mPreviewPlayBtn.setVisibility(View.GONE);
			}
		});

	}

	public void hidePreview() {

		preview.setVisibility(View.VISIBLE);
		previewHolder.addCallback(surfaceCallback);

		mPreviewParentView.setVisibility(View.GONE);
		mPreviewVideoView.setVisibility(View.GONE);
		mPreviewPlayBtn.setVisibility(View.GONE);
		mPreviewDeleteBtn.setVisibility(View.GONE);

		mPreviewDeleteBtn.setOnClickListener(null);

		mPreviewPlayBtn.setOnClickListener(null);

		mRecordButton.setVisibility(View.VISIBLE);
		mSendButton.setVisibility(View.GONE);
	}

	private String getNewFileName() {

		mFileDataName = FileUtil.generateRandomFileName() + "."
				+ targetExtension;
		mFileDataPath = FileUtil.getOutputVideoFile(mFileDataName).toString();
		Toast.makeText(this, mFileDataName, 3000).show();

		return mFileDataPath;
	}

	private void playVideo(String fileKey) {

		mImagePreview.setVisibility(View.GONE);

		mPreviewVideoView.setVideoPath(FileUtil.getLocalFile(fileKey));

		mPreviewVideoView.requestFocus();
		mPreviewVideoView.start();
		LogUtil.i("vid size: " + mPreviewVideoView.getHeight()
				+ mPreviewVideoView.getWidth());
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

		CameraUtil.setFrontFacingParams(recorder);

		targetExtension = FileUtil.getFileFormat(OutputFormat.MPEG_4);

		CamcorderProfile prof = CamcorderProfile
				.get(CamcorderProfile.QUALITY_LOW);

		LogUtil.i("Record size: " + prof.videoFrameWidth + " "
				+ prof.videoFrameHeight);

		recorder.setOrientationHint(270);

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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (inPreview) {
			camera.stopPreview();
		}

		camera.release();
		camera = null;
		inPreview = false;
		super.onDestroy();
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
			Camera.Size size = CameraUtil.getBestPreviewSize(
					(int) targetPreviewWidth, (int) targetPreviewHeight,
					parameters);
			LogUtil.i("Best size: " + size.width + " " + size.height);

			if (size != null) {
				parameters.setPreviewSize(size.width, size.height);
				camera.setParameters(parameters);
				camera.setDisplayOrientation(90);
				camera.startPreview();
				inPreview = true;
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {

		}
	};

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