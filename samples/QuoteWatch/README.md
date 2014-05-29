# Force Wear Demo App

*NOTE: These instructions are still under development.*


## Setup Google Cloud Messaging (GCM)

1. Follow the instructions at: http://developer.android.com/google/gcm/gs.html
2. Take note of your *Project Number* and *API Key*


## Setup Salesforce

1. Sign up for Salesforce Developer Edition: https://developer.salesforce.com/signup
2. ???? Contact support and have them enable the pre-beta for Mobile Push
3. Enable the `Quotes` object and add it to all of the `Opportunity` layouts
    1. Select *Setup*
    2. Select *Customize*
    3. Select *Quotes*
    4. Select to Enable Quotes
    5. Add *Quotes* to all of the layouts
4. Create a Quote Approval Process
    1. Select *Setup*
    2. Select *Create*
    3. Select *Workflow & Approvals*
    4. Select *Approval Processes*
    5. From *Manage Approval Processes For* select `Quote`
    6. Select *Create New Approval Process*
    7. Select *Use Jump Start Wizard*
    8. In *Name* enter `QuoteDiscountApproval`
    9. In the *Specify Entry Criteria* section set a row's *Field* to `Quote: Discount`, *Operator* to `greater or equal` and *Value* to `20`
    10. In the *Select Approver* section select *Automatically assign an approver using a standard or custom hierarchy field* and select *Manager* from the drop-down
    11. Save the Approval Process
    12. Select *View the Approval Process Detail Page*
    13. Activate the Approval Process
5. Create a new Connected App
    1. Select *Setup*
    2. Select *Create*
    3. Select *Apps*
    4. In *Connected Apps* select *New*
    5. In *Connected App Name* specify `ForceQuoteWatch`
    6. Enter your email in *Contact Email*
    7. Select to enable OAUTH
    8. Set the OAUTH Callback URL to `sfdc://success`
    9. Select all of the *Available OAUTH Scopes* and select *Add*
    10. Select to *Push Messaging Enabled*
    11. From the *Supported Push Platform* drop-down select *Android GCM*
    12. Enter your GCM API key in the *Server Key* field
    13. Select *Save*
    14. Take note of your OAUTH Consumer Key
5. Create a manager for your user
    1. Select *Setup*
    2. Select *Manage Users*
    3. Select *Users*
    4. Select *New User*
    5. Fill in the required fields
    6. For *User License* select *Salesforce*
    7. For *Profile* select *System Administrator*
    8. Save the user
    9. Select *Setup*
    10. Select *Manage Users*
    11. Select *Users*
    12. Select *Edit* for your user
    13. In the *Approver Settings* section select the previously created user as the *Manager*
    14. Save your user
6. Create a Trigger
    1. Select *Setup*
    2. Select *Develop*
    3. Select *Tools*
    4. Open the *Developer Console*
    5. Select *File* then *New* then *Apex Trigger*
    6. For the *Name* enter `QuoteApproval` and for *SObject* select `Quote`
    7. Paste the following into the code editor and save the Trigger:
```
// Trigger an approval workflow and push notification when a Quote has a discount >= 20%
trigger QuoteApproval on Quote (before insert, before update) {
    for (Integer i = 0; i < Trigger.new.size(); i++) {
        Quote newQuote = Trigger.new[i];
        Quote oldQuote = new Quote();
        if (Trigger.old != null) {
            oldQuote = Trigger.old[i];
        }
        if (newQuote.Discount >= 20) {
            
            newQuote.Status = 'Needs Review';

            // submit the quote for approval
            
            Approval.ProcessSubmitRequest req = new Approval.ProcessSubmitRequest();
            
            req.setComments('The discount is >= 20% so it requires approval.');
            req.setObjectId(newQuote.Id);
            
            Approval.ProcessResult result = Approval.process(req);
            
            
            // Fetch related objects: Opportunity and Opportunity Owner
            
            Opportunity opp = [ select Name, OwnerId from Opportunity where Id = :newQuote.OpportunityId ];
            
            User owner = [ select Name, FullPhotoUrl from User where Id = :opp.OwnerId ];
            
            
            // send the notification
    
            Messaging.PushNotification msg = new Messaging.PushNotification();
            
            Map<String, Object> payload = new Map<String, Object>();
            payload.put('ownerName', owner.Name);
            payload.put('ownerFullPhotoUrl', owner.FullPhotoUrl);
            payload.put('processId', result.InstanceId);
            payload.put('workItemId', result.getNewWorkitemIds()[0]);
            payload.put('oppName', opp.Name);
            payload.put('quoteId', newQuote.Id);
            payload.put('quoteName', newQuote.Name);
            payload.put('amount', newQuote.TotalPrice);
            payload.put('discount', newQuote.Discount);

            msg.setPayload(payload);
    
            // The approver's Id
            String userId = result.actorIds[0];
            System.debug('userId: ' + userId);
    
            Set<String> users = new Set<String>();
            users.add(userId);
    
            msg.send('ForceQuoteWatch', users);
        }
    }
}
```


## Setup Project

1. Clone the git repo
2. Follow the instructions to setup the Android Wear development environment: http://developer.android.com/wear/preview/start.html
3. Update the values in `src/main/res/values/bootconfig.xml`

        <string name="remoteAccessConsumerKey">YOUR_SFDC_OAUTH_CONSUMER_KEY</string>
        <string name="androidPushNotificationClientId">YOUR_GCM_PROJECT_NUMBER</string>

4. Run this app on the physical Android device that is connected to the Wear emulation device
5. When the app launches sign into Salesforce as the manager you created - you should then see a list of opportunities (just as validation that setup worked)
6. Press the `Send Notification` button as a test and verify the notification shows up on the Wear emulator


## Demo Script

1. Sign into Salesforce.com as a user who's approvals will be sent to the user you signed into the phone app with
2. Create a new `Opportunity` in Salesforce
3. Create a new `Quote` on the `Opportunity`
4. Add a `Line Item` to the `Quote`
5. Select a Product and specify a quantity and a discount >= 20%
6. A notification should display on the Wear emulator
7. Either Approve, Reject, or Open (on the phone) the Quote