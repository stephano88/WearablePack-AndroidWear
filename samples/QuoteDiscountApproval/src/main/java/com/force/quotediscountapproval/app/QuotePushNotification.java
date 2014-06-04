package com.force.quotediscountapproval.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preview.support.v4.app.NotificationManagerCompat;
import android.preview.support.wearable.notifications.RemoteInput;
import android.preview.support.wearable.notifications.WearableNotifications;
import android.support.v4.app.NotificationCompat;
import java.util.Formatter;
import com.salesforce.androidsdk.push.PushNotificationInterface;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class QuotePushNotification implements PushNotificationInterface {

    private static final String TAG = QuotePushNotification.class.getName();
    private final Context context;

    public QuotePushNotification(Context context) {
        this.context = context;
    }

    @Override
    public void onPushMessageReceived(final Bundle bundle) {

        final QuoteMessage quoteMessage = new QuoteMessage(bundle);

        // this must be async
        new AsyncTask<String, Void, Bitmap>() {

            protected Bitmap doInBackground(String... urls) {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(urls[0]);
                    httpGet.setHeader("Authorization", "Bearer " + QuoteDiscountApprovalSetup.client.getAuthToken());
                    HttpResponse response = httpClient.execute(httpGet);
                    return BitmapFactory.decodeStream(response.getEntity().getContent());
                } catch (Exception e) {
                    return null;
                }
            }

            protected void onPostExecute(Bitmap maybeChatterProfilePic) {
                Intent openIntent = new Intent(QuoteDiscountApprovalActions.OPEN);
                openIntent.setClass(context, QuoteDiscountApprovalActions.class);
                openIntent.putExtra(QuoteDiscountApprovalActions.BUNDLE, quoteMessage.toBundle());
                PendingIntent openPendingIntent = PendingIntent.getBroadcast(context, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Intent approveIntent = new Intent(QuoteDiscountApprovalActions.APPROVE);
                approveIntent.setClass(context, QuoteDiscountApprovalActions.class);
                approveIntent.putExtra(QuoteDiscountApprovalActions.BUNDLE, quoteMessage.toBundle());
                PendingIntent approvePendingIntent = PendingIntent.getBroadcast(context, 0, approveIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Intent rejectIntent = new Intent(QuoteDiscountApprovalActions.REJECT);
                rejectIntent.setClass(context, QuoteDiscountApprovalActions.class);
                rejectIntent.putExtra(QuoteDiscountApprovalActions.BUNDLE, quoteMessage.toBundle());
                PendingIntent rejectPendingIntent = PendingIntent.getBroadcast(context, 0, rejectIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                // Main notification
                final NotificationCompat.Builder mainNotification = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.s1_icon)
                        .setContentTitle(quoteMessage.ownerName)
                        .setContentText("Quote approval required: " + quoteMessage.quoteName);


                if (maybeChatterProfilePic != null) {
                    mainNotification.setLargeIcon(maybeChatterProfilePic);
                }

                // Detail Card
                NotificationCompat.BigTextStyle detailCard = new NotificationCompat.BigTextStyle();
                detailCard.setBigContentTitle(quoteMessage.oppName)
                        .bigText("Discount: " + quoteMessage.discount + "%           Total Price: $" + new Formatter().format("%,d", quoteMessage.amount));

                Notification detailNotification = new NotificationCompat.Builder(context)
                        .setStyle(detailCard)
                        .build();

                // Approve Card
                mainNotification.addAction(R.drawable.approve, "Approve", approvePendingIntent);

                // Reject Card
                String[] rejectChoices = {"Too high", "Lets wait"};

                RemoteInput remoteInput = new RemoteInput.Builder(QuoteDiscountApprovalActions.MESSAGE)
                        .setAllowFreeFormInput(true)
                        .setChoices(rejectChoices)
                        .setLabel("Reject")
                        .build();

                WearableNotifications.Action rejectAction = new WearableNotifications.Action.Builder(R.drawable.reject, "Reject", rejectPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();

                WearableNotifications.Builder wearableBuilder = new WearableNotifications.Builder(mainNotification);
                wearableBuilder.addPage(detailNotification).addAction(rejectAction);

                // Open Card
                mainNotification.setContentIntent(openPendingIntent);

                NotificationManagerCompat.from(context).notify(1, wearableBuilder.build());
            }

        }.execute(quoteMessage.ownerFullPhotoUrl);
    }

}