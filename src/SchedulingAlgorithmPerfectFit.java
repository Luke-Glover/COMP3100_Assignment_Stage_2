import java.util.*;

public class SchedulingAlgorithmPerfectFit implements SchedulingAlgorithm {

    Map<Server, Float> scores = new HashMap<Server, Float>();

    @Override
    public void makeSchedulingDecision() {
        Protocol protocol = Protocol.getInstanceOf();

        ArrayList<Job> jobs = SystemState.getJobsByState(Job.State.SUBMITTED);
        Job job = jobs.get(0);

        // Find server that gives best score (cpu utilisation closest to 100%) (score closer to 0)
        float bestScore = 1.0f;
        Server bestServer = null;
        for (Server server : SystemState.getServers()) {
            float score = score(server.getAvailableCore(), job.core);

            if (score == 0f) {
                bestScore = 0f;
                bestServer = server;
                break;
            } else if (score > 0 && score < bestScore) {
                bestScore = score;
                bestServer = server;
            }

            scores.put(server, score);
        }

        if (bestScore == 0f) {
            bestServer.scheduleJob(job);
        } else {
            bestServer.scheduleJob(job);
        }
    }

    private float score(int serverCore, int jobCore) {
        return (serverCore - jobCore) / (float) serverCore;
    }
}
