package com.valuestudio.contacts.utils;

public class TrimNumber {
	private static final String[] IPPFXS4 = { "1790", "1791", "1793", "1795",
			"1796", "1797", "1799" };
	private static final String[] IPPFXS5 = { "12583", "12593", "12589",
			"12520", "10193", "11808" };
	private static final String[] IPPFXS6 = { "118321" };

	/**
	 * 消除电话号码中 可能含有的 IP号码、+86、0086等前缀
	 * 
	 * @param telNum
	 * @return
	 */
	public static String trimTelNum(String telNum) {

		if (telNum == null || "".equals(telNum)) {
			return null;
		}

		String ippfx6 = substring(telNum, 0, 6);
		String ippfx5 = substring(telNum, 0, 5);
		String ippfx4 = substring(telNum, 0, 4);

		if (telNum.length() > 7
				&& (substring(telNum, 5, 1).equals("0")
						|| substring(telNum, 5, 1).equals("1")
						|| substring(telNum, 5, 3).equals("400") || substring(
							telNum, 5, 3).equals("+86"))
				&& (inArray(ippfx5, IPPFXS5) || inArray(ippfx4, IPPFXS4)))
			telNum = substring(telNum, 5);
		else if (telNum.length() > 8
				&& (substring(telNum, 6, 1).equals("0")
						|| substring(telNum, 6, 1).equals("1")
						|| substring(telNum, 6, 3).equals("400") || substring(
							telNum, 6, 3).equals("+86"))
				&& inArray(ippfx6, IPPFXS6))
			telNum = substring(telNum, 6);
		// remove ip dial

		telNum = telNum.replace("-", "");
		telNum = telNum.replace(" ", "");

		if (substring(telNum, 0, 4).equals("0086"))
			telNum = substring(telNum, 4);
		else if (substring(telNum, 0, 3).equals("+86"))
			telNum = substring(telNum, 3);
		else if (substring(telNum, 0, 5).equals("00186"))
			telNum = substring(telNum, 5);

		return telNum;
	}

	/**
	 * 截取字符串
	 * 
	 * @param s
	 * @param from
	 * @return
	 */
	protected static String substring(String s, int from) {
		try {
			return s.substring(from);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	protected static String substring(String s, int from, int len) {
		try {
			return s.substring(from, from + len);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 判断一个字符串是否在一个字符串数组中
	 * 
	 * @param target
	 * @param arr
	 * @return
	 */
	protected static boolean inArray(String target, String[] arr) {
		if (arr == null || arr.length == 0) {
			return false;
		}
		if (target == null) {
			return false;
		}
		for (String s : arr) {
			if (target.equals(s)) {
				return true;
			}
		}
		return false;
	}

}
