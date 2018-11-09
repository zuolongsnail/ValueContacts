package com.valuestudio.contacts.base;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.valuestudio.contacts.R;
import com.valuestudio.contacts.utils.ContactsUtil;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,保存log
 * 
 * @description
 * @date 2014-8-10
 * @author valuestudio
 */
public class CrashHandler implements UncaughtExceptionHandler {
	/**
	 * 捕获异常日志开关
	 */
	private boolean handlerSwitch;
	/**
	 * 系统默认的UncaughtException处理类
	 */
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	private Context mContext;
	/**
	 * 用来存储设备信息和异常信息
	 */
	private Map<String, String> info = new HashMap<String, String>();
	private SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd-HH-mm-ss");

	private String logFileDir;

	public static CrashHandler getInstance(boolean handlerSwitch) {
		return new CrashHandler(handlerSwitch);
	}

	public CrashHandler(boolean handlerSwitch) {
		this.handlerSwitch = handlerSwitch;
	}

	public void init(Context context, String logFileDir) {
		mContext = context;
		this.logFileDir = logFileDir;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该重写的方法来处理
	 */
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handlerSwitch || !handleException(ex) && mDefaultHandler != null) {
			// 如果自定义的没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// 退出程序
			android.os.Process.killProcess(android.os.Process.myPid());
			// System.exit(1);
		}
	}

	public boolean handleException(Throwable ex) {
		if (ex == null)
			return false;
		new Thread() {
			public void run() {
				Looper.prepare();
				Toast.makeText(mContext, R.string.crash_error_exit,
						Toast.LENGTH_SHORT).show();
				Looper.loop();
			}
		}.start();
		ex.printStackTrace();
		// 收集设备参数信息
		// collectDeviceInfo(mContext);
		// 保存日志文件
		// saveCrashInfo2File(ex);
		return true;
	}

	public void collectDeviceInfo(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				info.put("versionName", versionName);
				info.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		Field[] fields = Build.class.getDeclaredFields();// 反射机制
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				info.put(field.getName(), field.get("").toString());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	private String saveCrashInfo2File(Throwable ex) {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : info.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\r\n");
		}
		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		// 写入文件流
		ex.printStackTrace(pw);
		// 控制台打印
		ex.printStackTrace();
		Throwable cause = ex.getCause();
		// 循环着把所有的异常信息写入writer中
		while (cause != null) {
			cause.printStackTrace(pw);
			cause = cause.getCause();
		}
		pw.close();
		String result = writer.toString();
		sb.append(result);
		// 保存文件
		// long timetamp = System.currentTimeMillis();
		String time = format.format(new Date());
		String fileName = "crash-" + time + ".log";
		if (ContactsUtil.isSDCardExist()) {
			File dir = new File(Environment.getExternalStorageDirectory()
					+ logFileDir);
			Log.i("CrashHandler", dir.toString());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			ContactsUtil.writeTextFile(new File(dir, fileName), sb.toString());
			return fileName;
		}
		return null;
	}

}