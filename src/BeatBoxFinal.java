import javax.sound.midi.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * BeatBox client.
 * This application allows you to create a MIDI track.
 */
public class BeatBoxFinal {

    JFrame theFrame;
    JPanel mainPanel;
    JList incomingList;
    JTextField userMessage;
    ArrayList<JCheckBox> checkboxList;
    int nextNum;
    Vector<String> listVector = new Vector<String>();
    String userName;
    ObjectOutputStream out;
    ObjectInputStream in;
    HashMap<String, boolean[]> otherSeqsMap = new HashMap<String, boolean[]>();
    Sequencer sequencer;
    Sequence sequence;
    Sequence mySequence = null;
    Track track;

    String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare",
            "Crash Cymbal", "Hand Clap", "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga", "Cowbell",
            "Vibraslap", "Low-mid Tom", "High Agogo", "Open Hi Congo"};
    int[] instruments = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};


    /**
     * The method to build program.
     *
     * @param name
     */
    public void startUp(String name) {
        userName = name;

        try {
            /* Network setup and input/output. */
            Socket sock = new Socket("127.0.0.1", 4242);
            out = new ObjectOutputStream(sock.getOutputStream());
            in = new ObjectInputStream(sock.getInputStream());
            /* Create thread to the read message.*/
            Thread remote = new Thread(new RemoteReader());
            remote.start();
        } catch (Exception ex) {
            System.out.println("couldn't connect - you'll have to play alone.");
        }
        /*Links to used methods.
         * @see setUpMidi
         * @see buildGUI
         * */
        setUpMidi();
        buildGui();
    }

    /**
     * Create Gui
     */
    public void buildGui() {
        /* Create a GUI Window.*/
        theFrame = new JFrame("Cyber BeatBox");
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        /*Indent from edgest of interface.*/
        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        /* Add objects to the GUI.*/
        checkboxList = new ArrayList<JCheckBox>();

        Box buttonBox = new Box(BoxLayout.Y_AXIS);
        /*Create start button.*/
        JButton start = new JButton("Start");
        start.addActionListener(new MyStartListener());
        buttonBox.add(start);
        /*Create stop button.*/
        JButton stop = new JButton("Stop");
        stop.addActionListener(new MyStopListener());
        buttonBox.add(stop);
        /*Create button for more temp.*/
        JButton upTempo = new JButton("Tempo Up");
        upTempo.addActionListener(new MyUpTempoListener());
        buttonBox.add(upTempo);
        /*Create button for down temp.*/
        JButton downTempo = new JButton("Tempo Down");
        downTempo.addActionListener(new MyDownTempoListener());
        buttonBox.add(downTempo);
        /*Create button for send my music configuration.*/
        JButton sendIt = new JButton("sendIt");
        sendIt.addActionListener(new MySendListener());
        buttonBox.add(sendIt);
        /*Create button for clean frame.*/
        JButton clean = new JButton("clean");
        clean.addActionListener(new MyCleanListener());
        buttonBox.add(clean);

        userMessage = new JTextField();
        buttonBox.add(userMessage);
        /* Create interactive text field.*/
        incomingList = new JList();
        incomingList.addListSelectionListener(new MyListSelectionListener());
        incomingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane theList = new JScrollPane(incomingList);
        buttonBox.add(theList);
        incomingList.setListData(listVector);

        /*Create the musical instrument list on the GUI.*/

        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for (int i = 0; i < 16; i++) {
            nameBox.add(new Label(instrumentNames[i]));
        }
        /*Position buttons and musical instrument list on the GUI.*/
        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.WEST, nameBox);

        theFrame.getContentPane().add(background);
        /*Create main panel with check box.*/
        GridLayout grid = new GridLayout(16, 16); // Set the number of rows and columns.
        grid.setVgap(1); // Set vertical gap on 1
        grid.setHgap(2); // Set horizontal gap on 2
        mainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, mainPanel);
        /* Create the field with check box.*/
        for (int i = 0; i < 256; i++) {
            JCheckBox c = new JCheckBox();
            c.setSelected(false); // Set state to off
            checkboxList.add(c);
            mainPanel.add(c);
        }
        /*Set visible and position application on the window.*/
        theFrame.setBounds(300, 140, 300, 300);
        theFrame.pack();
        theFrame.setVisible(true);
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Primary setup of MIDI player.
     */

    public void setUpMidi() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Convert the state of the flags into MIDI events and add them to the track.
     */
    public void buildTrackAndStart() {
        /*List to save event for one instrument */
        ArrayList<Integer> trackList = null;
        /*Remove old track and build new*/
        sequence.deleteTrack(track);
        track = sequence.createTrack();
        /*Connect all the musical instruments*/
        for (int i = 0; i < 16; i++) {
            trackList = new ArrayList<Integer>();
            /*Check the status of flags on each bar*/
            for (int j = 0; j < 16; j++) {
                JCheckBox jc = checkboxList.get(j + (16 * i));
                if (jc.isSelected()) {
                    int key = instruments[i];
                    trackList.add(key);
                } else {
                    trackList.add(null);
                }
            }
            makeTracks(trackList);//Create the event and add to the track
        }
        /* Check the track for compliance with measures. */
        track.add(makeEvent(192, 9, 1, 0, 15));
        try {
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);// Continuous cycle creation
            sequencer.start();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The method for change the state for the flags
     *
     * @param checkboxState
     */

    public void changeSequence(boolean[] checkboxState) {
        for (int i = 0; i < 256; i++) {
            JCheckBox check = checkboxList.get(i);
            if (checkboxState[i]) {
                check.setSelected(true);
            } else {
                check.setSelected(false);
            }
        }
    }

    /**
     * The method for create one instrument event.
     *
     * @param list
     */
    public void makeTracks(ArrayList list) {
        Iterator it = list.iterator();
        for (int i = 0; i < 16; i++) {
            Integer num = (Integer) it.next();
            if (num != null) {
                int numKey = num.intValue();
                /* Create event for on/of and add this in the track.*/
                track.add(makeEvent(144, 9, numKey, 100, i));
                track.add(makeEvent(128, 9, numKey, 100, i + 1));
            }
        }
    }

    /**
     * Method for create and send a message that track event.
     *
     * @param comd
     * @param chan
     * @param one
     * @param two
     * @param tick
     * @return event
     */
    public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick) {
        MidiEvent event = null;
        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(comd, chan, one, two);
            event = new MidiEvent(a, tick);
        } catch (Exception e) {
        }
        return event;
    }

    /**
     * Next classes for button listeners:
     * MyStartListener
     * MyStopListener
     * MyUpTempoListener
     * MyDownTempoListener
     * MySendListener
     * MyCleanListener
     */
    public class MyStartListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            buildTrackAndStart();
        }
    }

    public class MyStopListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            sequencer.stop();
        }
    }

    public class MyUpTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            float tempoFactor = sequencer.getTempoFactor();
            /* Rate increase rate */
            sequencer.setTempoFactor((float) (tempoFactor * 1.03));
        }
    }

    public class MyDownTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            float tempoFactor = sequencer.getTempoFactor();
            /* Rate of reduction*/
            sequencer.setTempoFactor((float) (tempoFactor * 0.97));
        }
    }

    public class MyCleanListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            /* Set all the state of the Flags in false*/
            for (int i = 0; i < 256; i++) {
                JCheckBox check = checkboxList.get(i);
                check.setSelected(false);
            }
            sequencer.stop();
        }
    }

    /**
     * The class send track and message to the server
     */
    public class MySendListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            /*Check the state of the flags */
            boolean[] checkboxState = new boolean[256];
            for (int i = 0; i < 256; i++) {
                JCheckBox check = checkboxList.get(i);
                if (check.isSelected()) {
                    checkboxState[i] = true;
                }
            }
            String messageToSend = null;
            /*Send user message and track*/
            try {
                out.writeObject(userName + nextNum++ + ": " + userMessage.getText());
                out.writeObject(checkboxState);
            } catch (Exception ex) {
                System.out.println("Sorry dude. Could not send it to the server.");
            }
            userMessage.setText("");
        }
    }

    /**
     * The class takes the value of flags from the server and builds track
     */
    public class MyListSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent le) {
            if (!le.getValueIsAdjusting()) {
                String selected = (String) incomingList.getSelectedValue();
                if (selected != null) {
                    boolean[] selectedState = otherSeqsMap.get(selected);
                    changeSequence(selectedState);
                    sequencer.stop();
                    buildTrackAndStart();
                }
            }
        }
    }

    /**
     * The thread for read data from the server. Messages and tracks.
     * When information arrives we read the message and ArrayList with the state of the flags
     * and add to the JList.
     */
    public class RemoteReader implements Runnable {
        boolean[] checkboxState = null;
        Object obj = null;

        public void run() {
            try {
                while ((obj = in.readObject()) != null) {
                    System.out.println("got an object from server");
                    System.out.println(obj.getClass());
                    String nameToShow = (String) obj;
                    checkboxState = (boolean[]) in.readObject();
                    otherSeqsMap.put(nameToShow, checkboxState);
                    listVector.add(nameToShow);
                    incomingList.setListData(listVector);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
