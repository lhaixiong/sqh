package com.springapp.mvc.util;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	public static String getListString(List<Long> array) {
		if (null == array || array.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (Long l : array) {
			sb.append(l.longValue()).append(",");
		}
		if (sb.length() > 1) {
			sb = sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	public static String getArrayString(String[] array) {
		if (null == array || 0 == array.length) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		int length = array.length;
		for (int i = 0; i < length; i++) {
			sb.append(array[i]).append(",");
		}
		if (sb.length() > 1) {
			sb = sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	public static String getArrayString(List<String> list) {
		if (null == list || 0 == list.size()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		int length = list.size();
		for (int i = 0; i < length; i++) {
			if (i < length - 1) {
				sb.append(list.get(i)).append(",");
			} else {
				sb.append(list.get(i));
			}
		}
		return sb.toString();
	}

	/**
	 * 把数组字符串组合成字符串，以split分割
	 * 
	 * @param stringArray
	 * @param split
	 * @return
	 */
	public static String splicing(String[] stringArray, String split) {
		StringBuilder sb = new StringBuilder();
		int length = stringArray.length;

		for (int i = 0; i < length; i++) {
			sb.append(stringArray[i]).append(split);
		}
		sb = sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public static boolean isMac(String mac) {
		if (isEmpty(mac))
			return false;
		mac = mac.replace(":", "-");
		return mac
				.matches("[0-9A-Fa-f]{2}-[0-9A-Fa-f]{2}-[0-9A-Fa-f]{2}-[0-9A-Fa-f]{2}-[0-9A-Fa-f]{2}-[0-9A-Fa-f]{2}");
	}

	public static boolean isIp(String ipAddress) {
		String ip = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
		Pattern pattern = Pattern.compile(ip);
		Matcher matcher = pattern.matcher(ipAddress.trim());
		return matcher.matches();
	}

	public static int long2Int(Long l) {
		if (null == l) {
			return 0;
		}
		int result = new Integer(String.valueOf(l));
		return result;
	}

	public static int double2Int(Double d) {
		if (null == d) {
			return 0;
		}
		int result = d.intValue();
		return result;
	}

	public static String getCollectionString(Collection<?> collection) {
		if (null == collection || 0 == collection.size()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (Object string : collection) {
			sb.append(String.valueOf(string)).append(",");
		}
		if (sb.length() > 1) {
			sb = sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	/**
	 * 判断是否是数字
	 * 
	 * @param num
	 * @return
	 */
	public static boolean isNumber(String num) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(num).matches();
	}

	public static String getAdverTrainUrl() {
		InputStream is = StringUtils.class
				.getResourceAsStream("/database.properties");
		Properties properties = new Properties();
		try {
			properties.load(is);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		String url = properties.getProperty("adver_train");
		return url;
	}

	public static void deleteFile(File file) {
		if (null == file || !file.exists()) {
			return;
		}
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (null == files[i] || !files[i].exists()) {
					continue;
				}
				deleteFile(files[i]);
			}
		}
		file.delete();
	}
}
