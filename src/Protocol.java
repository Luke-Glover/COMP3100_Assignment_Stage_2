import java.io.IOException;
import java.util.ArrayList;

public class Protocol {


    public ArrayList<Server> globalServerList = new ArrayList<>();
    public ArrayList<Job> globalJobList = new ArrayList<>();


    private Connection connection;
    private SchedulingAlgorithm algorithm;


    private static Protocol instanceOf;


    // Singleton getter method
    public static Protocol getInstanceOf() {
        return instanceOf;
    }


    // Constructor
    public Protocol(Connection connection, SchedulingAlgorithm algorithm) {
        this.connection = connection;
        this.algorithm = algorithm;
        instanceOf = this;
    }


    public void parseMessage(String message) throws UnrecognisedCommandException, InvalidCommandException {

        String[] messageParts = message.split(" ");

        switch (messageParts[0]) {

            case "OK" -> {

            }

            case "JOBN", "JOBP" -> {
                if (messageParts.length != 7) {
                    throw new InvalidCommandException("Expects 7 parts in JOBN/JOBP message, got " + messageParts.length);
                }

                Job newJob = new Job();
                newJob.submitTime   = Integer.parseInt(messageParts[1]);
                newJob.id           = Integer.parseInt(messageParts[2]);
                newJob.estRuntTime  = Integer.parseInt(messageParts[3]);
                newJob.core         = Integer.parseInt(messageParts[4]);
                newJob.memory       = Integer.parseInt(messageParts[5]);
                newJob.disk         = Integer.parseInt(messageParts[6]);
                globalJobList.add(newJob);

                algorithm.makeSchedulingDecision(newJob);
            }

            case "JCPL" -> {
                // TODO: Get rid of the job somehow
            }

            case "RESF, RESR" -> {
                throw new UnrecognisedCommandException("Not yet implemented: " + messageParts[0]);
            }

            case "NONE" -> {
                // TODO: Handle client quit
            }

            default -> {
                throw new UnrecognisedCommandException("Unknown command: " + messageParts[0] );
            }

        }
    }


    public void sendMessage(String command, Object ... args) throws IOException {

        switch (command) {

            case "HELO" -> {
                connection.writeString("HELO");
            }

            case "AUTH" -> {
                if (args.length > 0 && args[0] instanceof String) {
                    connection.writeString("AUTH " + args[0]);
                } else {
                    connection.writeString("AUTH " + System.getProperty("user.name"));
                }
            }

            case "REDY" -> {
                connection.writeString("REDY");
            }

            case "GETS" -> {
                // TODO: Implement gets
            }

            case "SCHD" -> {
                Job job = null;
                for (Object obj : args) {
                    if (obj instanceof Job) {
                        job = (Job) obj;
                        break;
                    }
                }

                Server server = null;
                for (Object obj : args) {
                    if (obj instanceof Server) {
                        server = (Server) obj;
                        break;
                    }
                }
                connection.writeString("SCHD " + job.id + " " + server.type + " " + server.id);
            }

        }

    }

}
