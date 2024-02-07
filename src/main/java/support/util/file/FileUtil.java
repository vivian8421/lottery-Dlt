package support.util.file;


import support.util.CollectUtil;
import support.util.IoUtil;
import support.util.StringUtil;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author zhuangly
 */
public class FileUtil {
	public static boolean isExists(String filePath) {
		return FileUtil.isExists(new File(filePath));
	}
	public static boolean isExists(File file) {
		return file.exists();
	}
	public static boolean isDirectory(String filePath) {
		return FileUtil.isDirectory(new File(filePath));
	}
	public static boolean isDirectory(File file) {
		return file.isDirectory();
	}
	public static boolean isFile(String filePath) {
		return FileUtil.isFile(new File(filePath));
	}
	public static boolean isFile(File file) {
		return file.isFile();
	}

	public byte[] readBytesToByte(String filePath) {
		byte[] buffer = null;
		try {
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
			byte[] b = new byte[1024];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	public static void writeBytesByByte(byte[] bfile, String filePath, String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			if (!dir.exists() && dir.isDirectory()) {// 判断文件目录是否存在
				dir.mkdirs();
			}
			file = new File(filePath + "\\" + fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bfile);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	public static byte[] readBytesByFile(String pathStr) {
		File file = new File(pathStr);
		if (!isExists(file) || !isFile(file)) {
			throw new RuntimeException("file not exists or is not File");
		}
		try {
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			byte[] data = bos.toByteArray();
			bos.close();
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void writeBytesToFile(byte[] bytes, String filePath) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		try {
			File file = new File(filePath);

			opt.create(file);

			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static class find {
		public static File one(String dirPath, String fileName) {
			return find.one(new File(dirPath), fileName);
		}
		public static File one(File dirPath, String fileName) {
			if (dirPath == null) {
				return null;
			}
			if (!dirPath.exists()) {
				return null;
			}
			if (!dirPath.isDirectory()) {
				return null;
			}
			List<File> listFilter = find.listFilter(dirPath, false, new FileFilter() {
				@Override
				public boolean accept(File pathFile) {
					return pathFile.getName().equals(fileName);
				}
			});
			if (CollectUtil.isEmpty(listFilter)) {
				return null;
			}
			return listFilter.get(0);
		}

		public static File one(String filePath) {
			File file = new File(filePath);
			if (file.isDirectory()) {
				return null;
			}
			if (!file.exists()) {
				return null;
			}
			if (!file.isFile()) {
				return null;
			}
			return file;
		}

		public static List<String> list(String dirPath, boolean findSubDir) {
			return find.list(dirPath, findSubDir, null);
		}

		public static List<String> list(String dirPath, boolean findSubDir, FileFilter filter) {
			if (StringUtil.isEmpty(dirPath)) {
				return null;
			}
			File directory = new File(dirPath);
			if (!directory.exists()) {
				return null;
			}
			if (!directory.isDirectory()) {
				return null;
			}

			List<String> files = new ArrayList<>();

			File[] listFiles = directory.listFiles(filter);
			for (File file : listFiles) {
				if (file.isDirectory() && findSubDir) {
					files.addAll(find.list(file.getParent(), findSubDir, null));
					continue;
				}
				if (!filter.accept(file)) {
					continue;
				}
				files.add(file.getName());
			}
			return files;

		}

		public static List<File> list(String dirPath) {
			return find.listFilter(dirPath, true, null);
		}

		public static List<File> list(String dirPath, String nameRegex) {
			return find.listFilter(dirPath, true, new FileFilter() {
				@Override
				public boolean accept(File file) {
					return StringUtil.isNotEmpty(nameRegex) ? Pattern.matches(nameRegex, file.getName()) : true;
				}
			});
		}

		public static List<File> list(String dirPath, String nameRegex, boolean isDirectory) {
			return find.listFilter(dirPath, false, new FileFilter() {
				@Override
				public boolean accept(File file) {
					boolean nameRegexMatches = StringUtil.isNotEmpty(nameRegex) ? Pattern.matches(nameRegex, file.getName()) : true;
					return (file.isDirectory() == isDirectory) && nameRegexMatches;
				}
			});
		}

		public static List<File> listFilter(String dirPath, boolean findSubDir, FileFilter filter) {
			if (StringUtil.isEmpty(dirPath)) {
				return null;
			}
			File directory = new File(dirPath);
			return find.listFilter(directory, findSubDir, filter);
		}

		public static List<File> listFilter(File directory, boolean findSubDir, FileFilter filter) {
			if (directory == null) {
				return null;
			}
			if (!directory.exists()) {
				return null;
			}
			if (!directory.isDirectory()) {
				return null;
			}

			List<File> files = new ArrayList<>();

			File[] listFiles = directory.listFiles();
			for (File file : listFiles) {

				if (file.isDirectory() && findSubDir) {
					files.addAll(find.listFilter(file.getParent() + File.separator + file.getName(), findSubDir, null));
					continue;
				}
				if (filter != null && !filter.accept(file)) {
					continue;
				}
				files.add(file);
			}
			return files;
		}

		public static List<File> listStartWith(String dirPath, boolean findSubDir, String... startsWith) {
			return find.listFilter(dirPath, findSubDir, new FileFilter() {
				@Override
				public boolean accept(File file) {
					if (CollectUtil.isNotEmpty(startsWith)) {
						for (String prefix : startsWith) {
							if (file.getName().startsWith(prefix)) {
								return true;
							}
						}
					}
					return false;
				}
			});
		}

		public static List<File> listEndWith(String dirPath, boolean findSubDir, String... endsWith) {
			return find.listFilter(dirPath, findSubDir, new FileFilter() {
				@Override
				public boolean accept(File file) {
					if (CollectUtil.isNotEmpty(endsWith)) {
						for (String suffix : endsWith) {
							if (file.getName().endsWith(suffix)) {
								return true;
							}
						}
					}
					return false;
				}
			});
		}

		public static List<File> listContains(String dirPath, boolean findSubDir, String... contains) {
			return find.listFilter(dirPath, findSubDir, new FileFilter() {
				@Override
				public boolean accept(File file) {
					if (CollectUtil.isNotEmpty(contains)) {
						for (String contain : contains) {
							if (file.getName().contains(contain)) {
								return true;
							}
						}
					}
					return false;
				}
			});
		}

		public static List<File> listRegex(String dirPath, boolean findSubDir, String... regex) {
			List<Pattern> patternList = CollectUtil.getList(Pattern.class);
			for (String regexItem : regex) {
				patternList.add(Pattern.compile(regexItem));
			}
			return find.listFilter(dirPath, findSubDir, new FileFilter() {
				@Override
				public boolean accept(File file) {
					if (CollectUtil.isNotEmpty(patternList)) {
						for (Pattern patternItem : patternList) {
							if (patternItem.matcher(file.getName()).find()) {
								return true;
							}
						}
					}
					return false;
				}
			});
		}
	}
	public static class content {
		public static String readFileToStr(String filePath) {
			if (filePath == null || filePath.trim().equals("")) {
				return "";
			}
			return content.readFileToStr(new File(filePath));
		}

		public static String readFileToStr(File file) {
			if (!file.exists() || !file.isFile() || file.isHidden()) {
				return "";
			}
			try {
				return IoUtil.readStrByInputStream(new FileInputStream(file));
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}

		public static void appendStrToFile1(String filePath, String content) throws Exception {
			FileWriter fw = new FileWriter(filePath, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.append(content);
			bw.close();
			fw.close();
		}

		public static void appendStrToFile2(String filePath, String content) throws Exception {
			FileOutputStream fs = new FileOutputStream(filePath);
			PrintStream ps = new PrintStream(fs);
			ps.append(content);
			fs.close();
			ps.close();
		}

		public static void appendStrToFile3(String filePath, String content) throws Exception {
			FileWriter fw = new FileWriter(filePath);
			PrintWriter pw = new PrintWriter(fw);
			pw.append(content);
			fw.close();
			pw.close();
		}

		public static void writeStrToFile1(String filePath, String content) throws Exception {
			FileWriter fw = new FileWriter(filePath, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
			fw.close();
		}

		public static void writeStrToFile2(String filePath, String content) throws Exception {
			FileOutputStream fileOutputStream = new FileOutputStream(filePath);
			PrintStream ps = new PrintStream(fileOutputStream);
			ps.println(content);
			fileOutputStream.close();
			ps.close();
		}

		public static void writeStrToFile3(String filePath, String content) throws Exception {
			FileWriter fw = new FileWriter(filePath);
			PrintWriter pw = new PrintWriter(fw);
			pw.println(content);
			fw.close();
			pw.close();
		}

		public static void writeStringToFile4(String filePath, String content) throws Exception {
			RandomAccessFile rf = new RandomAccessFile(filePath, "rw");
			rf.writeBytes(content);
			rf.close();
		}

		public static void writeStringToFile5(String filePath, String content) throws Exception {
			FileOutputStream fos = new FileOutputStream(filePath);
			fos.write(content.getBytes());
			fos.close();
		}

		public static void replaceFileStr(String filePath, Map<String, String> replaceContent) throws Exception {
			if (filePath == null || filePath.trim().equals("") || CollectUtil.isEmpty(replaceContent)) {
				return;
			}
			File file = new File(filePath);
			if (!file.exists() || !file.isFile() || file.isHidden()) {
				return;
			}
			String readFileFileToStr = content.readFileToStr(filePath);
			for (String string : replaceContent.keySet()) {
				readFileFileToStr = readFileFileToStr.replace(string, replaceContent.get(string));
			}
			String replaceFilePath = file.getParent() + File.separator + file.getName();// .replace(".", "_Replace.");
			File replaceFile = new File(replaceFilePath);
			if (!file.exists()) {
				replaceFile.createNewFile();
			}
			content.writeStrToFile2(replaceFilePath, readFileFileToStr);
		}
	}
	public static class rename {
		public static void renameBatchTo(List<File> fileList, List<String> newFileNameList) {
			if (CollectUtil.isEmpty(fileList) || CollectUtil.isEmpty(newFileNameList) || fileList.size() != newFileNameList.size()) {
				return;
			}
			for (int i = 0; i < fileList.size(); i++) {
				renameTo(fileList.get(i), newFileNameList.get(i));
			}
		}

		public static void renameTo(File file, String newFileName) {
			if (file == null || StringUtil.isEmpty(newFileName)) {
				return;
			}
			file.renameTo(new File(file.getParent() + File.separator + newFileName));
		}
	}

	public static class opt {

		public static boolean create(String filePath) {
			return create(new File(filePath), false);
		}
		public static boolean create(String filePath, boolean force) {
			return create(new File(filePath), force);
		}
		public static boolean create(File file) {
			return create(file, false);
		}
		public static boolean create(File file, boolean force) {
			try {
				if (file.isDirectory()) {
					if (file.exists()) {
						if (force) {
							delete(file);
							return file.mkdirs();
						} else {
							return true;
						}
					} else {
						return file.mkdirs();
					}
				} else {
					if (file.exists()) {
						if (force) {
							delete(file);
							return file.createNewFile();
						} else {
							return true;
						}
					} else {
						File parentFile = file.getParentFile();
						if (!parentFile.exists()) {
							parentFile.mkdirs();
						}
						return file.createNewFile();
					}
				}

			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		public static boolean delete(String filePath) {
			return opt.delete(new File(filePath));
		}

		public static boolean delete(File file) {
			if (file == null) {
				return false;
			}
			if (file != null && !file.exists()) {
				return true;
			}
			return file.delete();
		}

		public static void rename(File file, String newFileName) {
			if (file == null || StringUtil.isEmpty(newFileName)) {
				return;
			}
			file.renameTo(new File(file.getParent() + File.separator + newFileName));
		}

		public static void rename(List<File> fileList, List<String> newFileNameList) {
			if (CollectUtil.isEmpty(fileList) || CollectUtil.isEmpty(newFileNameList) || fileList.size() != newFileNameList.size()) {
				return;
			}
			for (int i = 0; i < fileList.size(); i++) {
				rename(fileList.get(i), newFileNameList.get(i));
			}
		}

		public static void copy(String sourcePath, String targetPath) {
			copy(new File(sourcePath), new File(targetPath));
		}
		public static void copy(File source, File target) {
			try {
				Files.copy(target.toPath(), target.toPath());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		public static void move(String sourcePath, String targetPath) {
			move(new File(sourcePath), new File(targetPath));
		}
		public static void move(File source, File target) {
			try {
				Files.move(source.toPath(), target.toPath());
				delete(source);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		// public static void copy2(File source, File dest) {
		// InputStream input = null;
		// OutputStream output = null;
		// try {
		// input = new FileInputStream(source);
		// output = new FileOutputStream(dest);
		// byte[] buf = new byte[1024];
		// int bytesRead;
		// while ((bytesRead = input.read(buf)) > 0) {
		// output.write(buf, 0, bytesRead);
		// }
		// } catch (Exception e) {
		// throw new RuntimeException(e);
		// } finally {
		// try {
		// input.close();
		// output.close();
		// } catch (IOException e) {
		// throw new RuntimeException(e);
		// }
		// }
		// }
		//
		// @SuppressWarnings("resource")
		// public static void copy3(File source, File dest) {
		// FileChannel inputChannel = null;
		// FileChannel outputChannel = null;
		// try {
		// inputChannel = new FileInputStream(source).getChannel();
		// outputChannel = new FileOutputStream(dest).getChannel();
		// outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
		// } catch (Exception e) {
		// throw new RuntimeException(e);
		// } finally {
		// try {
		// inputChannel.close();
		// outputChannel.close();
		// } catch (IOException e) {
		// throw new RuntimeException(e);
		// }
		// }
		// }

	}
}
