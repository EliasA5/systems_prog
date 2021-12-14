package bgu.spl.mics.application.services;

import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConferenceInformation;
import bgu.spl.mics.MicroService;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConferenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {

    private int timer;
    private final ConferenceInformation info;
    public ConferenceService(String name, ConferenceInformation _info) {
        super(name);
        info = _info;
        timer = info.getDate();
    }

    @Override
    protected void initialize() {

        subscribeEvent(PublishResultsEvent.class, ev ->{
            info.addModelIfSuccessful(ev.getModel());
        });

        subscribeBroadcast(TickBroadcast.class, tick->{
            timer--;
            if(timer == 0){
                PublishConferenceBroadcast b = new PublishConferenceBroadcast(info);
                sendBroadcast(b);
                terminate();
            }
        });
        subscribeBroadcast(TerminateBroadcast.class, term -> terminate());
    }
}
