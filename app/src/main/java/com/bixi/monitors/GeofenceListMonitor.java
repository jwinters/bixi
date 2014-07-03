package com.bixi.monitors;

import android.content.Context;
import android.net.Uri;

import com.bixi.providers.BixiContentProvider;

import io.pivotal.arca.dispatcher.Insert;
import io.pivotal.arca.dispatcher.InsertResult;
import io.pivotal.arca.monitor.RequestMonitor.AbstractRequestMonitor;

public class GeofenceListMonitor extends AbstractRequestMonitor {

    @Override
    public int onPostExecute(Context context, Insert request, InsertResult result) {
        final Uri uri = BixiContentProvider.Uris.STATION_GEOFENCES;
        context.getContentResolver().notifyChange(uri, null);
        return 0;
    }
}