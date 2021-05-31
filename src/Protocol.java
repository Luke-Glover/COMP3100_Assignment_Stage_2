import java.io.IOException;

public class Protocol {


    public enum State {
        HANDHAKING,
        AUTHENTICATING,
        SCHEDULING,
        RECEIVING,
        FINISHING
    }

    public State state = State.HANDHAKING;

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

        switch (state) {

            case HANDHAKING -> {
                if (messageParts[0].equals("OK")) {
                    try {
                        sendMessage("AUTH");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    state = State.AUTHENTICATING;

                } else {
                    throw new UnrecognisedCommandException("Unknown command in state HANDSHAKING: " + messageParts[0]);
                }
            }

            case AUTHENTICATING -> {
                if (messageParts[0].equals("OK")) {
                    try {
                        XMLParser.parse(ClientMain.configurationPath);
                        sendMessage("REDY");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    state = State.SCHEDULING;
                }
            }

            case SCHEDULING -> {
                switch (messageParts[0]) {
                    case "OK" -> {
                        try {
                            sendMessage("REDY");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    case "JOBN", "JOBP" -> {
                        if (messageParts.length != 7) {
                            throw new InvalidCommandException("Expects 7 parts in JOBN/JOBP message, got " + messageParts.length);
                        }

                        Job newJob = new Job();
                        newJob.submitTime = Integer.parseInt(messageParts[1]);
                        newJob.id = Integer.parseInt(messageParts[2]);
                        newJob.estRuntTime = Integer.parseInt(messageParts[3]);
                        newJob.core = Integer.parseInt(messageParts[4]);
                        newJob.memory = Integer.parseInt(messageParts[5]);
                        newJob.disk = Integer.parseInt(messageParts[6]);
                        SystemState.submitJob(newJob);

                        // Call the algorithm to make a scheduling decision
                        if (SystemState.getJobsByState(Job.State.SUBMITTED).size() > 0) {
                            algorithm.schedule();
                        }

                    }

                    case "JCPL" -> {
                        Job completedJob = SystemState.getJobById(Integer.parseInt(messageParts[2]));
                        completedJob.getAssignedServer().completeJob(completedJob);

                        // Call the algorithm to make a scheduling decision
                        if (SystemState.getJobsByState(Job.State.QUEUED).size() > 0) {
                            algorithm.balance();
                        } else {
                            try {
                                sendMessage("REDY");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    case "RESF, RESR" -> {
                        throw new UnrecognisedCommandException("Not yet implemented: " + messageParts[0]);
                    }

                    case "NONE" -> {
                        try {
                            sendMessage("QUIT");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        state = State.FINISHING;
                    }

                    case "ERR:" -> {
                        if (ClientMain.debugMode) {
                            System.out.println(message);
                            System.out.println("ERROR: Internal state may be inconsistent");
                        }
                        try {
                            sendMessage("REDY");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    default -> {
                        throw new UnrecognisedCommandException("Unknown command: " + messageParts[0]);
                    }

                }

            }

            case FINISHING -> {
                switch (messageParts[0]) {
                    case "QUIT" -> {
                        System.exit(0);
                    }
                }
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

            case "MIGJ" -> {
                // Old server must come first
                Job job = null;
                for (Object obj : args) {
                    if (obj instanceof Job) {
                        job = (Job) obj;
                        break;
                    }
                }

                Server servers[] = new Server[2];
                int i = 0;
                for (Object obj : args) {
                    if (obj instanceof Server) {
                        servers[i] = (Server) obj;
                        i++;
                        if (i == 2) {
                            break;
                        }
                    }
                }

                connection.writeString("MIGJ " + job.id + " " + servers[0].type + " " + servers[0].id
                        + " " + servers[1].type + " " + servers[1].id);
            }

            case "TERM" -> {
                Server server = null;
                for (Object obj : args) {
                    if (obj instanceof Server) {
                        server = (Server) obj;
                        break;
                    }
                }

                connection.writeString("TERM " + server.type + " " + server.id);
            }

            case "QUIT" -> {
                connection.writeString("QUIT");
            }

        }

    }

}
