package com.valuestudio.contacts.entity;

import android.text.TextUtils;

public class CallLogEntity {
	private int id;
	/**
	 * photo id
	 */
	private long photoId;
	/**
	 * contact id
	 */
	private long contactId;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 号码
	 */
	private String number;
	/**
	 * 号码归属地
	 */
	private String location;
	/**
	 * 日期
	 */
	private String date;
	/**
	 * 来电：1，拨出：2，未接：3
	 */
	private int type;
	/**
	 * 通话次数
	 */
	private int count;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getPhotoId() {
		return photoId;
	}

	public void setPhotoId(long photoId) {
		this.photoId = photoId;
	}

	public long getContactId() {
		return contactId;
	}

	public void setContactId(long contactId) {
		this.contactId = contactId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CallLogEntity)) {
			return false;
		}
		CallLogEntity entity = (CallLogEntity) o;

		// boolean nameEqual = false;
		// if (!TextUtils.isEmpty(this.name)
		// && !TextUtils.isEmpty(entity.getName())) {
		// nameEqual = this.name.compareTo(entity.getName()) == 0;
		// }

		boolean numberEqual = false;
		if (!TextUtils.isEmpty(this.number)
				&& !TextUtils.isEmpty(entity.getNumber())) {
			numberEqual = this.number.compareTo(entity.getNumber()) == 0;
		}

		// if (nameEqual && numberEqual) {
		// return true;
		// }
		if (numberEqual) {
			return true;
		}
		return false;
	}
}
