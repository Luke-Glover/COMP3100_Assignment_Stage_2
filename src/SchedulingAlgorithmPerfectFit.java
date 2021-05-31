import java.io.IOException;
import java.util.*;

public class SchedulingAlgorithmPerfectFit implements SchedulingAlgorithm {

    @Override
    public void schedule() {

        ArrayList<Server> sortedServers = SystemState.getServers();
        sortedServers.sort(new Comparator<Server>() {
            @Override
            public int compare(Server o1, Server o2) {
                return o1.core - o2.core;
            }
        });


        // Search for server that can run job immediately
        for (Job job : SystemState.getJobsByState(Job.State.SUBMITTED)) {
            for (Server candidate : sortedServers) {
                if (candidate.getAvailableCore() >= job.core
                        && candidate.memory >= job.memory
                        && candidate.disk >= job.disk) {
                    candidate.scheduleJob(job);
                    return;
                }
            }

            // If no server has capacity to run immediately...
            for (Server candidate : sortedServers) {
                if (candidate.core >= job.core
                        && candidate.memory >= job.memory
                        && candidate.disk >= job.disk) {
                    candidate.scheduleJob(job);
                    return;
                }
            }
        }
    }

    public void reschedule() {
        ArrayList<Server> sortedServers = SystemState.getServers();
        sortedServers.sort(new Comparator<Server>() {
            @Override
            public int compare(Server o1, Server o2) {
                return o1.core - o2.core;
            }
        });

        for (Job job : SystemState.getJobsByState(Job.State.QUEUED)) {
            for (Server candidate : sortedServers) {
                if (candidate.getAvailableCore() >= job.core && !candidate.equals(job.getAssignedServer())) {
                    candidate.migrateJob(job);
                    return;
                }
            }
        }

        Protocol protocol = Protocol.getInstanceOf();
        try {
            protocol.sendMessage("REDY");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private float score(int serverCore, int jobCore) {
        return (serverCore - jobCore) / (float) serverCore;
    }
}
