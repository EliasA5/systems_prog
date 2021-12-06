package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.GPU;
/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    GPU gpu;
    public GPUService(String name) {
        super("Change_This_Name");
        // TODO Implement this
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class, tick -> terminate());
        subscribeBroadcast(TickBroadcast.class, tick ->{
            //TODO: complete TickBroadcast in GPU
        });

        subscribeEvent(TrainModelEvent.class, ev -> {
            gpu.trainModel(ev);
        });

        subscribeEvent(TestModelEvent.class, ev -> {
            Model mod = ev.getModel();
            Random rand = new Random();
            int probability = mod.getStudent().getStatus() == "MSc" ? 6 : 8;
            if(mod.getStatus() == "Trained") {
                mod.setStatus("Tested");
                mod.setResult(rand.nextInt(10) <= probability ? "Good" : "Bad");
            }
            bus.complete(ev, mod);
        });

    }
}
