package softparkmulti;

public class PrinterCommand {
	
	public static final char TAX0 = ' ';
	public static final char TAX1 = '!';
	public static final char TAX2 = '"';
	public static final char TAX3 = '#';
	
	private static final int CLIENT_DOCUMENT_DIGITS = 11;
	private static final int CLIENT_NAME_DIGITS = 40;
	
	private static final int ITEM_COMMENT_DIGITS = 40;
	
	private static final int PAYMENT_TYPE_DIGITS = 2;
	public static final int PAYMENT_TYPE_EFECTIVO_01 = 1;
	
	private static final int ITEM_PRICE_WHOLE_DIGITS = 8;
	private static final int ITEM_PRICE_FRACTIONAL_DIGITS = 2;
	
	private static final int ITEM_QUANTITY_WHOLE_DIGITS = 5;
	private static final int ITEM_QUANTITY_FRACTIONAL_DIGITS = 3;
	
	private static final int ITEM_DESCRIPTION_DIGITS = 150;
	
	private static final int DNF_DOCUMENT_TEXT_DIGITS = 56;
	
	
	public static String printTest(){
		return "D";
	}
	
	public static String cancelInvoice(){
		return "7";
	}
	
	public static String DnfDocumentText(String text){
		if(text.length() > DNF_DOCUMENT_TEXT_DIGITS){
			text = text.substring(0, DNF_DOCUMENT_TEXT_DIGITS);
		}
		return "800" + text;
	}
	
	public static String DnfDocumentEnd(String text){
		if(text.length() > DNF_DOCUMENT_TEXT_DIGITS){
			text = text.substring(0, DNF_DOCUMENT_TEXT_DIGITS);
		}
		return "810" + text;
	}
	
	public static String setClientDocument(String document){
		if(document.length() > CLIENT_DOCUMENT_DIGITS){
			document = document.substring(0, CLIENT_DOCUMENT_DIGITS - 1);
		}
		return "iR*" + document;
	}
	
	public static String setClientName(String name){
		if(name.length() > CLIENT_NAME_DIGITS){
			name = name.substring(0, CLIENT_NAME_DIGITS - 1);
		}
		return "iS*" + name;
	}
	
	public static String setClientInfo(int line, String clientInfo){
		return "i0" + line + clientInfo;
	}
	
	public static String setItemComment(String itemComment){
		if(itemComment.length() > ITEM_COMMENT_DIGITS){
			itemComment = itemComment.substring(0, ITEM_COMMENT_DIGITS - 1);
		}
		return "@" + itemComment;
	}
	
	public static String setItem(char taxType, double itemPrice, double itemQuantity, String itemCode, 
			String itemDescription){
		
		String stringPriceWhole = getWholeToString(itemPrice, ITEM_PRICE_WHOLE_DIGITS);
		String stringPriceFraction = getFractionToString(itemPrice, ITEM_PRICE_FRACTIONAL_DIGITS);
		
		String stringQuantityWhole = getWholeToString(itemQuantity, ITEM_QUANTITY_WHOLE_DIGITS);
		String stringQuantityFraction = getFractionToString(itemQuantity, ITEM_QUANTITY_FRACTIONAL_DIGITS);
		
		if(itemDescription.length() > ITEM_DESCRIPTION_DIGITS - 2 - itemCode.length()){
			itemDescription = itemDescription.substring(0, ITEM_DESCRIPTION_DIGITS - 3 - itemCode.length());
		}
		
		return String.valueOf(taxType) + stringPriceWhole + stringPriceFraction + stringQuantityWhole
				+ stringQuantityFraction + "|" + itemCode + "|" + itemDescription;
	}
	
	public static String setItem(char taxType, double itemPrice, double itemQuantity, String itemDescription){
		
		String stringPriceWhole = getWholeToString(itemPrice, ITEM_PRICE_WHOLE_DIGITS);
		String stringPriceFraction = getFractionToString(itemPrice, ITEM_PRICE_FRACTIONAL_DIGITS);
		
		String stringQuantityWhole = getWholeToString(itemQuantity, ITEM_QUANTITY_WHOLE_DIGITS);
		String stringQuantityFraction = getFractionToString(itemQuantity, ITEM_QUANTITY_FRACTIONAL_DIGITS);
		
		if(itemDescription.length() > ITEM_DESCRIPTION_DIGITS){
			itemDescription = itemDescription.substring(0, ITEM_DESCRIPTION_DIGITS - 1);
		}
		
		return String.valueOf(taxType) + stringPriceWhole + stringPriceFraction + stringQuantityWhole
				+ stringQuantityFraction + itemDescription;
	}
	
	public static String checkOut(int paymentType){
		
		return "1" + fillWithZeros(paymentType, PAYMENT_TYPE_DIGITS);
	}
	
	private static String getFractionToString(double itemPrice, int digits){
		double FractionalPart = (itemPrice % 1) * 100;
		
		int intFraction = (int) FractionalPart;
		
		String stringFraction = fillWithZeros(intFraction, digits);
		return stringFraction;
	}
	
	private static String getWholeToString(double itemPrice, int digits){
		double FractionalPart = itemPrice % 1;
		double WholePart = itemPrice - FractionalPart;
		
		int intWhole = (int) WholePart;
		
		String stringWhole = fillWithZeros(intWhole, digits);
		return stringWhole;
	}
	
	private static String fillWithZeros(int number, int digits){
		String stringNumber = String.valueOf(number);
		
		int numberLength = stringNumber.length();
		
		if(stringNumber.length() < digits){
			for(int i = 0; i < digits - numberLength; i++){
				stringNumber = "0" + stringNumber;
			}
		}
		
		return stringNumber;
	}
	
}
