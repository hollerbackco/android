package com.moziy.hollerback.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.moziy.hollerback.R;
import com.moziy.hollerback.bitmap.ImageFetcher;
import com.moziy.hollerback.helper.CustomActionBarHelper;
import com.moziy.hollerback.model.VideoModel;

public class VideoGalleryAdapter extends BaseAdapter {

	ArrayList<VideoModel> mVideos;
	LayoutInflater inflater;

	ImageFetcher mImageFetcher;

	int mVideoWidth;

	public VideoGalleryAdapter(ImageFetcher imageFetcher, Context context) {
		mImageFetcher = imageFetcher;
		inflater = LayoutInflater.from(context);
		mVideos = new ArrayList<VideoModel>();
	}

	public void clearVideos() {
		mVideos = new ArrayList<VideoModel>();
		this.notifyDataSetChanged();
	}

	public ArrayList<VideoModel> getVideos() {
		return mVideos;
	}

	public void setVideos(ArrayList<VideoModel> videos) {
		mVideos = videos;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mVideos.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mVideos.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.video_gallery_item, null);
			viewHolder.videoThumbnail = (ImageView) convertView
					.findViewById(R.id.iv_video_thumbnail);
			viewHolder.videoThumbnail
					.setScaleType(ImageView.ScaleType.CENTER_CROP);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (mVideos.get(position).getThumbUrl() != null
				&& mImageFetcher != null) {

			mImageFetcher.loadImage(mVideos.get(position).getThumbUrl(),
					viewHolder.videoThumbnail);
		}

		if (mVideoWidth == 0) {
			mVideoWidth = viewHolder.videoThumbnail.getWidth();
		}

		return convertView;
	}

	static class ViewHolder {
		ImageView videoThumbnail;
	}

	public int getVideoImagePreviewWidth() {
		return mVideoWidth;
	}

}
