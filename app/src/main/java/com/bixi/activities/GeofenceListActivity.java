package com.bixi.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bixi.R;

public class GeofenceListActivity extends Activity {

    public static final void newInstance(final Context context) {
        final Intent intent = new Intent(context, GeofenceListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence_list);
        setTitle(R.string.title_geofence);
    }

}