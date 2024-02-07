package support.util.java;

/**
 * @author zhuangly
 */
public class SystemProperty {

	public static enum SystemPropertyKey {
		JavaVersion("java.version", "Java运行时环境版本"),
		JavaVendor("java.vendor", "Java运行时环境供应商"),
		JavaVendorUrl("java.vendor.url", "Java供应商的URL"),
		JavaHome("java.home","Java安装目录"),
		JavaVmSpecificationVersion("java.vm.specification.version", "Java虚拟机规范版本"),
		JavaVmSpecificationVendor("java.vm.specification.vendor","Java虚拟机规范供应商"),
		JavaVmSpecificationName("java.vm.specification.name", "Java虚拟机规范名称"),
		JavaVmVersion("java.vm.version", "Java虚拟机实现版本"),
		JavaVmVendor("java.vm.vendor","Java虚拟机实现供应商"),
		JavaVmame("java.vm.name", "Java虚拟机实现名称"),
		JavaSpecificationVersion("java.specification.version", "Java运行时环境规范版本"),
		JavaSpecificationVendor("java.specification.vendor", "Java运行时环境规范供应商"),
		JavaSpecificationName("java.specification.name", "Java运行时环境规范名称"),
		JavaClassVersion("java.class.version","Java类格式版本号"),
		JavaClassPath("java.class.path", "Java类路径"),
		JavaLibraryPath("java.library.path", "加载库时搜索的路径列表"),
		JavaIoTmpdir("java.io.tmpdir","默认的临时文件路径"),
		JavaCompiler("java.compiler", "要使用的JIT编译器的名称"),
		JavaExtDirs("java.ext.dirs", "一个或多个扩展目录的路径"),
		OsName("os.name","操作系统的名称"),
		OsArch("os.arch", "操作系统的架构"),
		OsVersion("os.version", "操作系统的版本"),
		FileSeparator("file.separator","文件分隔符（在UNIX系统中是“/"),
		PathSeparator("path.separator", "路径分隔符（在UNIX系统中是“:"),
		LineSeparator("line.separator","行分隔符（在UNIX系统中是“/n”"),
		UserName("user.name","用户的账户名称"),
		UserHome("user.home", "用户的主目录"),
		UserDir("user.dir", "用户的当前工作目录"),
		SunBootClassPath("sun.boot.class.path","用户的当前工作目录");

		public String key;
		public String remark;
		private SystemPropertyKey(String key, String remark) {
			this.key = key;
			this.remark = remark;
		}
	}

	public static String getProperty(SystemPropertyKey propertyKey) {
		return System.getProperty(propertyKey.key);
	}

	public static String getUserDir() {
		return SystemProperty.getProperty(SystemPropertyKey.UserDir);
	}

	public static String getJavaClassPath() {
		return ClassLoader.getSystemResource("").getPath();
	}

	public static void main(String[] args) {
		System.out.println(getUserDir());
		System.out.println(getJavaClassPath());
		System.out.println(SystemProperty.class.getResource(""));
		System.out.println(SystemProperty.class.getResource("/"));
		System.out.println(Thread.currentThread().getContextClassLoader().getResource(""));
		System.out.println(SystemProperty.class.getClassLoader().getResource(""));
		System.out.println(ClassLoader.getSystemResource(""));
	}
}
