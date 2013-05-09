package com.moziy.hollerback.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.moziy.hollerback.R;
import com.moziy.hollerback.activity.HaveFunVideoActivity;
import com.moziy.hollerback.adapter.VideoGalleryAdapter;
import com.moziy.hollerback.bitmap.ImageCache;
import com.moziy.hollerback.bitmap.ImageFetcher;
import com.moziy.hollerback.cache.memory.TempMemoryStore;
import com.moziy.hollerback.communication.IABIntent;
import com.moziy.hollerback.communication.IABroadcastManager;
import com.moziy.hollerback.debug.LogUtil;
import com.moziy.hollerback.helper.S3RequestHelper;
import com.moziy.hollerback.model.VideoModel;
import com.moziy.hollerback.util.AppEnvironment;
import com.moziy.hollerback.util.FileUtil;
import com.moziy.hollerback.view.HorizontalListView;
import com.moziy.hollerbacky.connection.RequestCallbacks.OnProgressListener;

public class ConversationFragment extends BaseFragment {

	private HorizontalListView mVideoGallery;
	private VideoGalleryAdapter mVideoGalleryAdapter;

	// Image Loading
	private ImageFetcher mImageFetcher;
	private int mImageThumbSize;
	private int mImageThumbSpacing;

	private static final String IMAGE_CACHE_DIR = "thumbs";

	// Image Loading

	// Video Playback Stuff
	private VideoView mVideoView;
	private TextView mProgressText;

	private S3RequestHelper mS3RequestHelper;

	// Reply stuff
	private Button mReplyBtn;

	public int TAKE_VIDEO = 0x683;

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

		mS3RequestHelper = new S3RequestHelper();

		mS3RequestHelper.registerOnProgressListener(mOnProgressListener);

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
		IABroadcastManager.unregisterLocalReceiver(receiver);
		mS3RequestHelper.clearOnProgressListener();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initializeArgs();
		mImageFetcher.setExitTasksEarly(false);
		mVideoGalleryAdapter.notifyDataSetChanged();
		IABroadcastManager.registerForLocalBroadcast(receiver,
				IABIntent.INTENT_REQUEST_VIDEO);
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
		mProgressText = (TextView) view.findViewById(R.id.tv_progress);

		mReplyBtn = (Button) view.findViewById(R.id.btn_video_reply);

		mReplyBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						HaveFunVideoActivity.class);
				startActivityForResult(intent, TAKE_VIDEO);
			}
		});
	}

	OnItemClickListener mListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			LogUtil.i("Clicked ON: " + position);
			VideoModel model = mVideoGalleryAdapter.getVideos().get(position);
			mS3RequestHelper.downloadS3(AppEnvironment.PICTURE_BUCKET,
					model.getFileName());
			mProgressText.setVisibility(View.VISIBLE);

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

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (IABIntent.isIntent(intent, IABIntent.INTENT_REQUEST_VIDEO)) {
				LogUtil.i("Received ID: " + IABIntent.PARAM_ID);
				playVideo(intent.getStringExtra(IABIntent.PARAM_ID));

			}
		}
	};

	private void playVideo(String fileKey) {
		mVideoView.setVideoPath(FileUtil.getLocalFile(fileKey));
		mVideoView.requestFocus();
		mVideoView.start();
	}

	private OnProgressListener mOnProgressListener = new OnProgressListener() {

		@Override
		public void onProgress(long amount, long total) {
			String percent = Long.toString((amount * 100 / total)) + "%";
			if (!percent.equals(mProgressText.getText().toString())) {
				mProgressText.setText(percent);
			}
		}

		@Override
		public void onComplete() {
			mProgressText.setText("");
			mProgressText.setVisibility(View.GONE);

		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

}