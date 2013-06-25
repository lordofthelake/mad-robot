package com.madrobot.reflect;

public class PackageUtils {
	public static void checkPackageAccess(Class clazz) {
		checkPackageAccess(clazz.getName());
	}

	public static void checkPackageAccess(String name) {
		SecurityManager s = System.getSecurityManager();
		if (s != null) {
			String cname = name.replace('/', '.');
			if (cname.startsWith("[")) {
				int b = cname.lastIndexOf('[') + 2;
				if (b > 1 && b < cname.length()) {
					cname = cname.substring(b);
				}
			}
			int i = cname.lastIndexOf('.');
			if (i != -1) {
				s.checkPackageAccess(cname.substring(0, i));
			}
		}
	}

	public static boolean isPackageAccessible(Class clazz) {
		try {
			checkPackageAccess(clazz);
		} catch (SecurityException e) {
			return false;
		}
		return true;
	}
}
