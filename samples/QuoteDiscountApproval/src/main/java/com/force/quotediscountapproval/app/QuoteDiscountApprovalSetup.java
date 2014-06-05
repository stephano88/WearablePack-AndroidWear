package com.force.quotediscountapproval.app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.ui.sfnative.SalesforceActivity;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class QuoteDiscountApprovalSetup extends SalesforceActivity {

    private static final String TAG = QuoteDiscountApprovalSetup.class.getName();

    public static RestClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    public void onResume(RestClient client) {
        QuoteDiscountApprovalSetup.client = client;

        try {
            // todo: validate push notification registration worked
            RestRequest restRequest = RestRequest.getRequestForRetrieve(getString(R.string.api_version),
                    "User", client.getClientInfo().userId, Arrays.asList("Name"));

            client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                @Override
                public void onSuccess(RestRequest request, RestResponse result) {
                    try {
                        ((TextView)findViewById(R.id.connect_status)).setText(R.string.logged_in);
                        //Log.d(TAG, result.asString());
                    } catch (Exception e) {
                        onError(e);
                    }
                }

                @Override
                public void onError(Exception exception) {
                    Log.e(TAG, exception.getMessage(), exception);
                    ((TextView)findViewById(R.id.connect_status)).setText(getString(R.string.login_error));
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public void onSendTestNotificationClick(View v) {

        QuoteMessage quoteMessage = new QuoteMessage(
                "http://www.jamesward.com/uploads/2007/01/james_profile_2011-160x160.jpg",
                "James Ward",
                "1",
                "1",
                "1",
                "Widget Quote",
                "A Really Big Widget Deal",
                2000000L,
                "10");

        new QuotePushNotification(getApplicationContext()).onPushMessageReceived(quoteMessage.toBundle());
    }

    public void onLogoutClick(View view) {
        SalesforceSDKManager.getInstance().logout(this);
    }

}