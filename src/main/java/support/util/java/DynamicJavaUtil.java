package support.util.java;


import support.util.ClassUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * @author zhuangly
 */
public class DynamicJavaUtil {

	private static String javaClassPath = SystemProperty.getJavaClassPath();
	private static String custom = "custom" + SystemProperty.getProperty(SystemProperty.SystemPropertyKey.FileSeparator);
	/**
	 * 
	 * @param className
	 * @param classContent
	 * @param jarFileDir
	 * @param constructorParam
	 * @return
	 * @throws Exception
	 */
	public static Object dynamicJava(String className, String classContent, String jarFileDir, Object... constructorParam) throws Exception {
		String classPath = javaClassPath + custom + className + ".java";
		File file = new File(classPath);
		if (!file.exists()) {
			File parentFile = file.getParentFile();
			if (parentFile.exists()) {
				file.createNewFile();
			} else {
				if (parentFile.isDirectory()) {
					parentFile.mkdirs();
				}
				file.createNewFile();
			}
		}
		PrintStream ps = new PrintStream(new FileOutputStream(file));
		ps.println(classContent);
		ps.flush();
		ps.close();
		
		CompilerJava.compiler(jarFileDir, classPath, javaClassPath);
		Object newInstance = ClassUtil.newInstance(javaClassPath + custom, className, constructorParam);
		return newInstance;
	}
	public static void main(String[] args) throws Exception {
		dynamicJava("com.test.compiler.test.Hello",
				"package com.test.compiler.test;\n\npublic class Hello {\n\tpublic void test() {\n\t\tSystem.out.println(\"aasdf3333333333333asdfasdfsdfsadfsadfsafdd\");\n\t}\n}", "");

	}
}
