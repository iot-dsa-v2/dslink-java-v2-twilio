package org.iot.dsa.dslink.twilio;


import org.iot.dsa.dslink.DSLinkConnection;
import org.iot.dsa.dslink.DSMainNode;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSInt;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSValueType;
import org.iot.dsa.logging.DSLogger;


/**
 * Created by Ketan Hattalli on 6/27/2018.
 */

public class MainNode extends DSMainNode {

    ///////////////////////////////////////////////////////////////////////////
    // Constants
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // Fields
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Defines the permanent children of this node type, their existence is guaranteed in all
     * instances.  This is only ever called once per, type per process.
     */
    @Override
    protected void declareDefaults() {
        super.declareDefaults();

        declareDefault(Constants.ADDACCOUNT, makeAddAccontAction());
        declareDefault(Constants.DOCS, DSString.valueOf(Constants.TWILIODSLINKDOC))
                .setTransient(true)
                .setReadOnly(true);
    }

    /**
     * Handles Twilio Node action.
     */
    @Override
    public ActionResult onInvoke(DSInfo actionInfo, ActionInvocation invocation) {

        return super.onInvoke(actionInfo, invocation);
    }

    /**
     * Create Add Account Action
     */
    private DSAction makeAddAccontAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((MainNode) info.getParent()).addAccount(invocation.getParameters());
                return null;
            }
        };
        act.addParameter(Constants.NAME, DSValueType.STRING, "Name of Twilio account");
        act.addParameter(Constants.ACCOUNTSID, DSValueType.STRING, "Account SID");
        act.addParameter(Constants.ACCOUNTTOKEN, DSValueType.STRING, "Authorization Token");

        return act;
    }

    /**
     * Create Account Action
     */
    private void addAccount(DSMap parameters){
        String name = parameters.getString(Constants.NAME);
        put(name, new TwilioAccountNode(parameters));
        info("Added Account" + name);
    }

    @Override
    protected void onStarted() {
        super.onStarted();
        /*getLink().getConnection().addListener(new DSLinkConnection.Listener() {
            @Override
            public void onConnect(DSLinkConnection connection) {
                //MainNode.setRequester(getLink().getConnection().getRequester());
            }
            @Override
            public void onDisconnect(DSLinkConnection connection) {
            }
        });*/
    }


}
