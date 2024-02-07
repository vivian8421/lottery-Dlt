package support.util;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author zhuangly
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class CollectUtil {

	public static <T> boolean isNull(T[] array) {
		if (array == null) {
			return true;
		}
		return false;
	}
	public static <T> boolean isNotNull(T[] array) {
		return !isNull(array);
	}

	public static <T> boolean isEmpty(T[] array) {
		if (array == null || array.length == 0) {
			return true;
		}
		return false;
	}
	public static <T> boolean isNotEmpty(T[] array) {
		return !isEmpty(array);
	}

	public static boolean isNull(Collection<?> collect) {
		if (collect == null) {
			return true;
		}
		return false;
	}
	public static boolean isNotNull(Collection<?> collect) {
		return !isNull(collect);
	}

	public static boolean isEmpty(Collection<?> collect) {
		if (collect == null || collect.isEmpty()) {
			return true;
		}
		return false;
	}
	public static boolean isNotEmpty(Collection<?> collect) {
		return !isEmpty(collect);
	}

	public static boolean isNull(Map map) {
		if (map == null) {
			return true;
		}
		return false;
	}
	public static boolean isNotNull(Map map) {
		return !isNull(map);
	}

	public static boolean isEmpty(Map map) {
		if (map == null || map.isEmpty()) {
			return true;
		}
		return false;
	}
	public static boolean isNotEmpty(Map map) {
		return !isEmpty(map);
	}
	public static <T> List<T> getEmptyList() {
		return getList();
	}
	public static <T> Set<T> getEmptySet() {
		return getSet();
	}
	public static <K, V> Map<K, V> getEmptyMap() {
		return getMap();
	}
	public static <T> List<T> getList() {
		return new ArrayList<T>();
	}
	public static <T> List<T> getList(T... items) {
		List<T> list = new ArrayList<T>();
		for (T t : items) {
			list.add(t);
		}
		return list;
	}
	public static <T> List<T> getListBy(Collection<T> collect) {
		List<T> list = new ArrayList<T>();
		for (T t : collect) {
			list.add(t);
		}
		return list;
	}
	public static <T> List<T> getList(Class<T> clazz) {
		return getList();
	}
	public static <T> List<T> getList(Class<T> clazz, int size) {
		return new ArrayList<T>(size);
	}
	public static <T, A> List<T> getListEq(List<T> list, String propertyName, A... propertyvalue) {
		return getListEq(list, propertyName, getSet(propertyvalue));
	}
	public static <T, A> List<T> getListEq(List<T> list, String propertyName, Set<A> propertyvalue) {
		if (isEmpty(list)) {
			return list;
		}
		if (StringUtil.isEmpty(propertyName)) {
			return list;
		}
		if (StringUtil.isNotEmpty(propertyName) && isEmpty(propertyvalue)) {
			return getEmptyList();
		}
		List<T> arrayList = new ArrayList<T>();
		for (T t : list) {
			Object getPropertyValue = BeanUtil.getPropertyValue(t, propertyName);
			if (propertyvalue.contains(getPropertyValue)) {
				arrayList.add(t);
			}
		}
		return arrayList;
	}
	public static <T, A> List<T> getListNe(List<T> list, String propertyName, A... propertyvalue) {
		return getListNe(list, propertyName, getSet(propertyvalue));
	}
	public static <T, A> List<T> getListNe(List<T> list, String propertyName, Set<A> propertyvalue) {
		if (isEmpty(list)) {
			return list;
		}
		if (StringUtil.isEmpty(propertyName)) {
			return list;
		}
		if (StringUtil.isNotEmpty(propertyName) && isEmpty(propertyvalue)) {
			return getEmptyList();
		}
		List<T> arrayList = new ArrayList<T>();
		for (T t : list) {
			Object getPropertyValue = BeanUtil.getPropertyValue(t, propertyName);
			if (!propertyvalue.contains(getPropertyValue)) {
				arrayList.add(t);
			}
		}
		return arrayList;
	}

	public static <T> Set<T> getSet(T... items) {
		Set<T> set = new HashSet<T>();
		for (T t : items) {
			set.add(t);
		}
		return set;
	}
	public static <T> Set<T> getSetBy(Collection<T> collect) {
		Set<T> set = new HashSet<T>();
		for (T t : collect) {
			set.add(t);
		}
		return set;
	}
	public static <T> Set<T> getSet(int size) {
		return new HashSet<T>(size);
	}
	public static <T> Set<T> getSet(Class<T> clazz) {
		return getSet();
	}
	public static <T> Set<T> getSet(Class<T> clazz, int size) {
		return new HashSet<T>(size);
	}
	public static <T> T[] getArray(T... items) {
		if (CollectUtil.isEmpty(items)) {
			return null;
		}
		Class<T> itemClass = null;
		for (T t : items) {
			if (t != null) {
				itemClass = (Class<T>) t.getClass();
				break;
			}
		}
		if (itemClass == null) {
			return null;
		}
		T[] array = getArray(itemClass, items.length);
		for (int i = 0; i < items.length; i++) {
			Array.set(array, i, items[i]);
		}
		return array;
	}
	public static <T> T[] getArray(Class<T> clazz, int size) {
		if (clazz == null) {
			return null;
		}
		return (T[]) Array.newInstance(clazz, size);
	}
	public static <K, V> Map<K, V> getMap() {
		return new HashMap<K, V>();
	}
	public static Map<String, Object> getSOMap() {
		return new HashMap<String, Object>();
	}
	public static <K, V> Map<K, V> getLinkedMap() {
		return new LinkedHashMap<K, V>();
	}
	public static Map<String, Object> getSOLinkedMap() {
		return new LinkedHashMap<String, Object>();
	}
	public static <K, V> Map<K, V> getMap(Class<K> keyClass, Class<V> valueClass) {
		return getMap();
	}
	public static <K, V> Map<K, V> getMap(Map<K, V> map, K... keys) {
		return getMap(map, false, keys);
	}
	public static <K, V> Map<K, V> getMap(Map<K, V> map, boolean ignoreNull, K... keys) {
		Map<K, V> toMap = new HashMap<K, V>();
		for (K key : keys) {
			V value = null;
			if (map != null) {
				value = map.get(key);
			}
			if (value == null && ignoreNull) {
				continue;
			}
			toMap.put(key, value);
		}
		return toMap;
	}
	public static <K, V> Map<K, V> getMap(Map<K, V> map, Collection<K> keys) {
		return getMap(map, false, keys);
	}
	public static <K, V> Map<K, V> getMap(Map<K, V> map, boolean ignoreNull, Collection<K> keys) {
		Map<K, V> toMap = new HashMap<K, V>();
		for (K key : keys) {
			V value = null;
			if (map != null) {
				value = map.get(key);
			}
			if (value == null && ignoreNull) {
				continue;
			}
			toMap.put(key, value);
		}
		return toMap;
	}
	public static class chain {

		public static <K, V> ChainMap<K, V> getMap(Class<K> keyClass, Class<V> valueClass) {
			return getMap();
		}
		public static ChainMap<String, Object> getSOMap() {
			ChainMap chainMap = new ChainMap();
			chainMap.map = CollectUtil.getMap();
			return chainMap;
		}
		public static <K, V> ChainMap<K, V> getMap() {
			ChainMap chainMap = new ChainMap();
			chainMap.map = CollectUtil.getMap();
			return chainMap;
		}
		public static <T> ChainList<T> getList(Class<T> itmeClass) {
			return getList();
		}
		public static <T> ChainList<T> getList() {
			ChainList chainList = new ChainList();
			chainList.list = CollectUtil.getList();
			return chainList;
		}
		public static <T> ChainSet<T> getSet(Class<T> itmeClass) {
			return getSet();
		}
		public static <T> ChainSet<T> getSet() {
			ChainSet chainSet = new ChainSet();
			chainSet.set = CollectUtil.getSet();
			return chainSet;
		}

		public static class ChainList<T> {
			private List<T> list;
			private ChainList() {
			}
			public List<T> getList() {
				return this.list;
			}
			public <Tc extends T> ChainList<T> add(Tc t) {
				this.list.add(t);
				return this;
			}
			public <Tc extends T> ChainList<T> addAll(List<Tc> list) {
				this.list.addAll(list);
				return this;
			}
			public <Tc extends T> ChainList<T> addAll(ChainList<Tc> chainList) {
				if (chainList != null && chainList.list != null) {
					this.list.addAll(chainList.list);
				}
				return this;
			}
			public int size() {
				return this.list.size();
			}
			public <Tc extends T> boolean contains(Tc t) {
				return this.list.contains(t);
			}
			public <Tc extends T> ChainList<T> contains(int index, Tc t) {
				this.list.set(index, t);
				return this;
			}
			public T get(int index) {
				return this.list.get(index);
			}
		}
		public static class ChainMap<K, V> {
			private Map<K, V> map;
			private ChainMap() {
			}
			public Map<K, V> getMap() {
				return this.map;
			}
			public <Kc extends K, Vc extends V> ChainMap<K, V> put(Kc key, Vc value) {
				this.map.put(key, value);
				return this;
			}
			public <Kc extends K, Vc extends V> ChainMap<K, V> putAll(Map<Kc, Vc> map) {
				this.map.putAll(map);
				return this;
			}
			public <Kc extends K, Vc extends V> ChainMap<K, V> putAll(ChainMap<Kc, Vc> chainMap) {
				if (chainMap != null && chainMap.map != null) {
					this.map.putAll(chainMap.map);
				}
				return this;
			}
			public int size() {
				return this.map.size();
			}
			public <Kc extends K> boolean containsKey(Kc key) {
				return this.map.containsKey(key);
			}
			public <Kc extends K> V get(Kc key) {
				return this.map.get(key);
			}
		}

		public static class ChainSet<T> {
			private Set<T> set;
			private ChainSet() {
			}
			public Set<T> getSet() {
				return this.set;
			}
			public <Tc extends T> ChainSet<T> add(Tc t) {
				this.set.add(t);
				return this;
			}
			public <Tc extends T> ChainSet<T> addAll(Set<Tc> set) {
				this.set.addAll(set);
				return this;
			}
			public <Tc extends T> ChainSet<T> addAll(ChainSet<Tc> chainSet) {
				if (chainSet != null && chainSet.set != null) {
					this.set.addAll(chainSet.set);
				}
				return this;
			}
			public int size() {
				return this.set.size();
			}
			public <Tc extends T> boolean contains(Tc t) {
				return this.set.contains(t);
			}
		}
	}

	/** 返回对象都没变 只是换了个结构 */
	public static class to {
		public static <T> List<T> ArrayToList(T[] array) {
			if (isEmpty(array)) {
				return getEmptyList();
			}
			return getList(array);
		}
		public static <T> Set<T> ArrayToSet(T[] array) {
			if (isEmpty(array)) {
				return getEmptySet();
			}
			return CollecToSet(Arrays.asList(array));
		}
		public static <T> Set<T> CollecToSet(Collection<T> list) {
			if (isEmpty(list)) {
				return getEmptySet();
			}
			Set<T> set = new HashSet<T>();
			for (T t : list) {
				set.add(t);
			}
			return set;
		}
		public static <T> List<T> CollecToList(Collection<T> set) {
			if (isEmpty(set)) {
				return getEmptyList();
			}
			List<T> list = new ArrayList<T>();
			for (T t : set) {
				list.add(t);
			}
			return list;
		}
		public static <T> T[] CollectToArray(Collection<T> collect) {
			if (isEmpty(collect)) {
				return null;
			}
			T next = collect.iterator().next();
			if (next == null) {
				return null;
			}
			return collect.toArray(getArray((Class<T>) next.getClass(), collect.size()));
		}

		public static <T> T[] ArrayMapToBean(Map<String, Object>[] mapArray, Class<T> clazz, String... mappingProperty) {
			if (isEmpty(mapArray)) {
				return null;
			}
			T[] array = getArray(clazz, mapArray.length);
			for (int i = 0; i < mapArray.length; i++) {
				array[i] = BeanUtil.mapToBean(mapArray[i], clazz, mappingProperty);
			}
			return array;
		}
		public static <T> Map<String, Object>[] ArrayBeanToMap(T[] array, boolean ignoreNull, String... mappingProperty) {
			if (isEmpty(array)) {
				return null;
			}
			Map<String, Object>[] mapArray = new Map[array.length];
			for (int i = 0; i < mapArray.length; i++) {
				mapArray[i] = BeanUtil.beanToMap(mapArray[i], ignoreNull, mappingProperty);
			}
			return mapArray;
		}
		public static <T> List<T> CollectMapToBean(Collection<Map<String, Object>> mapList, Class<T> clazz, String... mappingProperty) {
			if (isEmpty(mapList)) {
				return getEmptyList();
			}
			List<T> list = getList();
			for (Map<String, Object> map : mapList) {
				list.add(BeanUtil.mapToBean(map, clazz, mappingProperty));
			}
			return list;
		}
		public static <T> List<Map<String, Object>> CollectBeanToMap(Collection<T> list, boolean ignoreNull, String... mappingProperty) {
			if (isEmpty(list)) {
				return getEmptyList();
			}
			List<Map<String, Object>> mapList = getList();
			for (T bean : list) {
				mapList.add(BeanUtil.beanToMap(bean, ignoreNull, mappingProperty));
			}
			return mapList;
		}
		public static <T, A> Map<A, T> CollectToIndexMap(Collection<T> list, String propertyName) {
			if (isEmpty(list)) {
				return getEmptyMap();
			}
			if (StringUtil.isBlank(propertyName)) {
				return getEmptyMap();
			}

			Map<A, T> map = getMap();
			for (T bean : list) {
				A propertyValue = (A) BeanUtil.getPropertyValue(bean, propertyName);
				map.put(propertyValue, bean);
			}
			return map;
		}
		public static <T> Map<Map<String, Object>, T> CollectToIndexMapMoreKey(Collection<T> list, String... propertyNames) {
			if (isEmpty(list)) {
				return getEmptyMap();
			}
			if (CollectUtil.isEmpty(propertyNames)) {
				return getEmptyMap();
			}

			Map<Map<String, Object>, T> map = getMap();
			for (T bean : list) {
				Map<String, Object> propertyValueMap = CollectUtil.getSOMap();
				for (int i = 0; i < propertyNames.length; i++) {
					String propertyName = propertyNames[i];
					if (StringUtil.isBlank(propertyName)) {
						continue;
					}
					propertyValueMap.put(propertyName, BeanUtil.getPropertyValue(bean, propertyName));
				}
				map.put(propertyValueMap, bean);
			}
			return map;
		}
		public static <T, A> Map<A, T> CollectToIndexMap(Collection<T> list, String methodName, Object[] params) {
			if (isEmpty(list)) {
				return getEmptyMap();
			}
			Map<A, T> map = getMap();
			for (T bean : list) {
				A propertyValue = (A) BeanUtil.invokeBeanMethod(bean, methodName, params);
				map.put(propertyValue, bean);
			}
			return map;
		}
		public static <T, A> Map<A, List<T>> CollectToGroupMap(Collection<T> list, String methodName, Object[] params) {
			if (isEmpty(list)) {
				return getEmptyMap();
			}
			// 此处默认为有序的Map，按list-Bean里的排序来生成Map
			Map<A, List<T>> map = getLinkedMap();
			for (T bean : list) {
				A propertyValue = BeanUtil.invokeBeanMethod(bean, methodName, params);
				List<T> vList = null;
				if (map.containsKey(propertyValue)) {
					vList = map.get(propertyValue);
				} else {
					vList = new ArrayList<T>();
				}
				vList.add(bean);
				map.put(propertyValue, vList);
			}
			return map;
		}
		public static <T, A> Map<A, List<T>> CollectToGroupMap(Collection<T> list, String propertyName) {
			if (isEmpty(list)) {
				return getEmptyMap();
			}
			if (StringUtil.isBlank(propertyName)) {
				return getEmptyMap();
			}

			// 此处默认为有序的Map，按list-Bean里的排序来生成Map
			Map<A, List<T>> map = getLinkedMap();
			for (T bean : list) {
				A propertyValue = BeanUtil.getPropertyValue(bean, propertyName);
				List<T> vList = null;
				if (map.containsKey(propertyValue)) {
					vList = map.get(propertyValue);
				} else {
					vList = new ArrayList<T>();
				}
				vList.add(bean);
				map.put(propertyValue, vList);
			}
			return map;
		}

		public static <T> Map<Map<String, Object>, List<T>> CollectToGroupMapMoreKey(Collection<T> list, String... propertyNames) {
			if (isEmpty(list)) {
				return getEmptyMap();
			}
			if (CollectUtil.isEmpty(propertyNames)) {
				return getEmptyMap();
			}
			// 此处默认为有序的Map，按list-Bean里的排序来生成Map
			Map<Map<String, Object>, List<T>> map = getLinkedMap();
			for (T bean : list) {
				Map<String, Object> propertyValueMap = CollectUtil.getSOMap();
				for (int i = 0; i < propertyNames.length; i++) {
					String propertyName = propertyNames[i];
					if (StringUtil.isBlank(propertyName)) {
						continue;
					}
					propertyValueMap.put(propertyName, BeanUtil.getPropertyValue(bean, propertyName));
				}
				List<T> vList = null;
				if (map.containsKey(propertyValueMap)) {
					vList = map.get(propertyValueMap);
				} else {
					vList = new ArrayList<T>();
				}
				vList.add(bean);
				map.put(propertyValueMap, vList);
			}
			return map;
		}

		@SuppressWarnings("unlikely-arg-type")
		public static <K, V> Map<V, List<Map<K, V>>> CollectMapToGroupMap(Collection<Map<K, V>> list, String propertyName) {
			if (isEmpty(list)) {
				return getEmptyMap();
			}
			Map<V, List<Map<K, V>>> groupMap = getLinkedMap();
			for (Map<K, V> mapItem : list) {
				V v = mapItem.get(propertyName);

				List<Map<K, V>> vMapList = null;
				if (mapItem.containsKey(v)) {
					vMapList = groupMap.get(v);
				} else {
					vMapList = new ArrayList<Map<K, V>>();
				}
				vMapList.add(mapItem);

				groupMap.put(v, vMapList);
			}
			return groupMap;
		}
		public static <T, A> Collection<T> CollectPropertyEqual(Collection<T> collect, String propertyName, Set<A> propertyvalue) {
			if (isEmpty(collect)) {
				return collect;
			}
			if (StringUtil.isEmpty(propertyName)) {
				return collect;
			}
			if (StringUtil.isNotEmpty(propertyName) && isEmpty(propertyvalue)) {
				return getEmptyList();
			}

			List<T> arrayList = new ArrayList<T>();
			for (T t : collect) {
				Object getPropertyValue = BeanUtil.getPropertyValue(t, propertyName);
				if (propertyvalue.contains(getPropertyValue)) {
					arrayList.add(t);
				}
			}
			return arrayList;
		}
		public static <T, A> Collection<T> CollectPropertyUnEqual(Collection<T> collect, String propertyName, Set<A> propertyvalue) {
			if (isEmpty(collect)) {
				return collect;
			}
			if (StringUtil.isEmpty(propertyName)) {
				return collect;
			}
			if (StringUtil.isNotEmpty(propertyName) && isEmpty(propertyvalue)) {
				return getEmptyList();
			}

			List<T> arrayList = new ArrayList<T>();
			for (T t : collect) {
				Object getPropertyValue = BeanUtil.getPropertyValue(t, propertyName);
				if (!propertyvalue.contains(getPropertyValue)) {
					arrayList.add(t);
				}
			}
			return arrayList;
		}
		public static <T, A> Collection<T> CollectInvokeEqual(Collection<T> collect, String methodName, Object[] params, Set<A> propertyvalue) {
			if (isEmpty(collect)) {
				return collect;
			}
			if (StringUtil.isEmpty(methodName)) {
				return collect;
			}
			if (StringUtil.isNotEmpty(methodName) && isEmpty(propertyvalue)) {
				return getEmptyList();
			}
			List<T> arrayList = new ArrayList<T>();
			for (T t : collect) {
				Object getPropertyValue = BeanUtil.invokeBeanMethod(t, methodName, params);
				if (propertyvalue.contains(getPropertyValue)) {
					arrayList.add(t);
				}
			}
			return arrayList;
		}
		public static <T, A> Collection<T> CollectInvokeUnEqual(Collection<T> collect, String methodName, Object[] params, Set<A> propertyvalue) {
			if (isEmpty(collect)) {
				return collect;
			}
			if (StringUtil.isEmpty(methodName)) {
				return collect;
			}
			if (StringUtil.isNotEmpty(methodName) && isEmpty(propertyvalue)) {
				return getEmptyList();
			}
			List<T> arrayList = new ArrayList<T>();
			for (T t : collect) {
				Object getPropertyValue = BeanUtil.invokeBeanMethod(t, methodName, params);
				if (!propertyvalue.contains(getPropertyValue)) {
					arrayList.add(t);
				}
			}
			return arrayList;
		}

	}
	/** 返回的对象变了 */
	public static class item {
		public static <T, A> List<A> CollectCopyItemList(Collection<T> list, Class<A> clazz, String... mappingProperty) {
			if (isEmpty(list)) {
				return getEmptyList();
			}
			List<A> otherLsit = new ArrayList<A>(list.size());
			for (T t : list) {
				A copyProperty = BeanUtil.copyProperty(t, ClassUtil.newInstance(clazz), mappingProperty);
				otherLsit.add(copyProperty);
			}
			return otherLsit;
		}

		public static <T, A> List<A> getPropertyList(Collection<T> collect, String propertyName) {
			return getPropertyList(collect, propertyName, false);
		}

		public static <T, A> List<A> getPropertyList(Collection<T> collect, String propertyName, boolean ignoreNull) {
			if (isEmpty(collect)) {
				return getEmptyList();
			}
			List<A> list = getList();
			for (T bean : collect) {
				if (BeanUtil.isPropertyOrMethod(bean, propertyName)) {
					A value = BeanUtil.getPropertyValue(bean, propertyName);
					if (value == null && ignoreNull) {
						continue;
					}
					list.add(value);
				}
			}
			return list;
		}

		public static <T, A> Set<A> getPropertySet(Collection<T> collect, String propertyName) {
			return getPropertySet(collect, propertyName, false);
		}

		public static <T, A> Set<A> getPropertySet(Collection<T> collect, String propertyName, boolean ignoreNull) {
			if (isEmpty(collect)) {
				return getEmptySet();
			}
			Set<A> set = getSet();
			for (T bean : collect) {
				if (BeanUtil.isPropertyOrMethod(bean, propertyName)) {
					A value = BeanUtil.getPropertyValue(bean, propertyName);
					if (value == null && ignoreNull) {
						continue;
					}
					set.add(value);
				}
			}
			return set;
		}

		public static <T, A> List<A> getInvokeList(Collection<T> collect, String methodName, Object[] params) {
			if (isEmpty(collect)) {
				return getEmptyList();
			}
			List<A> list = getList();
			for (T bean : collect) {
				if (BeanUtil.isPropertyOrMethod(bean, methodName)) {
					A value = BeanUtil.invokeBeanMethod(bean, methodName, params);
					list.add(value);
				}
			}
			return list;
		}

		public static <T, A> Set<A> getInvokeSet(Collection<T> collect, String methodName, Object[] params) {
			if (isEmpty(collect)) {
				return getEmptySet();
			}
			Set<A> set = getSet();
			for (T bean : collect) {
				if (BeanUtil.isPropertyOrMethod(bean, methodName)) {
					A value = BeanUtil.invokeBeanMethod(bean, methodName, params);
					set.add(value);
				}
			}
			return set;
		}

		public static <T, A> List<A> getInvokeList(T[] array, String methodName, Object[] params) {
			if (isEmpty(array)) {
				return getEmptyList();
			}
			List<A> list = getList();
			for (T bean : array) {
				if (BeanUtil.isPropertyOrMethod(bean, methodName)) {
					A value = BeanUtil.invokeBeanMethod(bean, methodName, params);
					list.add(value);
				}
			}
			return list;
		}

		public static <T, A> Set<A> getInvokeSet(T[] array, String methodName, Object[] params) {
			if (isEmpty(array)) {
				return getEmptySet();
			}
			Set<A> set = getSet();
			for (T bean : array) {
				if (BeanUtil.isPropertyOrMethod(bean, methodName)) {
					A value = BeanUtil.invokeBeanMethod(bean, methodName, params);
					set.add(value);
				}
			}
			return set;
		}

		public static <K, V> List<V> getMapValueList(Map<K, V> map, K... keys) {
			return getMapValueList(map, to.ArrayToList(keys));
		}
		public static <K, V> List<V> getMapValueList(Map<K, V> map, Collection<K> keys) {
			if (isEmpty(map)) {
				return getEmptyList();
			}
			List<V> list = getList();
			for (K key : map.keySet()) {
				if (isNotEmpty(keys) && keys.contains(key)) {
					list.add(map.get(key));
				}
			}
			return list;
		}

		public static <K, V> Set<V> getMapValueSet(Map<K, V> map, K... keys) {
			return getMapValueSet(map, to.ArrayToList(keys));
		}
		public static <K, V> Set<V> getMapValueSet(Map<K, V> map, Collection<K> keys) {
			if (isEmpty(map)) {
				return getEmptySet();
			}
			Set<V> set = getSet();
			for (K key : map.keySet()) {
				if (isNotEmpty(keys) && keys.contains(key)) {
					set.add(map.get(key));
				}
			}
			return set;
		}

		public static <T> T getCollectIndexItem(Collection<T> collect, int index) {
			if (index < 0) {
				return null;
			}
			if (index > collect.size()) {
				throw new RuntimeException(new ArrayIndexOutOfBoundsException());
			}
			int p_index = 0;
			for (T t : collect) {
				if (p_index == index) {
					return t;
				}
				p_index++;
			}
			return null;
		}
		public static <T> boolean setCollectIndexItem(Collection<T> collect, int index, T value) {
			if (collect == null || index < 0) {
				return false;
			}
			if (index > collect.size()) {
				throw new RuntimeException(new ArrayIndexOutOfBoundsException());
			}
			int p_index = 0;
			for (T t : collect) {
				if (p_index == index && t == null && value == null) {
					return true;
				}
				if (p_index == index && t != null && t.equals(value)) {
					return true;
				}
				if (p_index == index && t != null && !t.equals(value)) {
					List<T> list = getList();

					int p_index2 = 0;
					for (Iterator<T> iterator = collect.iterator(); iterator.hasNext(); p_index2++) {
						T next = iterator.next();
						if (p_index2 < p_index) {
							continue;
						}
						if (p_index2 > p_index) {
							list.add(next);
						}
						iterator.remove();
					}
					collect.add(value);
					collect.addAll(list);
					return true;
				}
				p_index++;
			}
			return false;
		}

		public static <T> T[] ArrayIndexSet(T[] array, int index, T obj) {
			if (isEmpty(array)) {
				return array;
			}
			Array.set(array, index, obj);
			return array;
		}

		public static <T> boolean ArrayContains(T[] array, T obj) {
			if (array == null) {
				return false;
			}
			return item.ArrayIndex(array, obj) != -1;
		}
		public static <T> Integer ArrayIndex(T[] array, T obj) {
			if (array == null) {
				return null;
			}
			for (int i = 0; i < array.length; i++) {
				if (array[i] == null && obj == null) {
					return i;
				}
				if (array[i] != null && array[i].equals(obj)) {
					return i;
				}
			}
			return -1;
		}

		public static <T> boolean CollectContains(Collection<T> collect, T obj) {
			if (collect == null) {
				return false;
			}
			return item.CollectIndex(collect, obj) != -1;
		}
		public static <T> Integer CollectIndex(Collection<T> collect, T obj) {
			if (collect == null) {
				return null;
			}
			int i = 0;
			for (T item : collect) {
				if (item == null && obj == null) {
					return i;
				}
				if (item != null && item.equals(obj)) {
					return i;
				}
				i++;
			}
			return -1;
		}

		public static <T, A> Collection<T> CollectSetProperty(Collection<T> collect, String propertyName, A propertyValue) {
			if (isEmpty(collect)) {
				return collect;
			}
			if (StringUtil.isEmpty(propertyName)) {
				return collect;
			}
			for (T t : collect) {
				if (BeanUtil.isPropertyOrMethod(t, propertyName)) {
					BeanUtil.setPropertyValue(t, propertyName, propertyValue);
				}
			}
			return collect;
		}
		public static <T, A> Collection<T> CollectInvokeMethod(Collection<T> collect, String methodName, Object[] params) {
			if (isEmpty(collect)) {
				return collect;
			}
			if (StringUtil.isEmpty(methodName)) {
				return collect;
			}
			for (T t : collect) {
				if (BeanUtil.isPropertyOrMethod(t, methodName)) {
					BeanUtil.invokeBeanMethod(t, methodName, params);
				}
			}
			return collect;
		}
	}
	/**
	 * 针对整个集合 <br/>
	 * <b>这里有一个隐藏很深的问题请注意</b><br/>
	 * 有一些是有动到原对象的 有一些例如对数组的基本是没有影响到原对象的 <br/>
	 */
	public static class opt {
		/** 有动到原对象 */
		public static <T> Collection<T> subNull(Collection<T> collect) {
			if (isEmpty(collect)) {
				return collect;
			}
			for (Iterator<T> iterator = collect.iterator(); iterator.hasNext();) {
				T t = iterator.next();
				if (t == null) {
					iterator.remove();
				}
			}
			return collect;
		}
		/** 没有动到原对象 */
		public static <T> T[] subNull(T... array) {
			if (isEmpty(array)) {
				return array;
			}
			List<T> arrayToList = to.ArrayToList(array);
			for (Iterator<T> iterator = arrayToList.iterator(); iterator.hasNext();) {
				T t = iterator.next();
				if (t == null) {
					iterator.remove();
				}
			}
			return to.CollectToArray(arrayToList);
		}
		/** 看情况有动到原对象 */
		public static <T> Collection<T> CollectAdd(Collection<T> collect, Collection<T> toCollect) {
			if (isNull(collect) && isNull(toCollect)) {
				return getEmptyList();
			}
			if (isNull(collect) && isNotNull(toCollect)) {
				return toCollect;
			}
			if (isNotNull(collect) && isNull(toCollect)) {
				return collect;
			}
			collect.addAll(toCollect);
			return collect;
		}

		/** 有动到原对象 */
		public static <T> Collection<T> CollectSubtract(Collection<T> collect, T... toCollect) {
			return CollectSubtract(collect, to.ArrayToList(toCollect));
		}
		/** 有动到原对象 */
		public static <T> Collection<T> CollectSubtract(Collection<T> collect, Collection<T> toCollect) {
			if (isEmpty(collect)) {
				return getEmptyList();
			}
			if (isEmpty(toCollect)) {
				return collect;
			}
			// List<T> resCollect = getList();
			// for (T t : collect) {
			// if (!toCollect.contains(t)) {
			// resCollect.add(t);
			// }
			// }
			// return resCollect;
			for (Iterator<T> iterator = collect.iterator(); iterator.hasNext();) {
				T t = iterator.next();
				if (toCollect.contains(t)) {
					iterator.remove();
				}
			}
			return collect;
		}

		/** 没有动到原对象 */
		public static <T> Set<T> CollectIntersection(Collection<T> collect, Collection<T> otherCollect) {
			if (isEmpty(collect)) {
				return getEmptySet();
			}
			if (isEmpty(otherCollect)) {
				return getEmptySet();
			}
			Set<T> intersection = new HashSet<T>();
			for (Iterator<T> iterator = collect.iterator(); iterator.hasNext();) {
				T t = iterator.next();
				if (otherCollect.contains(t)) {
					intersection.add(t);
				}
			}
			return intersection;
		}

		/** A集合中不属于（A交B）的部分 没有动到原对象 */
		public static <T> Set<T> CollectComplement(Collection<T> collect, Collection<T> otherCollect) {
			if (isEmpty(collect)) {
				return getEmptySet();
			}
			if (isNotEmpty(collect) && isEmpty(otherCollect)) {
				HashSet<T> hashSet = new HashSet<T>(0);
				hashSet.addAll(collect);
				return hashSet;
			}
			Set<T> intersection = new HashSet<T>();
			for (Iterator<T> iterator = collect.iterator(); iterator.hasNext();) {
				T t = iterator.next();
				if (!otherCollect.contains(t)) {
					intersection.add(t);
				}
			}
			return intersection;
		}

		/** 没有动到原对象 */
		public static <T> T[] ArrAdd(T[] arr, T[] toArr) {
			if (isNull(arr) && isNull(toArr)) {
				return null;
			}
			if (isNull(arr) && isNotNull(toArr)) {
				return toArr;
			}
			if (isNotNull(arr) && isNull(toArr)) {
				return arr;
			}
			List<T> arrayToList = to.ArrayToList(arr);
			List<T> toAarrayToList = to.ArrayToList(toArr);
			Collection<T> collectAdd = opt.CollectAdd(arrayToList, toAarrayToList);
			return to.CollectToArray(collectAdd);
		}
		/** 没有动到原对象 */
		public static <T> T[] ArrSubtract(T[] arr, T[] toArr) {
			if (isEmpty(arr) || isEmpty(toArr)) {
				return arr;
			}
			List<T> arrayToList = to.ArrayToList(arr);
			List<T> arrayToList2 = to.ArrayToList(toArr);
			Collection<T> collectAdd = opt.CollectSubtract(arrayToList, arrayToList2);
			return to.CollectToArray(collectAdd);
		}
		/** 没有动到原对象 */
		public static <T> T[] ArrIntersection(T[] arr, T[] toArr) {
			if (isEmpty(arr) || isEmpty(toArr)) {
				return arr;
			}
			List<T> arrayToList = to.ArrayToList(arr);
			List<T> arrayToList2 = to.ArrayToList(toArr);
			Collection<T> collectAdd = opt.CollectSubtract(arrayToList, arrayToList2);
			return to.CollectToArray(collectAdd);
		}
		/** 没有动到原对象 */
		public static <T> T[] ArrAddList(T[] array, List<T> list) {
			if (isNull(array) && isNull(list)) {
				return array;
			}
			if (isNotNull(array) && isNull(list)) {
				return array;
			}
			if (isNull(array) && isNotNull(list)) {
				return to.CollectToArray(list);
			}
			list.addAll(Arrays.asList(array));
			return to.CollectToArray(list);
		}
		/** 有动到原对象 */
		public static <T> List<T> ListAddArr(List<T> list, T[] array) {
			if (isNull(list) && isNull(array)) {
				return getEmptyList();
			}
			if (isNull(list) && isNotNull(array)) {
				return Arrays.asList(array);
			}
			if (isNotNull(list) && isNull(array)) {
				return list;
			}
			list.addAll(Arrays.asList(array));
			return list;
		}

	}
	public static class sort {
		public static <T> T[] ReverseArray(T[] array) {
			if (isEmpty(array)) {
				return array;
			}
			int quotient = array.length / 2;
			for (int i = 0; i < quotient; i++) {
				T left = array[i];
				T right = array[(array.length - 1) - i];

				array[i] = right;
				array[(array.length - 1) - i] = left;
			}
			return array;
		}
		public static <T> List<T> ReverseList(List<T> list) {
			if (isEmpty(list)) {
				return list;
			}
			int quotient = list.size() / 2;
			for (int i = 0; i < quotient; i++) {
				T left = list.get(i);
				T right = list.get((list.size() - 1) - i);

				list.set(i, right);
				list.set((list.size() - 1) - i, left);
			}
			return list;
		}

		/**
		 * 实现快速排序
		 * 
		 * @param list
		 *            待排序列
		 * @param leftIndex
		 *            待排序列起始位置
		 * @param rightIndex
		 *            待排序列结束位置
		 */
		private static <T extends Number> List<T> quickSort(List<T> list, int leftIndex, int rightIndex) {
			if (leftIndex >= rightIndex) {
				return null;
			}
			int left = leftIndex;
			int right = rightIndex;
			// 待排序的第一个元素作为基准值
			T key = list.get(left);
			// 从左右两边交替扫描，直到left = right
			while (left < right) {
				BigDecimal keyBigDecimal = new BigDecimal(key.toString());

				while (right > left && new BigDecimal(list.get(right).toString()).compareTo(keyBigDecimal) > -1) {
					// 从右往左扫描，找到第一个比基准值小的元素
					right--;
				}
				// 找到这种元素将arr[right]放入arr[left]中
				list.set(left, list.get(right));

				while (left < right && new BigDecimal(list.get(left).toString()).compareTo(keyBigDecimal) < 1) {
					// 从左往右扫描，找到第一个比基准值大的元素
					left++;
				}
				// 找到这种元素将arr[left]放入arr[right]中
				list.set(right, list.get(left));
			}
			// 基准值归位
			list.set(left, key);
			// 对基准值左边的元素进行递归排序
			quickSort(list, leftIndex, left - 1);
			// 对基准值右边的元素进行递归排序。
			quickSort(list, right + 1, rightIndex);

			return list;
		}
		public static <T extends Number> T[] NumberSort(T[] array) {
			if (isEmpty(array)) {
				return array;
			}
			List<T> toList = to.ArrayToList(array);

			List<T> subNull = (List<T>) opt.subNull(toList);
			if (isEmpty(subNull)) {
				return array;
			}

			List<T> quickSort = quickSort(subNull, 0, subNull.size() - 1);
			for (int i = 0; i < quickSort.size(); i++) {
				array[i] = quickSort.get(i);
			}
			for (int i = quickSort.size(); i < array.length; i++) {
				array[i] = null;
			}
			return array;
		}

		public static <T extends Number> List<T> NumberSort(Collection<T> collect) {
			if (isEmpty(collect)) {
				return getEmptyList();
			}
			Collection<T> subNull = opt.subNull(collect);
			if (isEmpty(subNull)) {
				return getEmptyList();
			}

			List<T> list = getList();
			for (Iterator<T> iterator = subNull.iterator(); iterator.hasNext();) {
				list.add(iterator.next());
			}
			List<T> quickSort = quickSort(list, 0, list.size() - 1);
			for (int i = quickSort.size(); i < collect.size(); i++) {
				quickSort.add(null);
			}
			return quickSort;
		}

		public static String[] StringUnCaseSort(String[] array) {
			Arrays.sort(array, String.CASE_INSENSITIVE_ORDER);
			return array;
		}

		public static interface AtSort<T> {
			/** RETURN 1 << ；RETURN -1 >> */
			public int getSortValue(T bean1, T bean2) throws Exception;
		}
		public static <T> T[] ArraySort(T[] array, final AtSort<T> atSort) {
			Arrays.sort(array, new Comparator<T>() {
				@Override
				public int compare(T bean1, T bean2) {
					try {
						return atSort.getSortValue(bean1, bean2);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			});
			return array;
		}
		public static <T> List<T> ListSort(List<T> list, final AtSort<T> atSort) {
			Collections.sort(list, new Comparator<T>() {
				@Override
				public int compare(T bean1, T bean2) {
					try {
						return atSort.getSortValue(bean1, bean2);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			});
			return list;
		}
	}
	public static class func {

	}
}
