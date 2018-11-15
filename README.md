# dslink-java-v2-twilio

* Java - version 1.8 and up.
* [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)


## Overview

This is a link for interacting with Twilio. This DSLink leverages Twilio APIs for sending messages.

If you are not familiar with DSA and links, an overview can be found at
[here](http://iot-dsa.org/get-started/how-dsa-works).

This link was built using the DSLink Java V2 SDK which can be found
[here](https://github.com/iot-dsa-v2/sdk-dslink-java-v2).


## Link Architecture

This section outlines the hierarchy of nodes defined by this link.

- _MainNode_ - The root node of the link, has an action to add an Twilio to the view.
  - _TwilioAccountNode_ - A node representing a specific Twilio Account. It has method to interact with Twilio.


## Node Guide

The following section provides detailed descriptions of each node in the link as well as
descriptions of actions, values and child nodes.


### MainNode

This is the root node of the link.

**Actions**
- Add Account - Connect to an Twilio account and add a child _TwilioAccountNode_ to represent it.
  - `Name` - Required. Any value to identify the account.
  - `Sid` - Required. Twilio account SID.
  - `Token` - Required. Twilio account Authorization Token.

**Child Nodes**
 - any _TwilioAccountNodes_ that have been added.

### TwilioAccountNode

This node represents a specific Twilio Account. While using phone numbers it is recommended but not necessary to specify country code. For example to send message to/from US phone number use +1<phone number>

**Actions**
- Send Message : Send Message.
  - `To` : Required 'To' phone number.
  - `From` : Required 'From' phone number. This must be your Twilio phone number associated with your account.
  - `Body` : Required if MediaUrl is not passed. The text body of the message. Up to 1600 characters long.
  - `MediaUrl` : Required if Body is not passed. This media URL or Location. Currently Twilio supports sending media in the US and Canada. This feature is based on your Twilio account. Twilio supports .gif, .png, or .jpeg content and will format the image on your recipient's device. If the content-type header of your MediaUrl does not match the media at that URL, Twilio will reject the request.
  - `Status`: 201 if successful. 400 if failed.
  - `Output` : If successful the entire status of message. Error code if failed. Some the error code includes.
    - 21211 : To number is not valid.
    - 21606 : From number is not valid.
- Get Message : Fetch single message using Message id.
  - `MessageSID` : Required message ID.
  - `Status`- 200 if successful. 404 if failed.
  - `Output`- If successful the entire message details. Error code if failed. Some the error code includes.
    - 20404 : If message ID id wrong.
- Get All Messages : Fetch all messages.
  - `DateSent` : Optional. Only show messages sent on this date (in GMT format), given as YYYY-MM-DD. Example: '=2009-07-06'. You can also specify inequality, such as '<=YYYY-MM-DD' for messages that were sent on or before midnight on a date, '>=YYYY-MM-DD' for messages sent on or after midnight on a date.
  - `To` : Optional. Only fetch messages sent to this number.
  - `From` : Optional. Only fetch messages sent from this phone number.
  - `Status`- 200 if successful. 404 if failed.
  - `Output` : List of all messages sorted by DateSent with most recent messages appearing first. If messages list is long (more than 50) a pagination URL is returned in next_page_uri field.
- Remove Account : Removes specific account and _TwilioAccountNode_ Node.
- Edit Account : Update Twilio account details.
  - `Sid` : Required. Twilio account SID.
  - `Token` : Required. Twilio account Authorization Token.

## Acknowledgements

SDK-DSLINK-JAVA

This software contains unmodified binary redistributions of 
[sdk-dslink-java-v2](https://github.com/iot-dsa-v2/sdk-dslink-java-v2), which is licensed 
and available under the Apache License 2.0. An original copy of the license agreement can be found 
at https://github.com/iot-dsa-v2/sdk-dslink-java-v2/blob/master/LICENSE
