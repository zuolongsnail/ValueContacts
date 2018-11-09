package com.valuestudio.contacts.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import com.valuestudio.contacts.R;
import com.valuestudio.contacts.base.AppManager;

public class ContactsUtil {

	public final static ThreadLocal<SimpleDateFormat> yyyy_MM_dd_HH_mm = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm");
		}
	};
	public final static ThreadLocal<SimpleDateFormat> yyyy_MM_dd = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};

	public final static ThreadLocal<SimpleDateFormat> HH_mm = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("HH:mm");
		}
	};
	public final static ThreadLocal<SimpleDateFormat> MM_dd_HH_mm = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("MM月dd日 HH:mm");
		}
	};

	/**
	 * 将字符串转位日期类型
	 * 
	 * @description
	 * @param formater
	 * @param dateStr
	 * @return
	 * @date 2014-7-28
	 * @author valuestudio
	 */
	public static Date toDate(ThreadLocal<SimpleDateFormat> formater,
			String dateStr) {
		try {
			return formater.get().parse(dateStr);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 将日期类型转换为制定格式的字符串
	 * 
	 * @param dateFormat
	 * @param date
	 * @return
	 */
	public static String toDateString(ThreadLocal<SimpleDateFormat> dateFormat,
			Object date) {
		return dateFormat.get().format(date);
	}

	/**
	 * 判断日期是否是昨天
	 * 
	 * @description
	 * @param theDate
	 * @return
	 * @throws ParseException
	 * @date 2014-7-28
	 * @author valuestudio
	 */
	public static boolean isYesterday(long theDate) {
		long dateTime = new Date().getTime();
		long todayMs = dateTime - dateTime % Constant.ONE_DAY_MS;
		if ((todayMs - theDate) > 0
				&& (todayMs - theDate) <= Constant.ONE_DAY_MS) {
			return true;
		}
		return false;
	}

	/**
	 * 期是否是今天
	 * 
	 * @description
	 * @param theDate
	 * @return
	 * @date 2014-12-3
	 * @author valuestudio
	 */
	public static boolean isToday(long theDate) {
		long dateTime = new Date().getTime();
		long todayMs = dateTime - dateTime % 86400000;
		if (theDate > todayMs) {
			return true;
		}
		return false;
	}

	public static String toTimeString(Context context, long duration) {
		int h = (int) duration / 3600;
		int m = ((int) duration - 3600 * h) / 60;
		int s = (int) duration - 3600 * h - 60 * m;
		if (h > 0) {
			return context.getString(R.string.duration_hms_format, h, m, s);
		} else {
			return context.getString(R.string.duration_ms_format, m, s);
		}
	}

	/**
	 * 判断是否为魅族手机
	 * 
	 * @description
	 * @return
	 * @date 2014-2-17
	 * @author valuestudio
	 */
	public static boolean isMeizuPhone() {
		if (android.os.Build.MANUFACTURER.equalsIgnoreCase(Constant.MEIZU)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断是否为三星手机
	 * 
	 * @description
	 * @return
	 * @date 2014-2-17
	 * @author valuestudio
	 */
	public static boolean isSamsungPhone() {
		if (android.os.Build.MANUFACTURER.equalsIgnoreCase(Constant.SAMSUNG)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断是否是飞行模式
	 * 
	 * @description
	 * @param context
	 * @return
	 * @date 2014-2-23
	 * @author valuestudio
	 */
	public static boolean isAirplaneMode(Context context) {
		int isAirplaneMode = Settings.System.getInt(
				context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON,
				0);
		return (isAirplaneMode == 1) ? true : false;
	}

	/**
	 * 获取是否第一次启动标识
	 * 
	 * @description
	 * @param context
	 * @return
	 * @date 2014-8-3
	 * @author valuestudio
	 */
	public static boolean getFirstLaunch(Context context) {
		return SharedPrefsUtil.getValue(context, Constant.FIRST_LAUNCH, true);
	}

	/**
	 * 设置第一次启动标识
	 * 
	 * @description
	 * @param context
	 * @param first
	 * @date 2014-8-3
	 * @author valuestudio
	 */
	public static void setFirstLaunch(Context context, boolean first) {
		SharedPrefsUtil.putValue(context, Constant.FIRST_LAUNCH, first);
	}

	/**
	 * 获取是否取得读取联系人权限标识
	 * 
	 * @description
	 * @param context
	 * @return
	 * @date 2014-8-3
	 * @author valuestudio
	 */
	public static boolean getReadContactPerm(Context context) {
		return SharedPrefsUtil.getValue(context, Constant.READ_CONTACT_PERM,
				false);
	}

	/**
	 * 设置是否取得读取联系人权限标识
	 * 
	 * @description
	 * @param context
	 * @param perm
	 * @date 2014-8-3
	 * @author valuestudio
	 */
	public static void setReadContactPerm(Context context, boolean perm) {
		SharedPrefsUtil.putValue(context, Constant.READ_CONTACT_PERM, perm);
	}

	/**
	 * 获取是否取得读取通话记录权限标识
	 * 
	 * @description
	 * @param context
	 * @return
	 * @date 2014-8-3
	 * @author valuestudio
	 */
	public static boolean getReadCallLogPerm(Context context) {
		return SharedPrefsUtil.getValue(context, Constant.READ_CALL_LOG_PERM,
				false);
	}

	/**
	 * 设置是否取得读取通话记录权限标识
	 * 
	 * @description
	 * @param context
	 * @param perm
	 * @date 2014-8-3
	 * @author valuestudio
	 */
	public static void setReadCallLogPerm(Context context, boolean perm) {
		SharedPrefsUtil.putValue(context, Constant.READ_CALL_LOG_PERM, perm);
	}

	/**
	 * 检查某一个应用是否安装
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean checkApkExist(Context context, String packageName) {
		if (TextUtils.isEmpty(packageName)) {
			return false;
		}
		try {
			ApplicationInfo info = context.getPackageManager()
					.getApplicationInfo(packageName,
							PackageManager.GET_UNINSTALLED_PACKAGES);
			if (info != null) {
				return true;
			} else {
				return false;
			}
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	/**
	 * 将文本内容写入文件 *
	 * 
	 * @param file
	 * @param str
	 * @throws IOException
	 */
	public static void writeTextFile(File file, String str) {
		FileOutputStream fileOutputStream = null;
		try {
			if (!file.exists() || !file.isFile()) {
				file.createNewFile();
			}
			fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(str.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 检查SDCard是否存在
	 * 
	 * @return
	 */
	public static boolean isSDCardExist() {
		String state = Environment.getExternalStorageState();
		boolean isSDCardExist = state.equals(Environment.MEDIA_MOUNTED);
		return isSDCardExist;
	}

	/**
	 * 修改SmartBar的返回按钮图片
	 * 
	 * @description
	 * @param actionbar
	 * @param backIcon
	 * @date 2014-8-24
	 * @author valuestudio
	 */
	public static void setBackIcon(android.app.ActionBar actionbar,
			Drawable backIcon) {
		try {
			Method method = Class.forName("android.app.ActionBar").getMethod(
					"setBackButtonDrawable", new Class[] { Drawable.class });
			try {
				method.invoke(actionbar, backIcon);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (NotFoundException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 重启应用
	 * 
	 * @description
	 * @date 2014-8-20
	 * @author valuestudio
	 */
	public static void restartApplication(Context context) {
		AppManager.getAppManager().finishAllActivity();
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setComponent(new ComponentName(Constant.APP_PACKAGE_NAME,
				Constant.APP_CLASS_NAME));
		context.startActivity(intent);
	}

	/**
	 * 检测service是否开启
	 * 
	 * @description
	 * @param context
	 * @param serviceName
	 * @return
	 * @date 2014-11-19
	 * @author zuolong
	 */
	public static boolean checkServiceRunning(Context context,
			String serviceName) {
		boolean isRunning = false;
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceName.equals(service.service.getClassName())) {
				isRunning = true;
				return isRunning;
			}
		}
		return isRunning;
	}

	/**
	 * 显示对话框
	 * 
	 * @description
	 * @param context
	 * @param title
	 * @param message
	 * @date 2014-2-16
	 * @author valuestudio
	 */
	public static void showAlertDialog(Context context, String title,
			String message) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(title)
				.setIcon(R.drawable.ic_dialog_alert)
				.setMessage(message)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).create();
		dialog.show();
	}

	/**
	 * 粘贴号码正则表达式
	 */
	public final static Pattern PASTE_NUMBER_PATTERN = Pattern
			.compile("[0-9*#,+;]+");

	/**
	 * 根据指定正则判断字符是否合法
	 * 
	 * @description
	 * @param str
	 *            字符串
	 * @param regex
	 *            指定正则
	 * @return
	 * @date 2014-5-15
	 * @author zuolong
	 */
	public static boolean isMatches(String str, Pattern regex) {
		if (str == null || str.trim().length() == 0) {
			return false;
		}
		return regex.matcher(str).matches();
	}

	/**
	 * 获取通话时间字符串
	 * 
	 * @description
	 * @param theDate
	 * @return
	 * @date 2015-7-10
	 * @author zuolong
	 */
	public static String getCallLogDateStr(long theDate) {
		String dateStr = "";
		Calendar now = Calendar.getInstance();
		long todayMs = (now.get(Calendar.HOUR_OF_DAY) * 3600
				+ now.get(Calendar.MINUTE) * 60 + now.get(Calendar.SECOND)) * 1000;// 从今天零点计算到当前时间的毫秒数
		long nowMs = now.getTimeInMillis();
		if (nowMs - theDate < todayMs) {// 今天
			dateStr = ContactsUtil.toDateString(ContactsUtil.HH_mm, new Date(
					theDate));
		} else if (nowMs - theDate < (todayMs + Constant.ONE_DAY_MS)) {// 昨天
			dateStr = "昨天 "
					+ ContactsUtil.toDateString(ContactsUtil.HH_mm, new Date(
							theDate));
		} else {// 更早
			dateStr = ContactsUtil.toDateString(ContactsUtil.MM_dd_HH_mm,
					new Date(theDate));
		}
		return dateStr;
	}

	public static AlertDialog.Builder makeDialogBuilderByTheme(Context context,
			int value) {
		AlertDialog.Builder builder;
		int style = R.style.Theme_Contacts_White;
		switch (value) {
		case Constant.THEME_WHITE:
			style = R.style.Theme_Contacts_White;
			break;
		case Constant.THEME_BLACK:
			style = R.style.Theme_Contacts_Black;
			break;
		case Constant.THEME_BLUE:
			style = R.style.Theme_Contacts_Blue;
			break;
		case Constant.THEME_PURPLE:
			style = R.style.Theme_Contacts_Purple;
			break;
		case Constant.THEME_GREEN:
			style = R.style.Theme_Contacts_Green;
			break;
		case Constant.THEME_YELLOW:
			style = R.style.Theme_Contacts_Yellow;
			break;
		case Constant.THEME_RED:
			style = R.style.Theme_Contacts_Red;
			break;
		case Constant.THEME_GOLD:
			style = R.style.Theme_Contacts_Gold;
			break;
		default:
			break;
		}
		builder = new AlertDialog.Builder(context, style);
		return builder;
	}

	/**
	 * 拨打电话
	 * 
	 * @description
	 * @param context
	 * @param phoneNumber
	 * @date 2016-1-10
	 * @author valuestudio
	 */
	public static void call(Context context, String phoneNumber) {
		Uri uri = Uri.parse("tel:" + phoneNumber);
		Intent intent = new Intent(Intent.ACTION_CALL, uri);
		context.startActivity(intent);
	}

	/**
	 * 
	 * 检测该包名所对应的应用是否存在
	 * 
	 * @param packageName
	 * 
	 * @return
	 */
	public static boolean checkPackage(Context context, String packageName) {
		if (TextUtils.isEmpty(packageName)) {
			return false;
		}
		try {
			context.getPackageManager().getApplicationInfo(packageName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

}
