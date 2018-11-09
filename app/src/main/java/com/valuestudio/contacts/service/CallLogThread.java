package com.valuestudio.contacts.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;

import com.valuestudio.contacts.entity.CallLogEntity;
import com.valuestudio.contacts.utils.Column;
import com.valuestudio.contacts.utils.Constant;
import com.valuestudio.contacts.utils.ContactsUtil;

public class CallLogThread implements Runnable {
	/**
	 * 开始查询
	 */
	public static final int QUERY_START_MESSAGE = 1;
	/**
	 * 结束查询
	 */
	public static final int QUERY_END_MESSAGE = 2;
	private Handler mHandler;
	private Context context;

	public CallLogThread(Context context, Handler handler) {
		this.context = context;
		this.mHandler = handler;
	}

	@Override
	public void run() {
		// 获取号码姓名键值对
		Map<String, String> nameMap = new HashMap<String, String>();
		// 获取头像id键值对
		Map<String, Long> photoIdMap = new HashMap<String, Long>();
		Cursor contactCursor = context.getContentResolver().query(
				Constant.PHONE_CONTENT_URI, null, null, null,
				"display_name collate localized asc");
		int contactNumberIndex = contactCursor.getColumnIndex(Phone.NUMBER);
		int contactNameIndex = contactCursor.getColumnIndex(Phone.DISPLAY_NAME);
		int photoIdIndex = contactCursor.getColumnIndex(Phone.PHOTO_ID);
		if (contactCursor != null && contactCursor.getCount() > 0) {
			contactCursor.moveToFirst();
			for (int i = 0; i < contactCursor.getCount(); i++) {
				contactCursor.moveToPosition(i);
				String number = contactCursor.getString(contactNumberIndex);
				// 判断前缀是否为“+86”，用于解决号码不能匹配联系人姓名的问题
				// if (number.startsWith(Constant.NumberFormat.PLUS_86)) {
				// number = number.substring(Constant.NumberFormat.PLUS_86
				// .length());
				// }
				// 判断手机号码前是否有前缀，用于解决号码不能匹配联系人姓名的问题
				if (number.length() > 12) {
					number = number.substring(number.length() - 11);
				}
				String contactName = contactCursor.getString(contactNameIndex);
				nameMap.put(number, contactName);
				long photoId = contactCursor.getLong(photoIdIndex);
				if (!TextUtils.isEmpty(contactName) && photoId > 0) {
					photoIdMap.put(contactName, photoId);
				}
			}
		}
		if (contactCursor != null) {
			contactCursor.close();
		}

		// 查询通话记录
		String[] projection = { CallLog.Calls._ID, /* Column.Calls.CONTACTSID, */
		CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER, Column.Calls.LOCATION,
				CallLog.Calls.TYPE, CallLog.Calls.DATE };
		Cursor cursor = context.getContentResolver().query(
				CallLog.Calls.CONTENT_URI, projection, null, null,
				CallLog.Calls.DEFAULT_SORT_ORDER);
		ArrayList<CallLogEntity> callLogList = new ArrayList<CallLogEntity>();
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				int id = cursor.getInt(Constant.CallLogIndex.ID_INDEX);
				// long contactsid = cursor
				// .getLong(Constant.CallLogIndex.CONTACT_ID_INDEX);
				String cachedName = cursor
						.getString(Constant.CallLogIndex.NAME_INDEX);
				String number = cursor
						.getString(Constant.CallLogIndex.NUMBER_INDEX);
				String location = cursor
						.getString(Constant.CallLogIndex.LOCATION_INDEX);
				int type = cursor.getInt(Constant.CallLogIndex.TYPE_INDEX);
				long theDate = cursor.getLong(Constant.CallLogIndex.DATE_INDEX);
				// 根据号码查询姓名
				if (TextUtils.isEmpty(cachedName)) {
					String tempNum = number;
					// if (tempNum.startsWith(Constant.NumberFormat.PLUS_86)) {
					// tempNum = tempNum
					// .substring(Constant.NumberFormat.PLUS_86
					// .length());
					// }
					// 判断手机号码前是否有前缀，用于解决号码不能匹配联系人姓名的问题
					if (tempNum.length() > 12) {
						tempNum = tempNum.substring(tempNum.length() - 11);
					}
					cachedName = nameMap.get(tempNum);
				}
				// String dateStr = "";
				// if (ContactsUtil.isToday(theDate)) {// 今天
				// dateStr = ContactsUtil.toDateString(ContactsUtil.HH_mm,
				// new Date(theDate));
				// } else if (ContactsUtil.isYesterday(theDate)) {// 昨天
				// dateStr = "昨天 "
				// + ContactsUtil.toDateString(ContactsUtil.HH_mm,
				// new Date(theDate));
				// } else {
				// dateStr = ContactsUtil.toDateString(
				// ContactsUtil.MM_dd_HH_mm, new Date(theDate));
				// }
				// 通话时间
				String dateStr = ContactsUtil.getCallLogDateStr(theDate);
				CallLogEntity entity = new CallLogEntity();
				entity.setId(id);
				// entity.setContactId(contactsid);
				entity.setName(cachedName);
				if (!TextUtils.isEmpty(cachedName)
						&& photoIdMap.containsKey(cachedName)) {
					entity.setPhotoId(photoIdMap.get(cachedName));
				}
				entity.setNumber(number);
				entity.setLocation(location);
				entity.setType(type);
				entity.setDate(dateStr);
				// 合并相同号码的通话记录
				if (!callLogList.contains(entity)) {
					callLogList.add(entity);
				}
			}
		}
		sendEndMessage(QUERY_END_MESSAGE, callLogList);
		if (cursor != null) {
			cursor.close();
		}
	}

	private void sendEndMessage(int messageWhat, ArrayList<CallLogEntity> result) {
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
