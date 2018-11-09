package com.valuestudio.contacts.utils;

import java.text.Collator;
import java.util.Comparator;

import com.valuestudio.contacts.entity.ContactEntity;

/**
 * 姓名排序
 * 
 * @description
 * @date 2014-1-11
 * @author valuestudio
 */
public class NameComparator implements Comparator<ContactEntity> {

	private Collator collator = Collator.getInstance();

	public NameComparator() {
	}

	/**
	 * compare 实现排序。
	 * 
	 * @param entity1
	 *            RenCKEntity
	 * @param entity2
	 *            RenCKEntity
	 * @return int
	 */
	public int compare(ContactEntity entity1, ContactEntity entity2) {
		// try {
		// byte[] buf1 = entity1.getName().getBytes("GB2312");
		// byte[] buf2 = entity2.getName().getBytes("GB2312");
		// int size = Math.min(buf1.length, buf2.length);
		// for (int i = 0; i < size; i++) {
		// if (buf1[i] < buf2[i])
		// return -1;
		// else if (buf1[i] > buf2[i])
		// return 1;
		// }
		// return buf1.length - buf2.length;
		// } catch (Exception ex) {
		// return 0;
		// }

		String str1 = entity1.getSortKey().toLowerCase();
		String str2 = entity2.getSortKey().toLowerCase();
		if (str1.compareTo(str2) > 0) {
			return 1;
		} else if (str1.compareTo(str2) < 0) {
			return -1;
		} else {
			return 0;
		}
	}
}
