package org.iot.dsa.dslink.twilio;

import org.apache.cxf.jaxrs.client.WebClient;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSMap.Entry;
import org.iot.dsa.util.DSException;

import java.util.Iterator;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Ketan Hattalli on 7/2/2018.
 */

public class TwilioWebClient {

    String accountsid = null;
    String accounttoken = null;
    String TWILioURL = "https://api.twilio.com/2010-04-01/Accounts/{AccountSid}/{API}";
    String TWilioBaseURL = "https://api.twilio.com/2010-04-01/Accounts";

    public TwilioWebClient(String accountsid, String accounttoken){
        this.accountsid = accountsid;
        this.accounttoken = accounttoken;
    }

    public Response getMessages(DSMap parameters){
        String url = getTwilioAccountURL() + "/Messages.json" ;
        WebClient client = getWbClient(url);
        client = getUpdateQueryParameters(client,parameters);
        return client.get();
    }

    public Response getMessage(DSMap parameters){
        String url = getTwilioAccountURL() + "/Messages/"+parameters.getString(Constants.MESSAGESID)+".json" ;
        WebClient client = getWbClient(url);
        return client.get();
    }

    public Response sendMessages(DSMap parameters){
        String url = getTwilioAccountURL() + "/Messages.json" ;
        WebClient client = getWbClient(url);
        Form form = new Form();

        Iterator iterator = parameters.iterator();
        while(iterator.hasNext())
        {
            Entry entry = (Entry) iterator.next();
            Object value = Util.dsElementToObject(entry.getValue());
            form.param(entry.getKey(), value.toString());
        }
        return client.form(form);
    }

    private WebClient getUpdateQueryParameters(WebClient client,DSMap parameters){

        Iterator iterator = parameters.iterator();
        while(iterator.hasNext())
        {
            Entry entry = (Entry) iterator.next();
            Object value = Util.dsElementToObject(entry.getValue());
            client.query(entry.getKey(), value);
        }
        return client;
    }

    private WebClient getWbClient(String url){
        WebClient client = WebClient.create(url, this.accountsid, this.accounttoken, null);
        client.accept(MediaType.APPLICATION_JSON);
        client.type(MediaType.APPLICATION_JSON);
        return client;
    }

    private String getTwilioAccountURL(){
        return TWilioBaseURL + "/" + this.accountsid;
    }
}
