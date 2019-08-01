import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/**
 * The class takes the value of flags from the server and builds track
 */


public class myListSelectionListener implements ListSelectionListener {
    public void valueChanged(ListSelectionEvent le) {
        if (!le.getValueIsAdjusting()) {
            String selected = (String) buildGUI.incomingList.getSelectedValue();
            if (selected != null) {
                boolean[] selectedState = remoteReader.otherSeqsMap.get(selected);
                changeSequence.changeSequence(selectedState);
                setUpMidi.sequencer.stop();
                buildTrackAndStart.buildTrackAndStart();
            }
        }
    }
}

