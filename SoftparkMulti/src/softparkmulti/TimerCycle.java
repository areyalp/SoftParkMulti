package softparkmulti;

import java.util.Timer;
import java.util.TimerTask;

public class TimerCycle {

	Timer timer;

    public TimerCycle() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new RemindTask(),
	               0,        //initial delay
	               1000);  //subsequent rate
    }
    
    public static void main(String[] args) {
    	new TimerCycle();
    }

    class RemindTask extends TimerTask {
    	int numWarningBeeps = 0;

        public synchronized void run() {
        	numWarningBeeps++;
        	System.out.format("Beep! " + numWarningBeeps + "%n");
        	if (numWarningBeeps > 5) {
        		timer.cancel();
        	}
        }
    }
	
}
