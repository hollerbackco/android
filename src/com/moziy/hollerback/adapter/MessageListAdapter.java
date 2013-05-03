package com.moziy.hollerback.adapter;

import java.util.ArrayList;

import com.moziy.hollerback.R;
import com.moziy.hollerback.model.MessageModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MessageListAdapter extends BaseAdapter {

	ArrayList<MessageModel> mMessages;

	LayoutInflater inflater;

	public MessageListAdapter(Context context) {
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mMessages.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mMessages.get(position);
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
			convertView = inflater.inflate(R.layout.message_list_item, null);
			convertView.setTag(viewHolder);
		} else {			
			viewHolder = (ViewHolder) convertView.getTag();
		}


		return convertView;
	}

	static class ViewHolder {

	}

}
