import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class startUp {


    /**
     * The class to build program.
     *
     * @param name
     */


    static ObjectOutputStream out;
    static ObjectInputStream in;
    static String userName;

    static void startUp(String name) {
        userName = name;

        try {
            /* Network setup and input/output. */
            Socket sock = new Socket("127.0.0.1", 4242);
            out = new ObjectOutputStream(sock.getOutputStream());
            in = new ObjectInputStream(sock.getInputStream());
            /* Create thread to the read message.*/
            Thread remote = new Thread(new remoteReader());
            remote.start();
        } catch (Exception ex) {
            System.out.println("couldn't connect - you'll have to play alone.");
        }
        /*Links to used methods.
         * @see setUpMidi
         * @see buildGUI
         * */
        setUpMidi.setUpMidi();
        buildGUI.buildGui();
    }
}
