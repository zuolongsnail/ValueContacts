package com.valuestudio.contacts.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.valuestudio.contacts.entity.ContactEntity;
import com.valuestudio.contacts.utils.Constant;
import com.valuestudio.contacts.utils.SharedPrefsUtil;
import com.valuestudio.contacts.utils.ToPinYin;

public class ContactsAsyncTask extends
		AsyncTask<Cursor, Void, ArrayList<ContactEntity>> {
	/**
	 * 开始查询
	 */
	public static final int QUERY_START_MESSAGE = 1;
	/**
	 * 结束查询
	 */
	public static final int QUERY_END_MESSAGE = 2;

	private Handler mHandler = null;
	private Context mContext = null;

	protected ContactsAsyncTask(Context context, Handler handler) {
		this.mHandler = handler;
		this.mContext = context;
	}

	@Override
	protected void onPreExecute() {
		sendStartMessage(QUERY_START_MESSAGE);
	}

	@Override
	protected ArrayList<ContactEntity> doInBackground(Cursor... params) {
		// 获取号码姓名键值对
		Map<String, String> nameMap = new HashMap<String, String>();
		Cursor cursor = params[0];
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
					// 合并联系人
					if (SharedPrefsUtil.getValue(mContext,
							Constant.MERGE_CONTACTS, false)) {
						if (!nameMap.containsKey(contactName)) {
							contactList.add(entity);
							nameMap.put(contactName, phoneNumber);
						}
					} else {
						if (!contactList.contains(entity)) {
							contactList.add(entity);
						}
					}
				}
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				e.printStackTrace();

			}
		}
		if (cursor != null) {
			cursor.close();
		}
		return contactList;
	}

	@Override
	protected void onPostExecute(ArrayList<ContactEntity> result) {
		sendEndMessage(QUERY_END_MESSAGE, result);
	}

	public static void startQueryContacts(Context context, Handler handler,
			Cursor cursor) {
		// new ContactsAsyncTask(context, handler).execute(cursor);
		new ContactsAsyncTask(context, handler).executeOnExecutor(
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
