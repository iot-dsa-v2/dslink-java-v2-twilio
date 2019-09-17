package org.iot.dsa.dslink.twilio;


import org.iot.dsa.dslink.ActionResults;
import org.iot.dsa.dslink.DSMainNode;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.action.DSAction;
import org.iot.dsa.node.action.DSIActionRequest;


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
     * Create Account Action
     */
    private void addAccount(DSMap parameters) {
        String name = parameters.getString(Constants.NAME);
        put(name, new TwilioAccountNode(parameters));
        info("Added Account" + name);
    }

    /**
     * Create Add Account Action
     */
    private DSAction makeAddAccontAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResults invoke(DSIActionRequest req) {
                ((MainNode) req.getTarget()).addAccount(req.getParameters());
                return null;
            }
        };
        act.addParameter(Constants.NAME, DSString.NULL, "Name of Twilio account");
        act.addParameter(Constants.ACCOUNTSID, DSString.NULL, "Account SID");
        act.addParameter(Constants.ACCOUNTTOKEN, DSString.NULL, "Authorization Token");

        return act;
    }

}
