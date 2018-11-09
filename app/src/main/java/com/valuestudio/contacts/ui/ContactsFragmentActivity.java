package com.valuestudio.contacts.ui;

import java.util.ArrayList;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.meizu.flyme.reflect.ActionBarProxy;
import com.valuestudio.contacts.R;
import com.valuestudio.contacts.adapter.FragmentAdapter;
import com.valuestudio.contacts.base.BaseFragmentActivity;
import com.valuestudio.contacts.entity.FragmentItem;

public class ContactsFragmentActivity extends BaseFragmentActivity implements
		OnClickListener {
	protected static final int CONTACTS_ID = 0;
	protected static final int DIALER_ID = 1;
	protected static final int SETTINGS_ID = 2;
	protected static final int MORE_ID = 3;
	/**
	 * 底部菜单
	 */
	private LinearLayout mBottomLayout;
	// 四个Tab，每个Tab包含一个按钮
	private LinearLayout mTabContacts;
	private LinearLayout mTabDialer;
	private LinearLayout mTabSettings;
	private LinearLayout mTabMore;
	// 四个按钮
	private ImageButton mContactsImg;
	private ImageButton mDialerImg;
	private ImageButton mSettingsImg;
	private ImageButton mMoreImg;

	/************************** 主题资源 ****************************/
	// 联系人icon资源id
	protected static int sbContactsIconResId;
	// 拨号键icon
	protected static int sbDialIconResId;
	// 拨号键显示icon
	protected static int sbDialDisplayIconResId;
	// 设置键icon
	protected static int sbSettingsIconResId;
	// 更多键icon
	protected static int sbMoreIconResId;
	// 按下联系人icon资源id
	protected static int sbContactsPressIconResId;
	// 按下拨号键icon
	protected static int sbDialPressIconResId;
	// 按下拨号键显示icon
	protected static int sbDialDisplayPressIconResId;
	// 按下设置键icon
	protected static int sbSettingsPressIconResId;
	/**
	 * 标签页视图
	 */
	private ViewPager viewPager;
	private FragmentAdapter homeAdapter;
	private ArrayList<FragmentItem> fragmentItems;
	/**
	 * 联系人
	 */
	private ContactsFragment contactsFragment;
	/**
	 * 拨号
	 */
	private DialerFragment dialerFragment;
	/**
	 * 设置
	 */
	private SettingsFragment settingsFragment;
	/**
	 * 是否回到桌面
	 */
	private boolean goLauncher;

	/**
	 * 主界面
	 */
	private LinearLayout mainBg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
		bindData();
	}

	@Override
	public void initViews() {
		mActionBar.hide();

		// mainBg = findViewById(R.id.main_bg);
		// // 获取壁纸管理器
		// WallpaperManager wallpaperManager = WallpaperManager
		// .getInstance(mContext);
		// // 获取当前壁纸
		// Drawable wallpaperDrawable = wallpaperManager.getDrawable();
		// mainBg.setBackgroundDrawable(wallpaperDrawable);

		// 初始化手势监听，首页不添加向右滑动时退出的功能
		flingGesture = new GestureDetector(this, new SimpleOnGestureListener());

		mBottomLayout = (LinearLayout) findViewById(R.id.bottom_layout);
		if (!ActionBarProxy.hasSmartBar()) {
			mBottomLayout.setVisibility(View.VISIBLE);
			TypedArray a = obtainStyledAttributes(null,
					R.styleable.ContactsAppView, R.attr.ContactsAppStyle, 0);
			sbContactsIconResId = a.getResourceId(
					R.styleable.ContactsAppView_smartBarContactsIcon,
					R.drawable.ic_tab_contacts_n);
			sbDialIconResId = a.getResourceId(
					R.styleable.ContactsAppView_smartBarDialerIcon,
					R.drawable.ic_tab_dialer_n);
			sbDialDisplayIconResId = a.getResourceId(
					R.styleable.ContactsAppView_smartBarDialerDisplayIcon,
					R.drawable.ic_tab_dialer_display_n);
			sbSettingsIconResId = a.getResourceId(
					R.styleable.ContactsAppView_smartBarSettingsIcon,
					R.drawable.ic_tab_settings_n);
			sbMoreIconResId = a.getResourceId(
					R.styleable.ContactsAppView_smartBarMoreIcon,
					R.drawable.ic_tab_more_n);
			sbContactsPressIconResId = a.getResourceId(
					R.styleable.ContactsAppView_smartBarContactsPressIcon,
					R.drawable.ic_tab_contacts_p);
			sbDialPressIconResId = a.getResourceId(
					R.styleable.ContactsAppView_smartBarDialerPressIcon,
					R.drawable.ic_tab_dialer_p);
			sbDialDisplayPressIconResId = a.getResourceId(
					R.styleable.ContactsAppView_smartBarDialerDisplayPressIcon,
					R.drawable.ic_tab_dialer_display_p);
			sbSettingsPressIconResId = a.getResourceId(
					R.styleable.ContactsAppView_smartBarSettingsPressIcon,
					R.drawable.ic_tab_settings_p);
			mTabContacts = (LinearLayout) findViewById(R.id.id_tab_contacts);
			mTabDialer = (LinearLayout) findViewById(R.id.id_tab_dialer);
			mTabSettings = (LinearLayout) findViewById(R.id.id_tab_settings);
			mTabMore = (LinearLayout) findViewById(R.id.id_tab_more);
			mTabContacts.setOnClickListener(this);
			mTabDialer.setOnClickListener(this);
			mTabSettings.setOnClickListener(this);
			mTabMore.setOnClickListener(this);
			mBottomLayout.setBackground(getResources().getDrawable(
					a.getResourceId(R.styleable.ContactsAppView_themeColor,
							R.color.theme_black)));
			a.recycle();
			// 初始化四个按钮
			mContactsImg = (ImageButton) findViewById(R.id.id_tab_contacts_img);
			mDialerImg = (ImageButton) findViewById(R.id.id_tab_dialer_img);
			mSettingsImg = (ImageButton) findViewById(R.id.id_tab_settings_img);
			mMoreImg = (ImageButton) findViewById(R.id.id_tab_more_img);
			mContactsImg.setBackgroundResource(sbContactsIconResId);
			mDialerImg.setBackgroundResource(sbDialPressIconResId);
			mSettingsImg.setBackgroundResource(sbSettingsIconResId);
			mMoreImg.setBackgroundResource(sbMoreIconResId);
		} else {
			mBottomLayout.setVisibility(View.GONE);
		}

		mainBg = (LinearLayout) findViewById(R.id.main_bg);
		LinearLayout view_pager = (LinearLayout) LayoutInflater.from(mContext)
				.inflate(R.layout.view_pager, null);
		viewPager = (ViewPager) view_pager.findViewById(R.id.pager);
		if (!ActionBarProxy.hasSmartBar()) {
			viewPager.setOnPageChangeListener(onPageChangeListener);
		}
		LinearLayout.LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			params.topMargin = tintManager.getConfig().getStatusBarHeight();
			if (ActionBarProxy.hasSmartBar()) {
				params.bottomMargin = tintManager.getConfig()
						.getActionBarHeight();
			}
		}
		mainBg.addView(view_pager, params);

		// 设置默认初始化标签页数量
		viewPager.setOffscreenPageLimit(3);

		fragmentItems = new ArrayList<FragmentItem>();
		// 联系人
		contactsFragment = ContactsFragment.newInstance();
		FragmentItem contactsFragmentItem = new FragmentItem(
				getString(R.string.contacts), contactsFragment);
		fragmentItems.add(contactsFragmentItem);
		// 拨号
		dialerFragment = DialerFragment.newInstance();
		FragmentItem dialerFragmentItem = new FragmentItem(
				getString(R.string.dialer), dialerFragment);
		fragmentItems.add(dialerFragmentItem);
		// 设置
		settingsFragment = SettingsFragment.newInstance();
		FragmentItem settingsFragmentItem = new FragmentItem(
				getString(R.string.settings), settingsFragment);
		fragmentItems.add(settingsFragmentItem);
		homeAdapter = new FragmentAdapter(getSupportFragmentManager(),
				fragmentItems);
		viewPager.setAdapter(homeAdapter);
		viewPager.setCurrentItem(DIALER_ID, false);
	}

	/**
	 * 设置当前选中的选项卡
	 * 
	 * @description
	 * @param item
	 * @date 2015-6-26
	 * @author zuolong
	 */
	public void setCurrentItem(int item) {
		viewPager.setCurrentItem(item, false);
	}

	OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {

		/**
		 * ViewPage左右滑动时
		 */
		@Override
		public void onPageSelected(int position) {
			int currentItem = viewPager.getCurrentItem();
			switch (currentItem) {
			case 0:
				resetImg();
				mContactsImg.setBackgroundResource(sbContactsPressIconResId);
				break;
			case 1:
				resetImg();
				mDialerImg.setBackgroundResource(sbDialPressIconResId);
				break;
			case 2:
				resetImg();
				mSettingsImg.setBackgroundResource(sbSettingsPressIconResId);
				break;
			case 3:
				Intent intent = new Intent(mContext, MoreActivity.class);
				startActivity(intent);
				break;
			default:
				break;
			}
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {

		}

		@Override
		public void onPageScrollStateChanged(int state) {

		}
	};

	/**
	 * 导航点击切换
	 */
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.id_tab_contacts:
			viewPager.setCurrentItem(CONTACTS_ID);
			break;
		case R.id.id_tab_dialer:
			if (viewPager.getCurrentItem() == DIALER_ID) {
				if (dialerFragment.getDialpadVisible()) {
					dialerFragment.setDialpadVisible(false);
					setDialerImg(sbDialDisplayPressIconResId);
				} else {
					dialerFragment.setDialpadVisible(true);
					setDialerImg(sbDialPressIconResId);
				}
			} else {
				viewPager.setCurrentItem(DIALER_ID);
			}
			break;
		case R.id.id_tab_settings:
			viewPager.setCurrentItem(SETTINGS_ID);
			break;
		case R.id.id_tab_more:
			Intent intent = new Intent(mContext, MoreActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	public void setContactsImg(int iconRes) {
		mContactsImg.setBackgroundResource(iconRes);
	}

	public void setDialerImg(int iconRes) {
		mDialerImg.setBackgroundResource(iconRes);
	}

	public void setSettingsImg(int iconRes) {
		mSettingsImg.setBackgroundResource(iconRes);
	}

	/**
	 * 重置图标
	 */
	private void resetImg() {
		mContactsImg.setBackgroundResource(sbContactsIconResId);
		mDialerImg.setBackgroundResource(sbDialIconResId);
		mSettingsImg.setBackgroundResource(sbSettingsIconResId);
		mMoreImg.setBackgroundResource(sbMoreIconResId);
	}

	public void bindData() {
	}

	@Override
	protected void onResume() {
		if (goLauncher) {
			viewPager.setCurrentItem(DIALER_ID, false);
			goLauncher = false;
		}
		super.onResume();
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_UP) {
			// 第三方应用调用拨号后返回后回到第三方应用
			String action = getIntent().getAction();
			if ((Intent.ACTION_DIAL.equals(action))
					|| (Intent.ACTION_VIEW.equals(action))) {
				return super.onKeyUp(keyCode, event);
			}
			goLauncher = true;
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
		}
		return true;
	}

	/**
	 * 清除所有通话记录后调用拨号盘页面重新加载通话记录
	 * 
	 * @description
	 * @date 2014-8-16
	 * @author valuestudio
	 */
	public void loadCallLog() {
		dialerFragment.loadCallLog();
	}

}
