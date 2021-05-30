public class Job {

    public enum State {
        SUBMITTED,
        QUEUED,
        RUNNING,
        COMPLETED
    }

    public State state = State.SUBMITTED;

    public int id;
    public String type;
    public int submitTime;
    public int estRuntTime;
    public int endTime;
    public int core;
    public int memory;
    public int disk;

    private Server server = null;

    public void assignTo(Server server) {
        this.server = server;
    }

    public void clearAssignment() {
        this.server = null;
    }

    public Server getAssignedServer() {
        return this.server;
    }

}
