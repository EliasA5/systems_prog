package bgu.spl.mics.application.objects;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
	private final static Cluster cluster = new Cluster();
	private final Statistics statistics = new Statistics();
	private final LinkedBlockingQueue<DataBatch> toProcessInCPUs = new LinkedBlockingQueue<>();
	private final ConcurrentHashMap<GPU, ArrayBlockingQueue<DataBatch>> GPUs = new ConcurrentHashMap<>();

	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		return cluster;
	}

	private Cluster(){
	}
	public void incNumOfGPUTicks(){statistics.incNumOfGPUTicks();}
	public void incNumOfCPUTicks(){statistics.incNumOfCPUTicks();}
	public void incNumOfProcDataBatch(){statistics.incNumOfProcDataBatch();}
	public void addModelName(String name){statistics.addModelName(name);}
	public int getNumOfGPUTicks(){return statistics.getNumOfGPUTicks();}
	public int getNumOfCPUTicks() {return statistics.getNumOfCPUTicks();}
	public int getNumOfProcDataBatch() {return statistics.getNumOfProcDataBatch();}
	public String[] getModelNames(){return statistics.getModelNames();}
	public String stats(){return statistics.stats();}



	public void addDataBatchToCPU(DataBatch batch){
		toProcessInCPUs.offer(batch);
	}

	public DataBatch getNextBatchCPU(){
		return toProcessInCPUs.poll();
	}
	public DataBatch getNextBatchGPU(GPU gpu){
		return GPUs.get(gpu).poll();
	}

	public ConcurrentHashMap<GPU, ArrayBlockingQueue<DataBatch>> getGPUs(){
		return GPUs;
	}

	public void addDataBatchToGPU(GPU gpu, DataBatch databatch){
		GPUs.compute(gpu, (key, value) -> {
			if(value == null)
				return new ArrayBlockingQueue<>(key.getMaxNumOfBatches());
			else
				return value;
		}).add(databatch);
	}


}
