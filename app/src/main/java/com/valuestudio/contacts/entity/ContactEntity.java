package com.valuestudio.contacts.entity;

import android.text.TextUtils;

public class ContactEntity {
	private long rawContactId;
	private String name;
	private String number;
	private long photoId;
	private String sortKey;
	private long contactId;
	private String pinyin;
	private String firstAlpha;
	private String firstAlphaNum;
	private String fullNameNum;

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

	public long getPhotoId() {
		return photoId;
	}

	public void setPhotoId(long photoId) {
		this.photoId = photoId;
	}

	public String getSortKey() {
		return sortKey;
	}

	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}

	public long getRawContactId() {
		return rawContactId;
	}

	public void setRawContactId(long rawContactId) {
		this.rawContactId = rawContactId;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getFirstAlpha() {
		return firstAlpha;
	}

	public void setFirstAlpha(String firstAlpha) {
		this.firstAlpha = firstAlpha;
	}

	public String getFirstAlphaNum() {
		return firstAlphaNum;
	}

	public void setFirstAlphaNum(String firstAlphaNum) {
		this.firstAlphaNum = firstAlphaNum;
	}

	public String getFullNameNum() {
		return fullNameNum;
	}

	public void setFullNameNum(String fullNameNum) {
		this.fullNameNum = fullNameNum;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ContactEntity)) {
			return false;
		}
		ContactEntity entity = (ContactEntity) o;

		boolean nameEqual = false;
		if (!TextUtils.isEmpty(this.name)
				&& !TextUtils.isEmpty(entity.getName())) {
			nameEqual = this.name.compareTo(entity.getName()) == 0;
		}

		boolean numberEqual = false;
		if (!TextUtils.isEmpty(this.number)
				&& !TextUtils.isEmpty(entity.getNumber())) {
			numberEqual = this.number.compareTo(entity.getNumber()) == 0;
		}

		if (nameEqual && numberEqual) {
			return true;
		}
		return false;
	}
}
