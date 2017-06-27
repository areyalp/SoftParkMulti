package ve.com.soted.softparkmulti.comm;


import jssc.SerialPort;
import jssc.SerialPortException;

public class RelayDriver extends SerialPort {
	
	String relays = "@DDDD$";
	SerialPort serialPort;
	
	private boolean busy = false;
	
	public static final int ACTIVE_STATE = 1;
	public static final int INACTIVE_STATE = 2;
	
	public RelayDriver(String portName){
		super(portName);
	}
	
	public boolean connect() throws Exception{
		boolean connected = false;
		if(!serialPort.isOpened()) {
			try {
				connected = serialPort.openPort();
				serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			} catch (SerialPortException e1) {
				e1.printStackTrace();
			}
		}
		return connected;
	}
	
	public boolean isOpened() {
		return serialPort.isOpened();
	}
	
	public boolean disconnect() {
		boolean disconnected = false;
		if(serialPort.isOpened()) {
			try {
				disconnected = serialPort.closePort();
			} catch (SerialPortException e) {
				e.printStackTrace();
			}
		}
		return disconnected;
	}
	
	public boolean switchRelay(int relay, int state){
		
		boolean writed = false;
		
		char code = 'D';
		
		if(state==RelayDriver.ACTIVE_STATE){
			code = 'A';
		}else if(state==RelayDriver.INACTIVE_STATE){
			code = 'D';
		}
		
		getSerialPort();
		
		StringBuilder sb = new StringBuilder(relays);
		String out = "";
		if(relay==0){
			out = "@" + code + code + code + code + "$";
		}else if(relay==1){
			sb.setCharAt(1, code);
			out = sb.toString();
		}else if(relay==2){
			sb.setCharAt(2, code);
			out = sb.toString();
		}
		relays = out;
		
		try {
			writed = this.writeString(out);
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
		
		leaveSerialPort();
		
		return writed;
	}

	public synchronized void getSerialPort() {		
		while (this.busy == true) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		this.busy = true;
		notifyAll();
		
	}
	
	public synchronized void leaveSerialPort() {
		this.busy = false;
		notifyAll();
		
	}
}
