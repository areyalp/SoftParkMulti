package ve.com.soted.softparkmulti.components;

import javax.print.DocPrintJob;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;

class PrintJobWatcher {
	  boolean done = false;

	  PrintJobWatcher(DocPrintJob job) {
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