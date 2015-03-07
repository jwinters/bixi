package com.bixi.fragments;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.Toast;

import com.bixi.R;
import com.bixi.application.BixiIntentService;
import com.bixi.binders.StationListViewBinder;
import com.bixi.datasets.GeofenceTable;
import com.bixi.datasets.StationView;
import com.bixi.monitors.GeofenceListMonitor;
import com.bixi.providers.BixiContentProvider;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationStatusCodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import io.pivotal.arca.adapters.Binding;
import io.pivotal.arca.adapters.ModernCursorAdapter;
import io.pivotal.arca.dispatcher.Error;
import io.pivotal.arca.dispatcher.Insert;
import io.pivotal.arca.dispatcher.Query;
import io.pivotal.arca.dispatcher.QueryResult;
import io.pivotal.arca.fragments.ArcaAdapterFragment;
import io.pivotal.arca.fragments.ArcaDispatcherFactory;
import io.pivotal.arca.monitor.ArcaDispatcher;

public class GeofenceListFragment extends ArcaAdapterFragment implements
        GooglePlayServicesClient.OnConnectionFailedListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        LocationClient.OnAddGeofencesResultListener,
        LocationClient.OnRemoveGeofencesResultListener,
        OnItemClickListener {

	private static final Collection<Binding> BINDINGS = Arrays.asList(new Binding[] {
		new Binding(R.id.station_station_name, StationView.Columns.STATION_NAME),
        new Binding(R.id.station_geofenced, StationView.Columns.GEOFENCED),
	});

    private LocationClient mLocationClient;
    private boolean mIsConnected;

    @Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_geofence_list, container, false);
		final AbsListView list = (AbsListView) view.findViewById(getAdapterViewId());
		list.setOnItemClickListener(this);
		return view;
	}

    @Override
    public void onStart() {
        super.onStart();
        mLocationClient = new LocationClient(getActivity(), this, this);
        mLocationClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mLocationClient.disconnect();
    }

    @Override
    public ArcaDispatcher onCreateDispatcher(Bundle savedInstanceState) {
        final ArcaDispatcher dispatcher = ArcaDispatcherFactory.generateDispatcher(this);
        dispatcher.setRequestMonitor(new GeofenceListMonitor());
        return dispatcher;
    }

    @Override
	public CursorAdapter onCreateAdapter(final AdapterView<CursorAdapter> adapterView, final Bundle savedInstanceState) {
		final ModernCursorAdapter adapter = new ModernCursorAdapter(getActivity(), R.layout.list_item_station, BINDINGS);
		adapter.setViewBinder(new StationListViewBinder());
		return adapter;
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		showLoading();
		loadStations();
	}

	private void loadStations() {
		final Uri uri = BixiContentProvider.Uris.STATION_GEOFENCES;
		final Query request = new Query(uri);
		execute(request);
	}

	@Override
	public void onContentChanged(final QueryResult result) {
		final CursorAdapter adapter = getCursorAdapter();
		if (adapter.getCount() > 0) {
			showResults();
		} else if (!result.isSyncing()) {
			showNoResults();
		}
	}

	@Override
	public void onContentError(final Error error) {
		showNoResults();
		showError(error);
	}

	private View getLoadingView() {
		return getView().findViewById(R.id.loading);
	}

	private View getEmptyView() {
		return getView().findViewById(R.id.empty);
	}

	private void showLoading() {
		getAdapterView().setVisibility(View.INVISIBLE);
		getLoadingView().setVisibility(View.VISIBLE);
		getEmptyView().setVisibility(View.INVISIBLE);
	}

	private void showResults() {
		getAdapterView().setVisibility(View.VISIBLE);
		getLoadingView().setVisibility(View.INVISIBLE);
		getEmptyView().setVisibility(View.INVISIBLE);
	}

	private void showNoResults() {
		getAdapterView().setVisibility(View.INVISIBLE);
		getLoadingView().setVisibility(View.INVISIBLE);
		getEmptyView().setVisibility(View.VISIBLE);
	}

	private void showError(final Error error) {
		final String message = String.format("ERROR: %s", error.getMessage());
		Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
        if (!mIsConnected) {
            Toast.makeText(getActivity(), "Please wait. Location client connecting...", Toast.LENGTH_SHORT).show();
        } else {
            final Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
            final boolean checked = updateCheckBoxState(view);

            updateLocationClient(cursor, checked);
            updateGeofenceRecord(cursor, checked);
        }
	}

    private boolean updateCheckBoxState(final View view) {
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.station_geofenced);
        final boolean checked = !checkBox.isChecked();
        checkBox.setChecked(checked);
        return checked;
    }

    private void updateLocationClient(final Cursor cursor, final boolean checked) {
        if (checked) {
            final List<Geofence> list = getGeofenceLocations(cursor);
            mLocationClient.addGeofences(list, getTransitionPendingIntent(), this);
        } else {
            final List<String> list = getGeofenceIds(cursor);
            mLocationClient.removeGeofences(list, this);
        }
    }

    private void updateGeofenceRecord(final Cursor cursor, final boolean checked) {
        final int itemId = cursor.getInt(cursor.getColumnIndex(StationView.Columns.ID));

        final ContentValues values = new ContentValues();
        values.put(GeofenceTable.Columns.GEOFENCED, checked);
        values.put(GeofenceTable.Columns.ID, itemId);

        final Uri uri = BixiContentProvider.Uris.GEOFENCES;
        final Insert request = new Insert(uri, values);

        getRequestDispatcher().execute(request);
    }

    private PendingIntent getTransitionPendingIntent() {
        final Intent intent = new Intent(getActivity(), BixiIntentService.class);
        return PendingIntent.getService(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private List<String> getGeofenceIds(final Cursor cursor) {
        final List<String> locations = new ArrayList<String>();
        locations.add(cursor.getString(cursor.getColumnIndex(StationView.Columns.STATION_NAME)));
        return locations;
    }

    private List<Geofence> getGeofenceLocations(final Cursor cursor) {
        final List<Geofence> locations = new ArrayList<Geofence>();
        locations.add(getGeofence(cursor));
        return locations;
    }

    private Geofence getGeofence(final Cursor cursor) {
        final String title = cursor.getString(cursor.getColumnIndex(StationView.Columns.STATION_NAME));
        final double latitude = cursor.getDouble(cursor.getColumnIndex(StationView.Columns.LATITUDE));
        final double longitude = cursor.getDouble(cursor.getColumnIndex(StationView.Columns.LONGITUDE));

        return new Geofence.Builder().setRequestId(title)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setCircularRegion(latitude, longitude, 500)
            .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mIsConnected = true;
    }

    @Override
    public void onDisconnected() {
        mIsConnected = false;
    }

    @Override
    public void onConnectionFailed(final ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), "Connection failed.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddGeofencesResult(final int statusCode, final String[] ids) {
        if (statusCode == LocationStatusCodes.SUCCESS) {
            Toast.makeText(getActivity(), "Location added successfully.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Location added failure.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRemoveGeofencesByRequestIdsResult(final int statusCode, final String[] ids) {
        if (statusCode == LocationStatusCodes.SUCCESS) {
            Toast.makeText(getActivity(), "Location removed successfully.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Location removed failed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRemoveGeofencesByPendingIntentResult(final int statusCode, final PendingIntent pendingIntent) {
        if (statusCode == LocationStatusCodes.SUCCESS) {
            Toast.makeText(getActivity(), "Location removed successfully.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Location removed failed.", Toast.LENGTH_SHORT).show();
        }
    }
}