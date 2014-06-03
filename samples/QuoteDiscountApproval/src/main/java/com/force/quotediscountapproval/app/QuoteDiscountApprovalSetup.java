package com.force.quotediscountapproval.app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.ui.sfnative.SalesforceActivity;
import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class QuoteDiscountApprovalSetup extends SalesforceActivity {

    private static final String TAG = QuoteDiscountApprovalSetup.class.getName();

    public static RestClient client;
    private ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup view
        setContentView(R.layout.main);
    }

    @Override
    public void onResume() {
        // Hide everything until we are logged in
        findViewById(R.id.root).setVisibility(View.INVISIBLE);

        // Create list adapter
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        ((ListView) findViewById(R.id.deal_list)).setAdapter(listAdapter);

        super.onResume();
    }

    @Override
    // todo: cleanup this ui
    public void onResume(RestClient client) {
        // Keeping reference to rest client
        QuoteDiscountApprovalSetup.client = client;

        // Show everything
        findViewById(R.id.root).setVisibility(View.VISIBLE);

        try {
            sendRequest("SELECT Name FROM Opportunity");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void onSendNotificationClick(View v) {

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

    private void sendRequest(String soql) throws UnsupportedEncodingException {
        RestRequest restRequest = RestRequest.getRequestForQuery(getString(R.string.api_version), soql);

        client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
            @Override
            public void onSuccess(RestRequest request, RestResponse result) {
                try {
                    listAdapter.clear();
                    JSONArray records = result.asJSONObject().getJSONArray("records");
                    for (int i = 0; i < records.length(); i++) {
                        Log.i(TAG, records.getJSONObject(i).toString());
                        listAdapter.add(records.getJSONObject(i).getString("Name"));
                    }
                } catch (Exception e) {
                    onError(e);
                }
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(QuoteDiscountApprovalSetup.this,
                        QuoteDiscountApprovalSetup.this.getString(SalesforceSDKManager.getInstance().getSalesforceR().stringGenericError(), exception.toString()),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

}
