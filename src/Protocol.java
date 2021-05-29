import ProtocolObject.Job;

import java.io.IOException;

public class Protocol {


    private Connection connection;


    public Protocol(Connection connection) {
        this.connection = connection;
    }


    public void parseMessage(String message) throws UnrecognisedCommandException, InvalidCommandException {

        String[] messageParts = message.split(" ");

        switch (messageParts[0]) {

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

                // TODO: Do something with the new job
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
                if (args[0] instanceof String) {
                    connection.writeString("AUTH " + args[0]);
                } else {
                    connection.writeString("AUTH " + System.getProperty("user.name"));
                }
            }

            case "GETS" -> {
                // TODO: Implement gets
            }

            case "SCHD" -> {

            }

        }

    }

}
