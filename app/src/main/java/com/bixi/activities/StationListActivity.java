package com.bixi.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.bixi.R;
import com.bixi.datasets.StationTable;
import com.bixi.monitors.StationListMonitor;
import com.bixi.providers.BixiContentProvider;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import io.pivotal.arca.dispatcher.Query;
import io.pivotal.arca.dispatcher.QueryListener;
import io.pivotal.arca.dispatcher.QueryResult;
import io.pivotal.arca.fragments.ArcaDispatcherFactory;
import io.pivotal.arca.monitor.ArcaDispatcher;

public class StationListActivity extends Activity implements QueryListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {

    public static final void newInstance(final Context context) {
        final Intent intent = new Intent(context, StationListActivity.class);
        context.startActivity(intent);
    }

    private LocationRequest mLocationRequest;
    private LocationClient mLocationClient;
    private ArcaDispatcher mDispatcher;
    private GoogleMap mMap;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_list);
        setTitle(R.string.title_stations);

        mLocationClient = new LocationClient(this, this, this);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5 * 1000);
        mLocationRequest.setFastestInterval(1);

        final FragmentManager manager = getFragmentManager();
        mMap = ((MapFragment) manager.findFragmentById(R.id.map_fragment)).getMap();
        mMap.setMyLocationEnabled(true);

        mDispatcher = ArcaDispatcherFactory.generateDispatcher(this);
        mDispatcher.setRequestMonitor(new StationListMonitor());
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fragment_station_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.menu_reload) {
            reload();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mLocationClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mLocationClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();

        reload();
    }

    private void reload() {
        final Uri uri = BixiContentProvider.Uris.STATIONS;
        mDispatcher.execute(new Query(uri), this);
    }

    @Override
    public void onRequestComplete(final QueryResult result) {
        final Cursor cursor = result.getResult();

        mMap.clear();

        while (cursor.moveToNext()) {
            addMarker(cursor);
        }
    }

    private void addMarker(final Cursor cursor) {
        final float lat = cursor.getFloat(cursor.getColumnIndex(StationTable.Columns.LATITUDE));
        final float lon = cursor.getFloat(cursor.getColumnIndex(StationTable.Columns.LONGITUDE));
        final String station = cursor.getString(cursor.getColumnIndex(StationTable.Columns.STATION_NAME));
        final int bikes = cursor.getInt(cursor.getColumnIndex(StationTable.Columns.AVAILABLE_BIKES));
        final int docks = cursor.getInt(cursor.getColumnIndex(StationTable.Columns.AVAILABLE_DOCKS));

        final String title = String.format("%s \n[%d bikes, %d docks]", station, bikes, docks);
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(title).flat(true));
    }

    @Override
    public void onRequestReset() {
        //do nothing
    }

    @Override
    public void onConnected(final Bundle bundle) {
        final Location location = mLocationClient.getLastLocation();
        if (location != null) {
            setLocation(location.getLatitude(), location.getLongitude());
        } else {
            setLocation(43.6525, -79.3836);
        }

        mLocationClient.requestLocationUpdates(mLocationRequest, this);
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(this, "Map services disconnected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(final ConnectionResult connectionResult) {
        Toast.makeText(this, "Map services not available", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(final Location location) {
        setLocation(location.getLatitude(), location.getLongitude());
    }

    private void setLocation(final double latitude, final double longitude) {
        final LatLng latLng = new LatLng(latitude, longitude);
        final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mMap.animateCamera(cameraUpdate);
    }
}