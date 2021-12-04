package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.LinkedList;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {

    private final Student student;

    public StudentService(String name, Student _student) {
        super(name);
        student = _student;
    }

    @Override
    protected void initialize() {

        subscribeBroadcast(TerminateBroadcast.class, term -> terminate());
        subscribeBroadcast(PublishConferenceBroadcast.class, broad ->{
            student.incPapersRead(broad.getConference().getSuccessfulModels());
        });

        LinkedList<Model> models = student.getModels();
        for(Model model: models){
            Future<Model> futureModelPreTrained = sendEvent(new TrainModelEvent(model));
            if(futureModelPreTrained != null){
                Model trainedModel = futureModelPreTrained.get();
                Future<Model> futureModelTested = sendEvent(new TestModelEvent(trainedModel));
                if(futureModelTested != null){
                    Model testedModel = futureModelTested.get();
                    if(testedModel.getResult() == "Good") {
                        student.incPublications();
                        sendEvent(new PublishResultsEvent(testedModel));
                    }
                }
            }
        }

    }
}
