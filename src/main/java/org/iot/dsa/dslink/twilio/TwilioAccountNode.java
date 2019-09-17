package org.iot.dsa.dslink.twilio;

import javax.ws.rs.core.Response;
import org.iot.dsa.dslink.Action.ResultsType;
import org.iot.dsa.dslink.ActionResults;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSLong;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.action.DSAction;
import org.iot.dsa.node.action.DSIAction;
import org.iot.dsa.node.action.DSIActionRequest;
import org.iot.dsa.util.DSException;

/**
 * Created by Ketan Hattalli on 6/27/2018.
 */

public class TwilioAccountNode extends DSNode {

    private TwilioWebClient client;
    private DSMap parameters;

    public TwilioAccountNode() {

    }

    public TwilioAccountNode(DSMap parameters) {
        this.parameters = parameters;
    }

    public String getSid() {
        return parameters.get(Constants.ACCOUNTSID, "");
    }

    public String getToken() {
        return parameters.get(Constants.ACCOUNTTOKEN, "");
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
        declareDefault(Constants.REMOVEACCOUNT, makeRemoveAccontAction());
    }

    /**
     * Override this method and Initializes nodes and parameters once Node is Stable.
     */
    @Override
    protected void onStable() {
        init();
    }

    @Override
    protected void onStarted() {
        if (this.parameters == null) {
            DSIObject o = get(Constants.PARAMS);
            if (o instanceof DSMap) {
                this.parameters = (DSMap) o;
            }
        } else {
            put(Constants.PARAMS, parameters.copy()).setPrivate(true);
        }
    }

    private void editAccount(DSMap parameters) {
        this.parameters.put(Constants.ACCOUNTSID, parameters.get(Constants.ACCOUNTSID));
        this.parameters.put(Constants.ACCOUNTTOKEN, parameters.get(Constants.ACCOUNTTOKEN));
        debug("Account Edited" + getInfo().getName());
        init();
    }

    private ActionResults getActionResponse(DSIActionRequest req, Response r) {
        if (r != null) {
            return DSIAction.toResults(req,
                                       DSString.valueOf(r.getStatus()),
                                       DSString.valueOf(r.readEntity(String.class)));
        }
        return null;
    }

    private ActionResults getActionResponse(DSIActionRequest req, String message) {
        if (!(message == null || message.equals(""))) {
            return DSIAction.toResults(req,
                                       DSString.valueOf("404"),
                                       DSString.valueOf(message));
        }
        return null;
    }

    /**
     * Get All Messages
     */
    private ActionResults getAllMessages(DSIActionRequest req) {
        DSMap parameters = req.getParameters();
        String DateSet = parameters.getString(Constants.DATESENT);

        // Check if DateSet has correct syntax and if the date is a valid date
        if (DateSet != null && !DateSet.equals("")) {
            String datePram = "";
            String dateStr = "";
            parameters.remove(Constants.DATESENT);
            if (DateSet.startsWith("=")) {
                datePram = Constants.DATESENT;
                dateStr = DateSet.substring(1);
            } else if (DateSet.startsWith(">=")) {
                datePram = Constants.DATESENT + "%3E";
                dateStr = DateSet.substring(2);
            } else if (DateSet.startsWith("<=")) {
                datePram = Constants.DATESENT + "%3C";
                dateStr = DateSet.substring(2);
            } else {
                DSException
                        .throwRuntime(new Throwable("Wrong DateSent value condition:" + DateSet));
                error("Wrong DateSent value condition:" + DateSet);
                return getActionResponse(req, "Wrong DateSent value condition:" + DateSet);
            }
            if (!Util.isThisDateValid(dateStr, "YYYY-MM-DD")) {
                DSException.throwRuntime(new Throwable("Invalid Date format :" + dateStr));
                error("Invalid Date format :" + dateStr);
                return getActionResponse(req, "Invalid Date format :" + dateStr);

            }
            parameters.put(datePram, dateStr);
        }

        // Check if the To phone number is valid number
        String toNumber = parameters.getString(Constants.TO);
        if (toNumber != null && !toNumber.trim().equals("")) {
            if (!toNumber.matches("\\+?\\d+")) {
                DSException.throwRuntime(new Throwable("To is not valid phone number:" + toNumber));
                error("To number not valid phone number:" + toNumber);
                return getActionResponse(req, "To is not valid phone number:" + toNumber);
            }
        }

        // Check if the From phone number is valid number
        String fromNumber = parameters.getString(Constants.FROM);
        if (fromNumber != null && !fromNumber.trim().equals("")) {
            if (!fromNumber.matches("\\+?\\d+")) {
                DSException
                        .throwRuntime(new Throwable("From is not valid phone number:" + toNumber));
                error("From number not valid phone number:" + toNumber);
                return getActionResponse(req, "From is not valid phone number:" + toNumber);
            }
        }

        return getActionResponse(req, client.getMessages(parameters));
    }

