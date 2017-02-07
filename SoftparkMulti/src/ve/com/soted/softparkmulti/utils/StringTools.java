package ve.com.soted.softparkmulti.utils;

import java.util.ArrayList;

public class StringTools {
	
	public static String removeFirstChar(String s) {
	    if (s == null || s.length() == 0) {
	        return s;
	    }
	    return s.substring(1, s.length());
	}
	
	public static String removeLastChar(String s) {
	    if (s == null || s.length() == 0) {
	        return s;
	    }
	    return s.substring(0, s.length()-1);
	}
	
	public static String fillWithZeros(int number, int digits){
		String stringNumber = String.valueOf(number);
		
		int numberLength = stringNumber.length();
		
		if(stringNumber.length() < digits){
			for(int i = 0; i < digits - numberLength; i++){
				stringNumber = "0" + stringNumber;
			}
		}
		
		return stringNumber;
	}
	
	public static String implode(CharSequence delimiter, Iterable<? extends CharSequence> elements) {
		return String.join(delimiter, elements);
	}
	
	public static String implode(CharSequence delimiter, ArrayList<Integer> elements) {
		CharSequence[] cs = elements.toArray(new CharSequence[elements.size()]);
		return String.join(delimiter, cs);
	}
	
	public static ArrayList<String> explode(String str) {
		ArrayList<String> arr = new ArrayList<String>();
		
		for(int i = 0; i < str.length(); i++) {
			arr.add(i, (String.valueOf(str.charAt(i))));
		}
		
		return arr;
	}
	
	public static ArrayList<Integer> explodeInt(String str) {
		ArrayList<Integer> arr = new ArrayList<Integer>();
		
		for(int i = 0; i < str.length(); i++) {
			arr.add(i, (Integer.valueOf(String.valueOf(str.charAt(i)))));
		}
		
		return arr;
	}
	
}
