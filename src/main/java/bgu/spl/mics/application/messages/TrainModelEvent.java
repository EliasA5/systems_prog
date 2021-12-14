package bgu.spl.mics.application.messages;

import  bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.Event;

public class TrainModelEvent implements Event<Model> {

    private final Model model;

    public TrainModelEvent(Model _model){
        model = _model;
    }

    public Model getModel() {
        return model;
    }

}
