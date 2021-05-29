import java.io.*;
import java.net.Socket;

public class Connection {

    private BufferedReader inStream;
    private BufferedWriter outStream;

    /**
     * @param host The IP address of the DSSimServer to which the client will connect
     * @param port The TCP port of the DSSimServer to which the client will connect
     * @throws IOException If an I/O error occurs
     */
    public Connection(String host, int port) throws IOException {
        Socket socket = new Socket(host, port);
        inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        outStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    /**
     * Writes a string to the DSSimServer, appending a newline to the string if required
     * @param message The string that will be sent to the server, with or without a trailing newline
     * @throws IOException If an I/O error occurs
     */
    public void writeString(String message) throws IOException {
        if (message != null) {

            if (message.endsWith("\n")) {
                outStream.write(message);
            } else {
                outStream.write(message + "\n");
            }

            if (ClientMain.verboseMode) System.out.println("C SEND: " + message);

            outStream.flush();
        }
    }

    /**
     * Tells whether the server has previously sent data that has not been processed by the client
     * @return True if a call to readString() is guaranteed not to block
     * @throws IOException If an I/O exception occurs in an underlying class
     */
    public Boolean readReady() throws IOException {
        return inStream.ready();
    }

    /**
     * Reads a single line (delimited by a newline character) from the underlying buffer
     * @return The string received from the server, without a trailing newline character
     * @throws IOException If an I/O exception occurs in an underlying class
     */
    public String readString() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        while (true) {
            int inCharacter = inStream.read();
            if (inCharacter == -1) {
                break;
            }
            if (inCharacter == (int) '\n') {
                break;
            }
            stringBuilder.append((char) inCharacter);
        }

        if (ClientMain.verboseMode) System.out.println("C RECV: " + stringBuilder);

        return stringBuilder.toString();
    }

}
