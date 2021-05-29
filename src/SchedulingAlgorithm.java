public interface SchedulingAlgorithm {

    Protocol protocol = Protocol.getInstanceOf();

    void makeSchedulingDecision(Job job);

}
