package com.valuestudio.contacts.service;

import java.util.ArrayList;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.valuestudio.contacts.entity.ContactEntity;
import com.valuestudio.contacts.utils.Constant;
import com.valuestudio.contacts.utils.ToPinYin;

public class LoadPhoneThread implements Runnable {
	/**
	 * 开始查询
	 */
	public static final int QUERY_START_MESSAGE = 1;
	/**
	 * 结束查询
	 */
	public static final int QUERY_END_MESSAGE = 2;

	private Handler mHandler;
	private Context mContext;

	public LoadPhoneThread(Context context, Handler handler) {
		this.mHandler = handler;
		this.mContext = context;
	}

	@Override
	public void run() {
		Cursor cursor = mContext.getContentResolver().query(
				Constant.PHONE_CONTENT_URI, Constant.PHONE_PROJECTION, null,
				null, "sort_key COLLATE LOCALIZED asc");
		ArrayList<ContactEntity> contactList = new ArrayList<ContactEntity>();
		if (cursor != null && cursor.getCount() > 0) {
			try {
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					// 得到手机号码
					String phoneNumber = cursor
							.getString(Constant.NUMBER_INDEX);
					// 当手机号码为空的或者为空字段 跳过当前循环
					if (TextUtils.isEmpty(phoneNumber)) {
						continue;
					}
					// 得到联系人ID
					Long rawContactId = cursor.getLong(Constant.ID_INDEX);
					// 得到联系人名称
					String contactName = cursor.getString(Constant.NAME_INDEX);
					// 得到联系人头像ID
					Long photoId = cursor.getLong(Constant.PHOTO_ID_INDEX);
					// sort_key
					// String sortKey =
					// cursor.getString(Constant.SORT_KEY_INDEX);
					String sortKey = ToPinYin.hanZiToPinYin(contactName);
					// 得到联系人ID
					Long contactId = cursor.getLong(Constant.CONTACT_ID_INDEX);

					ContactEntity entity = new ContactEntity();
					entity.setRawContactId(rawContactId);
					entity.setName(contactName);
					entity.setNumber(phoneNumber);
					entity.setPhotoId(photoId);
					entity.setSortKey(sortKey);
					entity.setContactId(contactId);
					if (TextUtils.isEmpty(entity.getName())) {
						entity.setName(entity.getNumber());
					}
					entity.setPinyin(ToPinYin.getPinYin(entity.getName()));
					entity.setFullNameNum(ToPinYin.getFullNameNum(entity
							.getName()));
					entity.setFirstAlpha(ToPinYin.getFirstAlpha(entity
							.getName()));
					entity.setFirstAlphaNum(ToPinYin.getFirstAlphaNum(entity
							.getName()));
					// 合并相同姓名和相同号码的联系人
					if (!contactList.contains(entity)) {
						contactList.add(entity);
					}
				}
				sendEndMessage(QUERY_END_MESSAGE, contactList);
				if (cursor != null) {
					cursor.close();
				}
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 结束查询
	 * 
	 * @param messageWhat
	 */
	private void sendEndMessage(int messageWhat, ArrayList<ContactEntity> result) {
		Message message = new Message();
		message.what = messageWhat;
		Bundle bundle = new Bundle();
		bundle.putSerializable(Constant.QUERY_RESULT, result);
		message.setData(bundle);
		if (mHandler != null) {
			mHandler.sendMessage(message);
		}
	}

}
