package com.valuestudio.contacts.utils;

import java.io.File;

import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.Contacts;

import com.valuestudio.contacts.R;

public class Constant {
	public static final String APP_PACKAGE_NAME = "com.valuestudio.contacts";
	public static final String APP_CLASS_NAME = "com.valuestudio.contacts.ui.ContactsFragmentActivity";
	/**
	 * 接管拨号标识
	 */
	public static final String RELAY_DIALER = "relay_dialer";
	/**
	 * 合并联系人标识
	 */
	public static final String MERGE_CONTACTS = "merge_contacts";
	/**
	 * 皮肤保存路径
	 */
	public static String SKIN_DIR = null;

	public static void initSkinDir(Context context) {

		SKIN_DIR = context.getFilesDir().getPath() + File.separator + "skin"
				+ File.separator;
		File file = new File(SKIN_DIR);
		if (!file.exists()) {
			file.mkdirs();
		}

	}

	public static final String SKIN_SETTINGS_KEY = "skin_settings_key";
	public static final String SKIN_DEFAULT = "default";
	public static final String SKIN_HELLOKITTY = "hellokitty";

	/**
	 * 前缀IP号码
	 */
	public static final String IP_CALL_NUM = "ip_call_num";

	/**
	 * 号码前缀
	 */
	public static class NumberFormat {
		public static final String IP_17951 = "17951";
		public static final String IP_17911 = "17911";
		public static final String PLUS_86 = "+86";
	}

	/**
	 * 子产品列表
	 */
	public static class Product {
		public static final String ID_001 = "001";
	}

	/**
	 * 系统存储路径
	 */
	public static final String APP_SD_PATH = "/MDialer";
	/**
	 * 系统日志保存目录
	 */
	public static final String LOG_FILE_DIR = "/log";
	/**
	 * 首次打开软件标识
	 */
	public static final String FIRST_LAUNCH = "first_launch";
	/**
	 * 读取联系人权限标识
	 */
	public static final String READ_CONTACT_PERM = "read_contact_perm";
	/**
	 * 读取通话记录权限标识
	 */
	public static final String READ_CALL_LOG_PERM = "read_call_log_perm";
	/**
	 * 魅族
	 */
	public static final String MEIZU = "meizu";
	/**
	 * 三星
	 */
	public static final String SAMSUNG = "samsung";
	/**
	 * 手机联系人uri
	 */
	public static final Uri PHONE_CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
	/**
	 * 查询结果
	 */
	public static final String QUERY_RESULT = "query_result";
	/**
	 * 允许输入的最大数字长度
	 */
	public static final int MAX_INPUT_LENGTH = 12;
	/**
	 * 一天的毫秒数：24*60*60*1000=86400000
	 */
	public static final long ONE_DAY_MS = 86400000;
	/**
	 * 查询电话数据库条件
	 */
	public static final String[] PHONE_PROJECTION = new String[] { Phone._ID,
			Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,
			Phone.SORT_KEY_PRIMARY, Phone.CONTACT_ID };
	/**
	 * 查询联系人数据库条件
	 */
	public static final String[] CONTACTS_PROJECTION = new String[] {
			Contacts._ID, Contacts.DISPLAY_NAME, Column.Contacts.PHONE_NUMBER,
			Contacts.PHOTO_ID, Contacts.SORT_KEY_PRIMARY };
	/**
	 * 联系人ID
	 */
	public static final int ID_INDEX = 0;
	/**
	 * 联系人显示名称
	 */
	public static final int NAME_INDEX = 1;
	/**
	 * 电话号码
	 */
	public static final int NUMBER_INDEX = 2;
	/**
	 * 头像ID
	 */
	public static final int PHOTO_ID_INDEX = 3;
	/**
	 * sort_key
	 */
	public static final int SORT_KEY_INDEX = 4;
	/**
	 * 联系人ID
	 */
	public static final int CONTACT_ID_INDEX = 5;

	public static final class CallLogIndex {
		/**
		 * 联系人ID
		 */
		public static final int ID_INDEX = 0;
		/**
		 * 联系人ID
		 */
		public static final int CONTACT_ID_INDEX = 1;
		/**
		 * 联系人显示名称
		 */
		public static final int NAME_INDEX = 1;
		/**
		 * 电话号码
		 */
		public static final int NUMBER_INDEX = 2;
		/**
		 * 归属地
		 */
		public static final int LOCATION_INDEX = 3;
		/**
		 * 通话类型
		 */
		public static final int TYPE_INDEX = 4;
		/**
		 * 时间
		 */
		public static final int DATE_INDEX = 5;
	}

