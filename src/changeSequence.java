import javax.swing.*;

public class changeSequence {


    /**
     * The class for change the state for the flags
     *
     * @param checkboxState
     */


    static void changeSequence(boolean[] checkboxState) {
        for (int i = 0; i < 256; i++) {
            JCheckBox check = buildGUI.checkboxList.get(i);
            if (checkboxState[i]) {
                check.setSelected(true);
            } else {
                check.setSelected(false);
            }
        }
    }
}
