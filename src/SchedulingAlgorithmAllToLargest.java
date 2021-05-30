import java.io.IOException;

public class SchedulingAlgorithmAllToLargest implements SchedulingAlgorithm {

    @Override
    public void makeSchedulingDecision() {
        Protocol protocol = Protocol.getInstanceOf();

        // Find largest server
        int largestCoreCount = 0;
        Server largestServer = null;
        for (Server server: SystemState.getServers()) {

            if (largestServer == null) {
                largestServer = server;
                largestCoreCount = server.core;
                continue;
            }

            if (server.core > largestCoreCount) {
                largestServer = server;
                largestCoreCount = server.core;
            }

        }

        // Get a job
        Job job = SystemState.getJobsByState(Job.State.SUBMITTED).get(0);
        largestServer.scheduleJob(job);

    }

}
