package softparkmulti;

public class MainSoftPark  {
	private static int stationId;
	public static void main(String[] args) {

		int stationId = 1; //TODO check the return parameter form CheckStation stationId...
		new CheckStation();
		
		new SoftParkMultiView(stationId);
	} // END OF main
	
}