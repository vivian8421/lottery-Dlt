package support.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author zhuangly
 */
public class StringUtil {
	public static String assertNull(Object string) {
		if (StringUtil.isEmpty(string)) {
			return "";
		}
		return string.toString();
	}

	public static boolean isNotNull(Object... string) {
		return !StringUtil.isNull(string);
	}

	public static boolean isNull(Object... string) {
		if (string == null) {
			return true;
		}
		for (Object obj : string) {
			if (obj == null) {
				return true;
			}
		}
		return false;
	}

	public static boolean isNotEmpty(Object... string) {
		return !StringUtil.isEmpty(string);
	}

	public static boolean isEmpty(Object... string) {
		if (string == null || "".equals(string)) {
			return true;
		}
		for (Object obj : string) {
			if (obj == null || "".equals(obj)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isNotBlank(Object string) {
		return !isBlank(string);
	}

	public static boolean isBlank(Object string) {
		return isEmpty(assertNull(string).trim());
	}

	public static String subString(String str, String begin, String end) {
		int endIndx = str.indexOf(end);
		if (isEmpty(end) || -1 == endIndx) {
			endIndx = str.length();
		}

		int beginIndex = str.lastIndexOf(begin, endIndx);
		if (isEmpty(begin) || -1 == beginIndex) {
			begin = "";
			beginIndex = 0;
		}

		return str.substring(beginIndex + begin.length(), endIndx);
	}

	public static String subString(String str, int beginIndex, String end) {
		int endIndx = str.indexOf(end);
		if (isEmpty(end) || -1 == endIndx) {
			endIndx = str.length();
		}

		return subString(str, beginIndex, endIndx);
	}

	public static String subString(String str, String begin, int endIndx) {
		int beginIndex = str.lastIndexOf(begin, endIndx);
		if (isEmpty(begin) || -1 == beginIndex) {
			begin = "";
			beginIndex = 0;
		}

		return subString(str, beginIndex + begin.length(), endIndx);
	}

	public static String subString(String str, int beginIndex, int endIndx) {
		if (beginIndex >= endIndx) {
			throw new IndexOutOfBoundsException("[ beginIndex > endIndx ] error");
		}
		return str.substring(beginIndex, endIndx);
	}

	public static String substring(String str, String indexSub, int subLength) {
		return substring(str, indexSub, subLength, false);
	}

	public static String substring(String str, String indexSub, int subLength, boolean containIndexSub) {
		if (StringUtil.isEmpty(str) || subLength > str.length()) {
			return null;
		}
		if (StringUtil.isEmpty(indexSub) || indexSub.length() > str.length()) {
			return null;
		}
		int indexSubOf = str.indexOf(indexSub);
		if (indexSubOf < 0) {
			return null;
		}

		int beginIndex = 0;
		int endIndex = 0;

		int allLength = str.length();
		if (subLength > 0) {
			beginIndex = indexSubOf + (containIndexSub ? 0 : indexSub.length());
			endIndex = beginIndex + (containIndexSub ? indexSub.length() : 0) + subLength;
			if (endIndex > allLength) {
				endIndex = allLength;
			}
		} else {
			endIndex = indexSubOf + (!containIndexSub ? 0 : indexSub.length());
			beginIndex = endIndex + subLength + -(containIndexSub ? indexSub.length() : 0);
			if (beginIndex < 0) {
				beginIndex = 0;
			}
		}

		return str.substring(beginIndex, endIndex);
	}

	public static Collection<String> subEmpty(Collection<String> collect) {
		if (CollectUtil.isEmpty(collect)) {
			return CollectUtil.getEmptyList();
		}
		for (Iterator<String> iterator = collect.iterator(); iterator.hasNext();) {
			String string = iterator.next();
			if (StringUtil.isEmpty(string)) {
				iterator.remove();
			}
		}
		return collect;
	}

	public static Collection<String> subBlank(Collection<String> collect) {
		if (CollectUtil.isEmpty(collect)) {
			return CollectUtil.getEmptyList();
		}
		for (Iterator<String> iterator = collect.iterator(); iterator.hasNext();) {
			String string = iterator.next();
			if (StringUtil.isBlank(string)) {
				iterator.remove();
			}
		}
		return collect;
	}

	@SuppressWarnings("unchecked")
	public static <N extends Number> N stringToNumber(Class<N> numClass, String str) {
		BigDecimal bigDecimal = new BigDecimal(str);
		if (numClass.equals(Integer.class))
			return (N) new Integer(bigDecimal.intValue());
		if (numClass.equals(Long.class))
			return (N) new Long(bigDecimal.longValue());
		if (numClass.equals(Double.class))
			return (N) new Double(bigDecimal.doubleValue());
		else {
			throw new RuntimeException();
		}
	}

	public static <N extends Number> List<N> stringToNumber(Class<N> numClass, String[] strings) {
		List<N> list = CollectUtil.getList();
		for (String string : strings) {
			N stringToNumber = stringToNumber(numClass, string);
			if (stringToNumber != null) {
				list.add(stringToNumber);
			}
		}
		return null;
	}

	public static <N extends Number> List<N> stringToNumber(Class<N> numClass, Collection<String> strings) {
		List<N> list = CollectUtil.getList();
		for (String string : strings) {
			N stringToNumber = stringToNumber(numClass, string);
			if (stringToNumber != null) {
				list.add(stringToNumber);
			}
		}
		return null;
	}

	public static boolean ArrayContains(String[] array, String obj, boolean ignoreCase) {
		Integer arrayIndex = ArrayIndex(array, obj, ignoreCase);
		if (arrayIndex == null) {
			return false;
		} else {
			return arrayIndex > -1;
		}
	}
	public static Integer ArrayIndex(String[] array, String obj, boolean ignoreCase) {
		if (array == null) {
			return null;
		}
		for (int i = 0; i < array.length; i++) {
			if (isNull(array[i], obj)) {
				return i;
			} else {
				if (array[i] != null && (array[i].equals(obj) || !ignoreCase && charCase.toLower(array[i]).equals(charCase.toLower(obj)))) {
					return i;
				}
				if (obj != null && (obj.equals(array[i]) || !ignoreCase && charCase.toLower(obj).equals(charCase.toLower(array[i])))) {
					return i;
				}
			}
		}
		return -1;
	}
	public static boolean CollectContains(Collection<String> collect, String obj, boolean ignoreCase) {
		Integer collectIndex = CollectIndex(collect, obj, ignoreCase);
		if (collectIndex == null) {
			return false;
		} else {
			return collectIndex > -1;
		}
	}
	public static Integer CollectIndex(Collection<String> collect, String obj, boolean ignoreCase) {
		if (collect == null) {
			return null;
		}
		int i = 0;
		for (String item : collect) {
			if (isNull(item, obj)) {
				return i;
			} else {
				if (item != null && (item.equals(obj) || !ignoreCase && charCase.toLower(item).equals(charCase.toLower(obj)))) {
					return i;
				}
				if (obj != null && (obj.equals(item) || !ignoreCase && charCase.toLower(obj).equals(charCase.toLower(item)))) {
					return i;
				}
			}
			i++;
		}
		return -1;
	}

	public static class joinStr {
		public static <T> String join(String join, Collection<T> collect) {
			return join("", "", "", join, collect);
		}

		public static <T> String join(String itemWrap, String join, Collection<T> collect) {
			return join("", "", itemWrap, join, collect);
		}

		public static <T> String join(String leftWrap, String rightWrap, String join, Collection<T> collect) {
			return join(leftWrap, rightWrap, "", join, collect);
		}

		public static <T> String join(String leftWrap, String rightWrap, String itemWrap, String join, Collection<T> collect) {
			if (CollectUtil.isEmpty(collect)) {
				return "";
			}
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append(leftWrap);
			for (T obj : collect) {
				if (StringUtil.isEmpty(obj)) {
					continue;
				}
				stringBuffer.append(itemWrap);
				stringBuffer.append(obj.toString());
				stringBuffer.append(itemWrap);
				stringBuffer.append(join);
			}
			stringBuffer.delete(stringBuffer.length() - join.length(), stringBuffer.length());
			stringBuffer.append(rightWrap);
			return stringBuffer.toString();
		}

		public static <T> String join(String join, T[] array) {
			return join("", "", "", join, array);
		}

		public static <T> String join(String itemWrap, String join, T[] array) {
			return join("", "", itemWrap, join, array);
		}

		public static <T> String join(String leftWrap, String rightWrap, String join, T[] array) {
			return join(leftWrap, rightWrap, "", join, array);
		}

		public static <T> String join(String leftWrap, String rightWrap, String itemWrap, String join, T[] array) {
			if (CollectUtil.isEmpty(array)) {
				return "";
			}
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append(leftWrap);
			for (T obj : array) {
				if (StringUtil.isEmpty(obj)) {
					continue;
				}
				stringBuffer.append(itemWrap);
				stringBuffer.append(obj.toString());
				stringBuffer.append(itemWrap);
				stringBuffer.append(join);
			}
			stringBuffer.delete(stringBuffer.length() - join.length(), stringBuffer.length());
			stringBuffer.append(rightWrap);
			return stringBuffer.toString();
		}

	}

	public static class compressStr {
		/**
		 * 就用这个 改别的会出错
		 */
		private static String encode = "ISO-8859-1";

		// 压缩
		public static String compress(String metadata) {
			if (StringUtil.isEmpty(metadata)) {
				return metadata;
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			GZIPOutputStream gzip = null;
			try {
				gzip = new GZIPOutputStream(out);
				gzip.write(metadata.getBytes(encode));
				gzip.close();
				return out.toString(encode);
			} catch (Exception e) {
				try {
					if (gzip != null) {
						gzip.close();
					}
					if (out != null) {
						out.close();
					}
				} catch (Exception ex) {
					ex.addSuppressed(e);
					throw new RuntimeException(ex);
				}
				throw new RuntimeException(e);
			}
		}

		// 解压缩
		public static String uncompress(String compress) {
			if (StringUtil.isEmpty(compress)) {
				return compress;
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ByteArrayInputStream in = null;
			try {
				in = new ByteArrayInputStream(compress.getBytes(encode));
				GZIPInputStream gunzip = new GZIPInputStream(in);
				byte[] buffer = new byte[256];
				int n;
				while ((n = gunzip.read(buffer)) >= 0) {
					out.write(buffer, 0, n);
				}
				return out.toString("UTF-8");
			} catch (Exception e) {
				try {
					if (in != null) {
						in.close();
					}
					if (out != null) {
						out.close();
					}
				} catch (Exception ex) {
					ex.addSuppressed(e);
					throw new RuntimeException(ex);
				}
				throw new RuntimeException(e);
			}
		}
	}

	public static class charCase {

		public static String toUpper(String str) {
			if (str == null) {
				return null;
			}
			return str.toUpperCase();
		}

		public static String toLower(String str) {
			if (str == null) {
				return null;
			}
			return str.toLowerCase();
		}

		public static boolean isUpper(char c) {
			if (65 <= c && c <= 90) {
				return true;
			}
			return false;
		}

		public static boolean isLower(char c) {
			if (97 <= c && c <= 122) {
				return true;
			}
			return false;
		}

		public static char upperToLower(char c) {
			if (!charCase.isUpper(c)) {
				return c;
			}
			return c += 32;
		}

		public static char lowerToUpper(char c) {
			if (!charCase.isLower(c)) {
				return c;
			}
			return c -= 32;
		}

		public static String firstToUpper(String string) {
			if (string == null) {
				return null;
			}
			byte[] bytes = string.getBytes();
			if (97 <= bytes[0] && bytes[0] <= 122) {
				bytes[0] ^= 32;
			}
			return new String(bytes);
		}

		public static String firstToLower(String string) {
			if (string == null) {
				return null;
			}
			char[] charArray = string.toCharArray();
			if (isUpper(charArray[0])) {
				char upperCaseToLowercase = upperToLower(charArray[0]);
				charArray[0] = upperCaseToLowercase;
			}
			return new String(charArray);
		}

		/**
		 * 驼峰转下划线
		 */
		public static String camelToUnderScore(String field) {
			if (field == null) {
				return null;
			}
			char[] charArray = field.toCharArray();
			StringBuilder builder = new StringBuilder("");
			for (int i = 0; i < charArray.length; i++) {
				char c = charArray[i];
				if (charCase.isUpper(c)) {
					if (i != 0) {
						builder.append("_");
					}
					char upperCaseToLowercase = charCase.upperToLower(c);
					builder.append(upperCaseToLowercase);
					continue;
				}
				builder.append(c);
			}
			return builder.toString();
		}

		/**
		 * 下划线转驼峰
		 */
		public static String underScoreToCamel(String column) {
			if (column == null) {
				return null;
			}
			column = column.toLowerCase();
			StringBuilder builder = new StringBuilder("");
			String[] split = column.split("_");
			for (int i = 0; i < split.length; i++) {
				String item = split[i];
				if (i != 0) {
					item = charCase.firstToUpper(item);
				}
				builder.append(item);
			}
			return charCase.firstToLower(builder.toString());
		}
	}

	public static class charHot {

		public static int countHot(String string, String subString) {
			if (StringUtil.isEmpty(string)) {
				return 0;
			}
			if (StringUtil.isEmpty(subString)) {
				return string.length();
			}
			return (string.length() - string.replace(subString, "").length()) / subString.length();
		}

	}

}
