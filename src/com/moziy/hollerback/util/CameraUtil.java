package com.moziy.hollerback.util;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OutputFormat;

import com.moziy.hollerback.debug.LogUtil;

public class CameraUtil {

	public static Camera.Size getBestPreviewSize(int width, int height,
			Camera.Parameters parameters) {
		Camera.Size result = null;
		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			LogUtil.i("Preview: w:" + size.width + " h:" + size.height);
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
		return (result);
	}

	public static void setFrontFacingParams(MediaRecorder recorder) {
	    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
	    recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
		recorder.setAudioSamplingRate(16000);
		recorder.setAudioEncodingBitRate(64000);
		recorder.setVideoEncodingBitRate(512000);
	    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		recorder.setVideoSize(480, 320);
	}

}
