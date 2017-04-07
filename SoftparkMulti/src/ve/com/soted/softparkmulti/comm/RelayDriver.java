package ve.com.soted.softparkmulti.comm;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JOptionPane;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class RelayDriver {

	String relays = "@DDDD$";
	CommPort commPort;
	SerialPort serialPort;
	OutputStream portOutput;
	
	private boolean busy = false;
	
	public static final int ACTIVE_STATE = 1;
	public static final int INACTIVE_STATE = 2;
	
	public RelayDriver(){
		super();
	}
	
	public void connect(String portName) throws Exception{
		
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		//JOptionPane.showMessageDialog(null, portIdentifier);
		if(portIdentifier.isCurrentlyOwned()){
			System.out.println("Error: puerto ocupado");
			JOptionPane.showMessageDialog(null, "Error: puerto ocupado");
		}else{
			commPort = portIdentifier.open(this.getClass().getName(),2000);
			
			if(commPort instanceof SerialPort){
				serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				
				portOutput = serialPort.getOutputStream();
			}
		}
	}
	
	public void disconnect() {
		commPort.close();
	}
	
	public void switchRelay(int relay, int state){
		
		char code = 'D';
		
		if(state==RelayDriver.ACTIVE_STATE){
			code = 'A';
		}else if(state==RelayDriver.INACTIVE_STATE){
			code = 'D';
		}
		
		new Thread(new SerialWriter(relay,code)).start();
		
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
	
	protected class SerialWriter implements Runnable{

		String out;
		int relay;
		char code;

		public SerialWriter(int relay, char code){
			this.relay = relay;
			this.code = code;
		}
		
		@Override
		public synchronized void run() {
			getSerialPort();
			StringBuilder sb = new StringBuilder(relays);
			if(relay==0){
				out = "@" + code + code + code + code + "$";
			}else if(relay==1){
				sb.setCharAt(1, code);
				out = sb.toString();
			}else if(relay==2){
				sb.setCharAt(2, code);
				out = sb.toString();
			}
			relays = sb.toString();
			
			for(int i = 0; i < out.length(); i++){
				try {
					portOutput.write(out.charAt(i));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				portOutput.write(13);
				portOutput.write(10);
			} catch (IOException e) {
				e.printStackTrace();
			}
			leaveSerialPort();
		}
		
	}
	
}
