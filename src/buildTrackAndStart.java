import javax.swing.*;
import java.util.ArrayList;

public class buildTrackAndStart {


    static int[] instruments = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};


    /**
     * The class for convert the state of the flags into MIDI events and add them to the track.
     */


    static void buildTrackAndStart() {
        /*List to save event for one instrument */
        ArrayList<Integer> trackList = null;
        /*Remove old track and build new*/
        setUpMidi.sequence.deleteTrack(setUpMidi.track);
        setUpMidi.track = setUpMidi.sequence.createTrack();
        /*Connect all the musical instruments*/
        for (int i = 0; i < 16; i++) {
            trackList = new ArrayList<Integer>();
            /*Check the status of flags on each bar*/
            for (int j = 0; j < 16; j++) {
                JCheckBox jc = buildGUI.checkboxList.get(j + (16 * i));
                if (jc.isSelected()) {
                    int key = instruments[i];
                    trackList.add(key);
                } else {
                    trackList.add(null);
                }
            }
            makeTracks.makeTracks(trackList);//Create the event and add to the track
        }
        /* Check the track for compliance with measures. */
        setUpMidi.track.add(makeEvent.makeEvent(192, 9, 1, 0, 15));
        try {
            setUpMidi.sequencer.setSequence(setUpMidi.sequence);
            setUpMidi.sequencer.setLoopCount(setUpMidi.sequencer.LOOP_CONTINUOUSLY);// Continuous cycle creation
            setUpMidi.sequencer.start();
            setUpMidi.sequencer.setTempoInBPM(120);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
