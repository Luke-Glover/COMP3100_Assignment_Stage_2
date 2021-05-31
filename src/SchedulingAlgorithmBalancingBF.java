import java.io.IOException;
import java.util.*;

public class SchedulingAlgorithmBalancingBF implements SchedulingAlgorithm {

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
        int bestScore = Integer.MAX_VALUE;
        Server bestServer = null;
        Job job = SystemState.getJobsByState(Job.State.SUBMITTED).get(0);
        for (Server candidate : sortedServers) {

            int score = candidate.getAvailableCore() - job.core;

            if (score == 0
                    && candidate.memory >= job.memory
                    && candidate.disk >= job.disk) {
                candidate.scheduleJob(job);
                return;
            }

            if (score > 0 && score < bestScore
                    && candidate.memory >= job.memory
                    && candidate.disk >= job.disk) {
                bestScore = candidate.getAvailableCore() - job.core;
                bestServer = candidate;
            }

        }

        if (bestServer != null) {
            bestServer.scheduleJob(job);
            return;
        }

        // If no server has capacity to run immediately
        // schedule job on first server that will have future
        // capacity to run the job
        for (Server candidate : sortedServers) {
            if (candidate.core >= job.core
                    && candidate.memory >= job.memory
                    && candidate.disk >= job.disk) {
                candidate.scheduleJob(job);
                return;
            }
        }
    }

    public void balance() {

        Job job = SystemState.getJobsByState(Job.State.QUEUED).get(0);
        Server oldServer = job.getAssignedServer();

        ArrayList<Server> sortedServers = SystemState.getServers();
        sortedServers.remove(oldServer);
        sortedServers.sort(new Comparator<Server>() {
            @Override
            public int compare(Server o1, Server o2) {
                return o1.core - o2.core;
            }
        });


        // Search for server that can run job immediately
        int bestScore = Integer.MAX_VALUE;
        Server bestServer = null;
        for (Server candidate : sortedServers) {

            int score = candidate.getAvailableCore() - job.core;

            if (score == 0
                    && candidate.memory >= job.memory
                    && candidate.disk >= job.disk) {
                candidate.migrateJob(job);
                return;
            }

            if (score > 0 && score < bestScore
                    && candidate.memory >= job.memory
                    && candidate.disk >= job.disk) {
                bestScore = candidate.getAvailableCore() - job.core;
                bestServer = candidate;
            }
        }

        if (bestServer != null) {
            bestServer.migrateJob(job);
            return;
        }

        // Attempt to reschedule jobs that are running alone
        // and not fully utilising a server
        bestScore = Integer.MAX_VALUE;
        bestServer = null;
        for (Job runningJob : SystemState.getJobsByState(Job.State.RUNNING)) {
            if (runningJob.getAssignedServer().localJobsList.size() == 1) {

                for (Server candidate : SystemState.getServersByState(Server.State.ACTIVE)) {
                    if (candidate.getAvailableCore() == runningJob.core) {
                        candidate.migrateJob(runningJob);
                        return;
                    }
                    int score = candidate.getAvailableCore() - job.core;
                    if (score > bestScore) {
                        bestScore = score;
                        bestServer = candidate;
                    }
                }

                if (bestServer != null) {
                    bestServer.migrateJob(runningJob);
                    return;
                }
            }
        }


        // If not reschedules could be made
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
