package com.valuestudio.contacts.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.valuestudio.contacts.entity.FragmentItem;

/**
 * @description 标签适配器
 * @date 2013-12-9
 * @author valuestudio
 */
public class FragmentAdapter extends FragmentPagerAdapter implements Adapter {
	private ArrayList<FragmentItem> fragmentItem;

	public FragmentAdapter(FragmentManager fm,
			ArrayList<FragmentItem> appModules) {
		super(fm);
		this.fragmentItem = appModules;
	}

	public void setAppModules(ArrayList<FragmentItem> appModules) {
		this.fragmentItem = appModules;
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment;
		try {
			fragment = fragmentItem.get(position).fragment;
			// 设置参数
			fragment.setArguments(fragmentItem.get(position).arguments);
		} catch (Exception e) {
			return null;
		}
		return fragment;
	}

	@Override
	public int getCount() {
		return fragmentItem.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return fragmentItem.get(position).title;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return super.getItemId(position);
	}
	
	@Override
	public int getItemViewType(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
