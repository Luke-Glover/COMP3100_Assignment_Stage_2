import java.io.IOException;
import java.util.ArrayList;

public class ClientMain {

    // Client-wide variables w/ sensible defaults
    public static boolean verboseMode = false;
    public static boolean debugMode = false;
    public static String configurationPath = "ds-system.xml";
    public static String algorithmString = "allToLargest";
    public static String remoteAddress = "127.0.0.1";
    public static int remotePort = 50000;


    public ArrayList<Job> jobList = new ArrayList<>();


    public static void main(String[] args) throws IOException {

        // Command line argument handling
        for (int i = 0; i < args.length; i++) {

            switch (args[i]) {
                case "-v" -> verboseMode = true;
                case "-d" -> debugMode = true;
                case "-c" -> {
                    try {
                        configurationPath = args[++i];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("FATAL: No path provided with '-c' argument");
                        System.exit(-1);
                    }
                }
                case "-a" -> {
                    try {
                        algorithmString = args[++i];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("FATAL: No algorithm provided with '-a' argument");
                        System.exit(-1);
                    }
                }
                case "-i", "--ip" -> {
                    try {
                        remoteAddress = args[++i];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("FATAL: No IP address given with '-ip' argument");
                        System.exit(-1);
                    }
                }
                case "-p", "--port" -> {
                    try {
                        remotePort = Integer.parseInt(args[++i]);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("FATAL: No port given with '-p' argument");
                        System.exit(-1);
                    } catch (NumberFormatException e) {
                        System.out.println("FATAL: Port not a number");
                        System.exit(-1);
                    }
                }
                default -> {
                    System.out.println("FATAL: Unrecognised argument " + args[i]);
                    System.exit(-1);
                }
            }

        }


        // Attempt to connect to ds-sim server
        Connection remoteConnection = null;
        try {
            remoteConnection = new Connection(remoteAddress, remotePort);
        } catch (IOException e) {
            System.out.println("FATAL: Could not connect to DS-Sim server");
            System.exit(-1);
        }


        // Set scheduling algorithm
        SchedulingAlgorithm algorithm;
        switch (algorithmString) {
            case "PF" -> {
                if (debugMode) {
                    System.out.println("USING: BestFit w/ Rescheduling");
                }
                algorithm = new SchedulingAlgorithmBalancingBF();
            }
            default -> {
                if (debugMode) {
                    System.out.println("USING: All to largest");
                }
                algorithm = new SchedulingAlgorithmAllToLargest();
            }
        }


        // Create necessary objects and send first message
        Protocol protocol = new Protocol(remoteConnection, algorithm);
        protocol.sendMessage("HELO");


        // Main event loop
        while (true) {

            // Blocks until a complete string has been received from the server
            String receivedMessage = remoteConnection.readString();

            // Parse message to the protocol translation
            try {
                protocol.parseMessage(receivedMessage);
            } catch (UnrecognisedCommandException | InvalidCommandException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }
}
