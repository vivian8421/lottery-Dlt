package support.util.file;

import support.util.CollectUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

/**
 * @author zhuangly
 */
public class JarFileUtil {

	@SuppressWarnings("resource")
	public static List<String> jarFind(String jarPath) {
		JarFile jarFile;
		try {
			jarFile = new JarFile(jarPath);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Enumeration<JarEntry> enumeration = jarFile.entries();

		List<String> jarFileList = CollectUtil.getList(String.class);
		while (enumeration.hasMoreElements()) {
			JarEntry jarEntry = (JarEntry) enumeration.nextElement();
			if (jarEntry.isDirectory()) {
				continue;
			}
			jarFileList.add(jarEntry.getName());
		}
		return jarFileList;
	}

	public static List<String> jarFindStartWith(String jarPath, String... startsWith) {
		List<String> jarFind = jarFind(jarPath);
		if (CollectUtil.isEmpty(startsWith)) {
			return jarFind;
		}
		List<String> jarFileList = CollectUtil.getList(String.class);
		jarFind : for (String jarFindFileName : jarFind) {
			for (String startsWithItem : startsWith) {
				if (!jarFindFileName.startsWith(startsWithItem)) {
					continue jarFind;
				}
			}
			jarFileList.add(jarFindFileName);
		}
		return jarFileList;
	}

	public static List<String> jarFindEndWith(String jarPath, String... endWith) {
		List<String> jarFind = jarFind(jarPath);
		if (CollectUtil.isEmpty(endWith)) {
			return jarFind;
		}
		List<String> jarFileList = CollectUtil.getList(String.class);
		jarFind : for (String jarFindFileName : jarFind) {
			for (String endWithItem : endWith) {
				if (!jarFindFileName.endsWith(endWithItem)) {
					continue jarFind;
				}
			}
			jarFileList.add(jarFindFileName);
		}
		return jarFileList;
	}

	public static List<String> jarFindContains(String jarPath, String... contains) {
		List<String> jarFind = jarFind(jarPath);
		if (CollectUtil.isEmpty(contains)) {
			return jarFind;
		}
		List<String> jarFileList = CollectUtil.getList(String.class);
		jarFind : for (String jarFindFileName : jarFind) {
			for (String containsItem : contains) {
				if (!jarFindFileName.contains(containsItem)) {
					continue jarFind;
				}
			}
			jarFileList.add(jarFindFileName);
		}
		return jarFileList;
	}

	public static List<String> jarFindRegex(String jarPath, String... regex) {
		List<String> jarFind = jarFind(jarPath);
		if (CollectUtil.isEmpty(regex)) {
			return jarFind;
		}
		List<Pattern> patternList = CollectUtil.getList(Pattern.class);
		for (String regexItem : regex) {
			patternList.add(Pattern.compile(regexItem));
		}
		List<String> jarFileList = CollectUtil.getList(String.class);
		jarFind : for (String jarFindFileName : jarFind(jarPath)) {
			for (Pattern patternItem : patternList) {
				if (!patternItem.matcher(jarFindFileName).find()) {
					continue jarFind;
				}
			}
			jarFileList.add(jarFindFileName);
		}
		return jarFileList;
	}

	public static String getJarFileContent(String jarPath, String fileName) {
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		try {
			@SuppressWarnings("resource")
			JarFile jarFile = new JarFile(jarPath);
			Enumeration<JarEntry> enumeration = jarFile.entries();

			while (enumeration.hasMoreElements()) {
				JarEntry jarEntry = (JarEntry) enumeration.nextElement();
				if (jarEntry.isDirectory()) {
					continue;
				}
				if (!jarEntry.getName().equals(fileName)) {
					continue;
				}

				inputStream = jarFile.getInputStream(jarEntry);
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

				StringBuffer content = new StringBuffer();
				for (String line = null; (line = bufferedReader.readLine()) != null;) {
					content.append(line);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return fileName;
	}

}
