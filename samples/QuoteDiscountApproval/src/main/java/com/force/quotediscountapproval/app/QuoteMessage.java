package com.force.quotediscountapproval.app;

import android.os.Bundle;

public class QuoteMessage {

    public QuoteMessage(String ownerFullPhotoUrl, String ownerName, String processId, String workItemId, String quoteId, String quoteName, String oppName, Long amount, String discount) {
        this.ownerFullPhotoUrl = ownerFullPhotoUrl;
        this.ownerName = ownerName;
        this.processId = processId;
        this.workItemId = workItemId;
        this.quoteId = quoteId;
        this.quoteName = quoteName;
        this.oppName = oppName;
        this.amount = amount;
        this.discount = discount;
    }

    public QuoteMessage(Bundle bundle) {

        ownerFullPhotoUrl = bundle.getString("ownerFullPhotoUrl");

        ownerName = bundle.getString("ownerName");

        processId = bundle.getString("processId");

        workItemId = bundle.getString("workItemId");

        quoteId = bundle.getString("quoteId");

        quoteName = bundle.getString("quoteName");

        oppName = bundle.getString("oppName");

        String amountString = bundle.getString("amount");

        if (amountString != null) {
            amount = Long.valueOf(amountString);
        }
        else {
            amount = 0L;
        }

        discount = bundle.getString("discount");
    }

    final public String ownerFullPhotoUrl;
    final public String ownerName;
    final public String processId;
    final public String workItemId;
    final public String quoteId;
    final public String quoteName;
    final public String oppName;
    final public Long amount;
    final public String discount;

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("ownerFullPhotoUrl", ownerFullPhotoUrl);
        bundle.putString("ownerName", ownerName);
        bundle.putString("processId", processId);
        bundle.putString("workItemId", workItemId);
        bundle.putString("quoteId", quoteId);
        bundle.putString("quoteName", quoteName);
        bundle.putString("oppName", oppName);
        bundle.putString("amount", amount.toString());
        bundle.putString("discount", discount);
        return bundle;
    }

    @Override
    public String toString() {
        return "QuoteMessage{" +
                "ownerFullPhotoUrl='" + ownerFullPhotoUrl + '\'' +
                ", processId='" + processId + '\'' +
                ", workItemId='" + workItemId + '\'' +
                ", quoteId='" + quoteId + '\'' +
                ", quoteName='" + quoteName + '\'' +
                ", oppName='" + oppName + '\'' +
                ", amount=" + amount +
                ", discount='" + discount + '\'' +
                '}';
    }
}
