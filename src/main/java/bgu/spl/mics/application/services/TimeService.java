package bgu.spl.mics.application.services;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.MicroService;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private final int speed;
	private int duration;
	public TimeService(int _speed, int _duration) {
		super("Timer");
		speed = _speed;
		duration = _duration;
	}

	@Override
	protected void initialize() {
		TickBroadcast tick = new TickBroadcast();
		while(duration >= 0){
			duration--;
			sendBroadcast(tick);
			try{Thread.sleep(speed);}
			catch(InterruptedException ignore){}
		}
		sendBroadcast(new TerminateBroadcast());
		terminate();
	}

}
