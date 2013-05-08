package com.moziy.hollerback.fragment;

import com.moziy.hollerback.R;
import com.moziy.hollerback.adapter.VideoGalleryAdapter;
import com.moziy.hollerback.cache.memory.TempMemoryStore;
import com.moziy.hollerback.view.HorizontalListView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ConversationFragment extends BaseFragment {

	private HorizontalListView mVideoGallery;
	private VideoGalleryAdapter mVideoGalleryAdapter;

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

}
