package es.uc3m.manager.util;

import android.widget.Spinner;

/**
 * Created by Snapster on 22-Aug-16.
 */
public class SpinnerUtils {

    //private method of your class
    public static int getIndex(Spinner spinner, String myString) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }
        }
        return index;
    }
}
