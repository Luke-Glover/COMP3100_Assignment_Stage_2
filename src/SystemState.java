import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;

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
        ArrayList<Server> tempList = (ArrayList<Server>) globalServerList.clone();
        tempList.sort(new Comparator<Server>() {
            @Override
            public int compare(Server o1, Server o2) {
                if (o1.core > o2.core) {
                    return 1;
                } else if (o2.core > o1.core) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        return tempList;
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

    public static ArrayList<Server> getServersByState(Server.State state) {
        ArrayList<Server> serversOfState = new ArrayList<>();
        for (Server candidate: globalServerList) {
            if (candidate.state == state) {
                serversOfState.add(candidate);
            }
        }
        return serversOfState;
    }

    public static void addServer(Server server) {
        int i = 0;
        for (Server server1 : globalServerList) {
            if (server.type.equals(server1.type)) {
                i++;
            }
        }
        server.id = i;
        globalServerList.add(server);
    }

}
