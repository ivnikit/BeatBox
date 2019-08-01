import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;

public class makeEvent {


    /**
     * The class for create and send a message that track event.
     *
     * @param comd
     * @param chan
     * @param one
     * @param two
     * @param tick
     * @return event
     */


    static MidiEvent makeEvent(int comd, int chan, int one, int two, int tick) {
        MidiEvent event = null;
        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(comd, chan, one, two);
            event = new MidiEvent(a, tick);
        } catch (Exception e) {
        }
        return event;
    }
}
