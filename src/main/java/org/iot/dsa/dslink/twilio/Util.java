package org.iot.dsa.dslink.twilio;

import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSList;

/**
 * Created by KE390804 on 7/2/2018.
 */

public class Util {

    public static Object dsElementToObject(DSElement element) {
        if (element.isBoolean()) {
            return element.toBoolean();
        } else if (element.isNumber()) {
            return element.toInt();
        } else if (element.isList()) {
            DSList dsl = element.toList();
            String[] arr = new String[dsl.size()];
            int i = 0;
            for (DSElement e: dsl) {
                arr[i] = e.toString();
                i++;
            }
            return arr;
        } else {
            return element.toString();
        }
    }
}
