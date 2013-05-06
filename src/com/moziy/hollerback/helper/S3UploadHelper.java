package com.moziy.hollerback.helper;

import java.net.URL;
import java.util.Date;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.moziy.hollerback.util.AppEnvironment;
import com.moziy.hollerback.video.S3UploadParams;

//TODO: Abstract the upload methods, verification and buckets

public class S3UploadHelper {

	private static AmazonS3Client s3Client;

	public S3UploadHelper() {
		if (s3Client == null) {
			s3Client = new AmazonS3Client(new BasicAWSCredentials(
					AppEnvironment.ACCESS_KEY_ID, AppEnvironment.SECRET_KEY));
		}

	}

	public String uploadFile(S3UploadParams params, String filePath) {
		return null;
	}

	private class S3PutObjectTask extends
			AsyncTask<S3UploadParams, Void, S3TaskResult> {

		ProgressDialog dialog;

		protected void onPreExecute() {
			// dialog = new ProgressDialog(HollerbackCameraActivity.this);
			// dialog.setMessage("Uploading");
			// dialog.setCancelable(false);
			// dialog.show();
		}

		protected S3TaskResult doInBackground(S3UploadParams... videos) {

			if (videos == null || videos.length != 1) {
				return null;
			}

			S3UploadParams requestParam = videos[0];

			S3TaskResult result = new S3TaskResult();

			// Put the image data into S3.
			try {
				s3Client.createBucket(AppEnvironment.getPictureBucket());

				// Content type is determined by file extension.
				PutObjectRequest por = new PutObjectRequest(
						AppEnvironment.getPictureBucket(),
						requestParam.getFileName(), new java.io.File(
								requestParam.getFilePath()));
				s3Client.putObject(por);
			} catch (Exception exception) {

				result.setErrorMessage(exception.getMessage());
			}

			return result;
		}

		protected void onPostExecute(S3TaskResult result) {

			dialog.dismiss();

			if (result.getErrorMessage() != null) {

				//displayErrorAlert("Upload Failure", result.getErrorMessage());
			}

			if (result != null && result.getUri() != null) {

				// Toast.makeText(getApplicationContext(),
				// "Uploaded to: " + result.getUri().toString(),
				// Toast.LENGTH_LONG).show();
				// Toast.makeText(getApplicationContext(),
				// "Uploaded to: " + result.getUri().getPath(),
				// Toast.LENGTH_LONG).show();

			}

			new S3GeneratePresignedUrlTask()
					.execute(new S3UploadParams[] { result.getS3UploadParams() });
		}
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
		S3UploadParams uploadParams;

		public void setS3UploadParams(S3UploadParams params) {
			uploadParams = params;
		}

		public S3UploadParams getS3UploadParams() {
			return uploadParams;
		}

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
