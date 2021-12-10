package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.Data;
import bgu.spl.mics.application.objects.DataBatch;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;

import java.util.Random;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the {@link /DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    private GPU gpu;
    private MessageBusImpl bus = MessageBusImpl.getInstance();
    private TrainModelEvent currEvent;
    public GPUService(String name, GPU _gpu) {
        super("name");
        gpu = _gpu;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class, tick -> terminate());
        subscribeBroadcast(TickBroadcast.class, tick ->{
            if(gpu.isBusy()) {
                Data d = gpu.getModel().getData();
                if(gpu.getNumOfTrainedBatches() == d.getSize()){
                    Model Trained = gpu.finishTrainingModel();
                    complete(currEvent, Trained);
                }
                else{
                    if(gpu.getCurrentNumOfBatches() != gpu.getMaxNumOfBatches()) //add batch when you can
                        gpu.incrementCurrentBatches(); //sends a new databatch to cluster if didn't send all already

                    if(gpu.getCurrentNumOfBatches() != 0)
                        gpu.decrementCounter();

                    if(gpu.getCounter() == 0) {
                        gpu.incrementCurrentTrainedBatches();
                        gpu.resetCounter();
                        gpu.decrementCurrentNumOfBatches();
                    }
                }
                gpu.incNumOfGPUTicks();
            }


        });

        subscribeEvent(TrainModelEvent.class, ev -> {
            if(gpu.isBusy()) {
                bus.sendEvent(ev);
                return;
            }
            currEvent = ev;
            gpu.trainModel(ev.getModel());
        });

        subscribeEvent(TestModelEvent.class, ev -> {
            if(gpu.isBusy()) {
                bus.sendEvent(ev);
                return;
            }
            Model mod = ev.getModel();
            Random rand = new Random();
            int probability = mod.getStudent().getStatus() == "MSc" ? 6 : 8;
            if(mod.getStatus() == "Trained") {
                mod.setStatus("Tested");
                mod.setResult(rand.nextInt(10) <= probability ? "Good" : "Bad");
            }
            gpu.incNumOfGPUTicks();
            gpu.addModelName(mod);
            bus.complete(ev, mod);
        });

    }
}
