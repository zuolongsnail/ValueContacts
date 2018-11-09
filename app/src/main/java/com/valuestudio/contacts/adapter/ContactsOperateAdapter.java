package com.valuestudio.contacts.adapter;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import android.content.ContentUris;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.valuestudio.contacts.R;
import com.valuestudio.contacts.entity.ContactEntity;
import com.valuestudio.contacts.utils.CommonUtils;
import com.valuestudio.contacts.utils.ToPinYin;

public class ContactsOperateAdapter extends BaseAdapter {

	protected Context mContext;
	private List<ContactEntity> mContactEntityList;
	/**
	 * 存储是否勾选状态
	 */
	private Map<Integer, Boolean> mIsCheckedMap;
	/**
	 * 存储已勾选联系人的id
	 */
	private HashSet<Integer> mCheckedIndexSet;

	public ContactsOperateAdapter(Context context, List<ContactEntity> phoneList) {
		this.mContext = context;
		this.mContactEntityList = phoneList;
		mIsCheckedMap = new HashMap<Integer, Boolean>();
		mCheckedIndexSet = new HashSet<Integer>();

		for (int i = 0; i < mContactEntityList.size(); i++) {
			mIsCheckedMap.put(i, false);
		}
	}

	/**
	 * 点击选择全部按钮
	 * 
	 * @description
	 * @param checked
	 * @date 2014-11-29
	 * @author valuestudio
	 */
	public void clickSelectAll(boolean checked) {
		// 若勾选则保存position，否则移除
		if (checked) {
			for (int i = 0; i < mContactEntityList.size(); i++) {
				mIsCheckedMap.put(i, true);
				mCheckedIndexSet.add(i);
			}
		} else {
			for (int i = 0; i < mContactEntityList.size(); i++) {
				mIsCheckedMap.put(i, false);
			}
			mCheckedIndexSet.clear();
		}
		notifyDataSetChanged();
	}

	public void setPhoneList(List<ContactEntity> phoneList) {
		this.mContactEntityList = phoneList;
	}

	public List<ContactEntity> getPhoneList() {
		return mContactEntityList;
	}

	public HashSet<Integer> getCheckedIndexSet() {
		return mCheckedIndexSet;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ContactEntity entity = mContactEntityList.get(position);
		ViewHolder viewHolder;
		if (null == convertView) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.contacts_operate_list_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.alphaLayout = (LinearLayout) convertView
					.findViewById(R.id.alpha_layout);
			viewHolder.alphaText = (TextView) convertView
					.findViewById(R.id.alpha_text);
			viewHolder.alphaDiv = (LinearLayout) convertView
					.findViewById(R.id.alpha_div_line_view);
			viewHolder.photoImg = (ImageView) convertView
					.findViewById(R.id.photo_img);
			viewHolder.nameText = (TextView) convertView
					.findViewById(R.id.name_text);
			viewHolder.numberText = (TextView) convertView
					.findViewById(R.id.number_text);
			viewHolder.itemCheck = (CheckBox) convertView
					.findViewById(R.id.item_check);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// 字母导航
		String currentStr = ToPinYin.getAlpha(entity.getSortKey());
		String previewStr = (position - 1) >= 0 ? ToPinYin
				.getAlpha(mContactEntityList.get(position - 1).getSortKey())
				: " ";
		if (!previewStr.equals(currentStr)) {
			viewHolder.alphaLayout.setVisibility(View.VISIBLE);
			viewHolder.alphaText.setText(currentStr);
		} else {
			viewHolder.alphaLayout.setVisibility(View.GONE);
		}
		// 设置字母颜色
		TypedArray a = mContext.obtainStyledAttributes(null,
				R.styleable.ContactsAppView, R.attr.ContactsAppStyle, 0);
		int alphaTextColor = a.getResourceId(
				R.styleable.ContactsAppView_themeColor, R.color.blue);
		if (alphaTextColor == R.color.theme_black
				|| alphaTextColor == R.color.theme_white) {
			alphaTextColor = R.color.blue;
		}
		viewHolder.alphaText.setTextColor(mContext.getResources().getColor(
				alphaTextColor));
		viewHolder.alphaDiv.setBackgroundColor(mContext.getResources()
				.getColor(alphaTextColor));
		a.recycle();
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
					.getDrawable(R.drawable.ic_photo_n));
		}
		// 设置头像背景色
		viewHolder.photoImg.setBackgroundColor(CommonUtils
				.getBackgroundColorId(entity.getName(), mContext));
		viewHolder.photoImg.setTag(position);
		// 姓名
		viewHolder.nameText.setText(entity.getName());
		// 号码
		viewHolder.numberText.setText(entity.getNumber());
		// 选择控件
		viewHolder.itemCheck.setTag(position);
		boolean checked = mIsCheckedMap.get(position);
		viewHolder.itemCheck.setChecked(checked);
		return convertView;
	}

	/**
	 * 设置checkbox的选中
	 * 
	 * @description
	 * @param position
	 * @param checked
	 * @date 2014-7-20
	 * @author valuestudio
	 */
	public void selectContacts(int position, boolean checked) {
		// 保存勾选状态
		mIsCheckedMap.put(position, checked);

		// 若勾选则保存position，否则移除
		if (checked) {
			mCheckedIndexSet.add(position);
		} else {
			mCheckedIndexSet.remove(position);
		}
	}

	@Override
	public int getCount() {
		return mContactEntityList.size();
	}

	@Override
	public Object getItem(int position) {
		return mContactEntityList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class ViewHolder {
		private LinearLayout alphaLayout;
		private TextView alphaText;
		private LinearLayout alphaDiv;
		private ImageView photoImg;
		private TextView nameText;
		private TextView numberText;
		private CheckBox itemCheck;
	}

	/**
	 * 初始化Adapter
	 */
	public void initSelectedAdapter() {
		for (int i = 0; i < mContactEntityList.size(); i++) {
			mIsCheckedMap.put(i, false);
		}
		mCheckedIndexSet.clear();
		notifyDataSetChanged();
	}

}
