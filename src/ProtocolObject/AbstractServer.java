package ProtocolObject;

public class AbstractServer implements Server {

    /**
     * An AbstractServer object represents a server that has not been started, but could be started
     * if required. When started, a ConcreteServer object is created.
     */

    public String type;
    public int limit;
    public float rate;
    public int bootup;
    public int core;
    public int memory;
    public int disk;

    public int numServers = 0;


    @Override
    public void scheduleJob(Job job) {

    }

}
