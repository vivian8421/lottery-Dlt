package support.util;

public class ObjectUtil {
	public static boolean equals(Object obj, Object oobj) {
		if (obj == null && oobj == null) {
			return true;
		}
		if (obj == null && oobj != null) {
			return false;
		}
		if (obj != null && oobj == null) {
			return false;
		}
		return obj.equals(oobj);
	}
}
