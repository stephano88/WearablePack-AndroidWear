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
    
            Set<String> users = new Set<String>();
            users.add(userId);
    
            msg.send('QuoteDiscountApproval', users);
        }
    }
}