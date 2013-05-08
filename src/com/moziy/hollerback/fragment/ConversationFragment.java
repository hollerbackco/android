package com.moziy.hollerback.fragment;

import java.net.URL;
import java.util.Date;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.moziy.hollerback.R;
import com.moziy.hollerback.adapter.VideoGalleryAdapter;
import com.moziy.hollerback.cache.memory.TempMemoryStore;
import com.moziy.hollerback.util.AppEnvironment;
import com.moziy.hollerback.video.S3UploadParams;
import com.moziy.hollerback.view.HorizontalListView;

public class ConversationFragment extends BaseFragment {

	private HorizontalListView mVideoGallery;
	private VideoGalleryAdapter mVideoGalleryAdapter;

	private AmazonS3Client s3Client = new AmazonS3Client(
			new BasicAWSCredentials(AppEnvironment.ACCESS_KEY_ID,
					AppEnvironment.SECRET_KEY));

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.conversation_fragment,
				null);
		initializeView(fragmentView);

		return fragmentView;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initializeArgs();
	}

	public void initializeArgs() {
		Bundle bundle = getArguments();
		int index = bundle.getInt("index");
		mVideoGalleryAdapter.setVideos(TempMemoryStore.conversations.get(index)
				.getVideos());
	}

	@Override
	protected void initializeView(View view) {
		mVideoGallery = (HorizontalListView) view
				.findViewById(R.id.hlz_video_gallery);
		mVideoGalleryAdapter = new VideoGalleryAdapter(getActivity());
		mVideoGallery.setAdapter(mVideoGalleryAdapter);
	}

	/**
	 * Create a new instance of CountingFragment, providing "num" as an
	 * argument.
	 */
	public static ConversationFragment newInstance(int index) {

		ConversationFragment f = new ConversationFragment();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("index", index);
		f.setArguments(args);
		return f;
	}

	private class S3GeneratePresignedUrlTask extends
			AsyncTask<S3UploadParams, Void, S3TaskResult> {

		protected S3TaskResult doInBackground(S3UploadParams... videos) {

			S3TaskResult result = new S3TaskResult();

			if (videos == null || videos.length != 1) {
				return null;
			}

			S3UploadParams uploadParams = videos[0];

			try {
				// Ensure that the image will be treated as such.
				ResponseHeaderOverrides override = new ResponseHeaderOverrides();
				override.setContentType("image/jpeg");

				// Generate the presigned URL.

				// Added an hour's worth of milliseconds to the current time.
				Date expirationDate = new Date(
						System.currentTimeMillis() + 3600000);
				GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(
						AppEnvironment.getPictureBucket(),
						uploadParams.getFileName());
				urlRequest.setExpiration(expirationDate);
				urlRequest.setResponseHeaders(override);

				URL url = s3Client.generatePresignedUrl(urlRequest);

				result.setUri(Uri.parse(url.toURI().toString()));

				// updateTextView.obtainMessage(VIDEO_SENT).sendToTarget();

			} catch (Exception exception) {

				result.setErrorMessage(exception.getMessage());
			}

			return result;
		}

		protected void onPostExecute(S3TaskResult result) {

			if (result.getErrorMessage() != null) {

				// displayErrorAlert("There was a failure",
				// result.getErrorMessage());
			} else if (result.getUri() != null) {

				// Display in Browser.
				// startActivity(new Intent(Intent.ACTION_VIEW,
				// result.getUri()));

				Log.i("Upload", "Uploaded to: " + result.getUri().toString());
				// Toast.makeText(getApplicationContext(),
				// "Uploaded successfully", 2000).show();

				// TestPostTask task = new TestPostTask();
				// task.execute(new String[] { result.getUri().toString() });
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

}
