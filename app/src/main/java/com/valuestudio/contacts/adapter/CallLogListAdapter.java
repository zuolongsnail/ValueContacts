package com.valuestudio.contacts.adapter;

import java.io.InputStream;
import java.util.List;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.valuestudio.contacts.R;
import com.valuestudio.contacts.entity.CallLogEntity;
import com.valuestudio.contacts.utils.CommonUtils;
import com.valuestudio.contacts.utils.ContactsUtil;

public class CallLogListAdapter extends BaseAdapter {

	protected Context mContext;
	private List<CallLogEntity> mCallLogList;

	public CallLogListAdapter(Context context, List<CallLogEntity> callLogList) {
		this.mContext = context;
		this.mCallLogList = callLogList;
	}

	public void setCallLogList(List<CallLogEntity> callLogList) {
		this.mCallLogList = callLogList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CallLogEntity entity = mCallLogList.get(position);
		ViewHolder viewHolder;
		if (null == convertView) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.call_log_list_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.photoImg = (ImageView) convertView
					.findViewById(R.id.photo_img);
			viewHolder.callView = (ImageButton) convertView
					.findViewById(R.id.call_view);
			viewHolder.nameText = (TextView) convertView
					.findViewById(R.id.name_text);
			viewHolder.numberText = (TextView) convertView
					.findViewById(R.id.number_text);
			viewHolder.timeText = (TextView) convertView
					.findViewById(R.id.time_text);
			viewHolder.typeView = (ImageView) convertView
					.findViewById(R.id.type_view);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// 联系人头像Bitamp
		Bitmap contactPhoto = null;
		// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
		if (entity.getPhotoId() > 0) {
			Uri uri = ContentUris.withAppendedId(
					ContactsContract.Contacts.CONTENT_URI,
					entity.getContactId());
			InputStream input = ContactsContract.Contacts
					.openContactPhotoInputStream(mContext.getContentResolver(),
							uri);
			contactPhoto = BitmapFactory.decodeStream(input);
			viewHolder.photoImg.setImageBitmap(contactPhoto);
		} else {
			viewHolder.photoImg.setImageDrawable(mContext.getResources()
					.getDrawable(R.drawable.photo_selector));
		}
		viewHolder.photoImg.setTag(position);
		addListener(viewHolder.photoImg);
		viewHolder.callView.setTag(position);
		addListener(viewHolder.callView);
		// 姓名
		String name = entity.getName();
		// 号码
		String number = entity.getNumber();
		if (TextUtils.isEmpty(name)) {
			if (TextUtils.isEmpty(number) || "-1".equals(number)) {
				viewHolder.nameText.setText("未知");
				viewHolder.numberText.setText("本地号码");
				// 设置头像背景色
				viewHolder.photoImg.setBackgroundColor(CommonUtils
						.getBackgroundColorId("", mContext));
			} else {
				viewHolder.nameText.setText(number);
				if (TextUtils.isEmpty(entity.getLocation())) {
					viewHolder.numberText.setText("本地号码");
				} else {
					viewHolder.numberText.setText(entity.getLocation());
				}
				// 设置头像背景色
				viewHolder.photoImg.setBackgroundColor(CommonUtils
						.getBackgroundColorId(number, mContext));
			}
		} else {
			viewHolder.nameText.setText(name);
			viewHolder.numberText.setText(number);
			// 设置头像背景色
			viewHolder.photoImg.setBackgroundColor(CommonUtils
					.getBackgroundColorId(name, mContext));
		}
		viewHolder.timeText.setText(entity.getDate());
		if (entity.getType() == CallLog.Calls.INCOMING_TYPE) {
			viewHolder.typeView
					.setBackgroundResource(R.drawable.call_list_incoming);
		} else if (entity.getType() == CallLog.Calls.OUTGOING_TYPE) {
			viewHolder.typeView
					.setBackgroundResource(R.drawable.call_list_outgoing);
		} else if (entity.getType() == CallLog.Calls.MISSED_TYPE) {
			viewHolder.typeView
					.setBackgroundResource(R.drawable.call_list_missed);
		} else {
			viewHolder.typeView
					.setBackgroundResource(R.drawable.call_list_reject);
		}
		return convertView;
	}

	private void addListener(View view) {
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				int position = Integer.parseInt(view.getTag().toString());
				CallLogEntity entity = mCallLogList.get(position);
				switch (view.getId()) {
				case R.id.call_view:
				case R.id.photo_img:
					ContactsUtil.call(mContext, entity.getNumber());
					break;
				}
			}
		});
	}

	@Override
	public int getCount() {
		return mCallLogList.size();
	}

	@Override
	public Object getItem(int position) {
		return mCallLogList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class ViewHolder {
		private ImageView photoImg;
		private ImageButton callView;
		private TextView nameText;
		private TextView numberText;
		private TextView timeText;
		private ImageView typeView;
	}

}
