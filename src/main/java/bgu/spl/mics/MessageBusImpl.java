package bgu.spl.mics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private static final MessageBusImpl instance = new MessageBusImpl();
	private final ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> broadCasts;
	private final ConcurrentHashMap<Class<? extends Event<?>>, ConcurrentLinkedQueue<MicroService>> Events;
	private final ConcurrentHashMap<Event<?>, Future> eventFutures;
	private final ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> mServiceMessageQueues;

	public static MessageBusImpl getInstance(){
		return instance;
	}
	private MessageBusImpl(){
		broadCasts = new ConcurrentHashMap<>();
		Events = new ConcurrentHashMap<>();
		eventFutures = new ConcurrentHashMap<>();
		mServiceMessageQueues = new ConcurrentHashMap<>();
	}
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		//TODO: check on the case where m is unregistered
		Events.computeIfAbsent(type, k -> new ConcurrentLinkedQueue<>()).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		//TODO: check on the case where m is unregistered
		broadCasts.computeIfAbsent(type, k -> new ConcurrentLinkedQueue<>()).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		eventFutures.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		if(broadCasts.get(b.getClass()) != null)
			for(MicroService m: broadCasts.get(b.getClass()))
				try{mServiceMessageQueues.get(m).put(b);}
				catch(InterruptedException ignore){}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		ConcurrentLinkedQueue<MicroService> q = Events.get(e.getClass());
		if(q == null)
			return null;
		MicroService m;
		//TODO check if synchronized can be removed
		synchronized (q) {
			m = q.poll();
			q.add(m);
		}
		mServiceMessageQueues.get(m).add(e);
		Future<T> fut = new Future<>();
		eventFutures.put(e, fut);
		return fut;
	}

	@Override
	public void register(MicroService m) {
		mServiceMessageQueues.putIfAbsent(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if(!isRegistered(m))
			throw new IllegalStateException();
		return mServiceMessageQueues.get(m).take();
	}

	@Override
	public <T> boolean isSubscribedEvent(Class<? extends Event<T>> type, MicroService m){
		return Events.get(type).contains(m);
	}

	@Override
	public boolean isSubscribedBroadcast(Class<? extends Broadcast> type, MicroService m){
		return broadCasts.get(type).contains(m);
	}

	@Override
	public boolean isRegistered(MicroService m){
		return mServiceMessageQueues.contains(m);
	}

}
