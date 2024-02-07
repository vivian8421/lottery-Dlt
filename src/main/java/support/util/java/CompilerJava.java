package support.util.java;


import support.util.file.FileUtil;

import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhuangly
 */
public class CompilerJava {

	private static JavaCompiler javaCompiler;

	private static JavaCompiler getJavaCompiler() {
		if (javaCompiler == null) {
			synchronized (CompilerJava.class) {
				if (javaCompiler == null) {
					// 根据JavaCompiler 的获取方式来看，一直都去new所以这边采用单例
					javaCompiler = ToolProvider.getSystemJavaCompiler();
				}
			}
		}
		return javaCompiler;
	}

	/**
	 * 编译java文件
	 * 
	 * @param jarFileDir
	 *            需要加载的jar
	 * @param javaFileDir
	 *            java源文件存放目录
	 * @param classFileDir
	 *            编译后class类文件存放目录
	 * @return
	 * @throws Exception
	 */
	public static boolean compiler(String jarFileDir, String javaFileDir, String classFileDir) {
		File javaFile = new File(javaFileDir);
		if (!javaFile.exists()) {
			return false;
		}
		File classFile = new File(classFileDir);
		if (!classFile.exists()) {
			classFile.mkdirs();
		}
		// 获取目录下全部需编译的java源文件
		List<File> sourceFileList = FileUtil.find.listEndWith(javaFileDir, true, ".java");
		// 没有java文件，直接返回
		if (sourceFileList.size() == 0) {
			return false;
		}
		// 如果遍历class文件夹为空，就放在源文件下
		if (classFileDir == null || "".equals(classFileDir.trim())) {
			classFileDir = javaFileDir;
		}

		// 获取目录下全部需加载的jar文件
		List<File> jarFiles = FileUtil.find.listEndWith(jarFileDir, true, ".jar");
		StringBuilder jarFilePaths = new StringBuilder();
		for (File file : jarFiles) {
			jarFilePaths.append(file.getPath());
			jarFilePaths.append(";");
		}
		String jarFilePathsStr = jarFilePaths.toString();

		// 编译选项，在编译java文件时，编译程序会自动的去寻找java文件引用的其他的java源文件或者class。
		Iterable<String> options = Arrays.asList("-encoding", "UTF-8", "-classpath", jarFilePathsStr, "-d", classFileDir, "-sourcepath", javaFileDir);

		// 获取编译器实例
		JavaCompiler javaCompiler = getJavaCompiler();
		// 获取标准文件管理器实例
		StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(null, null, null);
		// 编译信息监听器
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		Iterable<? extends JavaFileObject> javaFileObjectsFromFiles = fileManager.getJavaFileObjectsFromFiles(sourceFileList);
		CompilationTask compilationTask = javaCompiler.getTask(null, fileManager, diagnostics, options, null, javaFileObjectsFromFiles);

		boolean taskCallResult = compilationTask.call();
		if (!taskCallResult) {
			StringBuilder diagnosticMsg = new StringBuilder();
			for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
				diagnosticMsg.append(diagnostic.getMessage(null));
			}
			System.out.println(diagnosticMsg);
		}
		try {
			fileManager.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		// 运行编译任务
		return taskCallResult;
	}


	public static void main(String[] args) {
		// ClassLoader.getSystemResourceAsStream(name)
		String jarFileDir = "E:/as";
		String javaFileDir = "E:/as";
		String classFileDir = "E:/as";
		boolean compiler = CompilerJava.compiler(jarFileDir, javaFileDir, classFileDir);
		System.out.println(compiler);
	}

}