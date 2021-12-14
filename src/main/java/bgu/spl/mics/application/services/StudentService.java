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


        subscribeBroadcast(PublishConferenceBroadcast.class, broad ->{
            student.incPapersRead(broad.getConference().getSuccessfulModels());
        });
        Thread testModels = new Thread( () -> {
            LinkedList<Model> models = student.getModels();
            Thread curr = Thread.currentThread();
            for (Model model : models) {
                Future<Model> futureModelPreTrained = sendEvent(new TrainModelEvent(model));
                if(curr.isInterrupted())
                    return;
                if (futureModelPreTrained != null) {
                    Model trainedModel = futureModelPreTrained.get();
                    Future<Model> futureModelTested = sendEvent(new TestModelEvent(trainedModel));
                    if (!curr.isInterrupted() && futureModelTested != null) {
                        Model testedModel = futureModelTested.get();
                        if (!curr.isInterrupted() && testedModel != null && testedModel.getResult().equals("Good")) {
                            student.incPublications();
                            sendEvent(new PublishResultsEvent(testedModel));
                        }
                    }
                }
            }
        });
        subscribeBroadcast(TerminateBroadcast.class, term -> {
            terminate();
            testModels.interrupt();
        });
        testModels.start();
    }
}
