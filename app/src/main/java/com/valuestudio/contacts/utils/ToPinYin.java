package com.valuestudio.contacts.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.text.TextUtils;

public class ToPinYin {
	/**
	 * 多音字库
	 */
	public static Map<String, String> multiChineseMap = new HashMap<String, String>();
	static {
		multiChineseMap.put("卜", "BU");
		multiChineseMap.put("长", "CHANG");
		multiChineseMap.put("缪", "MIAO");
		multiChineseMap.put("区", "OU");
		multiChineseMap.put("朴", "PIAO");
		multiChineseMap.put("仇", "QIU");
		multiChineseMap.put("单", "SHAN");
		multiChineseMap.put("冼", "XIAN");
		multiChineseMap.put("解", "XIE");
		multiChineseMap.put("曾", "ZENG");
		multiChineseMap.put("查", "ZHA");
	}

	public static List<String> getPinyinList(List<String> list) {
		List<String> pinyinList = new ArrayList<String>();
		for (Iterator<String> i = list.iterator(); i.hasNext();) {
			String str = (String) i.next();
			try {
				String pinyin = getPinYin(str);
				pinyinList.add(pinyin);
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				e.printStackTrace();
			}
		}
		return pinyinList;
	}

	/**
	 * 获取中文全拼
	 * 
	 * @description
	 * @param zhongwen
	 * @return
	 * @throws BadHanyuPinyinOutputFormatCombination
	 * @date 2014-8-7
	 * @author valuestudio
	 */
	public static String getPinYin(String zhongwen)
			throws BadHanyuPinyinOutputFormatCombination {
		String zhongWenPinYin = "";
		char[] chars = zhongwen.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			String pinYin = toHanyuPinyinStringArray(chars[i],
					getDefaultOutputFormat());
			if (pinYin != null) {
				zhongWenPinYin += pinYin;
			} else {
				zhongWenPinYin += chars[i];
			}
		}
		return zhongWenPinYin;
	}

	private static HanyuPinyinOutputFormat getDefaultOutputFormat() {
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_U_AND_COLON);
		return format;
	}

	/**
	 * 全拼转数字
	 * 
	 * @description
	 * @param name
	 * @return
	 * @date 2014-8-7
	 * @author valuestudio
	 */
	public static String getFullNameNum(String name) {
		StringBuffer nameNum = new StringBuffer();
		try {
			if (!TextUtils.isEmpty(name)) {
				String pinyin = getPinYin(name).toLowerCase();
				char[] nameChar = pinyin.toCharArray();
				for (int index = 0; index < nameChar.length; index++) {
					nameNum.append(getOneNumFromAlpha(nameChar[index]));
				}
			}
			return nameNum.toString();
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		return nameNum.toString();
	}

	/**
	 * 字符转数字
	 * 
	 * @description
	 * @param firstAlpha
	 * @return
	 * @date 2014-8-7
	 * @author valuestudio
	 */
	private static char getOneNumFromAlpha(char firstAlpha) {
		switch (firstAlpha) {
		case 'a':
		case 'b':
		case 'c':
			return '2';
		case 'd':
		case 'e':
		case 'f':
			return '3';
		case 'g':
		case 'h':
		case 'i':
			return '4';
		case 'j':
		case 'k':
		case 'l':
			return '5';
		case 'm':
		case 'n':
		case 'o':
			return '6';
		case 'p':
		case 'q':
		case 'r':
		case 's':
			return '7';
		case 't':
		case 'u':
		case 'v':
			return '8';
		case 'w':
		case 'x':
		case 'y':
		case 'z':
			return '9';
		default:
			return '0';
		}
	}

	/**
	 * 数字转字符
	 * 
	 * @description
	 * @param digit
	 * @return
	 * @date 2014-8-7
	 * @author valuestudio
	 */
	public static char[] digit2Char(int digit) {
		char[] cs = null;
		switch (digit) {
		case 0:
			cs = new char[] {};
			break;
		case 1:
			break;
		case 2:
			cs = new char[] { 'a', 'b', 'c' };
			break;
		case 3:
			cs = new char[] { 'd', 'e', 'f' };
			break;
		case 4:
			cs = new char[] { 'g', 'h', 'i' };
			break;
		case 5:
			cs = new char[] { 'j', 'k', 'l' };
			break;
		case 6:
			cs = new char[] { 'm', 'n', 'o' };
			break;
		case 7:
			cs = new char[] { 'p', 'q', 'r', 's' };
			break;
		case 8:
			cs = new char[] { 't', 'u', 'v' };
			break;
		case 9:
			cs = new char[] { 'w', 'x', 'y', 'z' };
			break;
		}
		return cs;
	}

	/**
	 * 首字母
	 * 
	 * @description
	 * @param chinese
	 * @return
	 * @date 2014-8-7
	 * @author valuestudio
	 */
	public static String getFirstAlpha(String chinese) {
		StringBuffer firstAlpha = new StringBuffer();
		try {
			char[] nameChar = chinese.toCharArray();
			for (int index = 0; index < nameChar.length; index++) {
				String pinyin = getPinYin(String.valueOf(nameChar[index]));
				firstAlpha.append(getAlpha(pinyin).toLowerCase().charAt(0));
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		return firstAlpha.toString();
	}

	/**
	 * 首字母数字
	 * 
	 * @description
	 * @param chinese
	 * @return
	 * @date 2014-8-7
	 * @author valuestudio
	 */
	public static String getFirstAlphaNum(String chinese) {
		StringBuffer firstAlphaName = new StringBuffer();
		try {
			char[] nameChar = chinese.toCharArray();
			for (int index = 0; index < nameChar.length; index++) {
				String pinyin = getPinYin(String.valueOf(nameChar[index]));
				firstAlphaName.append(getOneNumFromAlpha(getAlpha(pinyin)
						.toLowerCase().charAt(0)));
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		return firstAlphaName.toString();
	}

	/**
	 * 汉字转拼音
	 * 
	 * @param chinese
	 * @return
	 */
	public static String hanZiToPinYin(String chinese) {
		StringBuffer pinyinName = new StringBuffer();
		char[] nameChar = chinese.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < nameChar.length; i++) {
			if (nameChar[i] > 128) {
				// 获取单个字符的拼音
				String pinyingStr = toHanyuPinyinStringArray(nameChar[i],
						defaultFormat);
				// 过滤中文符号
				if (pinyingStr == null) {
					pinyinName.append("#");
				} else {
					if (i > 0) {
						pinyinName.append(" ");
					}
					pinyinName.append(pinyingStr.toUpperCase()).append(" ")
							.append(nameChar[i]);
				}
			} else {
				pinyinName.append(nameChar[i]);
			}
		}
		return pinyinName.toString();
	}

	/**
	 * 中文转换为拼音
	 * 
	 * @description
	 * @param nameChar
	 * @param format
	 * @return
	 * @date 2014-8-27
	 * @author zuolong
	 */
	private static String toHanyuPinyinStringArray(char nameChar,
			HanyuPinyinOutputFormat format) {
		// 先判断是否为多音字
		if (multiChineseMap.containsKey(String.valueOf(nameChar))) {
			return multiChineseMap.get(String.valueOf(nameChar));
		} else {
			try {
				String[] pinyinStr = PinyinHelper.toHanyuPinyinStringArray(
						nameChar, format);
				// 过滤中文符号
				if (pinyinStr == null || pinyinStr.length <= 0) {
					return null;
				} else {
					return pinyinStr[0];
				}
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	/**
	 * 获得汉语拼音首字母
	 * 
	 * @param str
	 * @return
	 */
	public static String getAlpha(String str) {
		if (str == null) {
			return "#";
		}

		if (str.trim().length() == 0) {
			return "#";
		}

		char c = str.trim().substring(0, 1).charAt(0);
		// 正则表达式，判断首字母是否是英文字母
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return String.valueOf(Character.toUpperCase(c));
		} else {
			return "#";
		}
	}

	/**
	 * 判断是否含有中文
	 * 
	 * @description
	 * @param str
	 * @return
	 * @date 2014-2-15
	 * @author valuestudio
	 */
	public static boolean isChinese(String str) {
		return str.getBytes().length != str.length();
	}
}
