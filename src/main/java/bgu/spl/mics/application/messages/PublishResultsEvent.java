package bgu.spl.mics.application.messages;

import  bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.Event;

public class PublishResultsEvent implements Event<Model> {

    private final Model model;

    public PublishResultsEvent(Model _model){
        model = _model;
    }

    public Model getModel() {
        return model;
    }

}
