package support.util;


import support.util.file.FileUtil;

import java.io.*;

/**
 * @author zhuangly
 */
public class IoUtil {
	private static String encoding = "UTF-8";

	public static String readStrByInputStream(InputStream inputStream) {
		return readStrByInputStream(inputStream, encoding);
	}
	public static String readStrByInputStream(InputStream inputStream, String encoding) {
		InputStreamReader insReader;
		try {
			insReader = new InputStreamReader(inputStream, encoding);
			BufferedReader bfReader = new BufferedReader(insReader);

			StringBuffer content = new StringBuffer();
			for (String line = bfReader.readLine(); line != null;) {
				content.append(line);
				line = bfReader.readLine();
				if (line != null) {
					content.append("\r\n");
				}
			}
			bfReader.close();
			insReader.close();
			return content.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public static byte[] readByteByInputStream(InputStream inputStream) {
		// try块退出时，会自动调用res.close()方法，关闭资源
		// PS:在coreJava第9版的第一卷的486页有解释
		try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			byte[] buffer = new byte[4096];
			int n = 0;
			while (-1 != (n = inputStream.read(buffer))) {
				output.write(buffer, 0, n);
			}
			return output.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public static File readFileByInputStream(InputStream inputStream, String fileFullName) {
		File file = new File(fileFullName);
		if (FileUtil.isExists(file)) {
			throw new RuntimeException("File is Exists");
		}
		if (!FileUtil.isExists(file.getParent())) {
			FileUtil.opt.create(file.getParent());
		}
		try (FileOutputStream outputStream = new FileOutputStream(file)) {
			byte[] bytes = new byte[1024];
			for (int read; (read = inputStream.read(bytes)) != -1;) {
				outputStream.write(bytes, 0, read);
			}
			return file;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
