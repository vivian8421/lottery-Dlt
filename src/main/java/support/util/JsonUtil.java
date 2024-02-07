package support.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * @author zhuangly
 */
//<dependency>
//	<groupId>com.fasterxml.jackson.core</groupId>
//	<artifactId>jackson-databind</artifactId>
//	<scope>provided</scope>
//</dependency>
public class JsonUtil {

	// 全局对象映射（线程安全）
	private static final ObjectMapper mapper = new ObjectMapper();
	// 全局对象映射（HTML专用，线程安全）
	private static final ObjectMapper mapperForHtml = new ObjectMapper();

	static {
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		// 设置输入时忽略JSON字符串中存在而Java对象实际没有的属性
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapperForHtml.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	}

	/**
	 * 判断JSON字符串是否是object类型
	 *
	 * @param json
	 * @return
	 */
	public static boolean isObject(String json) {
		if (json == null || "".equals(json))
			return false;

		if (json.startsWith("{") && json.endsWith("}") && StringUtil.charHot.countHot(json, "{") == StringUtil.charHot.countHot(json, "}")) {

			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断JSON字符串是否是数组类型
	 *
	 * @param json
	 * @return
	 */
	public static boolean isArray(String json) {
		if (json == null || "".equals(json))
			return false;

		if (json.startsWith("[") && json.endsWith("]") && StringUtil.charHot.countHot(json, "{") == StringUtil.charHot.countHot(json, "}")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 将对象转化为Json格式字符串
	 *
	 * @param obj
	 * @return
	 */
	public static String toJson(Object obj) {
		if (null == obj) {
			return null;
		}
		try {
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 将对象转化为Json格式字符串，并进行html编码
	 *
	 * @param obj
	 * @return
	 */
	public static String toJsonForHtml(Object obj) {
		if (null == obj) {
			return null;
		}
		try {
			return mapperForHtml.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T toObject(String json, Class<T> type) {
		if (StringUtil.isEmpty(json)) {
			return null;
		}
		if (null == type) {
			return null;
		}
		try {
			return mapper.readValue(json, type);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 将Json格式字符串转化为集合
	 *
	 * @param <T>
	 * @param json
	 * @param type
	 * @return
	 */
	public static <T> T toObject(String json, TypeReference<T> type) {
		if (StringUtil.isEmpty(json)) {
			return null;
		}
		if (null == type) {
			return null;
		}
		try {
			return mapper.readValue(json, type);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T toObject(Map<String, Object> obj, Class<T> type) {
		if (null == obj) {
			return null;
		}
		if (null == type) {
			return null;
		}
		try {
			String json = toJson(obj);
			return mapper.readValue(json, type);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public static <T> T toObject(Map<String, Object> obj, TypeReference<T> type) {
		if (null == obj) {
			return null;
		}
		if (null == type) {
			return null;
		}
		try {
			String json = toJson(obj);
			return mapper.readValue(json, type);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 将Json格式字符串转化为集合
	 *
	 * @param json
	 * @return
	 */
	public static Map<String, Object> toMap(String json) {
		if (StringUtil.isEmpty(json)) {
			return null;
		}
		try {
			MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
			return mapper.readValue(json, type);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public static List<Map<String, Object>> toMapList(String json) {
		if (StringUtil.isEmpty(json)) {
			return null;
		}
		if (!isArray(json) && isObject(json)) {
			json = "[" + json + "]";
		}
		return toObject(json, new TypeReference<List<Map<String, Object>>>() {
		});
	}
}