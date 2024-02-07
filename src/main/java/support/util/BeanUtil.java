package support.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * @author zhuangly
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class BeanUtil {
	public static <T, A> A invokeBeanMethod(T bean, String methodName) {
		return invokeBeanMethod(bean, methodName, null, null);
	}
	/** 请注意参数本身就是数组的情况 */
	public static <T, A> A invokeBeanMethod(T bean, String methodName, Object[] params) {
		return invokeBeanMethod(bean, methodName, null, params);
	}
	/** 请注意参数本身就是数组的情况 */
	public static <T, A> A invokeBeanMethod(T bean, String methodName, Class<?>[] parameterTypes, Object[] params) {
		if (bean == null) {
			return null;
		}

		int childIndexOf = methodName.indexOf(".");
		if (-1 != childIndexOf) {
			String childBeanName = methodName.substring(0, childIndexOf);
			String childMethodName = methodName.substring(childIndexOf + 1, methodName.length());
			if (StringUtil.isEmpty(childMethodName)) {
				throw new RuntimeException(new NoSuchFieldException());
			} else {
				return invokeBeanMethod(getPropertyValue(bean, childBeanName), childMethodName, parameterTypes, params);
			}
		}

		Method invokeMethod = ClassUtil.getClassMethod(bean.getClass(), methodName, parameterTypes, params);
		if (invokeMethod == null) {
			throw new RuntimeException("Method Not Found");
		}

		try {
			// 解除私有方法访问限制
			invokeMethod.setAccessible(true);
			if ("void".equals(invokeMethod.getReturnType().getName())) {
				invokeMethod.invoke(bean, params);
				return null;
			} else {
				return (A) invokeMethod.invoke(bean, params);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T, A> boolean isPropertyOrMethod(T bean, String propertyOrMethodName) {
		if (bean == null) {
			return false;
		}
		if (StringUtil.isEmpty(propertyOrMethodName)) {
			return false;
		}

		int childIndexOf = propertyOrMethodName.indexOf(".");
		if (-1 != childIndexOf) {
			String thisPropertyName = propertyOrMethodName.substring(0, childIndexOf);
			String childPropertyName = propertyOrMethodName.substring(childIndexOf + 1, propertyOrMethodName.length());
			if (StringUtil.isEmpty(childPropertyName)) {
				return false;
			} else {
				if (!isPropertyOrMethod(bean, thisPropertyName)) {
					return false;
				}
				return isPropertyOrMethod(getPropertyValue(bean, thisPropertyName), childPropertyName);
			}
		}

		int collecLeftIndexOf = propertyOrMethodName.lastIndexOf("[");
		int collecRightIndexOf = propertyOrMethodName.indexOf("]", collecLeftIndexOf);
		if (-1 != collecLeftIndexOf && -1 != collecRightIndexOf) {
			String indexStr = propertyOrMethodName.substring(collecLeftIndexOf + 1, collecRightIndexOf);
			if (StringUtil.isEmpty(indexStr)) {
				return false;
			}
			Integer index = null;
			try {
				index = Integer.valueOf(indexStr);
				if (index == null) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}

			String collecPropertyName = propertyOrMethodName.substring(0, collecLeftIndexOf);
			Object collecBean = null;
			if (StringUtil.isNotEmpty(collecPropertyName)) {
				collecBean = getPropertyValue(bean, collecPropertyName);
			} else {
				collecBean = bean;
			}

			if (ClassUtil.isArray(collecBean.getClass())) {
				Object[] array = (Object[]) collecBean;
				if (index < 0 || array.length - 1 < index) {
					return false;
				} else {
					return true;
				}
			} else if (ClassUtil.isCollection(collecBean.getClass())) {
				Collection collection = (Collection) collecBean;
				if (index < 0 || collection.size() - 1 < index) {
					return false;
				} else {
					return true;
				}
			}
		}

		int methodLeftIndexOf = propertyOrMethodName.lastIndexOf("(");
		int methodRightIndexOf = propertyOrMethodName.indexOf(")", methodLeftIndexOf);
		if (-1 != methodLeftIndexOf && -1 != methodRightIndexOf) {
			String methodPropertyName = propertyOrMethodName.substring(0, methodLeftIndexOf);
			if (StringUtil.isEmpty(methodPropertyName)) {
				return false;
			}
			Method invokeMethod = ClassUtil.getClassMethod(bean.getClass(), methodPropertyName, null, null);
			if (invokeMethod == null) {
				return false;
			} else {
				return true;
			}
		}

		Class<? extends Object> beanClass = bean.getClass();

		if (ClassUtil.isSuperClass(Map.class, beanClass)) {
			Map mapBean = (Map) bean;
			if (!mapBean.containsKey(propertyOrMethodName)) {
				return false;
			} else {
				return true;
			}
		}

		Field field = ClassUtil.getClassField(beanClass, propertyOrMethodName);

		Method method = ClassUtil.getClassMethod(beanClass, propertyOrMethodName, null, null);

		if (field == null && method == null) {
			return false;
		} else {
			return true;
		}
	}

	public static <T, A> A getPropertyValue(T bean, String propertyName) {
		if (bean == null) {
			throw new RuntimeException(new NullPointerException());
		}
		if (StringUtil.isEmpty(propertyName)) {
			throw new RuntimeException(new NoSuchFieldException());
		}

		int childIndexOf = propertyName.indexOf(".");
		if (-1 != childIndexOf) {
			String thisPropertyName = propertyName.substring(0, childIndexOf);
			String childPropertyName = propertyName.substring(childIndexOf + 1, propertyName.length());
			if (StringUtil.isEmpty(childPropertyName)) {
				throw new RuntimeException(new NoSuchFieldException());
			} else {
				return getPropertyValue(getPropertyValue(bean, thisPropertyName), childPropertyName);
			}
		}

		int collecLeftIndexOf = propertyName.lastIndexOf("[");
		int collecRightIndexOf = propertyName.indexOf("]", collecLeftIndexOf);
		if (-1 != collecLeftIndexOf && -1 != collecRightIndexOf) {
			String indexStr = propertyName.substring(collecLeftIndexOf + 1, collecRightIndexOf);
			if (StringUtil.isEmpty(indexStr)) {
				throw new RuntimeException(new ArrayIndexOutOfBoundsException());
			}
			Integer index = null;
			try {
				index = Integer.valueOf(indexStr);
				if (index == null) {
					throw new RuntimeException(new ArrayIndexOutOfBoundsException());
				}
			} catch (Exception e) {
				throw new RuntimeException(new ArrayIndexOutOfBoundsException(propertyName + " Index " + indexStr) + " is not Integer");
			}

			String collecPropertyName = propertyName.substring(0, collecLeftIndexOf);
			Object collecBean = null;
			if (StringUtil.isNotEmpty(collecPropertyName)) {
				collecBean = getPropertyValue(bean, collecPropertyName);
			} else {
				collecBean = bean;
			}

			if (ClassUtil.isArray(collecBean.getClass())) {
				Object[] array = (Object[]) collecBean;
				if (index < 0 || array.length - 1 < index) {
					throw new RuntimeException(new ArrayIndexOutOfBoundsException());
				}
				return (A) array[index];
			} else if (ClassUtil.isCollection(collecBean.getClass())) {
				Collection collection = (Collection) collecBean;
				if (index < 0 || collection.size() - 1 < index) {
					throw new RuntimeException(new ArrayIndexOutOfBoundsException());
				}
				return (A) CollectUtil.item.getCollectIndexItem(collection, index);
			}
		}

		int methodLeftIndexOf = propertyName.lastIndexOf("(");
		int methodRightIndexOf = propertyName.indexOf(")", methodLeftIndexOf);
		if (-1 != methodLeftIndexOf && -1 != methodRightIndexOf) {
			String methodPropertyName = propertyName.substring(0, methodLeftIndexOf);
			if (StringUtil.isEmpty(methodPropertyName)) {
				throw new RuntimeException(new NoSuchMethodException());
			}
			return invokeBeanMethod(bean, methodPropertyName);
		}

		Class<? extends Object> beanClass = bean.getClass();

		if (ClassUtil.isSuperClass(Map.class, beanClass)) {
			Map mapBean = (Map) bean;
			if (!mapBean.containsKey(propertyName)) {
				throw new RuntimeException(new NoSuchFieldException());
			} else {
				return (A) mapBean.get(propertyName);
			}
		}

		Field field = null;
		while (beanClass != null && field == null) {
			try {
				field = beanClass.getDeclaredField(propertyName);
			} catch (NoSuchFieldException e) {
				beanClass = beanClass.getSuperclass();
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			}
		}
		if (field == null) {
			throw new RuntimeException(new NoSuchFieldException());
		}
		try {
			Method getMethod = new PropertyDescriptor(field.getName(), beanClass).getReadMethod();
			if (getMethod == null) {
				throw new NoSuchMethodError();
			}
			getMethod.setAccessible(true);
			return (A) getMethod.invoke(bean);
		} catch (Exception e) {
			try {
				field.setAccessible(true);
				return (A) field.get(bean);
			} catch (Exception e2) {
				e2.addSuppressed(e);
				throw new RuntimeException(e2);
			}
		}
	}

	public static <T, A> void setPropertyValue(T bean, String propertyName, A propertyValue) {
		if (bean == null) {
			throw new RuntimeException(new NullPointerException());
		}
		if (StringUtil.isEmpty(propertyName)) {
			throw new RuntimeException(new NoSuchFieldException());
		}

		int indexOf = propertyName.indexOf(".");
		if (-1 != indexOf) {
			String thisPropertyName = propertyName.substring(0, indexOf);
			String childPropertyName = propertyName.substring(indexOf + 1, propertyName.length());
			if (StringUtil.isEmpty(childPropertyName)) {
				throw new RuntimeException(new NoSuchFieldException());
			} else {
				setPropertyValue(getPropertyValue(bean, thisPropertyName), childPropertyName, propertyValue);
			}
		}

		int collecLeftIndexOf = propertyName.lastIndexOf("[");
		int collecRightIndexOf = propertyName.indexOf("]", collecLeftIndexOf);
		if (-1 != collecLeftIndexOf && -1 != collecRightIndexOf) {
			String indexStr = propertyName.substring(collecLeftIndexOf + 1, collecRightIndexOf);
			if (StringUtil.isEmpty(indexStr)) {
				throw new RuntimeException(new ArrayIndexOutOfBoundsException());
			}
			Integer index = null;
			try {
				index = Integer.valueOf(indexStr);
				if (index == null) {
					throw new RuntimeException(new ArrayIndexOutOfBoundsException());
				}
			} catch (Exception e) {
				throw new RuntimeException(new ArrayIndexOutOfBoundsException(propertyName + " Index " + indexStr) + " is not Integer");
			}

			String collecPropertyName = propertyName.substring(0, collecLeftIndexOf);
			Object collecBean = null;
			if (StringUtil.isNotEmpty(collecPropertyName)) {
				collecBean = getPropertyValue(bean, collecPropertyName);
			} else {
				collecBean = bean;
			}

			// 这边其实可以直接扩展长度 但是如果用户填了一个极大的数呢 所以安全起见不扩展现有集合长度
			if (ClassUtil.isArray(collecBean.getClass())) {
				Object[] array = (Object[]) collecBean;
				if (index < 0 || array.length - 1 < index) {
					throw new RuntimeException(new ArrayIndexOutOfBoundsException());
				}
				array[index] = propertyValue;
				return;
			} else if (ClassUtil.isCollection(collecBean.getClass())) {
				Collection collection = (Collection) collecBean;
				if (index < 0 || collection.size() - 1 < index) {
					throw new RuntimeException(new ArrayIndexOutOfBoundsException());
				}
				CollectUtil.item.setCollectIndexItem(collection, index, propertyValue);
				return;
			}
		}

		Class<? extends Object> beanClass = bean.getClass();

		if (ClassUtil.isSuperClass(Map.class, beanClass)) {
			Map mapBean = (Map) bean;
			mapBean.put(propertyName, propertyValue);
			return;
		}

		Field field = null;
		while (beanClass != null && field == null) {
			try {
				field = beanClass.getDeclaredField(propertyName);
			} catch (NoSuchFieldException e) {
				beanClass = beanClass.getSuperclass();
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			}
		}
		if (field == null) {
			return;
		}
		if (Modifier.isFinal(field.getModifiers())) {
			return;
		}
		if (Modifier.isStatic(field.getModifiers())) {
			// 静态值也可以赋值--安全起见这个工具不支持
			// 走ClassUtil。setStaticPropertyValue()
			return;
		}
		try {
			Method setMethod = new PropertyDescriptor(field.getName(), beanClass).getWriteMethod();
			if (setMethod == null) {
				throw new NoSuchMethodError();
			}
			setMethod.setAccessible(true);
			setMethod.invoke(bean, propertyValue);
			return;
		} catch (Exception e) {
			try {
				field.setAccessible(true);
				field.set(bean, propertyValue);
				return;
			} catch (Exception e2) {
				e2.addSuppressed(e);

				if (propertyValue == null) {
					return;
				}

				Class<?> fieldType = getCanonicalName(field.getType());
				String propertyValueStr = String.valueOf(propertyValue);

				if (ClassUtil.isSuperClass(Number.class, fieldType)) {
					if (propertyValueStr.trim().length() == 0) {
						return;
					}
					BigDecimal gigDecimal = new BigDecimal(propertyValueStr);
					String fieldTypeName = fieldType.getName();
					switch (fieldTypeName) {
						case "java.lang.Integer" :
							setPropertyValue(bean, propertyName, gigDecimal.intValue());
							break;
						case "java.lang.Long" :
							setPropertyValue(bean, propertyName, gigDecimal.longValue());
							break;
						case "java.lang.Double" :
							setPropertyValue(bean, propertyName, gigDecimal.doubleValue());
							break;
						case "java.lang.Float" :
							setPropertyValue(bean, propertyName, gigDecimal.floatValue());
							break;
						case "java.lang.Short" :
							setPropertyValue(bean, propertyName, gigDecimal.shortValue());
							break;
					}
					return;
				}

				if (ClassUtil.isSuperClass(String.class, fieldType)) {
					if (Date.class.equals(propertyValue.getClass())) {
						setPropertyValue(bean, propertyName, DateUtil.DateToString((Date) propertyValue, DateUtil.DateStyle.YYYY_MM_DD_HH_MM_SS));
						return;
					}

					setPropertyValue(bean, propertyName, propertyValueStr);
					return;
				}

				if (ClassUtil.isArray(fieldType)) {
					if (ClassUtil.isSuperClass(fieldType.getComponentType(), propertyValue.getClass())) {
						setPropertyValue(bean, propertyName, CollectUtil.getArray(propertyValue));
						return;
					}
					if (ClassUtil.isSuperClass(String.class, propertyValue.getClass()) && JsonUtil.isArray(propertyValueStr)) {
						setPropertyValue(bean, propertyName, JsonUtil.toObject(propertyValueStr, fieldType));
						return;
					}
				}

				if (ClassUtil.isSuperClass(List.class, fieldType)) {
					if (StringUtil.subString(field.getGenericType().getTypeName(), "<", ">").equals(propertyValue.getClass().getName())) {
						setPropertyValue(bean, propertyName, CollectUtil.getList(propertyValue));
						return;
					}
					if (ClassUtil.isSuperClass(String.class, propertyValue.getClass()) && JsonUtil.isArray(propertyValueStr)) {
						setPropertyValue(bean, propertyName, JsonUtil.toObject(propertyValueStr, fieldType));
						return;
					}
				}

				if (ClassUtil.isSuperClass(Set.class, fieldType)) {
					if (StringUtil.subString(field.getGenericType().getTypeName(), "<", ">").equals(propertyValue.getClass().getName())) {
						setPropertyValue(bean, propertyName, CollectUtil.getSet(propertyValue));
						return;
					}
					if (ClassUtil.isSuperClass(String.class, propertyValue.getClass()) && JsonUtil.isArray(propertyValueStr)) {
						setPropertyValue(bean, propertyName, JsonUtil.toObject(propertyValueStr, fieldType));
						return;
					}
				}

				if (ClassUtil.isEnum(fieldType) && String.class.equals(propertyValue.getClass())) {
					setPropertyValue(bean, propertyName, ClassUtil.invokeStaticMethod(fieldType, "valueOf", new Object[]{propertyValue}));
					return;
				}

				if (ClassUtil.isSuperClass(Date.class, fieldType)) {
					if (ClassUtil.isSuperClass(String.class, propertyValue.getClass()) && DateUtil.isDate(propertyValueStr)) {
						setPropertyValue(bean, propertyName, DateUtil.StringToDate(propertyValueStr));
						return;
					}
					if (ClassUtil.isSuperClass(Long.class, propertyValue.getClass())) {
						setPropertyValue(bean, propertyName, new Date((Long) propertyValue));
						return;
					}
					if (ClassUtil.isSuperClass(LocalDateTime.class, propertyValue.getClass())) {
						LocalDateTime date = (LocalDateTime) propertyValue;

						Instant instant = date.atZone(ZoneId.systemDefault()).toInstant();
						setPropertyValue(bean, propertyName, Date.from(instant));
						return;
					}
				}

				throw new RuntimeException(e2);
			}
		}
	}

	private static Class<?> getCanonicalName(Class<?> fieldType) {
		String fieldTypeCanonicalName = fieldType.getCanonicalName();// 基本类型和包装类型的转化
		switch (fieldTypeCanonicalName) {
			case "int" :
				fieldType = Integer.class;
				break;
			case "long" :
				fieldType = Long.class;
				break;
			case "double" :
				fieldType = Double.class;
				break;
			case "float" :
				fieldType = Float.class;
				break;
			case "short" :
				fieldType = Short.class;
				break;
			case "char" :
				fieldType = Character.class;
				break;
			case "byte" :
				fieldType = Byte.class;
				break;
			case "boolean" :
				fieldType = Boolean.class;
				break;
		}
		return fieldType;
	}

	public static <T> Map<String, Object> beanToMap(T bean, String... mappingProperty) {
		return BeanUtil.beanToMap(bean, true, mappingProperty);
	}
	public static <T> Map<String, Object> beanToMap(T bean, boolean ignoreNull, String... mappingProperty) {
		if (bean == null) {
			throw new RuntimeException("Bean Not Found");
		}

		Map<String, Object> beanToMap = new HashMap<String, Object>();
		if (ClassUtil.isSuperClass(Map.class, bean.getClass())) {
			beanToMap.putAll((Map<String, Object>) bean);
		} else {
			for (Class<? super T> beanClass = (Class<T>) bean.getClass(); beanClass != null; beanClass = beanClass.getSuperclass()) {
				Field[] fields = beanClass.getDeclaredFields();
				if (CollectUtil.isEmpty(fields)) {
					fields = new Field[0];
				}
				for (Field field : fields) {
					String propertyName = field.getName();
					Object propertyValue = BeanUtil.getPropertyValue(bean, propertyName);
					beanToMap.put(propertyName, propertyValue);
				}
			}
		}

		Set<String> neToMap = ToMappingCopy.getNeToMap(mappingProperty);
		Map<String, Set<String>> linkMap = ToMappingCopy.getLinkMap(mappingProperty);

		for (String propertyName : CollectUtil.getSetBy(beanToMap.keySet())) {
			Object propertyValue = beanToMap.get(propertyName);

			if (propertyValue == null && ignoreNull) {
				beanToMap.remove(propertyName);
				continue;
			}
			if (neToMap.contains(propertyName)) {
				beanToMap.remove(propertyName);
				continue;
			}

			Set<String> linkMapSet = linkMap.get(propertyName);
			if (CollectUtil.isNotEmpty(linkMapSet)) {
				for (String linkItem : linkMapSet) {
					beanToMap.put(linkItem, propertyValue);
				}
			}
		}
		return beanToMap;
	}

	public static <T> T mapToBean(Map<String, Object> map, Class<T> clazz, String... mappingProperty) {
		return mapToBean(map, true, clazz, mappingProperty);
	}
	public static <T> T mapToBean(Map<String, Object> map, boolean nullToBean, Class<T> clazz, String... mappingProperty) {
		if (clazz == null) {
			return null;
		}

		T toBean = null;
		if (ClassUtil.isSuperClass(Map.class, clazz)) {
			toBean = (T) new HashMap<String, Object>();
		} else {
			toBean = ClassUtil.newInstance(clazz);
		}

		if (CollectUtil.isEmpty(map)) {
			return toBean;
		}
		return copyMapToBean(map, nullToBean, toBean, mappingProperty);
	}

	/**
	 * <p>
	 * Bean属性拷贝方法 -<b>浅层拷贝！浅层拷贝！浅层拷贝！深度拷贝用copyPropertyDepth()</b>
	 * <p/>
	 * <b>映射方式为【property->toProperty】，中间为英文半角破折号加一个小于号</b>
	 */
	public static <T, A> A copyProperty(T bean, A toBean, String... mappingProperty) {
		return copyProperty(bean, true, toBean, mappingProperty);
	}
	public static <T, A> A copyProperty(T bean, boolean nullToBean, A toBean, String... mappingProperty) {
		Map<String, Object> beanMap = BeanUtil.beanToMap(bean, false);
		if (CollectUtil.isEmpty(beanMap)) {
			return toBean;
		}
		return copyMapToBean(beanMap, nullToBean, toBean, mappingProperty);
	}
	/** 深度拷贝，<b>修改返回对象的属性不会影响到原来的对象<b> */
	public static <T, A> A copyPropertyDepth(T bean, A toBean, String... mappingProperty) {
		return copyPropertyDepth(bean, true, toBean, mappingProperty);
	}
	public static <T, A> A copyPropertyDepth(T bean, boolean nullToBean, A toBean, String... mappingProperty) {
		Map<String, Object> beanMap = JsonUtil.toMap(JsonUtil.toJson(bean));
		if (CollectUtil.isEmpty(beanMap)) {
			return toBean;
		}
		return copyMapToBean(beanMap, nullToBean, toBean, mappingProperty);
	}

	public static <A> A copyMapToBean(Map<String, Object> beanMap, boolean nullToBean, A toBean, String... mappingProperty) {
		if (toBean == null) {
			return null;
		}

		beanMap = beanToMap(beanMap, false, mappingProperty);
		if (CollectUtil.isEmpty(beanMap)) {
			return toBean;
		}

		Map<String, Object> toBeanMap = null;
		if (ClassUtil.isSuperClass(Map.class, toBean.getClass())) {
			toBeanMap = (Map<String, Object>) toBean;
			for (String propertyName : beanMap.keySet()) {
				if (!toBeanMap.containsKey(propertyName)) {
					BeanUtil.setPropertyValue(toBean, propertyName, null);
				}
			}
		} else {
			toBeanMap = BeanUtil.beanToMap(toBean, false);
			if (CollectUtil.isEmpty(toBeanMap)) {
				return toBean;
			}
		}

		Set<String> neToBean = ToMappingCopy.getNeToBean(mappingProperty);
		Map<String, String> linkBean = ToMappingCopy.getLinkBean(mappingProperty);

		for (String toPropertyName : CollectUtil.getSetBy(toBeanMap.keySet())) {
			if (neToBean.contains(toPropertyName)) {
				continue;
			}

			String mappingToPropertyName = toPropertyName;
			if (linkBean.containsKey(toPropertyName) && StringUtil.isNotEmpty(linkBean.get(toPropertyName))) {
				mappingToPropertyName = linkBean.get(toPropertyName);
			}
			if (beanMap.containsKey(mappingToPropertyName)) {
				Object propertyValue = beanMap.get(mappingToPropertyName);
				if (propertyValue == null && nullToBean) {
					continue;
				}
				BeanUtil.setPropertyValue(toBean, toPropertyName, propertyValue);
			}
		}
		return toBean;
	}

	private static class ToMappingCopy {
		private static String LINK = ">>";
		private static String NELINK = "!>";

		private static Set<String> getNeToMap(String... mappingProperty) {
			if (CollectUtil.isEmpty(mappingProperty)) {
				return CollectUtil.getEmptySet();
			}

			Set<String> neToMapSet = CollectUtil.getSet();
			for (String mapping : mappingProperty) {
				if (!mapping.contains(NELINK)) {
					continue;
				}

				String neToMap = mapping.substring(0, mapping.indexOf(NELINK));
				String neToBean = mapping.substring(mapping.indexOf(NELINK) + NELINK.length(), mapping.length());

				if (StringUtil.isNotEmpty(neToMap) && StringUtil.isEmpty(neToBean)) {
					neToMapSet.add(neToMap);
				}
			}
			return neToMapSet;
		}
		private static Set<String> getNeToBean(String... mappingProperty) {
			if (CollectUtil.isEmpty(mappingProperty)) {
				return CollectUtil.getEmptySet();
			}

			Set<String> neToBeanSet = CollectUtil.getSet();
			for (String mapping : mappingProperty) {
				if (!mapping.contains(NELINK)) {
					continue;
				}

				String neToMap = mapping.substring(0, mapping.indexOf(NELINK));
				String neToBean = mapping.substring(mapping.indexOf(NELINK) + NELINK.length(), mapping.length());

				if (StringUtil.isEmpty(neToMap) && StringUtil.isNotEmpty(neToBean)) {
					neToBeanSet.add(neToBean);
				}
			}
			return neToBeanSet;
		}
		private static Map<String, Set<String>> getLinkMap(String... mappingProperty) {
			if (CollectUtil.isEmpty(mappingProperty)) {
				return CollectUtil.getMap();
			}
			Set<String> neToMap = getNeToMap(mappingProperty);

			Map<String, Set<String>> linkMapMapping = CollectUtil.getMap();
			for (String mapping : mappingProperty) {
				if (!mapping.contains(LINK)) {
					continue;
				}
				String[] split = mapping.split(LINK);
				if (split == null || split.length != 2) {
					throw new RuntimeException(new NoSuchFieldException());
				}
				if (neToMap.contains(split[0])) {
					continue;
				}

				Set<String> toMapLink = CollectUtil.getSet();
				if (linkMapMapping.containsKey(split[0])) {
					toMapLink = linkMapMapping.get(split[0]);
				}
				toMapLink.add(split[1]);

				linkMapMapping.put(split[0], toMapLink);
			}
			return linkMapMapping;
		}
		private static Map<String, String> getLinkBean(String... mappingProperty) {
			if (CollectUtil.isEmpty(mappingProperty)) {
				return CollectUtil.getMap();
			}

			Set<String> neToBean = getNeToBean(mappingProperty);

			Map<String, String> linkBeanMapping = CollectUtil.getMap();
			for (String mapping : mappingProperty) {
				if (!mapping.contains(LINK)) {
					continue;
				}
				String[] split = mapping.split(LINK);
				if (split == null || split.length != 2) {
					throw new RuntimeException(new NoSuchFieldException());
				}
				if (neToBean.contains(split[1])) {
					continue;
				}

				linkBeanMapping.put(split[1], split[0]);
			}

			return linkBeanMapping;
		}
	}

	public static void main(String[] args) {
		Map<String, Object> map = CollectUtil.getMap();
		map.put("a", "A");
		map.put("b", "B");
		map.put("c", "C");

		Map<String, Object> beanToMap = BeanUtil.beanToMap(map, "a!>", "c>>d");
		System.out.println(beanToMap);

		Map mapToBean = BeanUtil.mapToBean(beanToMap, Map.class, "b>>d");
		System.out.println(mapToBean);

		Map<String, Object> copyProperty = BeanUtil.copyProperty(map, CollectUtil.getMap(), "!>a");
		System.out.println(copyProperty);

		map.put("a", "1");
		map.put("b", "2");
		map.put("c", "3");
		map.put("d", "4");
		System.out.println(map);
		Map<String, Object> copyProperty2 = BeanUtil.copyProperty(map, copyProperty, "d!>","!>c", "c>>d", "c>>e");
		System.out.println(copyProperty2);
	}
}
