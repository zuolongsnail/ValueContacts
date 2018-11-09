package com.valuestudio.contacts.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;

import com.valuestudio.contacts.entity.CallLogEntity;
import com.valuestudio.contacts.utils.Constant;
import com.valuestudio.contacts.utils.ContactsUtil;

public class CallLogAsyncTask extends
		AsyncTask<Cursor, Void, ArrayList<CallLogEntity>> {
	/**
	 * 开始查询
	 */
	public static final int QUERY_START_MESSAGE = 1;
	/**
	 * 结束查询
	 */
	public static final int QUERY_END_MESSAGE = 2;

	private Handler mHandler = null;

	private Context context;

	protected CallLogAsyncTask(Context context, Handler handler) {
		this.context = context;
		this.mHandler = handler;
	}

	@Override
	protected void onPreExecute() {
		sendStartMessage(QUERY_START_MESSAGE);
	}

	@Override
	protected ArrayList<CallLogEntity> doInBackground(Cursor... params) {
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
				if (number.startsWith(Constant.NumberFormat.PLUS_86)) {
					number = number.substring(Constant.NumberFormat.PLUS_86
							.length());
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
		Cursor cursor = params[0];
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
					if (tempNum.startsWith(Constant.NumberFormat.PLUS_86)) {
						tempNum = tempNum
								.substring(Constant.NumberFormat.PLUS_86
										.length());
					}
					cachedName = nameMap.get(tempNum);
				}
				String dateStr = "";
				if (ContactsUtil.isToday(theDate)) {// 今天
					dateStr = ContactsUtil.toDateString(ContactsUtil.HH_mm,
							new Date(theDate));
				} else if (ContactsUtil.isYesterday(theDate)) {// 昨天
					dateStr = "昨天 "
							+ ContactsUtil.toDateString(ContactsUtil.HH_mm,
									new Date(theDate));
				} else {
					dateStr = ContactsUtil.toDateString(
							ContactsUtil.MM_dd_HH_mm, new Date(theDate));
				}
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
		if (cursor != null) {
			cursor.close();
		}
		return callLogList;
	}

	@Override
	protected void onPostExecute(ArrayList<CallLogEntity> result) {
		sendEndMessage(QUERY_END_MESSAGE, result);
	}

	public static void startQueryCallLog(Context context, Handler handler,
			Cursor cursor) {
		new CallLogAsyncTask(context, handler).executeOnExecutor(
				AsyncTask.THREAD_POOL_EXECUTOR, cursor);
	}

	/**
	 * 开始查询
	 * 
	 * @param messageWhat
	 */
	private void sendStartMessage(int messageWhat) {
		Message message = new Message();
		message.what = messageWhat;
		if (mHandler != null) {
			mHandler.sendMessage(message);
		}
	}

	/**
	 * 结束查询
	 * 
	 * @param messageWhat
	 */
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
