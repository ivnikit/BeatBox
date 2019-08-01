import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

/**
 * The class for create buildGUI.
 */

public class buildGUI {
    static JFrame theFrame;
    static JPanel mainPanel;
    static ArrayList<JCheckBox> checkboxList;
    static JList incomingList;
    static JTextField userMessage;
    static Vector<String> listVector = new Vector<String>();
    static int nextNum;
    static String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare",
            "Crash Cymbal", "Hand Clap", "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga", "Cowbell",
            "Vibraslap", "Low-mid Tom", "High Agogo", "Open Hi Congo"};


    public static void buildGui() {
        /* Create a buildGUI Window.*/
        theFrame = new JFrame("Cyber BeatBox");
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        /*Indent from edgest of interface.*/
        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        /* Add objects to the buildGUI.*/
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
        incomingList.addListSelectionListener(new myListSelectionListener());
        incomingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane theList = new JScrollPane(incomingList);
        buttonBox.add(theList);
        incomingList.setListData(listVector);

        /*Create the musical instrument list on the buildGUI.*/

        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for (int i = 0; i < 16; i++) {
            nameBox.add(new Label(instrumentNames[i]));
        }
        /*Position buttons and musical instrument list on the buildGUI.*/
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
     * Next classes for button listeners:
     * MyStartListener
     * MyStopListener
     * MyUpTempoListener
     * MyDownTempoListener
     * MySendListener
     * MyCleanListener
     */


    public static class MyStartListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            buildTrackAndStart.buildTrackAndStart();
        }
    }

    public static class MyStopListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            setUpMidi.sequencer.stop();
        }
    }

    public static class MyUpTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            float tempoFactor = setUpMidi.sequencer.getTempoFactor();
            /* Rate increase rate */
            setUpMidi.sequencer.setTempoFactor((float) (tempoFactor * 1.03));
        }
    }

    public static class MyDownTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            float tempoFactor = setUpMidi.sequencer.getTempoFactor();
            /* Rate of reduction*/
            setUpMidi.sequencer.setTempoFactor((float) (tempoFactor * 0.97));
        }
    }

    public static class MyCleanListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            /* Set all the state of the Flags in false*/
            for (int i = 0; i < 256; i++) {
                JCheckBox check = checkboxList.get(i);
                check.setSelected(false);
            }
            setUpMidi.sequencer.stop();
        }
    }


    /**
     * The class send track and message to the server
     */


    public static class MySendListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            /*Check the state of the flags */
            boolean[] checkboxState = new boolean[256];
            for (int i = 0; i < 256; i++) {
                JCheckBox check = checkboxList.get(i);
                if (check.isSelected()) {
                    checkboxState[i] = true;
                }
            }
            /*Send user message and track*/
            try {
                startUp.out.writeObject(startUp.userName + nextNum++ + ": " + userMessage.getText());
                startUp.out.writeObject(checkboxState);
            } catch (Exception ex) {
                System.out.println("Sorry dude. Could not send it to the server.");
            }
            userMessage.setText("");
        }
    }

}
