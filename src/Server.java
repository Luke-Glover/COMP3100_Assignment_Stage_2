import java.io.IOException;
import java.util.ArrayList;

public class Server {

    public int id;
    public String type;
    public float rate;
    public int bootup;
    public int core;
    public int memory;
    public int disk ;

    public ArrayList<Job> localRunningJobsList = new ArrayList<>();
    public ArrayList<Job> localQueuedJobsList = new ArrayList<>();

    public boolean started = false;

    public void scheduleJob(Job job) {
        Protocol p = Protocol.getInstanceOf();

        if (!started) {
            startServer();
        }

        try {
            p.sendMessage("SCHD", job, this);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void startServer() {
        Protocol p = Protocol.getInstanceOf();

        if (!started) {
            this.id = p.globalServerList.indexOf(this);
        }
        started = true;
    }

}
