package ProtocolObject;

import java.util.ArrayList;

public class ConcreteServer extends AbstractServer implements Server {

    public ArrayList<Job> localRunningJobsList = new ArrayList<>();
    public ArrayList<Job> localQueuedJobsList = new ArrayList<>();

    @Override
    public void scheduleJob(Job job) {

    }
}
