package com.valuestudio.contacts.ui;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.valuestudio.contacts.R;
import com.valuestudio.contacts.base.BaseActivity;

public class SearchNumActivity extends BaseActivity {
	/**
	 * 刷新
	 */
	private final static int REFRESH_ID = 0;
	/**
	 * url
	 */
	public static String SEARCH_URL_HEADER = "http://www.baidu.com/s?word=";
	/**
	 * keyword
	 */
	public static String KEYWORD = "keyword";

	private String keyword;
	private WebView mWebView;
	private ProgressBar mProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.BaseWhiteAppTheme);
		setContentView(R.layout.search_num_activity);
		initActionBar();

		initViews();
		bindData();
	}

	@Override
	protected void initActionBar() {
		super.initActionBar();
		// 设置底部actionbar背景
		mActionBar.setSplitBackgroundDrawable(getResources().getDrawable(
				R.color.theme_black));
	}

	@Override
	public void initViews() {
		mWebView = (WebView) findViewById(R.id.webview);
		mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
	}

	@Override
	public void bindData() {
		keyword = getIntent().getStringExtra(KEYWORD);
		initSettings();
		loadUrl();
	}

	/**
	 * 初始化设置
	 */
	private void initSettings() {
		WebSettings webSettings = mWebView.getSettings();
		// JavaScript可用
		webSettings.setJavaScriptEnabled(true);
		// 支持页面缩放
		webSettings.setSupportZoom(true);
		// 启用内置的缩放控件
		webSettings.setBuiltInZoomControls(true);
		// 页面自适应屏幕大小
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);
		// 允许访问文件数据
		webSettings.setAllowFileAccess(true);
		// 支持应用缓存
		webSettings.setAppCacheEnabled(true);
		// 使用默认缓存机制
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

		webSettings.setLoadsImagesAutomatically(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		// 可聚焦
		mWebView.requestFocus();
	}

	/**
	 * 加载url
	 */
	private void loadUrl() {
		mWebView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				if (progress == 0) {
					mProgressBar.setVisibility(View.VISIBLE);
				}
				mProgressBar.setProgress(progress);
				mProgressBar.postInvalidate();
				if (progress == 100) {
					mProgressBar.setVisibility(View.GONE);
				}
			}
		});
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		mWebView.loadUrl(SEARCH_URL_HEADER + keyword);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem refreshItem = menu.add(0, REFRESH_ID, 0, R.string.refresh);
		refreshItem.setIcon(R.drawable.ic_refresh_light);
		refreshItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		refreshItem.setTitle(getString(R.string.refresh));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case REFRESH_ID:
			loadUrl();
			break;
		default:
			super.onOptionsItemSelected(item);
			break;
		}
		return true;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onSlideRight() {
		super.onSlideRight();
		onBackPressed();
	}

}