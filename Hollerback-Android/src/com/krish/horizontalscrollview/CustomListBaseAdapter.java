package com.krish.horizontalscrollview;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.moziy.hollerback.HollerbackInterfaces.OnCustomItemClickListener;
import com.moziy.hollerback.R;
import com.moziy.hollerback.bitmap.ImageFetcher;
import com.moziy.hollerback.debug.LogUtil;
import com.moziy.hollerback.model.VideoModel;

public class CustomListBaseAdapter extends BaseAdapter {
	Context context;
	public View view;
	public int currPosition = 0;

	private ArrayList<VideoModel> mVideoModels;

	ImageFetcher mImageFetcher;

	private OnCustomItemClickListener mCustomClickListener;

	int mVideoWidth;

	public CustomListBaseAdapter(Context context, ImageFetcher imageFetcher) {
		mImageFetcher = imageFetcher;
		this.context = context;
		mVideoModels = new ArrayList<VideoModel>();
	}

	public void setOnCustomItemClickListener(OnCustomItemClickListener listener) {
		mCustomClickListener = listener;
	}

	@Override
	public int getCount() {
		return mVideoModels.size();
	}

	public void setListItems(ArrayList<VideoModel> videos) {
		mVideoModels = videos;
	}

	@Override
	public VideoModel getItem(int position) {
		return mVideoModels.get(position);

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new ViewHolder();

			convertView = (RelativeLayout) View.inflate(context,
					R.layout.video_gallery_item, null);

			viewHolder.videoThumbnail = (ImageView) convertView
					.findViewById(R.id.iv_video_thumbnail);
			viewHolder.videoThumbnail
					.setScaleType(ImageView.ScaleType.CENTER_CROP);
			viewHolder.unreadCircle = (ImageView) convertView
					.findViewById(R.id.iv_unread_circle);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCustomClickListener != null) {
					LogUtil.i("Clicking on position: " + position);
					mCustomClickListener.onItemClicked(position);
				}
			}
		});

		LogUtil.i("Loading Thumb: " + getItem(position).getThumbUrl());

		if (getItem(position).getThumbUrl() != null && mImageFetcher != null) {

			mImageFetcher.loadImage(getItem(position).getThumbUrl(),
					viewHolder.videoThumbnail);
		}

		if (mVideoWidth == 0) {
			mVideoWidth = viewHolder.videoThumbnail.getWidth();
		}

		if (getItem(position).isRead()) {
			viewHolder.unreadCircle.setVisibility(View.GONE);
		} else {
			viewHolder.unreadCircle.setVisibility(View.VISIBLE);
		}

		Log.v("Test", "lo frm newsadpater");

		return convertView;
	}

	static class ViewHolder {
		ImageView videoThumbnail;
		ImageView unreadCircle;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
}
