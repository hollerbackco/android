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
import com.moziy.hollerback.helper.ContactsHelper;
import com.moziy.hollerback.model.LocalContactItem;

public class ContactsListAdapter extends BaseAdapter implements
		StickyListHeadersAdapter, SectionIndexer {

	private String[] contacts;
	private int[] sectionIndices;
	private Character[] sectionsLetters;
	private LayoutInflater inflater;
	private Context context;

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
		sectionIndices = getSectionIndices();
		sectionsLetters = getStartingLetters();
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

	private Character[] getStartingLetters() {
		Character[] letters = new Character[sectionIndices.length];
		for (int i = 0; i < sectionIndices.length; i++) {
			letters[i] = contacts[sectionIndices[i]].charAt(0);
		}
		return letters;
	}

	private int[] getSectionIndices() {
		ArrayList<Integer> sectionIndices = new ArrayList<Integer>();
		char lastFirstChar = contacts[0].charAt(0);
		sectionIndices.add(0);
		for (int i = 1; i < contacts.length; i++) {
			if (contacts[i].charAt(0) != lastFirstChar) {
				lastFirstChar = contacts[i].charAt(0);
				sectionIndices.add(i);
			}
		}
		int[] sections = new int[sectionIndices.size()];
		for (int i = 0; i < sectionIndices.size(); i++) {
			sections[i] = sectionIndices.get(i);
		}
		return sections;
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
			holder.text2 = (TextView) convertView.findViewById(R.id.text2);
			convertView.setTag(holder);
		} else {
			holder = (HeaderViewHolder) convertView.getTag();
		}
		// set header text as first char in name
		char headerChar = contacts[position].subSequence(0, 1).charAt(0);
		String headerText;
		if (headerChar % 2 == 0) {
			headerText = headerChar + "\n" + headerChar + "\n" + headerChar;
		} else {
			headerText = headerChar + "\n" + headerChar;
		}
		holder.text1.setText(headerText);
		holder.text2.setText(headerText);
		return convertView;
	}

	// remember that these have to be static, postion=1 should walys return the
	// same Id that is.
	@Override
	public long getHeaderId(int position) {
		// return the first character of the country as ID because this is what
		// headers are based upon
		return contacts[position].subSequence(0, 1).charAt(0);
	}

	class HeaderViewHolder {
		TextView text1;
		TextView text2;
	}

	class ViewHolder {
		TextView text;
	}

	@Override
	public int getPositionForSection(int section) {
		if (section >= sectionIndices.length) {
			section = sectionIndices.length - 1;
		} else if (section < 0) {
			section = 0;
		}
		return sectionIndices[section];
	}

	@Override
	public int getSectionForPosition(int position) {
		for (int i = 0; i < sectionIndices.length; i++) {
			if (position < sectionIndices[i]) {
				return i - 1;
			}
		}
		return sectionIndices.length - 1;
	}

	@Override
	public Object[] getSections() {
		return sectionsLetters;
	}

	public void clear() {
		sectionIndices = new int[0];
		sectionsLetters = new Character[0];
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

		sectionIndices = getSectionIndices();
		sectionsLetters = getStartingLetters();
		notifyDataSetChanged();
	}

}
