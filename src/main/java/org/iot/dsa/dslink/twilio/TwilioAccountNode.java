package org.iot.dsa.dslink.twilio;

import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.DSValueType;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.ActionSpec.ResultType;
import org.iot.dsa.node.action.DSAction;

import org.apache.cxf.jaxrs.client.WebClient;
import org.iot.dsa.node.action.DSActionValues;
import org.iot.dsa.util.DSException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Ketan Hattalli on 6/27/2018.
 */

public class TwilioAccountNode extends DSNode {

    private DSMap parameters;
    private TwilioWebClient client;

    public TwilioAccountNode(){

    }

    public TwilioAccountNode(DSMap parameters){
        this.parameters = parameters;
    }

    /**
     * Defines the permanent children of this node type, their existence is guaranteed in all
     * instances.  This is only ever called once per, type per process.
     */
    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault(Constants.SENDMESSAGE, makeSendMessageAction());
        declareDefault(Constants.GETMESSAGE, makeGetMessageAction());
        declareDefault(Constants.GETMESSAGES, makeGetAllMessagesAction());
        declareDefault(Constants.REMOVEACCOUNT, makeRemoveAccontAction() );
    }

    /**
     * Handles Twilio Node action.
     */
    @Override
    public ActionResult onInvoke(DSInfo actionInfo, ActionInvocation invocation) {
        return super.onInvoke(actionInfo, invocation);
    }

    @Override
    protected void onStarted() {
        if (this.parameters == null) {
            DSIObject o = get(Constants.PARAMS);
            if (o instanceof DSMap) {
                this.parameters = (DSMap) o;
            }
        } else {
            //put(Constants.PARAMS, parameters.copy()).setHidden(true);
            put(Constants.PARAMS, parameters.copy()).setHidden(true);
        }
    }

    @Override
    protected void onStable() {
        init();
    }

    /**
     * Initializes Twilio Account Node
     */
    private void init(){
        // Call this here in init which is called in onStable. This is needed because parameter is not available at that time
        // Also set transient true will persist the action only when its created.
        put(Constants.EDITACCOUNT, makeEditAccontAction() ).setTransient(true);
        put(Constants.ACCOUNTSID, DSString.valueOf(getSid())).setReadOnly(true);
        put(Constants.ACCOUNTTOKEN, DSString.valueOf(getToken())).setHidden(true); // Hide Account Token
        client = new TwilioWebClient(getSid(),getToken());
    }

    /**
     * Make Get Message Action
     */
    private DSAction makeGetMessageAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                return ((TwilioAccountNode) info.getParent()).getMessage(this,info,invocation.getParameters());
            }
        };

        act.addParameter(Constants.MESSAGESID, DSValueType.STRING, "Required. Message SID");
        act.setResultType(ResultType.VALUES);
        act.addValueResult("Status", DSValueType.NUMBER);
        act.addValueResult("Output", DSValueType.STRING);
        return act;
    }

    private DSAction makeGetAllMessagesAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                return ((TwilioAccountNode) info.getParent()).getAllMessages(this,info,invocation.getParameters());
            }
        };
        act.addParameter(Constants.DATESENT, DSValueType.STRING, "Optional. in GMT format YYYY-MM-DD. Example: =2009-07-06. <=YYYY-MM-DD, >=YYYY-MM-DD ");
        act.addParameter(Constants.TO, DSValueType.STRING, "Optional. 'To' phone number.");
        act.addParameter(Constants.FROM, DSValueType.STRING, "Optional. 'From' phone number.");
        act.setResultType(ResultType.VALUES);
        act.addValueResult("Status", DSValueType.NUMBER);
        act.addValueResult("Output", DSValueType.STRING);
        return act;
    }

    private DSAction makeSendMessageAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                return ((TwilioAccountNode) info.getParent()).sendMessage(this,info,invocation.getParameters());
                //return null;
            }
        };
        act.addParameter(Constants.TO, DSValueType.STRING, "Enter mandatory 'To' phone number.");
        act.addParameter(Constants.FROM, DSValueType.STRING, "Enter mandatory 'From' phone number.");
        act.addParameter(Constants.BODY, DSValueType.STRING, "Message Body");
        act.addParameter(Constants.MEDIA, DSValueType.STRING, "Media URL or Location");
        act.setResultType(ResultType.VALUES);
        act.addValueResult("Status", DSValueType.NUMBER);
        act.addValueResult("Output", DSValueType.STRING);
        return act;
    }

    private DSAction makeRemoveAccontAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((TwilioAccountNode) info.getParent()).removeAccount(invocation.getParameters());
                return null;
            }
        };
        return act;
    }

    private DSAction makeEditAccontAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((TwilioAccountNode) info.getParent()).editAccount(invocation.getParameters());
                return null;
            }
        };
        // Use this instead of addParameter so default values are set
        act.addDefaultParameter(Constants.ACCOUNTSID, DSString.valueOf(getSid()), "Account SID");
        act.addDefaultParameter(Constants.ACCOUNTTOKEN, DSString.valueOf(getToken()), "Authorization Token");
        return act;
    }

    private ActionResult getMessage(DSAction action, DSInfo actionInfo,DSMap parameters){

        String messagesid = parameters.getString(Constants.MESSAGESID);
        if(messagesid==null || messagesid.equals("")){
             DSException.throwRuntime(new Throwable(Constants.MESSAGESID + " is black"));
             warn(Constants.MESSAGESID + "is blank");
             return null;
        }

        return getActionResponse(action,client.getMessage(parameters));
    }

    private ActionResult getAllMessages(DSAction action, DSInfo actionInfo,DSMap parameters){
        String DateSet = parameters.getString(Constants.DATESENT);
        if(DateSet!=null && !DateSet.equals("")){
            parameters.remove(Constants.DATESENT);
            if(DateSet.startsWith("=")){
                parameters.put(Constants.DATESENT,DSString.valueOf(DateSet.substring(1)));
            } else if (DateSet.startsWith(">=")){
                parameters.put(Constants.DATESENT+"%3E",DSString.valueOf(DateSet.substring(2)));
            } else if (DateSet.startsWith("<=")){
                parameters.put(Constants.DATESENT+"%3C",DSString.valueOf(DateSet.substring(2)));
            }else {
                DSException.throwRuntime(new Throwable("Wrong DateSent value condition:"+DateSet));
                error("Wrong DateSent value condition:"+DateSet);
                return null;
            }
        }
        return getActionResponse(action,client.getMessages(parameters));
    }

    private ActionResult sendMessage(DSAction action, DSInfo actionInfo,DSMap parameters){
        String to = parameters.getString(Constants.TO);
        String from = parameters.getString(Constants.FROM);
        String body = parameters.getString(Constants.BODY);
        String media = parameters.getString(Constants.MEDIA);

        String errorMessage = validateMessage(to,from,body,media);
        if(!errorMessage.equals("")){
            error(errorMessage);
            DSException.throwRuntime(new Throwable(errorMessage));
            return null;
        }

        return getActionResponse(action,client.sendMessages(parameters));
    }

    private String validateMessage(String to, String from, String body, String media){

        String errorMessage = "";
        if(to==null || to.equals("")){
            errorMessage = errorMessage+ " Enter " + Constants.TO+ " value.\n";
        }
        if(from==null || from.equals("")){
            errorMessage = errorMessage + " Enter " + Constants.FROM + " value.\n";
        }
        if((body==null ||body.equals("")) && (media==null ||media.equals("")) ){
            errorMessage = errorMessage + " Enter either Message Body or Media URL.\n";
        }

        return errorMessage;
    }

    private void removeAccount(DSMap parameters){
        getParent().remove(getInfo());
        debug("Account Edited" + getInfo().getName());
    }

    private void editAccount(DSMap parameters){
        this.parameters.put(Constants.ACCOUNTSID,parameters.get(Constants.ACCOUNTSID));
        this.parameters.put(Constants.ACCOUNTTOKEN,parameters.get(Constants.ACCOUNTTOKEN));
        debug("Account Edited" + getInfo().getName());
        init();
    }

    private ActionResult getActionResponse(DSAction action,Response r){
        if(r!=null){
            DSActionValues result = new DSActionValues(action);
            result.addResult(DSString.valueOf(r.getStatus()));
            result.addResult(DSString.valueOf(r.readEntity(String.class)));
            return result;
        }
        return null;
    }

    public String getSid() {
        return parameters.get(Constants.ACCOUNTSID,"");
    }

    public String getToken() {
        return parameters.get(Constants.ACCOUNTTOKEN,"");
    }
}