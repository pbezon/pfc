package es.uc3m.manager.util;

/**
 * Created by Snapster on 17/04/2015.
 */
public class Constants {

    public static boolean OFFLINE = false;

    public enum ITEM_ACTION {
        WITHDRAW(0), RETURN(1), ADD(0), REMOVE(3);

        private final int value;

        ITEM_ACTION(int value) {
            this.value = value;
        }
    }
}
