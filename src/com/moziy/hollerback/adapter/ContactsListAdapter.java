package com.moziy.hollerback.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.emilsjolander.components.stickylistheaders.StickyListHeadersAdapter;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.moziy.hollerback.R;
import com.moziy.hollerback.cache.memory.TempMemoryStore;
import com.moziy.hollerback.debug.LogUtil;
import com.moziy.hollerback.model.LocalContactItem;

public class ContactsListAdapter extends BaseAdapter implements
		StickyListHeadersAdapter, SectionIndexer {

	private String[] contacts;
	private LayoutInflater inflater;
	private Context context;
	private int[] sectionId;

	public ContactsListAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		ArrayList<String> names = new ArrayList<String>();
		for (LocalContactItem c : TempMemoryStore.contacts) {
			String num = parseNumber(c.mPhone);
			if (num != null) {
				names.add(c.mDisplayName + " x " + num);
			}
		}
		contacts = new String[names.size()];
		names.toArray(contacts);

	}

	public String parseNumber(String number) {
		PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
		try {
			PhoneNumber phoneNumber = phoneUtil.parse(number, "US");
			if (phoneUtil.isValidNumber(phoneNumber)) {
				return phoneUtil.format(phoneNumber, PhoneNumberFormat.E164);
			}
			return null;
		} catch (NumberParseException e) {
			System.err.println("NumberParseException was thrown: "
					+ e.toString());
			return null;
		}
	}

	@Override
	public int getCount() {
		return contacts.length;
	}

	@Override
	public Object getItem(int position) {
		return contacts[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.contact_list_item, parent,
					false);
			holder.text = (TextView) convertView
					.findViewById(R.id.tv_contact_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.text.setText(contacts[position]);

		return convertView;
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		HeaderViewHolder holder;
		if (convertView == null) {
			holder = new HeaderViewHolder();
			convertView = inflater.inflate(R.layout.header, parent, false);
			holder.text1 = (TextView) convertView.findViewById(R.id.text1);
			convertView.setTag(holder);
		} else {
			holder = (HeaderViewHolder) convertView.getTag();
		}
		// set header text as first char in name

		LogUtil.i("Header View get " + position);

		holder.text1.setText("Hello Rollo");
		return convertView;
	}

	// remember that these have to be static, postion=1 should walys return the
	// same Id that is.
	@Override
	public long getHeaderId(int position) {
		// return the first character of the country as ID because this is what
		// headers are based upon
		if (position == 0) {
			return 0;
		} else if (position == 9) {
			return 9;
		} else if (position == 20) {
			return 24;
		}
		return 0;
	}

	class HeaderViewHolder {
		TextView text1;
	}

	class ViewHolder {
		TextView text;
	}

	public void clear() {
		contacts = new String[0];
		notifyDataSetChanged();
	}

	public void restore() {
		contacts = new String[TempMemoryStore.contacts.size()];
		ArrayList<String> names = new ArrayList<String>();
		for (LocalContactItem c : TempMemoryStore.contacts) {
			names.add(c.mDisplayName);
		}
		names.toArray(contacts);

		notifyDataSetChanged();
	}

	@Override
	public Object[] getSections() {
		sectionId = new int[3];
		sectionId[0] = 0;
		sectionId[1] = 9;
		sectionId[2] = 20;
		return new String[] { "Recent", "Hollerback", "Phonebook" };
	}

	@Override
	public int getPositionForSection(int section) {
		if (section == 0) {
			return sectionId[0];
		} else if (1 == section) {
			return sectionId[1];
		} else if (2 == section) {
			return sectionId[2];
		} else
			return 0;
	}

	@Override
	public int getSectionForPosition(int position) {
		if (position == sectionId[0]) {
			return sectionId[0];
		} else if (position == sectionId[1]) {
			return sectionId[1];
		} else if (position == sectionId[2]) {
			return sectionId[2];
		} else
			return 0;
	}

}
