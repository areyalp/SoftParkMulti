package ve.com.soted.softparkmulti.printer;

import javax.print.DocPrintJob;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;

public class PrintJobWatcher {
	  boolean done = false;

	  public PrintJobWatcher(DocPrintJob job) {
	    job.addPrintJobListener(new PrintJobAdapter() {
	      @Override
		public void printJobCanceled(PrintJobEvent pje) {
	        synchronized (PrintJobWatcher.this) {
	          done = true;
	          PrintJobWatcher.this.notify();
	        }
	      }

	      @Override
		public void printJobCompleted(PrintJobEvent pje) {
	        synchronized (PrintJobWatcher.this) {
	          done = true;
	          PrintJobWatcher.this.notify();
	        }
	      }

	      @Override
		public void printJobFailed(PrintJobEvent pje) {
	        synchronized (PrintJobWatcher.this) {
	          done = true;
	          PrintJobWatcher.this.notify();
	        }
	      }

	      @Override
		public void printJobNoMoreEvents(PrintJobEvent pje) {
	        synchronized (PrintJobWatcher.this) {
	          done = true;
	          PrintJobWatcher.this.notify();
	        }
	      }
	    });
	  }

	  public synchronized void waitForDone() {
	    try {
	      while (!done) {
	        wait();
	      }
	    } catch (InterruptedException e) {
	    }
	  }
	}