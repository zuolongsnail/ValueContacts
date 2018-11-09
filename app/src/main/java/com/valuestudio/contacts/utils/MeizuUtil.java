package com.valuestudio.contacts.utils;

import java.util.ArrayList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;

import com.valuestudio.contacts.entity.ContactEntity;

public class MeizuUtil {
	public static final String TAG = "MeizuUtil";
	/**
	 * 魅族软件中心
	 */
	public static final String MSTORE_URI = "mstore:http://app.meizu.com/phone/apps/";
	/**
	 * 魅族应用ID
	 */
	public static final String APPIDENTIFY = "2bcb7324d243491d9fd1adde0ff90288";
	/**
	 * 随变铃声应用ID
	 */
	public static final String RANDTONE_APPIDENTIFY = "5789ede543da4108abdc86ff3652ea86";
	/**
	 * flat icon主题ID
	 */
	public static final String FLAT_ICON_APPIDENTIFY = "3de323b5962947208002130cf11fc107";
	/**
	 * 是否为正式版key
	 */
	public static final String LICENSE_KEY = "license_key";
	/**
	 * 魅族账号
	 */
	public static final String MEIZU_ACCOUNT = "com.meizu.account";
	/**
	 * flyme账号
	 */
	private static Account[] accounts;

	/**
	 * 插入单个sim卡联系人
	 * 
	 * @description
	 * @param entity
	 * @date 2014-2-11
	 * @author valuestudio
	 */
	public static boolean copy2Phone(Context context, ContactEntity entity) {
		// 账户类型
		Object type = null;
		// 账户名称
		Object name = null;
		if (accounts == null) {
			// 获取账号信息
			AccountManager am = AccountManager.get(context);
			accounts = am.getAccountsByType(MEIZU_ACCOUNT);
			if (accounts != null && accounts.length > 0) {
				type = accounts[0].type;
				name = accounts[0].name;
			}
		}
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.RawContacts.CONTENT_URI)
				.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, type)
				.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, name)
				.build());
		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
				.withValue(
						ContactsContract.Data.MIMETYPE,
						ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
				.withValue(
						ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
						entity.getName()).build());
		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
				.withValue(
						ContactsContract.Data.MIMETYPE,
						ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
				.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
						entity.getNumber())
				.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
						ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
				.build());
		try {
			context.getContentResolver().applyBatch(ContactsContract.AUTHORITY,
					ops);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 插入单个sim卡联系人
	 * 
	 * @description
	 * @param entity
	 * @date 2014-2-11
	 * @author valuestudio
	 */
	public static boolean copy2Sim(Context context, ContactEntity entity) {
		ContentValues values = new ContentValues();
		// sim存储时姓名长度处理 for meizu
		String name = entity.getName();
		if (ToPinYin.isChinese(name) && name.length() > 6) {
			name = name.substring(0, 6);
		}
		values.put(Constant.SIM_CONTACT_NAME, name);
		values.put(Constant.SIM_CONTACT_NUMBER, entity.getNumber());
		final Uri uri = Constant.SIM_CONTENT_URI.buildUpon()
				.appendQueryParameter("request_by_id", "true").build();
		Uri insertUri = context.getContentResolver().insert(uri, values);
		if (insertUri != null) {
			// 获取执行结果
			String insertUriStr = insertUri.toString();
			String numberStr = insertUriStr.substring(insertUriStr
					.indexOf(Constant.SIM_CONTENT_URI_STR)
					+ Constant.SIM_CONTENT_URI_STR.length());
			try {
				int number = Integer.parseInt(numberStr);
				if (number > 0) {
					return true;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	/**
	 * 批量删除sim卡联系人
	 * 
	 * @description
	 * @param deleteIdArray
	 *            将要删除的联系人id数组
	 * @return
	 * @date 2014-2-12
	 * @author valuestudio
	 */
	public static int deleteFromSim(Context context, String[] deleteIdArray) {
		final Uri uri = Constant.SIM_CONTENT_URI.buildUpon()
				.appendQueryParameter("request_by_id", "true").build();
		return context.getContentResolver().delete(uri, null, deleteIdArray);
	}

}
