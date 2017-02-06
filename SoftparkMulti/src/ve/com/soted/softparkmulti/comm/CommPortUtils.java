package ve.com.soted.softparkmulti.comm;
import jssc.SerialPortList;

public class CommPortUtils{
	public static String[] getSerialPorts(){
		String[] portEnum = SerialPortList.getPortNames();
		return portEnum;
	}
}