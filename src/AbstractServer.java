public class AbstractServer extends Server {

    /**
     * An AbstractServer object represents a server that has not been started, but could be started
     * if required. When started, a ConcreteServer object is created.
     */

    // Inherited from Server
    // public String type;
    // public float rate;
    // public int bootup;
    // public int core;
    // public int memory;
    // public int disk ;

    public int limit;
    public int numServers = 0;


    public ConcreteServer startServer() {
        if (numServers < limit) {
            ConcreteServer startedServer = (ConcreteServer) this;
            protocol.globalServerList.add(startedServer);
            if ((numServers += 1) == limit) {
                protocol.globalServerList.remove(this);
            }
            return startedServer;
        }
        return null;
    }

    @Override
    public void scheduleJob(Job job) {
        this.startServer().scheduleJob(job);
    }

}
