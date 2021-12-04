package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.ConferenceInformation;

public class PublishConferenceBroadcast implements Broadcast {

    private final ConferenceInformation conference;

    public PublishConferenceBroadcast(ConferenceInformation _conference){
        conference = _conference;
    }
    public ConferenceInformation getConference(){
        return conference;
    }
}
