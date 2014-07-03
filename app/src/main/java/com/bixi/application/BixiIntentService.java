package com.bixi.application;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.bixi.activities.StationListActivity;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import java.util.List;

public class BixiIntentService extends IntentService {

    public BixiIntentService() {
        super("BixiIntentService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        if (!LocationClient.hasError(intent)) {
            int transitionType = LocationClient.getGeofenceTransition(intent);
            if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {

                final List<Geofence> triggerList = LocationClient.getTriggeringGeofences(intent);
                final String[] triggerIds = new String[triggerList.size()];

                for (int i = 0; i < triggerIds.length; i++) {
                    triggerIds[i] = triggerList.get(i).getRequestId();
                    addNotification(triggerIds[i]);
                }
            }
        }
    }

    private void addNotification(final String triggerId) {
        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, StationListActivity.class), 0);

        final String msg = triggerId;
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
            .setContentTitle("Geofence")
            .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
            .setContentText(msg);

        builder.setContentIntent(contentIntent);
        notificationManager.notify(1, builder.build());
    }
}
