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
import android.view.View;
import android.widget.Toast;

import com.bixi.R;
import com.bixi.datasets.StationTable;
import com.bixi.monitors.StationListMonitor;
import com.bixi.providers.BixiContentProvider;
import com.bixi.views.LocationView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

import io.pivotal.arca.dispatcher.Query;
import io.pivotal.arca.dispatcher.QueryListener;
import io.pivotal.arca.dispatcher.QueryResult;
import io.pivotal.arca.fragments.ArcaDispatcherFactory;
import io.pivotal.arca.monitor.ArcaDispatcher;

public class StationListActivity extends Activity implements QueryListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {

    public static void newInstance(final Context context) {
        final Intent intent = new Intent(context, StationListActivity.class);
        context.startActivity(intent);
    }

    private LocationView mLocationView;
    private LocationRequest mLocationRequest;
    private LocationClient mLocationClient;
    private ArcaDispatcher mDispatcher;
    private GoogleMap mMap;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_list);
        setTitle(R.string.title_stations);

        final int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (result != ConnectionResult.SUCCESS) {
            final String message = GooglePlayServicesUtil.getErrorString(result);
            Toast.makeText(this, "Google Play Services: " + message, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            setup();
        }
    }

    private void setup() {
        setupMapView();
        setupLocationView();
        setupLocationClient();
        setupDispatcher();
    }

    private void setupMapView() {
        final FragmentManager manager = getFragmentManager();
        mMap = ((MapFragment) manager.findFragmentById(R.id.map_fragment)).getMap();
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setPadding(10, 175, 10, 10);
        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
    }

    private void setupLocationView() {
        mLocationView = (LocationView) findViewById(R.id.location_view);
    }

    private void setupLocationClient() {
        mLocationClient = new LocationClient(this, this, this);
        mLocationClient.connect();
    }

    private void setupDispatcher() {
        mDispatcher = ArcaDispatcherFactory.generateDispatcher(this);
        mDispatcher.setRequestMonitor(new StationListMonitor());
    }

    @Override
    protected void onStart() {
        super.onStart();

        reload();
    }

    private void reload() {
        final Uri uri = BixiContentProvider.Uris.STATIONS;
        mDispatcher.execute(new Query(uri, 1000), this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mLocationClient != null) {
            mLocationClient.disconnect();
        }
    }

    @Override
    public void onRequestComplete(final QueryResult result) {
        mMap.clear();

        final Cursor cursor = result.getResult();
        while (cursor.moveToNext()) {
            addMarker(cursor);
        }
    }

    @Override
    public void onRequestReset() {
        //do nothing
    }

    private void addMarker(final Cursor cursor) {
        final float lat = cursor.getFloat(cursor.getColumnIndex(StationTable.Columns.LATITUDE));
        final float lon = cursor.getFloat(cursor.getColumnIndex(StationTable.Columns.LONGITUDE));
        final String station = cursor.getString(cursor.getColumnIndex(StationTable.Columns.STATION_NAME));
        final int bikes = cursor.getInt(cursor.getColumnIndex(StationTable.Columns.AVAILABLE_BIKES));
        final int docks = cursor.getInt(cursor.getColumnIndex(StationTable.Columns.AVAILABLE_DOCKS));

        final String title = String.format(Locale.getDefault(), "%s", station);
        final String snippet = String.format(Locale.getDefault(), "[%d bikes, %d docks]", bikes, docks);
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(title).snippet(snippet).flat(true));
    }

    private void setLocation(final double latitude, final double longitude) {
        setLocation(latitude, longitude, 15.0f);
    }

    private void setLocation(final double latitude, final double longitude, final float zoom) {
        final LatLng latLng = new LatLng(latitude, longitude);
        final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.animateCamera(cameraUpdate, 200, null);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        final LatLng position = marker.getPosition();
        final float zoom = mMap.getCameraPosition().zoom;
        setLocation(position.latitude, position.longitude, zoom);
        mLocationView.setTitle(marker.getTitle());
        mLocationView.setSubtitle(marker.getSnippet());
        mLocationView.setVisibility(View.VISIBLE);
        return true;
    }

    @Override
    public void onMapClick(final LatLng latLng) {
        mLocationView.setVisibility(View.GONE);
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
        } else if (item.getItemId() == R.id.menu_locations) {
            GeofenceListActivity.newInstance(this);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnected(final Bundle bundle) {
        final Location location = mLocationClient.getLastLocation();
        if (location != null) {
            setLocation(location.getLatitude(), location.getLongitude());
        } else {
            setLocation(43.6525, -79.3836);
        }

        mLocationClient.requestLocationUpdates(getLocationRequest(), this);
    }

    public LocationRequest getLocationRequest() {
        if (mLocationRequest == null) {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(5 * 1000);
            mLocationRequest.setFastestInterval(1);
        }
        return mLocationRequest;
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
        mLocationClient.removeLocationUpdates(this);
    }

}