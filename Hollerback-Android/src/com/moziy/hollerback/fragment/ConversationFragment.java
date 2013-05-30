package com.moziy.hollerback.fragment;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.krish.horizontalscrollview.CenterLockHorizontalScrollview;
import com.krish.horizontalscrollview.CustomListAdapter;
import com.moziy.hollerback.HollerbackInterfaces.OnCustomItemClickListener;
import com.moziy.hollerback.R;
import com.moziy.hollerback.activity.HollerbackBaseActivity;
import com.moziy.hollerback.activity.HollerbackCameraActivity;
import com.moziy.hollerback.adapter.VideoGalleryAdapter;
import com.moziy.hollerback.bitmap.ImageCache;
import com.moziy.hollerback.bitmap.ImageFetcher;
import com.moziy.hollerback.communication.IABIntent;
import com.moziy.hollerback.communication.IABroadcastManager;
import com.moziy.hollerback.debug.LogUtil;
import com.moziy.hollerback.helper.CustomActionBarHelper;
import com.moziy.hollerback.helper.S3RequestHelper;
import com.moziy.hollerback.model.ConversationModel;
import com.moziy.hollerback.model.VideoModel;
import com.moziy.hollerback.util.AppEnvironment;
import com.moziy.hollerback.util.FileUtil;
import com.moziy.hollerback.util.QU;
import com.moziy.hollerback.util.ViewUtil;
import com.moziy.hollerback.video.S3UploadParams;
import com.moziy.hollerback.view.HorizontalListView;
import com.moziy.hollerbacky.connection.HBRequestManager;
import com.moziy.hollerbacky.connection.RequestCallbacks.OnProgressListener;

public class ConversationFragment extends BaseFragment {

	/**
	 * This piece of shit takes up 100% height unless you restrict it
	 */
	private CenterLockHorizontalScrollview mVideoGallery;
	private CustomListAdapter mVideoGalleryAdapter;

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

	private String mConversationId;

	private ImageButton mPlayBtn;

	// state
	boolean urlLoaded = false;

	S3RequestHelper helper = new S3RequestHelper();

	private ConversationModel conversation;

	private ArrayList<VideoModel> mVideos;

