package com.valuestudio.contacts.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import android.text.TextUtils;

public class ZipUtil {

	public static boolean unZip(InputStream is, String filename,
			String folderPath) {
		File file = null;
		FileOutputStream fos = null;
		try {
			file = new File(filename);
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			byte[] bt = new byte[1024];
			int len = 0;
			while ((len = is.read(bt)) != -1) {
				fos.write(bt, 0, len);
				fos.flush();
			}
			fos.close();
			upZipFile(file, folderPath);
			file.delete();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (file != null && file.exists()) {
				file.delete();

			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				is = null;
			}
		}
		return false;
	}

	/**
	 * 解压缩一个文件
	 * 
	 * @param zipFile
	 *            压缩文件
	 * @param folderPath
	 *            解压缩的目标目录
	 * @throws IOException
	 *             当解压缩过程出错时抛出
	 */
	public static void upZipFile(File zipFile, String folderPath)
			throws ZipException, IOException {

		File desDir = new File(folderPath);
		if (!desDir.exists()) {
			desDir.mkdirs();
		}

		ZipFile zf = new ZipFile(zipFile);
		for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
			ZipEntry entry = ((ZipEntry) entries.nextElement());
			InputStream in = zf.getInputStream(entry);
			if (TextUtils.isEmpty(entry.getName())) {
				continue;
			}
			String str = folderPath + File.separator
					+ new String(entry.getName().getBytes("8859_1"), "GB2312");

			File desFile = new File(str);
			if (!desFile.exists()) {
				File fileParentDir = desFile.getParentFile();
				if (!fileParentDir.exists()) {
					fileParentDir.mkdirs();
				}
				desFile.createNewFile();
			} else {
				desFile.delete();
			}
			OutputStream out = new FileOutputStream(desFile);
			byte buffer[] = new byte[1024];
			int realLength;
			while ((realLength = in.read(buffer)) > 0) {
				out.write(buffer, 0, realLength);
			}
			in.close();
			out.close();

		}
		zf.close();

	}
}
