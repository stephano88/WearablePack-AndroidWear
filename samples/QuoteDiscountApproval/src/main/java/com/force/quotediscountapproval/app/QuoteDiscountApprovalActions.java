package com.force.quotediscountapproval.app;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QuoteDiscountApprovalActions extends BroadcastReceiver {

    public static final String OPEN = "open";
    public static final String APPROVE = "approve";
    public static final String REJECT = "reject";

    private static final String TAG = QuoteDiscountApprovalActions.class.getName();

    public static final String BUNDLE = "bundle";
    public static final String MESSAGE = "message";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getBundleExtra(BUNDLE);
        QuoteMessage quoteMessage = new QuoteMessage(bundle);

        if (OPEN.equals(intent.getAction())) {
            open(context, intent, quoteMessage);
        } else if (APPROVE.equals(intent.getAction())) {
            approve(context, intent, quoteMessage);
        } else if (REJECT.equals(intent.getAction())) {
            reject(context, intent, quoteMessage);
        }

        // dismiss the notification
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(1);
    }

    protected void open(Context context, Intent intent, QuoteMessage quoteMessage) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setData(Uri.parse("chatter://" + quoteMessage.quoteId));
        context.startActivity(i);
    }

    protected void approve(Context context, Intent intent, QuoteMessage quoteMessage) {
        approveOrRejectWorkItem(context, quoteMessage.workItemId, "Approve", "");
    }

    protected void reject(Context context, Intent intent, QuoteMessage quoteMessage) {
        String message = intent.getStringExtra(MESSAGE);
        if (message == null) {
            message = "";
        }
        Log.d(TAG, message);
        approveOrRejectWorkItem(context, quoteMessage.workItemId, "Reject", message);
    }

    protected void approveOrRejectWorkItem(Context context, String workItemId, String action, String comments) {
        try {
            JSONObject approvalRequest = new JSONObject();
            approvalRequest.put("actionType", action);
            approvalRequest.put("contextId", workItemId);
            approvalRequest.put("comments", comments);

            JSONArray requests = new JSONArray();
            requests.put(approvalRequest);

            JSONObject json = new JSONObject();
            json.put("requests", requests);

            StringEntity entity = new StringEntity(json.toString(), HTTP.UTF_8);
            entity.setContentType("application/json");

            String url = "/services/data/" + context.getString(R.string.api_version) + "/process/approvals/";

            final RestRequest restRequest = new RestRequest(RestRequest.RestMethod.POST, url, entity);

            QuoteDiscountApprovalSetup.client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                @Override
                public void onSuccess(RestRequest request, RestResponse result) {
                    Log.d(TAG, result.toString());
                }

                @Override
                public void onError(Exception exception) {
                    exception.printStackTrace();
                }
            });

        } catch (java.io.IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}