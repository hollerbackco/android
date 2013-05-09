package com.moziy.hollerback.fragment;

import java.util.ArrayList;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.MediaController;
import android.widget.VideoView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.moziy.hollerback.R;
import com.moziy.hollerback.adapter.VideoGalleryAdapter;
import com.moziy.hollerback.bitmap.ImageCache;
import com.moziy.hollerback.bitmap.ImageFetcher;
import com.moziy.hollerback.cache.memory.TempMemoryStore;
import com.moziy.hollerback.debug.LogUtil;
import com.moziy.hollerback.helper.S3UploadHelper;
import com.moziy.hollerback.model.VideoModel;
import com.moziy.hollerback.util.AppEnvironment;
import com.moziy.hollerback.video.S3UploadParams;
import com.moziy.hollerback.view.HorizontalListView;

public class ConversationFragment extends BaseFragment {

	private HorizontalListView mVideoGallery;
	private VideoGalleryAdapter mVideoGalleryAdapter;

	private AmazonS3Client s3Client = new AmazonS3Client(
			new BasicAWSCredentials(AppEnvironment.ACCESS_KEY_ID,
					AppEnvironment.SECRET_KEY));

	// Image Loading
	private ImageFetcher mImageFetcher;
	private int mImageThumbSize;
	private int mImageThumbSpacing;

	private static final String IMAGE_CACHE_DIR = "thumbs";

	// Image Loading

	// Video Playback Stuff
	private VideoView mVideoView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.conversation_fragment,
				null);

		ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(
				getActivity(), IMAGE_CACHE_DIR);

		mImageFetcher = new ImageFetcher(getActivity(), mImageThumbSize);
		mImageFetcher.setLoadingImage(R.drawable.test_thumb);
		mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(),
				cacheParams);
		initializeView(fragmentView);

		return fragmentView;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mImageFetcher.setPauseWork(false);
		mImageFetcher.setExitTasksEarly(true);
		mImageFetcher.flushCache();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mImageFetcher.closeCache();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initializeArgs();
		mImageFetcher.setExitTasksEarly(false);
		mVideoGalleryAdapter.notifyDataSetChanged();

		// String UrlPath = "android.resource://com.moziy.hollerback/"
		// + R.raw.test_video;
		// mVideoView.setVideoURI(Uri.parse(UrlPath));
		// MediaController mc = new MediaController(getActivity());
		// mVideoView.setMediaController(mc);
		// mVideoView.requestFocus();
		// mVideoView.start();
		// mc.show();
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
		mVideoGalleryAdapter = new VideoGalleryAdapter(mImageFetcher,
				getActivity());
		mVideoGallery.setAdapter(mVideoGalleryAdapter);
		mVideoView = (VideoView) view
				.findViewById(R.id.vv_conversation_playback);
		mVideoGallery.setOnItemClickListener(mListener);
		// mVideoGallery.setOnScrollListener(mOnScrollListener);
	}

	OnItemClickListener mListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			LogUtil.i("Clicked ON: " + position);
			VideoModel model = mVideoGalleryAdapter.getVideos().get(position);
			// LogUtil.i("URL: " + model.getFileUrl());
			// LogUtil.i("FILENAME: " + model.getFileName());
			//
			// mVideoView.setOnPreparedListener(new OnPreparedListener() {
			//
			// @Override
			// public void onPrepared(MediaPlayer mp) {
			// mp.start();
			// }
			// });
			//
			//
			//
			//
			// mVideoView.setOnCompletionListener(new OnCompletionListener() {
			//
			// @Override
			// public void onCompletion(MediaPlayer mp) {
			// mp.reset();
			// }
			// });
			// String UrlPath = "android.resource://com.moziy.hollerback/"
			// + R.raw.test_video;
			// mVideoView.setVideoURI(Uri.parse(UrlPath));
			// // mVideoView.setVideoURI(Uri.parse(UrlPath));
			//
			// mVideoView.requestFocus();
			// mVideoView.start();

			S3UploadHelper helper = new S3UploadHelper();
			helper.downloadS3(AppEnvironment.PICTURE_BUCKET,
					model.getFileName());

		}
	};

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

	// TODO: Move out of here
	private ArrayList<S3UploadParams> generateUploadParams() {

		ArrayList<S3UploadParams> mGetUrls = new ArrayList<S3UploadParams>();
		for (VideoModel video : TempMemoryStore.conversations.get(0)
				.getVideos()) {
			S3UploadParams param = new S3UploadParams();
			param.setFileName(video.getFileName());
			param.setOnS3UploadListener(null);
			param.mVideo = video;
			mGetUrls.add(param);
		}

		return mGetUrls;
	}

}
