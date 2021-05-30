import java.io.IOException;
import java.util.ArrayList;

public class Server {

    public enum State {
        INACTIVE,
        BOOTING,
        IDLE,
        ACTIVE,
        UNAVAILABLE
    }

    public State state = State.INACTIVE;

    public int id;
    public String type;
    public float rate;
    public int bootup;
    public int core;
    public int memory;
    public int disk ;

    public ArrayList<Job> localJobsList = new ArrayList<>();

    public int getAvailableCore() {
        int coreUsage = 0;
        for (Job job : localJobsList) {
            if (job.state == Job.State.RUNNING) {
                coreUsage += job.core;
            }
        }
        return core - coreUsage;
    }

    public void scheduleJob(Job job) {
        job.assignTo(this);
        localJobsList.add(job);

        if (this.getAvailableCore() > job.core) {
            job.state = Job.State.RUNNING;
        } else {
            job.state = Job.State.QUEUED;
        }

        Protocol protocol = Protocol.getInstanceOf();
        try {
            protocol.sendMessage("SCHD", job, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void completeJob(Job job) {
        job.state = Job.State.COMPLETED;
        localJobsList.remove(job);
    }

}
