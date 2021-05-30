import java.util.ArrayDeque;
import java.util.ArrayList;

public class SystemState {

    private static ArrayList<Job> globalJobList = new ArrayList<>();
    private static ArrayList<Server> globalServerList = new ArrayList<>();

    public static Job getJobById(int id) {
        for (Job candidate: globalJobList) {
            if (candidate.id == id) {
                return candidate;
            }
        }
        return null;
    }

    public static ArrayList<Job> getJobsByState(Job.State state) {
        ArrayList<Job> submittedJobs = new ArrayList<>();
        for (Job candidate: globalJobList) {
            if (candidate.state == state) {
                submittedJobs.add(candidate);
            }
        }
        return submittedJobs;
    }

    public static void submitJob(Job job) {
        job.state = Job.State.SUBMITTED;
        globalJobList.add(job);
    }

    public static Server getServerById(int id) {
        for (Server candidate: globalServerList) {
            if (candidate.id == id) {
                return candidate;
            }
        }
        return null;
    }

    public static ArrayList<Server> getServers() {
        return (ArrayList<Server>) globalServerList.clone();
    }

    public static ArrayList<Server> getServersByType(String type) {
        ArrayList<Server> serversOfType = new ArrayList<>();
        for (Server candidate: globalServerList) {
            if (candidate.type == type) {
                serversOfType.add(candidate);
            }
        }
        return serversOfType;
    }

    public static void addServer(Server server) {
        globalServerList.add(server);
    }

}
