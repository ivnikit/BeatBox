import java.util.ArrayList;
import java.util.Iterator;

public class makeTracks {


    /**
     * The class for create one instrument event.
     *
     * @param list
     */


    static void makeTracks(ArrayList list) {
        Iterator it = list.iterator();
        for (int i = 0; i < 16; i++) {
            Integer num = (Integer) it.next();
            if (num != null) {
                int numKey = num.intValue();
                /* Create event for on/of and add this in the track.*/
                setUpMidi.track.add(makeEvent.makeEvent(144, 9, numKey, 100, i));
                setUpMidi.track.add(makeEvent.makeEvent(128, 9, numKey, 100, i + 1));
            }
        }
    }
}
