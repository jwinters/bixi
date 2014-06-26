package com.bixi.application;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import java.util.List;

public class BixiIntentService extends IntentService {

    public BixiIntentService() {
        super("BixiIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!LocationClient.hasError(intent)) {
            int transitionType = LocationClient.getGeofenceTransition(intent);
            if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {

                final List<Geofence> triggerList = LocationClient.getTriggeringGeofences(intent);
                final String[] triggerIds = new String[triggerList.size()];

                for (int i = 0; i < triggerIds.length; i++) {
                    triggerIds[i] = triggerList.get(i).getRequestId();
                }
            }
        }
    }
}
