package org.iot.dsa.dslink.twilio;

import org.apache.cxf.jaxrs.client.WebClient;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSMap.Entry;
import org.iot.dsa.util.DSException;

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
        if(parameters!=null) {
            for (int i = 0; i < parameters.size(); i++) {
                Entry entry = parameters.getEntry(i);
                Object value = Util.dsElementToObject(entry.getValue());
                form.param(entry.getKey(), value.toString());
            }
        }
        return client.form(form);
    }

    /*
    public Response invoke(String method,String service, DSMap parameters){

        WebClient client = configureWebClient(service);

        Response r = null;
        try {
            if(method.equalsIgnoreCase("POST")){
                r = invokePost(client,parameters);
            }else if(method.equalsIgnoreCase("DELETE")){
                r = invokeDelete(client,parameters);
            } else if(method.equalsIgnoreCase("GET")){
                r = invokeGet(client,parameters);
            } else {
                DSException.throwRuntime(new Throwable("Wrong Method : "+method));
            }
        } catch (Exception ex) {
            DSException.throwRuntime(ex);
        }finally {
            client.close();
        }
        return r;
    }

    private Response invokePost(WebClient client,DSMap parameters){

        Form form = new Form();
        if(parameters!=null) {
            for (int i = 0; i < parameters.size(); i++) {
                Entry entry = parameters.getEntry(i);
                Object value = Util.dsElementToObject(entry.getValue());
                form.param(entry.getKey(), value.toString());
            }
        }
        return client.form(form);
    }

    private Response invokeGet(WebClient client,DSMap parameters){
        client = getUpdateQueryParameters(client,parameters);
        return client.get();
    }

    private Response invokeDelete(WebClient client,DSMap parameters){
        client = getUpdateQueryParameters(client,parameters);
        return client.delete();
    }
    */
    private WebClient getUpdateQueryParameters(WebClient client,DSMap parameters){
        if(parameters!=null) {
            for (int i = 0; i < parameters.size(); i++) {
                Entry entry = parameters.getEntry(i);
                Object value = Util.dsElementToObject(entry.getValue());
                client.query(entry.getKey(), value);
            }
        }
        return client;
    }

    /*
    private WebClient configureWebClient(String service){

        String url = getTwilioURL(service);
        //Configure Webclient with basic Authentication
        WebClient client = WebClient.create(url, this.accountsid, this.accounttoken, null);
        client.accept(MediaType.APPLICATION_JSON);
        client.type(MediaType.APPLICATION_JSON);
        return client;
    }

    private String getTwilioURL(String service){
        String url = TWILioURL.replaceAll("\\{AccountSid\\}",this.accountsid);
        if(service.equals(Constants.SENDMESSAGE)) {
            url = url.replaceAll("\\{API\\}","Messages.json");
        } else if(service.equals(Constants.GETMESSAGES)){
            url = url.replaceAll("\\{API\\}","Messages.json");
        }
        return url;
    }
    */

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
