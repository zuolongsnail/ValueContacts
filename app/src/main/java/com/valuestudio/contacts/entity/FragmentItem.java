package com.valuestudio.contacts.entity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * @description FragmentItem
 * @date 2013-12-9
 * @author valuestudio
 */
public class FragmentItem {
	/**
	 * 标题
	 */
	public String title;
	/**
	 * icon
	 */
	public int icon;
	/**
	 * fragment实例
	 */
	public Fragment fragment;
	/**
	 * 参数
	 */
	public Bundle arguments;

	public FragmentItem(String title, Fragment fragment) {
		this.title = title;
		this.fragment = fragment;
	}

	public FragmentItem(String title, int icon, Fragment fragment) {
		this.title = title;
		this.icon = icon;
		this.fragment = fragment;
	}

	public FragmentItem(String title, Fragment fragment, Bundle arguments) {
		this.title = title;
		this.fragment = fragment;
		this.arguments = arguments;
	}
}
