package com.valuestudio.contacts.adapter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.valuestudio.contacts.R;
import com.valuestudio.contacts.base.IAdapterCallback;
import com.valuestudio.contacts.entity.ContactEntity;
import com.valuestudio.contacts.utils.CommonUtils;
import com.valuestudio.contacts.utils.ContactsUtil;

public class ContactListAdapter extends BaseAdapter {

	protected Context mContext;
	private List<ContactEntity> mContactList;
	private List<ContactEntity> mAllContactList;
	private IAdapterCallback adapterCallback;
	private String filterNum;

	public ContactListAdapter(Context context, List<ContactEntity> contactList) {
		this.mContext = context;
		this.mAllContactList = contactList;
		this.mContactList = contactList;
	}

	public void setContactList(List<ContactEntity> contactList) {
		this.mContactList = contactList;
	}

	public List<ContactEntity> getContactList() {
		return mContactList;
	}

	public void setAllContactList(List<ContactEntity> allContactList) {
		this.mAllContactList = allContactList;
	}

	public List<ContactEntity> getAllContactList() {
		return mAllContactList;
	}

	public void setAdapterCallback(IAdapterCallback callback) {
		adapterCallback = callback;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ContactEntity entity = mContactList.get(position);
		ViewHolder viewHolder;
		if (null == convertView) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.contact_list_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.photoImg = (ImageView) convertView
					.findViewById(R.id.photo_img);
			viewHolder.callView = (ImageButton) convertView
					.findViewById(R.id.call_view);
			viewHolder.nameText = (TextView) convertView
					.findViewById(R.id.name_text);
			viewHolder.numberText = (TextView) convertView
					.findViewById(R.id.number_text);
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
		// 设置头像背景色
		viewHolder.photoImg.setBackgroundColor(CommonUtils
				.getBackgroundColorId(entity.getName(), mContext));
		viewHolder.photoImg.setTag(position);
		addListener(viewHolder.photoImg);
		viewHolder.callView.setTag(position);
		addListener(viewHolder.callView);
		// 姓名
		viewHolder.nameText.setText(entity.getName());
		// 号码
		if (TextUtils.isEmpty(filterNum)) {
			viewHolder.numberText.setText(entity.getNumber());
		} else {
			viewHolder.numberText.setText(Html.fromHtml(entity.getNumber()
					.replace(filterNum,
							"<font color='#00A1E8'>" + filterNum + "</font>")));
		}
		return convertView;
	}

	private void addListener(View view) {
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				int position = Integer.parseInt(view.getTag().toString());
				ContactEntity entity = mContactList.get(position);
				switch (view.getId()) {
				case R.id.photo_img:
				case R.id.call_view:
					ContactsUtil.call(mContext, entity.getNumber());
					adapterCallback.afterCall();
					break;
				}
			}
		});
	}

	@Override
	public int getCount() {
		return mContactList.size();
	}

	@Override
	public Object getItem(int position) {
		return mContactList.get(position);
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
	}

	public Filter getFilter() {
		Filter filter = new Filter() {
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				mContactList = (ArrayList<ContactEntity>) results.values;
				adapterCallback.filterCallback();
				if (results.count > 0) {
					notifyDataSetChanged();
				}
			}

			protected FilterResults performFiltering(CharSequence s) {
				String str = s.toString();
				filterNum = str;
				FilterResults results = new FilterResults();
				ArrayList<ContactEntity> contactList = new ArrayList<ContactEntity>();
				if (mAllContactList != null && mAllContactList.size() > 0) {
					for (ContactEntity entity : mAllContactList) {
						if (entity.getFirstAlphaNum().equals(str)) {
							if (!contactList.contains(entity)) {
								contactList.add(entity);
							}
						}
					}
					for (ContactEntity entity : mAllContactList) {
						if (entity.getFirstAlphaNum().indexOf(str) >= 0) {
							if (!contactList.contains(entity)) {
								contactList.add(entity);
							}
						}
					}
					for (ContactEntity entity : mAllContactList) {
						if (entity.getFullNameNum().indexOf(str) == 0) {
							if (!contactList.contains(entity)) {
								contactList.add(entity);
							}
						}
					}
					for (ContactEntity entity : mAllContactList) {
						if (entity.getFullNameNum().indexOf(str) > 0) {
							if (!contactList.contains(entity)) {
								contactList.add(entity);
							}
						}
					}
					for (ContactEntity entity : mAllContactList) {
						if (entity.getNumber().indexOf(str) >= 0) {
							if (!contactList.contains(entity)) {
								contactList.add(entity);
							}
						}
					}
				}
				results.values = contactList;
				results.count = contactList.size();
				return results;
			}
		};
		return filter;
	}

}
