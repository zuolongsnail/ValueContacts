package com.valuestudio.contacts.widget;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.valuestudio.contacts.R;
import com.valuestudio.contacts.base.BaseActivity;

/**
 * 
 * @author Administrator
 * 
 */
public class WebViewWidget extends BaseActivity {
	public static final String COLOR_RING_URL = "http://api.openspeech.cn/kyls/NTY4MzgxNjU=";
	private WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		initViews();
		initSettings();
		loadUrl();
	}

	public void initViews() {
		super.initViews();
		mWebView = (WebView) findViewById(R.id.webview);
	};

	/**
	 * 初始化设置
	 */
	private void initSettings() {
		WebSettings webSettings = mWebView.getSettings();
		// JavaScript可用
		webSettings.setJavaScriptEnabled(true);
	}

	/**
	 * 加载url
	 */
	private void loadUrl() {
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
				view.loadUrl(url);
				return false;
			}
		});
		mWebView.loadUrl(COLOR_RING_URL);
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