	/**
	 * 联系人变化请求码
	 */
	public static final int CONTACTS_CHANGED_REQ_CODE = 200;
	/**
	 * 主题设置
	 */
	public static final String THEME_SET = "theme_set";
	/**
	 * 主题样式数组
	 */
	public static final int[] THEME_ARRAY = new int[] {
			R.style.Theme_Contacts_White, R.style.Theme_Contacts_Black,
			R.style.Theme_Contacts_Blue, R.style.Theme_Contacts_Purple,
			R.style.Theme_Contacts_Green, R.style.Theme_Contacts_Yellow,
			R.style.Theme_Contacts_Red, R.style.Theme_Contacts_Gold };
	/**
	 * 白色主题
	 */
	public static final int THEME_WHITE = 0;
	/**
	 * 黑色主题
	 */
	public static final int THEME_BLACK = 1;
	/**
	 * 蓝色主题
	 */
	public static final int THEME_BLUE = 2;
	/**
	 * 紫色主题
	 */
	public static final int THEME_PURPLE = 3;
	/**
	 * 绿色主题
	 */
	public static final int THEME_GREEN = 4;
	/**
	 * 黄色主题
	 */
	public static final int THEME_YELLOW = 5;
	/**
	 * 红色主题
	 */
	public static final int THEME_RED = 6;
	/**
	 * 土豪金主题
	 */
	public static final int THEME_GOLD = 7;
	/**
	 * HelloKitty 皮肤主题
	 */
	public static final int THEME_HELLOKITTY = 8;
	/**
	 * 避免重复点击
	 */
	public static final int CLICK_FINISH = 1;
	/**
	 * 查询成功码
	 */
	public static final int QUERY_SUCCESS = 1;
	/**
	 * 复制成功码
	 */
	public static final int COPY_SUCCESS = 2;
	/**
	 * 复制失败码
	 */
	public static final int COPY_FAILED = 3;
	/**
	 * 删除成功码
	 */
	public static final int DELETE_SUCCESS = 4;
	/**
	 * 更新进度条
	 */
	public static final int UPDATE_PROGRESS = 5;
	/**
	 * 正在复制到对话框
	 */
	public static final int COPY_DOING_DIALOG = 1;
	/**
	 * 正在删除联系人对话框
	 */
	public static final int DELETE_CONTACTS_DIALOG = 2;
	/**
	 * 进度条进度数标识
	 */
	public static final String PROGRESS_MAX = "progress_max";
	/**
	 * 进度条显示信息标识
	 */
	public static final String PROGRESS_MESSAGE = "progress_message";
	/**
	 * 进度条显示取消按钮
	 */
	public static final String PROGRESS_CANCEL = "progress_cancel";
	/**
	 * 意见反馈key
	 */
	public static final String FEEDBACK_KEY = "feedback_key";
	/**
	 * sim卡联系人uri
	 */
	public static final Uri SIM_CONTENT_URI = Uri.parse("content://icc/adn");
	/**
	 * sim卡联系人字符串
	 */
	public static final String SIM_CONTENT_URI_STR = "content://icc/adn/";
	/**
	 * 三星联系人是否是sim联系人字段
	 */
	public static final String IS_SIM = "is_sim";
	/**
	 * 三星查询联系人数据库条件
	 */
	public static final String[] PHONE_PROJECTION_SAMSUNG = new String[] {
			Phone._ID, Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,
			Phone.SORT_KEY_PRIMARY, Phone.CONTACT_ID, IS_SIM };
	/**
	 * sim卡联系人id字段
	 */
	public static final String SIM_CONTACT_ID = "_id";
	/**
	 * sim卡联系人姓名name字段
	 */
	public static final String SIM_CONTACT_NAME = "name";
	/**
	 * sim卡联系人姓名tag字段，用于sim卡的增删改操作
	 */
	public static final String SIM_CONTACT_TAG = "tag";
	/**
	 * sim卡联系人号码字段
	 */
	public static final String SIM_CONTACT_NUMBER = "number";
}
