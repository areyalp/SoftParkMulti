package softparkmulti;
import jssc.SerialPortList;

public class CommPortUtils{
	protected static String[] getSerialPorts(){
		String[] portEnum = SerialPortList.getPortNames();
		return portEnum;
	}
}