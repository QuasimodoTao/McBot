package util;

public class Event {
	private volatile boolean ev = false;
	private boolean autoClear = true;
	
	public Event(boolean autoClear) {
		this.autoClear = autoClear;
	}
	public Event() {}
	
	public void setEvent() {
		ev = true;
		synchronized(this) {
			this.notifyAll();
		}
	}
	public void clearEvent() {
		ev = false;
	}
	public void waitEvent() throws InterruptedException {
		synchronized(this) {
			if(ev) {
				if(autoClear) ev = false;
				return;
			}
			this.wait();
		}
	}
	public boolean waitEvent(int time) throws InterruptedException {
		if(time <= 0) waitEvent();
		synchronized(this) {
			if(ev) {
				if(autoClear) ev = false;
				return true;
			}
			this.wait(time);
			if(ev) {
				if(autoClear) ev = false;
				return true;
			}
			return false;
		}
	}
	public boolean getEvent() {
		return this.ev;
	}
}