    /**
     * Get Message
     */
    private ActionResults getMessage(DSIActionRequest req) {
        DSMap parameters = req.getParameters();
        String messagesid = parameters.getString(Constants.MESSAGESID);
        if (messagesid == null || messagesid.equals("")) {
            DSException.throwRuntime(new Throwable(Constants.MESSAGESID + " is black"));
            warn(Constants.MESSAGESID + "is blank");
            return getActionResponse(req, Constants.MESSAGESID + "is blank");
        }

        return getActionResponse(req, client.getMessage(parameters));
    }

    /**
     * Initializes Twilio Account Node
     */
    private void init() {
        // Call this here in init which is called in onStable. This is needed because parameter is not available at that time
        // Also set transient true will persist the action only when its created.
        put(Constants.EDITACCOUNT, makeEditAccontAction()).setTransient(true);
        put(Constants.ACCOUNTSID, DSString.valueOf(getSid())).setReadOnly(true);
        put(Constants.ACCOUNTTOKEN, DSString.valueOf(getToken()))
                .setPrivate(true); // Hide Account Token
        client = new TwilioWebClient(getSid(), getToken());
    }

    /**
     * Make Edit Message Action
     */
    private DSAction makeEditAccontAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResults invoke(DSIActionRequest req) {
                ((TwilioAccountNode) req.getTarget()).editAccount(req.getParameters());
                return null;
            }
        };
        // Use this instead of addParameter so default values are set
        act.addDefaultParameter(Constants.ACCOUNTSID, DSString.valueOf(getSid()), "Account SID");
        act.addDefaultParameter(Constants.ACCOUNTTOKEN, DSString.valueOf(getToken()),
                                "Authorization Token");
        return act;
    }

    /**
     * Make Get All Messages Action
     */
    private DSAction makeGetAllMessagesAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResults invoke(DSIActionRequest req) {
                return ((TwilioAccountNode) req.getTarget()).getAllMessages(req);
            }
        };
        act.addParameter(Constants.DATESENT, DSString.NULL,
                         "Optional. in GMT format YYYY-MM-DD. Example: =2009-07-06. <=YYYY-MM-DD, >=YYYY-MM-DD ");
        act.addParameter(Constants.TO, DSString.NULL, "Optional. 'To' phone number.");
        act.addParameter(Constants.FROM, DSString.NULL, "Optional. 'From' phone number.");
        act.setResultsType(ResultsType.VALUES);
        act.addColumnMetadata("Status", DSLong.NULL);
        act.addColumnMetadata("Output", DSString.NULL);
        return act;
    }

    /**
     * Make Get Message Action
     */
    private DSAction makeGetMessageAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResults invoke(DSIActionRequest req) {
                return ((TwilioAccountNode) req.getTarget()).getMessage(req);
            }
        };

        act.addParameter(Constants.MESSAGESID, DSString.NULL, "Required. Message SID");
        act.setResultsType(ResultsType.VALUES);
        act.addColumnMetadata("Status", DSLong.NULL);
        act.addColumnMetadata("Output", DSString.NULL);
        return act;
    }

    /**
     * Make Remove Account Action
     */
    private DSAction makeRemoveAccontAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResults invoke(DSIActionRequest req) {
                ((TwilioAccountNode) req.getTarget()).removeAccount(req.getParameters());
                return null;
            }
        };
        return act;
    }

    /**
     * Make Send Message Action
     */
    private DSAction makeSendMessageAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResults invoke(DSIActionRequest req) {
                return ((TwilioAccountNode) req.getTarget()).sendMessage(req);
            }
        };
        act.addParameter(Constants.TO, DSString.NULL, "Enter mandatory 'To' phone number.");
        act.addParameter(Constants.FROM, DSString.NULL,
                         "Enter mandatory 'From' phone number.");
        act.addParameter(Constants.BODY, DSString.NULL, "Message Body");
        act.addParameter(Constants.MEDIA, DSString.NULL, "Media URL or Location");
        act.setResultsType(ResultsType.VALUES);
        act.addColumnMetadata("Status", DSLong.NULL);
        act.addColumnMetadata("Output", DSString.NULL);
        return act;
    }

    private void removeAccount(DSMap parameters) {
        getParent().remove(getInfo());
        debug("Account Edited" + getInfo().getName());
    }

    private ActionResults sendMessage(DSIActionRequest req) {
        DSMap parameters = req.getParameters();
        String to = parameters.getString(Constants.TO);
        String from = parameters.getString(Constants.FROM);
        String body = parameters.getString(Constants.BODY);
        String media = parameters.getString(Constants.MEDIA);

        String errorMessage = validateMessage(to, from, body, media);
        if (!errorMessage.equals("")) {
            error(errorMessage);
            DSException.throwRuntime(new Throwable(errorMessage));
            return null;
        }

        return getActionResponse(req, client.sendMessages(parameters));
    }

    private String validateMessage(String to, String from, String body, String media) {

        String errorMessage = "";
        if (to == null || to.equals("")) {
            errorMessage = errorMessage + " Enter " + Constants.TO + " value.\n";
        }
        if (from == null || from.equals("")) {
            errorMessage = errorMessage + " Enter " + Constants.FROM + " value.\n";
        }
        if ((body == null || body.equals("")) && (media == null || media.equals(""))) {
            errorMessage = errorMessage + " Enter either Message Body or Media URL.\n";
        }

        return errorMessage;
    }
}
