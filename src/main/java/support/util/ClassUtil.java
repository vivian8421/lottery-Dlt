package support.util;


import support.util.file.FileUtil;
import support.util.file.JarFileUtil;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.*;

/**
 * @author zhuangly
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ClassUtil {
	public static boolean isLoadClass(String className) {
		if (StringUtil.isEmpty(className) || StringUtil.isEmpty(className.trim())) {
			return false;
		}
		try {
			loadClass(className);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * clazz是不是atClazz的父类
	 * 
	 * @param clazz
	 * @param atClazz
	 * @return
	 */
	public static boolean isSuperClass(Class<?> clazz, Class<?> atClazz) {
		if (clazz == null) {
			return false;
		}
		if (clazz != null && atClazz == null) {
			return false;
		}
		return clazz.isAssignableFrom(atClazz);
	}

	public static boolean isArray(Class<?> clazz) {
		if (clazz == null) {
			return false;
		}
		return clazz.isArray();
	}

	public static boolean isCollection(Class<?> clazz) {
		if (clazz == null) {
			return false;
		}
		return isSuperClass(Collection.class, clazz);
	}

	public static boolean isInterface(Class<?> clazz) {
		if (clazz == null) {
			return false;
		}
		return clazz.isInterface();
	}

	public static boolean isEnum(Class<?> clazz) {
		if (clazz == null) {
			return false;
		}
		return clazz.isEnum();
	}

	public static boolean isAnnotation(Class<?> clazz) {
		if (clazz == null) {
			return false;
		}
		return clazz.isAnnotation();
	}

	public static boolean isAbstract(Class<?> clazz) {
		if (clazz == null) {
			return false;
		}
		return Modifier.isAbstract(clazz.getModifiers());
	}

	/** 这边注意，class名需包含包 */
	public static <T> Class<T> loadClass(String className) {
		try {
			return (Class<T>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/** 这边注意，class名需包含包 */
	public static List<Class<?>> loadClass(List<String> classNameList) {
		List<Class<?>> classList = new ArrayList<>(classNameList.size());
		try {
			for (String className : classNameList) {
				classList.add(Class.forName(className));
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return classList;
	}

	/**
	 * 
	 * @param classFileDir
	 * @param loadClassName
	 * @return
	 */
	@SuppressWarnings("resource")
	public static Class<?> loadDirClass(String classFileDir, String loadClassName) {
		File classFile = new File(classFileDir);
		if (!classFile.exists()) {
			return null;
		}
		if (!classFile.isDirectory()) {
			return null;
		}
		try {
			Class<?> clazz = new URLClassLoader(new URL[]{classFile.toURI().toURL()}).loadClass(loadClassName);
			return clazz;
		} catch (Throwable e) {
			if (isSuperClass(NoClassDefFoundError.class, e.getClass())) {
				String message = e.getMessage();
				if (message.startsWith("IllegalName:")) {
					// IllegalName: xxx/xxx/xxx/ClassName
					// 出现这个提示是因为传进来的类名是xxx/xxx/xxx/ClassName格式
					// 格式不对，要变成xxx.xxx.xxx.ClassName,测试过别的不会出现
					return loadDirClass(classFileDir, loadClassName.replace("/", "."));
				}
				String separatorClassName = "/" + loadClassName.substring(loadClassName.lastIndexOf(".") + 1);
				String defFoundErrorFormatInfo = "wrong name: ";
				if (message.startsWith(loadClassName.replace(".", "/")) && message.endsWith(separatorClassName + ")") && message.indexOf(defFoundErrorFormatInfo) != -1) {
					// 出现ClassName (wrong name: xxx/xxx/xxx/ClassName)这个提示是因为class生成没问题，但是加载有问题
					// 比如文件名跟类名不一致或者只有类名没有包名
					// 可以用这个来找出一个class的包名
					return loadDirClass(classFileDir, message.replace(")", "").split(defFoundErrorFormatInfo)[1].replace("/", "."));
				}
			}
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("resource")
	public static Class<?> loadJarClass(String jarFilePath, String loadClassName) {
		File jarFile = new File(jarFilePath);
		if (!jarFile.exists()) {
			return null;
		}
		if (!jarFile.getName().endsWith(".jar")) {
			return null;
		}

		try {
			// Class<?> clazz = new URLClassLoader(new URL[]{jarFile.toURI().toURL()}).loadClass(loadClassName);
			URLClassLoader classLoader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()}, Thread.currentThread().getContextClassLoader());
			return classLoader.loadClass(loadClassName);
		} catch (Throwable e) {
			if (isSuperClass(NoClassDefFoundError.class, e.getClass())) {
				String message = e.getMessage();
				if (message.startsWith("IllegalName:")) {
					// IllegalName: xxx/xxx/xxx/ClassName
					// 出现这个提示是因为传进来的类名是xxx/xxx/xxx/ClassName格式
					// 格式不对，要变成xxx.xxx.xxx.ClassName,测试过别的不会出现
					return loadJarClass(jarFilePath, loadClassName.replace("/", "."));
				}
				String separatorClassName = "/" + loadClassName.substring(loadClassName.lastIndexOf(".") + 1);
				String defFoundErrorFormatInfo = "wrong name: ";
				if (message.startsWith(loadClassName.replace(".", "/")) && message.endsWith(separatorClassName + ")") && message.indexOf(defFoundErrorFormatInfo) != -1) {
					// 出现ClassName (wrong name: xxx/xxx/xxx/ClassName)这个提示是因为class生成没问题，但是加载有问题
					// 比如文件名跟类名不一致或者只有类名没有包名
					// 可以用这个来找出一个class的包名
					return loadJarClass(jarFilePath, message.replace(")", "").split(defFoundErrorFormatInfo)[1].replace("/", "."));
				}
			}
			throw new RuntimeException(e);
		}
	}

	public static List<Class<?>> findClass(String packageName) {
		return findClass(packageName, null, null, null, true);
	}

	public static List<Class<?>> findClass(String packageName, String prefix) {
		return findClass(packageName, prefix, null, null, true);
	}

	public static List<Class<?>> findClass(String packageName, String prefix, String suffix, String contains, boolean childPackage) {
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

		List<String> classNameList = new ArrayList<>();

		URL url = contextClassLoader.getResource(packageName.replace(".", "/"));
		if (url != null) {
			String type = url.getProtocol();

			String resourcePath = url.getPath();
			try {
				resourcePath = URLDecoder.decode(url.getPath(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}

			if (type.equals("file")) {
				classNameList = findClassByFile(resourcePath, packageName, childPackage);
			}
			if (type.equals("jar")) {
				if (resourcePath.contains("!")) {
					String[] resourcePathSplit = resourcePath.split("!");
					resourcePath = resourcePathSplit[0].substring(resourcePathSplit[0].indexOf("/"));
				}
				classNameList = findClassByJar(resourcePath, packageName);
			}
		} else {
			// 没有找到指定包就编译路径下全部的类
			URL[] urls = ((URLClassLoader) contextClassLoader).getURLs();
			if (urls != null) {
				for (int i = 0; i < urls.length; i++) {
					String resourcePath = urls[i].getPath();
					try {
						resourcePath = URLDecoder.decode(urls[i].getPath(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						throw new RuntimeException(e);
					}
					if (resourcePath.endsWith("classes/")) {
						continue;
					}
					if (resourcePath.contains("!")) {
						String[] resourcePathSplit = resourcePath.split("!");
						resourcePath = resourcePathSplit[0].substring(resourcePathSplit[0].indexOf("/"));
					}
					classNameList.addAll(findClassByJar(resourcePath, packageName));
				}
			}
		}
		for (Iterator<String> iterator = classNameList.iterator(); iterator.hasNext();) {
			String className = iterator.next();
			if (StringUtil.isEmpty(prefix)) {
				continue;
			}
			// className = xxx.xxx.xxx.xxx.prefixXXX
			if (className.startsWith(prefix, className.lastIndexOf(".") + 1)) {
				continue;
			}
			iterator.remove();
		}
		for (Iterator<String> iterator = classNameList.iterator(); iterator.hasNext();) {
			String className = iterator.next();
			if (StringUtil.isEmpty(suffix)) {
				continue;
			}
			if (className.endsWith(suffix)) {
				continue;
			}
			iterator.remove();
		}
		for (Iterator<String> iterator = classNameList.iterator(); iterator.hasNext();) {
			String className = iterator.next();
			if (StringUtil.isEmpty(contains)) {
				continue;
			}
			if (className.contains(contains)) {
				continue;
			}
			iterator.remove();
		}
		return loadClass(classNameList);
	}

	public static List<String> findClassByFile(String filePath, String packageName, boolean findChildPackage) {
		List<String> classNames = new ArrayList<>();
		List<File> listClassEndWith = FileUtil.find.listEndWith(filePath, findChildPackage, ".class");
		for (File file : listClassEndWith) {
			Class<?> loadClass = loadDirClass(file.getParent(), file.getName().replace(".class", ""));
			String className = loadClass.getName();
			if (className.contains(packageName)) {
				classNames.add(className);
			}
		}
		return classNames;
	}

	public static List<String> findClassByJar(String jarFilePath, String packageName) {
		List<String> classNames = new ArrayList<>();

		List<String> jarFindFile = JarFileUtil.jarFindRegex(jarFilePath, packageName.replace(".", "/") + ".*?.class$");
		for (String fileName : jarFindFile) {
			Class<?> loadClass = loadJarClass(jarFilePath, fileName.replace(".class", ""));
			String className = loadClass.getName();
			if (className.contains(packageName)) {
				classNames.add(className);
			}
		}
		return classNames;
	}

	public static <T> List<Class<?>> findSubclass(Class<?> clazz, Package pack) {
		if (pack == null) {
			pack = clazz.getPackage();
		}
		return findSubclass(clazz, pack.getName());
	}

	/**
	 * 
	 * 获取指定clazz的子类集合
	 * 
	 * @author yuanji
	 * @created 2020年2月3日 上午5:44:37
	 * @param clazz
	 * @param packageName
	 * @return
	 */
	public static List<Class<?>> findSubclass(Class<?> clazz, String packageName) {
		return findSubclass(clazz, packageName, null);
	}

	public static List<Class<?>> findSubclass(Class<?> clazz, String packageName, String prefix) {
		List<Class<?>> packageClass = findClass(packageName, prefix);

		List<Class<?>> allSubclassList = new ArrayList<Class<?>>();
		for (Class<?> pClazz : packageClass) {
			if (isSuperClass(clazz, pClazz)) {
				if (pClazz.equals(clazz)) {
					continue;
				}
				allSubclassList.add(pClazz);
			}
		}
		return allSubclassList;
	}

	public static Constructor getClassConstructorByParam(Class clazz, Object... constructorParam) {
		Class<?>[] parameterTypes = null;
		if (CollectUtil.isNotEmpty(constructorParam)) {
			List<Class<?>> constructorParamClassList = new ArrayList<Class<?>>(constructorParam.length);
			for (Object object : constructorParam) {
				Class<?> constructorParameterClass = object.getClass();
				constructorParamClassList.add(constructorParameterClass);
			}
			parameterTypes = CollectUtil.to.CollectToArray(constructorParamClassList); // 将构造器参数列表转换为数组
		}
		return getClassConstructorTypes(clazz, parameterTypes);
	}

	public static Constructor getClassConstructorTypes(Class clazz, Class... parameterTypes) {
		if (clazz == null) {
			return null;
		}

		Constructor indexConstructor = null;
		if (CollectUtil.isEmpty(parameterTypes)) {
			for (Constructor constructor : clazz.getConstructors()) {
				if (constructor.getParameterTypes().length == 0) {
					indexConstructor = constructor;
					break;
				}
			}
		} else {
			Class<?>[] subNull = CollectUtil.opt.subNull(parameterTypes);
			if (CollectUtil.isEmpty(subNull)) {
				for (Constructor constructor : clazz.getConstructors()) {
					if (constructor.getParameterTypes().length == parameterTypes.length) {
						indexConstructor = constructor;
						break;
					}
				}
			} else if (subNull.length != parameterTypes.length) {
				Constructor partHitConstructor = null;

				for (Constructor constructor : clazz.getConstructors()) {
					if (constructor.getParameterTypes().length == parameterTypes.length) {
						Class<?>[] constructorParameterTypes = constructor.getParameterTypes();

						boolean partHit = true;
						for (int i = 0; i < constructorParameterTypes.length; i++) {
							Class<?> methodParameterType = constructorParameterTypes[i];
							if (parameterTypes[i] != null && !isSuperClass(methodParameterType, parameterTypes[i])) {
								partHit = false;
							}
						}
						if (partHitConstructor == null && partHit) {
							indexConstructor = partHitConstructor;
							break;
						}
					}
				}

			} else {
				Constructor partHitConstructor = null;

				for (Constructor constructor : clazz.getConstructors()) {
					if (constructor.getParameterTypes().length == parameterTypes.length) {
						Class<?>[] constructorParameterTypes = constructor.getParameterTypes();

						boolean allHit = true;
						boolean partHit = true;
						for (int i = 0; i < constructorParameterTypes.length; i++) {
							Class<?> methodParameterType = constructorParameterTypes[i];
							if (!methodParameterType.equals(parameterTypes[i])) {
								allHit = false;
							}
							if (!isSuperClass(methodParameterType, parameterTypes[i])) {
								partHit = false;
							}
						}
						if (allHit) {
							indexConstructor = constructor;
							break;
						}
						if (partHitConstructor == null && partHit) {
							partHitConstructor = constructor;
						}
					}
				}
				if (partHitConstructor != null) {
					indexConstructor = partHitConstructor;
				}
			}
		}
		return indexConstructor;
	}

	public static <T> T newInstance(Constructor<T> constructor, Object... constructorParam) {
		if (constructor == null) {
			throw new NullPointerException();
		}

		try {
			return constructor.newInstance(constructorParam); // 返回对象实例
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * 创建实例对象
	 * 
	 * @param clazz
	 * @param constructorParam
	 * @return
	 */
	public static <T> T newInstance(Class<T> clazz, Object... constructorParam) {
		if (clazz == null) {
			throw new RuntimeException(new NullPointerException());
		}

		Class<?>[] parameterTypes = null;
		if (CollectUtil.isNotEmpty(constructorParam)) {
			parameterTypes = CollectUtil.getArray(Class.class, constructorParam.length);
			for (int i = 0; i < constructorParam.length; i++) {
				if (constructorParam[i] != null) {
					parameterTypes[i] = constructorParam[i].getClass();
				}
			}
		}
		Constructor<T> classConstructor = getClassConstructorTypes(clazz, parameterTypes);

		return newInstance(classConstructor, constructorParam);
	}

	public static Object newInstance(String classFileDir, String loadClassName, Object... constructorParam) {
		Class<?> loadClass = loadDirClass(classFileDir, loadClassName);
		Object newInstance = newInstance(loadClass, constructorParam);
		return newInstance;
	}

	public static <T> List<T> newSubclassInstance(Class<T> clazz, String packageName) {
		List<Class<?>> packageClass = findClass(packageName);

		List<T> allImplClassList = new ArrayList<T>();
		for (Class<?> pClazz : packageClass) {
			if (isSuperClass(clazz, pClazz)) {
				if (pClazz.equals(clazz)) {
					continue;
				}
				T newInstance = (T) newInstance(pClazz);
				allImplClassList.add(newInstance);
			}
		}
		return allImplClassList;
	}

	public static Method getFieldGet(Class<?> clazz, String fieldName) {
		Field field = getClassField(clazz, fieldName);
		if (field == null) {
			throw new RuntimeException(new NoSuchFieldException());
		}
		PropertyDescriptor pd = null;
		try {
			pd = new PropertyDescriptor(field.getName(), clazz);
		} catch (IntrospectionException e) {
			return null;
		}
		return pd.getReadMethod();
	}

	public static Method getFieldSet(Class<?> clazz, String fieldName) {
		Field field = getClassField(clazz, fieldName);
		if (field == null) {
			throw new RuntimeException(new NoSuchFieldException());
		}
		PropertyDescriptor pd = null;
		try {
			pd = new PropertyDescriptor(field.getName(), clazz);
		} catch (IntrospectionException e) {
			return null;
		}
		return pd.getWriteMethod();
	}

	public static List<Field> getClassField(Class<?> clazz) {
		List<Field> list = CollectUtil.getList(Field.class);
		do {
			Field[] fields = clazz.getDeclaredFields();
			CollectUtil.opt.ListAddArr(list, fields);
			clazz = clazz.getSuperclass();
		} while (clazz != null);
		return list;
	}

	public static List<Field> getClassField(Class<?> clazz, String[] fieldName, Class<?>[] fieldType, Class<? extends Annotation>[] annoClass) {
		if (clazz == null) {
			return null;
		}
		List<Field> classField = getClassField(clazz);

		List<Field> nameFieldList = null;
		if (CollectUtil.isNotEmpty(fieldName)) {
			nameFieldList = getFieldByName(clazz, fieldName);
		} else {
			nameFieldList = classField;
		}
		List<Field> annoFieldList = null;
		if (CollectUtil.isNotEmpty(annoClass)) {
			annoFieldList = getFieldByAnno(clazz, annoClass);
		} else {
			annoFieldList = classField;
		}
		List<Field> typeFieldList = null;
		if (CollectUtil.isNotEmpty(fieldType)) {
			typeFieldList = getFieldByType(clazz, fieldType);
		} else {
			typeFieldList = classField;
		}
		Set<Field> collectIntersection = CollectUtil.opt.CollectIntersection(nameFieldList, typeFieldList);
		Set<Field> collectIntersection2 = CollectUtil.opt.CollectIntersection(collectIntersection, annoFieldList);
		return CollectUtil.to.CollecToList(collectIntersection2);
	}

	public static List<String> getClassFieldNames(Class<?> clazz) {
		List<Field> classField = getClassField(clazz);
		return CollectUtil.item.getPropertyList(classField, "name");
	}

	/**
	 * 检测类中加了特定注解的字段名称，返回注解中的值和字段名称的map
	 * 
	 * @param clazz
	 * @param annoClass
	 * @param annoValueField
	 * @param annoValueType
	 * @return
	 */
	public static <T> Map<T, String> getFieldNameByAnnoToMap(Class<?> clazz, Class<? extends Annotation> annoClass, String annoValueField, Class<T> annoValueType) {
		if (clazz == null) {
			return null;
		}
		if (annoClass == null || StringUtil.isBlank(annoValueField) || annoValueType == null) {
			return CollectUtil.getEmptyMap();
		}
		Map<T, String> annoValue_fieldName = CollectUtil.getMap();
		do {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {

				if (field.getAnnotation(annoClass) != null) {
					T annoValue = BeanUtil.getPropertyValue(field.getAnnotation(annoClass), "h.memberValues." + annoValueField);
					if (annoValue != null) {
						annoValue_fieldName.put(annoValue, field.getName());
					}
					continue;
				}
				Method getMethod = null;
				try {
					PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
					getMethod = pd.getReadMethod();
				} catch (Exception e) {
					continue;
				}
				if (getMethod == null) {
					continue;
				}
				if (getMethod.getAnnotation(annoClass) != null) {
					T annoValue = BeanUtil.getPropertyValue(field.getAnnotation(annoClass), "h.memberValues." + annoValueField);
					if (annoValue != null) {
						annoValue_fieldName.put(annoValue, field.getName());
					}
				}
			}
			clazz = clazz.getSuperclass();
		} while (clazz != null);
		return annoValue_fieldName;
	}

	public static Field getClassField(Class<?> clazz, String fieldName) {
		if (StringUtil.isEmpty(fieldName)) {
			return null;
		}
		Field classProperty = null;
		do {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				String name = field.getName();
				if (fieldName.equals(name)) {
					classProperty = field;
					break;
				}
			}
			clazz = clazz.getSuperclass();
		} while (clazz != null);
		return classProperty;
	}

	public static List<Field> getFieldByName(Class<?> clazz, String... propertyNames) {
		if (CollectUtil.isEmpty(propertyNames)) {
			return null;
		}

		List<Field> list = CollectUtil.getList(Field.class);
		do {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if (CollectUtil.item.ArrayContains(propertyNames, field.getName())) {
					list.add(field);
				}
			}
			clazz = clazz.getSuperclass();
		} while (clazz != null);
		return list;
	}

	public static List<Field> getFieldByType(Class<?> clazz, Class<?>... fieldType) {
		List<Field> list = CollectUtil.getList(Field.class);
		do {
			Field[] fields = clazz.getDeclaredFields();
			CollectUtil.opt.ListAddArr(list, fields);
			clazz = clazz.getSuperclass();
		} while (clazz != null);

		if (CollectUtil.isEmpty(fieldType)) {
			return list;
		}

		for (Iterator<Field> iterator = list.iterator(); iterator.hasNext();) {
			Field f = iterator.next();

			boolean fit = false;
			for (Class<?> t : fieldType) {
				if (!fit && isSuperClass(t, f.getType())) {
					fit = true;
				}
			}
			if (fit) {
				continue;
			} else {
				iterator.remove();
			}
		}
		return list;
	}

	public static List<Field> getFieldByAnno(Class<?> clazz, Class<? extends Annotation>... annoClass) {
		if (clazz == null) {
			return null;
		}
		List<Field> list = CollectUtil.getList(Field.class);
		do {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {

				if (CollectUtil.isEmpty(annoClass)) {
					list.add(field);
					continue;
				}
				for (Class<? extends Annotation> anno : annoClass) {
					if (field.isAnnotationPresent(anno)) {
						list.add(field);
						continue;
					}
					PropertyDescriptor pd = null;
					try {
						pd = new PropertyDescriptor(field.getName(), clazz);
					} catch (IntrospectionException e) {
						continue;
					}
					Method getMethod = pd.getReadMethod();
					if (getMethod == null || (getMethod != null && getMethod.isAnnotationPresent(anno))) {
						list.add(field);
					}
				}
			}
			clazz = clazz.getSuperclass();
		} while (clazz != null);
		return list;
	}

	/** 请注意参数本身就是数组的情况 */
	public static Method getClassMethod(Class<?> clazz, String methodName, Class<?>[] paramTypes, Object[] params) {
		if (clazz == null) {
			return null;
		}
		if (StringUtil.isEmpty(methodName)) {
			return null;
		}

		List<Method> allMethodList = CollectUtil.getList();

		// 获得全部方法，并粗略筛选名称
		for (Class<?> atClass = clazz; atClass != null; atClass = atClass.getSuperclass()) {
			for (Method method : atClass.getDeclaredMethods()) {
				if (method.getName().equals(methodName)) {
					allMethodList.add(method);
				}
			}
		}

		if (CollectUtil.isEmpty(allMethodList)) {
			return null;
		}

		// 无任何类型筛选直接返回第一个
		if (paramTypes == null && params == null) {
			return allMethodList.get(0);
		}

		// 参数类型不为空，个数为0，且参数为空
		// 参数类型为空，参数不为空，个数为0
		// 参数类型不为空，参数不为空，且个数都为0
		// 直接拿没参数的第一个
		if ((paramTypes != null && paramTypes.length == 0 && params == null) || (paramTypes == null && params != null && params.length == 0) || (paramTypes != null && params != null && paramTypes.length == params.length && paramTypes.length == 0)) {
			filterMethod(allMethodList, new Class[0]);

			if (CollectUtil.isNotEmpty(allMethodList)) {
				return allMethodList.get(0);
			} else {
				return null;
			}
		}

		// 参数类型为空，参数不为空，那将参数转为参数类型去筛选
		if (paramTypes == null && params != null) {
			paramTypes = new Class<?>[params.length];
			for (int i = 0; i < params.length; i++) {
				if (params[i] != null) {
					paramTypes[i] = params[i].getClass();
				}
			}
		}

		filterMethod(allMethodList, paramTypes);
		if (CollectUtil.isNotEmpty(allMethodList)) {
			return allMethodList.get(0);
		} else {
			return null;
		}
	}
	private static void filterMethod(List<Method> methodList, Class[] paramTypes) {
		if (methodList == null || paramTypes == null) {
			return;
		}

		// 过滤个数
		for (Iterator<Method> iterator = methodList.iterator(); iterator.hasNext();) {
			Method method = iterator.next();

			if (paramTypes.length != method.getParameterTypes().length) {
				iterator.remove();
			}
		}

		// 过滤泛类型
		for (int i = 0; i < paramTypes.length; i++) {
			Class paramType = paramTypes[i];
			if (paramType != null) {
				for (Iterator<Method> iterator = methodList.iterator(); iterator.hasNext();) {
					Method method = iterator.next();

					Class methodParamType = method.getParameterTypes()[i];

					boolean oneHit = true;
					if (paramType.isPrimitive() && !methodParamType.isPrimitive()) {
						oneHit = methodParamType.getSimpleName().toLowerCase().startsWith(paramType.getSimpleName());
					} else if (!paramType.isPrimitive() && methodParamType.isPrimitive()) {
						oneHit = paramType.getSimpleName().toLowerCase().startsWith(methodParamType.getSimpleName());
					} else {
						oneHit = ClassUtil.isSuperClass(methodParamType, paramType);
					}
					if (!oneHit) {
						iterator.remove();
					}
				}
			}
		}
		// 没有泛命中的就不用精准过滤了
		if (CollectUtil.isEmpty(methodList)) {
			return;
		}

		boolean existAllHit = false;
		for (Iterator<Method> iterator = methodList.iterator(); iterator.hasNext();) {
			Method method = iterator.next();

			if (existAllHit) {
				iterator.remove();// 如果已找到精准命中，那后续的全部删除，但可能已经过了一些泛命中，所以后续还要删除只留一下最后一个
			}

			boolean allHit = true;
			Class<?>[] methodParamTypes = method.getParameterTypes();

			for (int i = 0; i < paramTypes.length; i++) {
				Class paramType = paramTypes[i];
				if (paramType != null) {
					Class methodParamType = methodParamTypes[i];

					boolean oneHit = true;
					if (paramType.isPrimitive() && !methodParamType.isPrimitive()) {
						oneHit = methodParamType.getSimpleName().toLowerCase().startsWith(paramType.getSimpleName());
					} else if (!paramType.isPrimitive() && methodParamType.isPrimitive()) {
						oneHit = paramType.getSimpleName().toLowerCase().startsWith(methodParamType.getSimpleName());
					} else {
						oneHit = methodParamType.equals(paramType);
					}
					if (!oneHit) {
						allHit = false;// 一个不是完全相等，就不认为是相等
						// 这边有一个问题，因为经过Object[]进来的paramType永远都不会是原始类型，但是方法上是可能会有原始类型的
						// 而且java一个类里，同名同返回，同参数个数，就是参数原始类型和包装类型的差别，他认为是两个方法
						// 那这种情况，这边的是找到第一个就算
					}
				}
			}

			if (allHit) {
				existAllHit = allHit;
			}
		}
		if (methodList.size() != 1) {
			int existAllHitBeforeRemove = 0;
			for (ListIterator<Method> iterator = methodList.listIterator(methodList.size()); iterator.hasPrevious(); existAllHitBeforeRemove++) {
				iterator.previous();// 指向这个元素，必须先做且不可缺少的步骤

				if (existAllHitBeforeRemove != 0) {
					iterator.remove();
				}
			}
		}
	}

	public static Map<String, Object> getStaticPropertyValue(Class<?> clazz) {
		if (clazz == null) {
			throw new RuntimeException(new NullPointerException());
		}
		Map<String, Object> map = CollectUtil.getMap();
		do {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if (!Modifier.isStatic(field.getModifiers())) {
					continue;
				}
				try {
					field.setAccessible(true);
					map.put(field.getName(), field.get(clazz));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
			clazz = clazz.getSuperclass();
		} while (clazz != null);
		return map;
	}
	public static <T, A> A getStaticPropertyValue(Class<?> clazz, String propertyName) {
		if (clazz == null) {
			throw new RuntimeException(new NullPointerException());
		}
		if (StringUtil.isEmpty(propertyName)) {
			throw new RuntimeException(new NoSuchFieldException());
		}
		Field field = null;
		try {
			field = clazz.getDeclaredField(propertyName);
		} catch (NoSuchFieldException | SecurityException e) {
			throw new RuntimeException(e);
		}
		if (field == null) {
			throw new RuntimeException(new NoSuchFieldException());
		}
		if (!Modifier.isStatic(field.getModifiers())) {
			throw new RuntimeException(new NoSuchFieldException());
		}
		try {
			field.setAccessible(true);
			return (A) field.get(clazz);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T, A> void setStaticPropertyValue(Class<?> clazz, String propertyName, A propertyValue) {
		if (clazz == null) {
			throw new RuntimeException(new NullPointerException());
		}
		if (StringUtil.isEmpty(propertyName)) {
			throw new RuntimeException(new NoSuchFieldException());
		}
		Field field = null;
		try {
			field = clazz.getDeclaredField(propertyName);
		} catch (NoSuchFieldException | SecurityException e) {
			throw new RuntimeException(e);
		}
		if (field == null) {
			throw new RuntimeException(new NoSuchFieldException());
		}
		if (!Modifier.isStatic(field.getModifiers())) {
			throw new RuntimeException(new NoSuchFieldException());
		}
		if (Modifier.isFinal(field.getModifiers())) {
			throw new RuntimeException("Can not set final field");
		}
		try {
			field.setAccessible(true);
			field.set(null, propertyValue);
			return;
		} catch (IllegalArgumentException | IllegalAccessException fe) {
			throw new RuntimeException(fe);
		}
	}

	/** 请注意参数本身就是数组的情况 */
	public static <A> A invokeStaticMethod(Class<?> clazz, String methodName) {
		return invokeStaticMethod(clazz, methodName, null, null);
	}
	/** 请注意参数本身就是数组的情况 */
	public static <A> A invokeStaticMethod(Class<?> clazz, String methodName, Object[] params) {
		return invokeStaticMethod(clazz, methodName, null, params);
	}
	/** 请注意参数本身就是数组的情况 */
	public static <A> A invokeStaticMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes, Object[] params) {
		Method classMethod = getClassMethod(clazz, methodName, parameterTypes, params);
		// 解除私有方法访问限制
		classMethod.setAccessible(true);

		if (!Modifier.isStatic(classMethod.getModifiers())) {
			return null;
		}

		try {
			if ("void".equals(classMethod.getReturnType().getName())) {
				classMethod.invoke(null, params);
				return null;
			} else {
				return (A) classMethod.invoke(null, params);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
