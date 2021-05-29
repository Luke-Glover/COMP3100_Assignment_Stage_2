import java.util.ArrayList;

public class ConcreteServer extends AbstractServer {


    // Inherited from AbstractServer
    // public String type;
    // public float rate;
    // public int bootup;
    // public int core;
    // public int memory;
    // public int disk ;

    public int id;

    public ArrayList<Job> localRunningJobsList = new ArrayList<>();
    public ArrayList<Job> localQueuedJobsList = new ArrayList<>();

    @Override
    public void scheduleJob(Job job) {
        int coreSum = 0;
        for (Job runningJob : localRunningJobsList) {
            coreSum += runningJob.core;
        }

        int availableCore = core - coreSum;

        if (availableCore >= job.core) {
            localRunningJobsList.add(job);
        } else {
            localQueuedJobsList.add(job);
        }

    }
}
