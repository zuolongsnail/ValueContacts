package com.valuestudio.contacts.base;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.meizu.flyme.reflect.StatusBarProxy;
import com.valuestudio.contacts.R;
import com.valuestudio.contacts.utils.Constant;
import com.valuestudio.contacts.utils.ContactsUtil;
import com.valuestudio.contacts.utils.SharedPrefsUtil;
import com.valuestudio.contacts.widget.SystemBarTintManager;

public abstract class BaseFragmentActivity extends FragmentActivity {

	protected Context mContext;

	protected ActionBar mActionBar;
	/**
	 * 手势操作
	 */
	protected GestureDetector flingGesture;
	private static final int MIN_DISTANCE_FOR_FLING = 50;// dp
	private static final int MIN_FLING_VELOCITY = 200;// dp
	/**
	 * 滑动最小距离
	 */
	private int minDistance;
	/**
	 * 滑动最小速度
	 */
	private int minVelocity;
	/**
	 * Y上滑动控制，防止滑动ListView时，导致回退
	 */
	private int maxDistanceY;
	/**
	 * 沉浸式状态栏
	 */
	protected SystemBarTintManager tintManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 更改状态栏颜色主题为白色
		StatusBarProxy.setStatusBarDarkIcon(getWindow(), false);
		int themeSetValue = SharedPrefsUtil.getValue(this, Constant.THEME_SET,
				Constant.THEME_WHITE);
		if (themeSetValue != Constant.THEME_WHITE) {
			setTheme(Constant.THEME_ARRAY[themeSetValue]);
		} else {
			// 更改状态栏颜色主题为黑色
			StatusBarProxy.setStatusBarDarkIcon(getWindow(), true);
			setTheme(R.style.Theme_Contacts_White);
		}
		super.onCreate(savedInstanceState);
		mContext = this;
		// 添加Activity到堆栈
		AppManager.getAppManager().addActivity(this);
		initActionBar();
		initFlingGesture();
		initSystemBarTintManager();
		themeSet();
	}

	/**
	 * 初始化沉浸式状态栏
	 * 
	 * @description
	 * @date 2014-12-2
	 * @author valuestudio
	 */
	private void initSystemBarTintManager() {
		// 初始化沉浸式
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(true);
		}
		tintManager = new SystemBarTintManager(this);
		tintManager.setStatusBarTintEnabled(true);
	}

	@TargetApi(19)
	private void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}

	private void themeSet() {
		TypedArray a = obtainStyledAttributes(null,
				R.styleable.ContactsAppView, R.attr.ContactsAppStyle, 0);
		mActionBar.setSplitBackgroundDrawable(getResources().getDrawable(
				a.getResourceId(R.styleable.ContactsAppView_themeColor,
						R.color.theme_black)));
		mActionBar.setBackgroundDrawable(getResources().getDrawable(
				a.getResourceId(R.styleable.ContactsAppView_themeColor,
						R.color.theme_black)));
		// 设置状态栏颜色
		tintManager.setStatusBarTintResource(a.getResourceId(
				R.styleable.ContactsAppView_themeColor, R.color.theme_black));
//		// 设置SmartBar返回按键图片
//		ContactsUtil.setBackIcon(
//				getActionBar(),
//				getResources().getDrawable(
//						a.getResourceId(
//								R.styleable.ContactsAppView_smartBarBackIcon,
//								R.drawable.ic_tab_back_white)));
		a.recycle();
	}

	/**
	 * 初始化ActionBar
	 */
	protected void initActionBar() {
		mActionBar = getActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		// // 设置标题actionbar背景
		// mActionBar.setBackgroundDrawable(getResources().getDrawable(
		// R.color.theme));
		// // 设置底部actionbar背景
		// // skin
		// mActionBar.setSplitBackgroundDrawable(getResources().getDrawable(
		// R.color.theme));
		mActionBar.setIcon(R.drawable.ic_launcher);
		// 隐藏title栏
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
	}

	/**
	 * 初始化手势相关参数
	 */
	private void initFlingGesture() {
		flingGesture = new GestureDetector(this, new FlingGestureListener());
		final float density = getResources().getDisplayMetrics().density;
		minDistance = (int) density * MIN_DISTANCE_FOR_FLING;
		maxDistanceY = minDistance;
		minVelocity = (int) density * MIN_FLING_VELOCITY;
	}

	public void initViews() {
	}

	/**
	 * 绑定数据
	 */
	public void bindData() {
	}

	/**
	 * 一律使用此启动方式，添加左侧滑出动画
	 */
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		// 回到首页时不显示动画
		// overridePendingTransition(R.anim.pt__push_left_in,
		// R.anim.pt__push_left_out);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		// overridePendingTransition(R.anim.pt__push_left_in,
		// R.anim.pt__push_left_out);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		return flingGesture.onTouchEvent(event);
	}

	public boolean dispatchTouchEvent(MotionEvent ev) {
		super.dispatchTouchEvent(ev);
		return flingGesture.onTouchEvent(ev);
	}

	protected class FlingGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {

			if ((e1.getX() - e2.getX()) > minDistance
					&& Math.abs(velocityX) > minVelocity) { // 左滑

			} else if ((e2.getX() - e1.getX()) > minDistance
					&& Math.abs(velocityX) > minVelocity
					&& Math.abs(e2.getY() - e1.getY()) < maxDistanceY) { // 右滑
				// 向右退出
				onBackPressed();
			}
			return false;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// 退出动画
		overridePendingTransition(R.anim.pt__push_right_in,
				R.anim.pt__push_right_out);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 结束Activity&从堆栈中移除
		AppManager.getAppManager().finishActivity(this);
	}

}