	boolean playStartInitialized;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.conversation_fragment,
				null);

		ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(
				getActivity(), IMAGE_CACHE_DIR);

		mImageFetcher = new ImageFetcher(getActivity(), mImageThumbSize);
		mImageFetcher.setLoadingImage(R.drawable.placeholder_sq);
		mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(),
				cacheParams);

		mS3RequestHelper = new S3RequestHelper();

		mS3RequestHelper.registerOnProgressListener(mOnProgressListener);

		initializeView(fragmentView);

		initializeArgs();

		return fragmentView;
	}

	public void playNewestVideo() {
		if (playStartInitialized) {
			if (mVideoGalleryAdapter.getCount() > 0) {

				mVideoGalleryAdapter.notifyDataSetChanged();

			}
			setGalleryToEnd();
			return;
		}

		if (mVideoGalleryAdapter.getCount() < 1) {
			return;
		}

		playStartInitialized = true;
		boolean set = false;

		for (int i = mVideoGalleryAdapter.getCount() - 1; i >= 0; i--) {
			if (mVideoGalleryAdapter.getItem(i).isRead() == false) {

				// model.setRead(true);
				set = true;
				mVideoGalleryAdapter.notifyDataSetChanged();
				// }
			}
		}

		if (!set) {
			if (mVideoGalleryAdapter.getCount() > 0) {

				mVideoGalleryAdapter.notifyDataSetChanged();

			}
			setGalleryToEnd();
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		mImageFetcher.setPauseWork(false);
		mImageFetcher.setExitTasksEarly(true);
		mImageFetcher.flushCache();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mImageFetcher.closeCache();
		IABroadcastManager.unregisterLocalReceiver(receiver);
		mS3RequestHelper.clearOnProgressListener();
	}

	@Override
	public void onResume() {
		super.onResume();

		// TODO: Do this less often
		QU.getDM().getVideos(false, mConversationId);

		mImageFetcher.setExitTasksEarly(false);
		IABroadcastManager.registerForLocalBroadcast(receiver,
				IABIntent.INTENT_REQUEST_VIDEO);
		IABroadcastManager.registerForLocalBroadcast(receiver,
				IABIntent.INTENT_GET_URLS);
		IABroadcastManager.registerForLocalBroadcast(receiver,
				IABIntent.INTENT_GET_CONVERSATION_VIDEOS);

	}

	// TODO: Move out of here
	private ArrayList<S3UploadParams> generateUploadParams(String hash,
			String conversationId) {

		ArrayList<S3UploadParams> mGetUrls = new ArrayList<S3UploadParams>();

		ArrayList<VideoModel> videos = ((ArrayList<VideoModel>) QU.getDM()
				.getObjectForToken(hash));

		if (videos != null && videos.size() > 0) {
			for (VideoModel video : videos) {
				S3UploadParams param = new S3UploadParams();
				param.setFileName(video.getFileName());
				param.setOnS3UploadListener(null);
				param.mVideo = video;
				mGetUrls.add(param);
			}
		}

		return mGetUrls;
	}

	public void initializeArgs() {
		Bundle bundle = getArguments();
		mConversationId = bundle.getString("conv_id");

		conversation = QU.getConv(mConversationId);

		HollerbackBaseActivity.getCustomActionBar().setHeaderText(
				conversation.getConversationName());
		LogUtil.i("Conversation Fragment: ID: " + mConversationId);
		// mVideoGalleryAdapter.setVideos(TempMemoryStore.conversations.get(index)
		// .getVideos());
		// helper.getS3URLParams(generateUploadParams(index));

	}

	@Override
	protected void initializeView(View view) {

		mPlayBtn = (ImageButton) view.findViewById(R.id.ib_play_btn);

		mVideoGallery = (CenterLockHorizontalScrollview) view
				.findViewById(R.id.hlz_video_gallery);

		mVideoView = (VideoView) view
				.findViewById(R.id.vv_conversation_playback);

		// mVideoGallery.setOnItemClickListener(mListener);
		// mVideoGallery.setOnScrollListener(mOnScrollListener);
		mProgressText = (TextView) view.findViewById(R.id.tv_progress);

		mReplyBtn = (Button) view.findViewById(R.id.btn_video_reply);

		mReplyBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						HollerbackCameraActivity.class);
				LogUtil.i("Putting Extra ID: " + mConversationId == null ? "null"
						: mConversationId);

				Bundle mBundle = new Bundle();
				mBundle.putString(IABIntent.PARAM_ID, mConversationId);
				intent.putExtras(mBundle);

				// intent.putExtra(IABIntent.PARAM_ID, mConversationId);
				startActivity(intent);
			}
		});

		mVideoView.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				mPlayBtn.setVisibility(View.VISIBLE);
				mPlayBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						playVideo(currentVideo);
					}
				});
			}
		});
	}

	OnCustomItemClickListener mListener = new OnCustomItemClickListener() {

		@Override
		public void onItemClicked(int position) {
			LogUtil.i("Clicked ON: " + position);
			VideoModel model = mVideoGalleryAdapter.getItem(position);
			mS3RequestHelper.downloadS3(
					AppEnvironment.getInstance().PICTURE_BUCKET,
					model.getFileName());
			mProgressText.setVisibility(View.VISIBLE);
			if (!model.isRead()) {
				model.setRead(true);
			}
			mVideoGalleryAdapter.notifyDataSetChanged();

			mVideoGallery.setCenter(position);
			HBRequestManager
					.postVideoRead(Integer.toString(model.getVideoId()));

		}
	};

	/**
	 * Create a new instance of CountingFragment, providing "num" as an
	 * argument.
	 */
	public static ConversationFragment newInstance(String conversation_id) {

		ConversationFragment f = new ConversationFragment();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putString("conv_id", conversation_id);
		f.setArguments(args);
		return f;
	}

	String currentVideo;

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (IABIntent.isIntent(intent, IABIntent.INTENT_REQUEST_VIDEO)) {
				LogUtil.i("Received ID: "
						+ intent.getStringExtra(IABIntent.PARAM_ID));
				playVideo(intent.getStringExtra(IABIntent.PARAM_ID));

			} else if (IABIntent.isIntent(intent,
					IABIntent.INTENT_GET_CONVERSATION_VIDEOS)) {

				String hash = intent
						.getStringExtra(IABIntent.PARAM_INTENT_DATA);

				mVideos = (ArrayList<VideoModel>) QU.getDM().getObjectForToken(
						intent.getStringExtra(IABIntent.PARAM_INTENT_DATA));

				// helper.getS3URLParams(generateUploadParams(hash,
				// intent.getStringExtra(IABIntent.PARAM_ID)));

				mVideos = (ArrayList<VideoModel>) mVideos.clone();

				if (mVideos != null) {

					LogUtil.i("Setting Received videos: " + mVideos.size());

					if (mVideoGalleryAdapter == null) {
						mVideoGalleryAdapter = new CustomListAdapter(
								getActivity(), mImageFetcher, mVideos);
						mVideoGallery.setAdapter(getActivity(),
								mVideoGalleryAdapter);
						mVideoGalleryAdapter
								.setOnCustomItemClickListener(mListener);
					} else {
						mVideoGalleryAdapter.setListItems(mVideos);
						mVideoGalleryAdapter.notifyDataSetChanged();
					}

					LogUtil.d("Setting new Videos size: " + mVideos.size());

					// TODO: Fix issues here
					// playNewestVideo();
					LogUtil.i("Setting center index: " + mVideos.size());
					if (mVideoGalleryAdapter.getCount() > 0) {
						// mVideoGalleryAdapter.selectedIndex =
						// mVideoGalleryAdapter
						// .getCount() - 1;
						// mVideoGalleryAdapter.notifyDataSetChanged();
						mVideoGallery.snapCenter(mVideos.size() - 1);

					}
					// setGalleryToEnd();

				}

			}
		}
	};

	private void setGalleryToEnd() {
		mVideoGallery.post(new Runnable() {
			@Override
			public void run() {
				mVideoGallery.requestFocusFromTouch();
				// mVideoGallery.setSelection(TempMemoryStore.conversations
				// .get(index).getVideos().size() - 1);

				// mVideoGallery.setSelection(mVideoGallery.getRight());

				if (getActivity() != null && !getActivity().isFinishing()) {
					int imageWidth = (int) ViewUtil.convertDpToPixel(80,
							getActivity());

					LogUtil.i("Image Width: " + imageWidth);

					// mVideoGallery.scrollToEnd(imageWidth
					// * mVideoGalleryAdapter.getCount());
					LogUtil.i("Gallery x: " + imageWidth
							* mVideoGalleryAdapter.getCount());
					mVideoGallery.requestFocus();

				}

			}
		});
	}

	private void playVideo(String fileKey) {
		String path = FileUtil.getLocalFile(fileKey);

		LogUtil.i("Play video: " + path);
		mVideoView.setVisibility(View.VISIBLE);
		mPlayBtn.setVisibility(View.GONE);

		currentVideo = fileKey;

		mVideoView.setVideoPath(path);
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
	protected void onActionBarIntialized(CustomActionBarHelper viewHelper) {
		viewHelper.hideSideButtons();
	}

}