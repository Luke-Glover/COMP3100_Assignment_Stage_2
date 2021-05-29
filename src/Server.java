public abstract class Server {

    Protocol protocol = Protocol.getInstanceOf();

    public String type;
    public float rate;
    public int bootup;
    public int core;
    public int memory;
    public int disk ;

    public abstract void scheduleJob(Job job);

}
