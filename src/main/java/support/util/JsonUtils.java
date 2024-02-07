package support.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author: Hank
 * @Date: 2019/6/20 11:28
 * @Description: TODO
 */
public class JsonUtils {
	private static final ObjectMapper mapper = new ObjectMapper();
	private static final ObjectMapper notNullMapper = new ObjectMapper();

	public static Map<String, Object> toMap(String json) throws IOException {
		return (Map) mapper.readValue(json, Map.class);
	}

	public static String toJson(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String toJsonNotNull(Object object) {
		try {
			notNullMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			return notNullMapper.writeValueAsString(object);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> T toObject(String json, Class<T> clazz) {
		try {
			return mapper.readValue(json, clazz);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<?> json2List(String json, TypeReference<?> typeRef) throws IOException {
		return (List) mapper.readValue(json, typeRef);
	}

	public static void main(String[] args) {
		Map<String, Object> soMap = CollectUtil.getSOMap();
		soMap.put("A", "A");
		soMap.put("B", "A");
		soMap.put("C", "A");
		List<Map<String, Object>> list = CollectUtil.getList(soMap);
		list.stream().forEach(item -> {
			System.out.println(JsonUtils.toJson(item));
		});
	}
}
