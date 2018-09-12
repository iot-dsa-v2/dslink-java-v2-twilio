package org.iot.dsa.dslink.twilio;

import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static boolean isThisDateValid(String dateToValidate, String dateFromat){

        if(dateToValidate == null){
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(dateFromat);
        sdf.setLenient(false);

        try {
            //if not valid, it will throw ParseException
            Date date = sdf.parse(dateToValidate);
            System.out.println(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
