package softparkmulti;

import java.util.Timer;
import java.util.TimerTask;

public class TimerCycle {

	Timer timer;

    public TimerCycle() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new RemindTask(),
	               0,        //initial delay
	               1*1000);  //subsequent rate
    }

    class RemindTask extends TimerTask {
	int numWarningBeeps = 0;

        public void run() {
        	numWarningBeeps++;
        	System.out.format("Beep! " + numWarningBeeps + "%n");
        }
    }
	
}
