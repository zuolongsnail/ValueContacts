package com.valuestudio.contacts.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.valuestudio.contacts.utils.Constant;

public class RelayDialerService extends Service {
	public static List<String> relayList = new ArrayList<String>();
	static {
		relayList.add("com.android.dialer");
		relayList.add("com.android.contacts");
	}
	/**
	 * 窗口管理
	 */
	private WindowManager windowManager;
	/**
	 * activity管理
	 */
	private ActivityManager activityManager;
	/**
	 * 全屏窗口
	 */
	private ScreenView screenView;
	/**
	 * 全屏窗口设置
	 */
	private WindowManager.LayoutParams wmParams;
	/**
	 * 点击的界面所在的包名
	 */
	private String clickPackageName;
	/**
	 * 桌面的包名列表
	 */
	private List<String> launcherList;

	@Override
	public void onCreate() {
		super.onCreate();

		windowManager = (WindowManager) getApplication().getSystemService(
				Context.WINDOW_SERVICE);
		activityManager = (ActivityManager) getApplication().getSystemService(
				Context.ACTIVITY_SERVICE);
		launcherList = getLauncherList();

		createScreenView();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void createScreenView() {
		wmParams = new WindowManager.LayoutParams();
		// 设置window type
		wmParams.type = LayoutParams.TYPE_SYSTEM_ERROR;
		// 设置图片格式，效果为背景透明
		wmParams.format = PixelFormat.RGBA_8888;
		// 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
		wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		// 调整悬浮窗显示的停靠位置为左侧置顶
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		// 以屏幕左上角为原点，设置x、y初始值，相对于gravity
		wmParams.x = 0;
		wmParams.y = 0;
		// 设置悬浮窗口长宽数据
		wmParams.width = 1;
		wmParams.height = 1;

		screenView = new ScreenView(getApplicationContext());
		windowManager.addView(screenView, wmParams);
	}

	@Override
	public void onDestroy() {
		// 移除全屏窗口
		if (screenView != null) {
			windowManager.removeView(screenView);
		}
		super.onDestroy();
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
		}
	};

	/**
	 * 获取桌面的包名列表
	 * 
	 * @description
	 * @return
	 * @date 2014-11-19
	 * @author zuolong
	 */
	private List<String> getLauncherList() {
		Intent homeIntent = new Intent(Intent.ACTION_MAIN);
		homeIntent.addCategory(Intent.CATEGORY_HOME);

		ArrayList<String> launcherList = new ArrayList<String>();
		Iterator<ResolveInfo> localIterator = getApplicationContext()
				.getPackageManager()
				.queryIntentActivities(homeIntent,
						PackageManager.MATCH_DEFAULT_ONLY).iterator();
		while (localIterator.hasNext()) {
			ResolveInfo localResolveInfo = (ResolveInfo) localIterator.next();
			try {
				launcherList.add(localResolveInfo.activityInfo.packageName);
			} catch (Exception localException) {
				localException.printStackTrace();
			}
		}
		return launcherList;
	}

	/**
	 * 全屏窗口
	 * 
	 * @description
	 * @date 2014-11-19
	 * @author zuolong
	 */
	class ScreenView extends View {
		public ScreenView(Context context) {
			super(context);
		}

		@Override
		public boolean onTouchEvent(MotionEvent paramMotionEvent) {
			clickPackageName = getTopActivityPkgName(getApplicationContext());
			if (launcherList.contains(clickPackageName)) {
				handler.post(new LaunchListener());
			}

			return super.onTouchEvent(paramMotionEvent);
		}
	}

	/**
	 * 启动监听
	 * 
	 * @description
	 * @date 2014-11-19
	 * @author zuolong
	 */
	class LaunchListener implements Runnable {

		@Override
		public void run() {
			try {
				long start = System.currentTimeMillis();

				while (true) {
					String packageName = getTopActivityPkgName(getApplicationContext());
					if (relayList.contains(packageName)
							&& !clickPackageName.equals(packageName)) {
						activityManager.killBackgroundProcesses(packageName);
						Intent intent = new Intent();
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.setComponent(new ComponentName(
								Constant.APP_PACKAGE_NAME,
								Constant.APP_CLASS_NAME));
						getApplicationContext().startActivity(intent);
						break;
					}
					Thread.sleep(10);
					if (System.currentTimeMillis() - start > 500) {
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取当前运行的activity包名
	 * 
	 * @description
	 * @param context
	 * @return
	 * @date 2016-1-21
	 * @author valuestudio
	 */
	public static String getTopActivityPkgName(Context context) {
		if (context == null) {
			return null;
		}
		ActivityManager am = (ActivityManager) context
				.getSystemService(Activity.ACTIVITY_SERVICE);
		if (am == null) {
			return null;
		}
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {// 5.0以后不适用
			List<RunningTaskInfo> tasks = am.getRunningTasks(1);
			if (tasks != null && !tasks.isEmpty()) {
				ComponentName componentName = tasks.get(0).topActivity;
				if (componentName != null) {
					return componentName.getPackageName();
				}
			}
		} else if (new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
				.resolveActivity(context.getPackageManager()) == null) {
			RunningAppProcessInfo currentInfo = null;
			Field field = null;
			int START_TASK_TO_FRONT = 2;
			String pkgName = null;
			try {
				field = RunningAppProcessInfo.class
						.getDeclaredField("processState");
			} catch (Exception e) {
				return null;
			}
			List<RunningAppProcessInfo> appList = am.getRunningAppProcesses();
			if (appList == null || appList.isEmpty()) {
				return null;
			}
			for (RunningAppProcessInfo app : appList) {
				if (app != null
						&& app.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					Integer state = null;
					try {
						state = field.getInt(app);
					} catch (Exception e) {
						return null;
					}
					if (state != null && state == START_TASK_TO_FRONT) {
						currentInfo = app;
						break;
					}
				}
			}
			if (currentInfo != null) {
				pkgName = currentInfo.processName;
			}
			return pkgName;
		} else {// 根据使用情况权限
			String pkgName = null;
			UsageStatsManager mUsageStatsManager = (UsageStatsManager) context
					.getSystemService("usagestats");
			long time = System.currentTimeMillis();
			List<UsageStats> stats = mUsageStatsManager.queryUsageStats(
					UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);
			if (stats != null) {
				SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
				for (UsageStats usageStats : stats) {
					mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
				}
				if (mySortedMap != null && !mySortedMap.isEmpty()) {
					pkgName = mySortedMap.get(mySortedMap.lastKey())
							.getPackageName();
				}
			}
			return pkgName;
		}
		return null;
	}

}
