import java.io.IOException;

public class SchedulingAlgorithmAllToLargest implements SchedulingAlgorithm {

    @Override
    public void makeSchedulingDecision(Job job) {
        Protocol protocol = Protocol.getInstanceOf();

        // Find largest server
        int largestCoreCount = 0;
        Server largestServer = null;
        for (Server server: protocol.globalServerList) {

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

        try {
            protocol.sendMessage("SCHD", job, largestServer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
