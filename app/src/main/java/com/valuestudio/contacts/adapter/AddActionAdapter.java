package com.valuestudio.contacts.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.valuestudio.contacts.R;

public class AddActionAdapter extends BaseAdapter {

	private Context mContext;
	public int[] mImageIds;

	public AddActionAdapter(Context context, int[] items) {
		this.mContext = context;
		this.mImageIds = items;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (null == convertView) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.add_action_item, null);
			viewHolder = new ViewHolder();
			viewHolder.actionView = (TextView) convertView
					.findViewById(R.id.action_text);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.actionView.setText(mContext.getResources().getStringArray(
				R.array.action_type)[position]);
		return convertView;
	}

	public int getCount() {
		return mImageIds.length;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		private TextView actionView;
	}

}
