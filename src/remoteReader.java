import java.util.HashMap;

public class remoteReader implements Runnable {


    /**
     * The thread for read data from the server. Messages and tracks.
     * When information arrives we read the message and ArrayList with the state of the flags
     * and add to the JList.
     */


    static boolean[] checkboxState = null;
    static Object obj = null;
    static HashMap<String, boolean[]> otherSeqsMap = new HashMap<String, boolean[]>();

    public void run() {
        try {
            while ((obj = startUp.in.readObject()) != null) {
                System.out.println("got an object from server");
                System.out.println(obj.getClass());
                String nameToShow = (String) obj;
                checkboxState = (boolean[]) startUp.in.readObject();
                otherSeqsMap.put(nameToShow, checkboxState);
                buildGUI.listVector.add(nameToShow);
                buildGUI.incomingList.setListData(buildGUI.listVector);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

