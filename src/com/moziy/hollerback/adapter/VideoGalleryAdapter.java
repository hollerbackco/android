package com.moziy.hollerback.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Video.Thumbnails;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.moziy.hollerback.R;
import com.moziy.hollerback.model.VideoModel;

public class VideoGalleryAdapter extends BaseAdapter {

	ArrayList<VideoModel> mVideos;
	LayoutInflater inflater;

	public VideoGalleryAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		mVideos = new ArrayList<VideoModel>();
	}

	public void clearVideos() {
		mVideos = new ArrayList<VideoModel>();
		this.notifyDataSetChanged();
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
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (mVideos.get(position).getFileUrl() != null
				&& !mVideos.get(position).getFileUrl().isEmpty()) {
			viewHolder.videoThumbnail.setImageBitmap(ThumbnailUtils
					.createVideoThumbnail(mVideos.get(position).getURLPath(),
							Thumbnails.MINI_KIND));
		}

		return convertView;
	}

	static class ViewHolder {
		ImageView videoThumbnail;
	}

}
