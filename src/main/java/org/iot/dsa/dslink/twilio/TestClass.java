package org.iot.dsa.dslink.twilio;

import org.iot.dsa.node.DSMap;



/**
 * Created by KE390804 on 7/2/2018.
 */

public class TestClass {

    public static void main(String[] args){
        System.out.println("hello");

        TwilioWebClient client = new TwilioWebClient("ACe926c34da1dc6afab47b7c15fe9efc3e","60b8adcd791cc12bc16643c8f2d6a38b");

        // Get Messages
        /*
        //Response r = client.invoke("GET", Constants.GETMESSAGES,null);
        Response r = client.getMessages(null);
        System.out.println("response"+r.toString() + "\nstatus"+r.getStatus());
        System.out.println("data"+r.readEntity(String.class));
        */

        // Get Message
        /*
        DSMap parameters = new DSMap();
        parameters.put(Constants.MESSAGESID,"SMdf091cfeeb2a4cb3815e0d6d102578ce");
        Response r = client.getMessage(parameters);
        System.out.println("response"+r.toString() + "\nstatus"+r.getStatus());
        System.out.println("data"+r.readEntity(String.class));
        */

        //Send Messages
        /*
        DSMap parameters = new DSMap();
        parameters.put("To","+12013753393");
        parameters.put("From","+12014313659");
        parameters.put("Body","From Test");

        //Response r = client.invoke("POST", Constants.SENDMESSAGE,parameters);
        Response r = client.sendMessages(parameters);
        System.out.println("response"+r.toString() + "\nstatus"+r.getStatus());
        System.out.println("data"+r.readEntity(String.class));
        */

        //EntityUtils e
    }

}
