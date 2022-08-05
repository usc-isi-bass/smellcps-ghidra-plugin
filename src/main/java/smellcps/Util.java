package smellcps;

public class Util {

	public static boolean isAsciiPrintable(String str) {
		if (str == null) {
		    return false;
		}
		int sz = str.length();
		for (int i = 0; i < sz; i++) {
			if (isAsciiPrintable(str.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}
	public static boolean isAsciiPrintable(char ch) {
		return ch >= 32 && ch < 127;
	}
}

