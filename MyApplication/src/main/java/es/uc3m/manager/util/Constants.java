package es.uc3m.manager.util;

/**
 * Created by Snapster on 17/04/2015.
 */
public class Constants {

    public enum PRODUCT_ACTION {
        WITHDRAW(0), RETURN(1), ADD(0), REMOVE(3);

        private final int value;

        PRODUCT_ACTION(int value) {
            this.value = value;
        }
    }

}
