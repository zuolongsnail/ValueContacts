package com.valuestudio.contacts.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SkinManager {

	static Context mContext = null;
	static LayoutInflater mInflater = null;

	/**
	 * 根据资源获取视图
	 * 
	 * @param resource
	 * @param root
	 * @param attachToRoot
	 * @return
	 */
	public static View createViewFromResource(Context context,
			String layoutName, ViewGroup root, boolean attachToRoot) {
		View resultView = null;

		int resid = context.getResources().getIdentifier(layoutName, "layout",
				context.getPackageName());
		resultView = mInflater.inflate(resid, root, attachToRoot);
		return resultView;
	}

	public static Drawable getDrawable(Context context, String drawableName)
			throws NotFoundException {
		Drawable resultDrawable = null;
		int resid = getIdentifier2(context, drawableName.trim(), "drawable");
		resultDrawable = context.getResources().getDrawable(resid);

		return resultDrawable;
	}

	public static int getIdentifier2(Context context, String name,
			String defType) throws NotFoundException {
		int result = 0;
		try {
			result = context.getResources().getIdentifier(name, defType,
					context.getPackageName());
		} catch (Exception e) {
			e.printStackTrace();

		}
		return result;
	}

	public static int getIdentifier(Context context, String name, String defType)
			throws NotFoundException {
		int result = 0;
		result = context.getResources().getIdentifier(name, defType,
				context.getPackageName());
		return result;
	}

	/**
	 * 获取对应路径下drawable
	 * 
	 * @param context
	 * @param imagePath
	 * @return
	 */
	public static Drawable createDrawableByPath(Context context,
			String imagePath) {
		try {
			if (TextUtils.isEmpty(imagePath))
				return null;
			Bitmap bm = BitmapFactory.decodeFile(imagePath);
			bm.setDensity(400);
			Drawable dw = new BitmapDrawable(context.getResources(), bm);
			return dw;
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError ex) {
			System.gc();
			ex.printStackTrace();
		}
		return null;
	}

	public static boolean isInstallPackageName(Context context,
			String packageName) {
		PackageManager pm = context.getPackageManager();
		try {
			pm.getPackageGids(packageName);
			return true;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
