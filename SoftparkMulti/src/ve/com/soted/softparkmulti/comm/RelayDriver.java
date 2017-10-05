package ve.com.soted.softparkmulti.comm;


import jssc.SerialPort;
import jssc.SerialPortException;

public class RelayDriver extends SerialPort {
	
	String relays = "@DDDD$";
	
	private boolean busy = false;
	
	public static final int ACTIVE_STATE = 1;
	public static final int INACTIVE_STATE = 2;
	
	public RelayDriver(String portName){
		super(portName);
	}
	
	public boolean connect() {
		boolean connected = false;
		if(!this.isOpened()) {
			try {
				connected = this.openPort();
				this.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				//this.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
			} catch (SerialPortException e1) {
				e1.printStackTrace();
			}
		}
		return connected;
	}
	
	public boolean isConnected() {
		return this.isOpened();
	}
	
	public boolean disconnect() {
		boolean disconnected = false;
		if(this.isOpened()) {
			try {
				disconnected = this.closePort();
			} catch (SerialPortException e) {
				e.printStackTrace();
			}
		}
		return disconnected;
	}
	
	public boolean switchRelay(int relay, int state){
		
		boolean writed = false;
		
		char code = 'D';
		char code2 = 'D';
		
		if(state==RelayDriver.ACTIVE_STATE){
			code = 'A';
			code2 = 'D';
		}else if(state==RelayDriver.INACTIVE_STATE){
			code = 'D';
		}
		
		getSerialPort();
		
		StringBuilder sb = new StringBuilder(relays);
		StringBuilder sb2 = new StringBuilder(relays);
		String out = "";
		if(relay==0){
			out = "@" + code + code + code + code + "$";
		}else if(relay==1){
			sb.setCharAt(1, code);
			sb2.setCharAt(1, code2);
			out = sb.toString();
		}else if(relay==2){
			sb.setCharAt(2, code);
			sb2.setCharAt(2, code2);
			out = sb.toString();
		}
		relays = out;
		
		try {
			writed = this.writeBytes(out.getBytes());
		} catch (SerialPortException e) {
			e.printStackTrace();
		} finally {
			leaveSerialPort();
		}
		
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
