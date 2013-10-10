package utils;

import java.util.ArrayList;
import java.util.List;

public class BinaryValues {
	public static final String TRUE_VALUE = "TRUE";
	public static final String FALSE_VALUE = "FALSE";
	private static final List<String> binaryValues = getBinaryValues();

	private static List<String> getBinaryValues() {
		List<String> binaryValues = new ArrayList<String>(2);
		binaryValues.add(FALSE_VALUE);
		binaryValues.add(TRUE_VALUE);
		return binaryValues;
	}

	public static List<String> getValues() {
		return binaryValues;
	}

	public static boolean getResult(String binaryName) throws IllegalArgumentException{
		if (binaryName.equals(TRUE_VALUE))
			return true;
		else if (binaryName.equals(FALSE_VALUE))
			return false;
		else {
			throw new IllegalArgumentException();
		}
	}

}
