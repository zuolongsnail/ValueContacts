package com.valuestudio.contacts.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.valuestudio.contacts.R;

public class ColorsListAdapter extends BaseAdapter {
	private Context mContext;
	private List<Integer> list;
	private int checkItem;

	public ColorsListAdapter(Context context, List<Integer> list) {
		this.mContext = context;
		this.list = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.colors_image_layout, null);
			holder = new Holder();
			holder.imageView1 = (ImageView) convertView
					.findViewById(R.id.img_1);
			holder.imageView2 = (ImageView) convertView
					.findViewById(R.id.img_2);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.imageView1.setImageResource(list.get(position));
		if (checkItem == position) {
			if (checkItem == 0) {
				holder.imageView2.setImageResource(R.drawable.ic_done_black);
			} else {
				holder.imageView2.setImageResource(R.drawable.ic_done_white);
			}
		}
		return convertView;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Integer getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public int getCheckItem() {
		return checkItem;
	}

	public void setCheckItem(int checkItem) {
		this.checkItem = checkItem;
	}

	static class Holder {
		ImageView imageView1;
		ImageView imageView2;
	}
}
