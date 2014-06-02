package com.bixi.fragments;

import java.util.Arrays;
import java.util.Collection;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.Toast;

import io.pivotal.arca.adapters.Binding;
import io.pivotal.arca.adapters.ModernCursorAdapter;
import io.pivotal.arca.dispatcher.Error;
import io.pivotal.arca.dispatcher.Query;
import io.pivotal.arca.dispatcher.QueryResult;
import io.pivotal.arca.fragments.ArcaAdapterFragment;
import io.pivotal.arca.monitor.ArcaDispatcher;
import com.bixi.R;
import com.bixi.binders.StationListViewBinder;
import com.bixi.datasets.StationTable;
import com.bixi.monitors.StationListMonitor;
import com.bixi.providers.BixiContentProvider;

public class StationListFragment extends ArcaAdapterFragment implements OnItemClickListener {

	private static final Collection<Binding> BINDINGS = Arrays.asList(new Binding[] {
		new Binding(R.id.station_id, StationTable.Columns.ID),
		new Binding(R.id.station_station_name, StationTable.Columns.STATION_NAME),
		new Binding(R.id.station_available_docks, StationTable.Columns.AVAILABLE_DOCKS),
        new Binding(R.id.station_available_bikes, StationTable.Columns.AVAILABLE_BIKES),
		new Binding(R.id.station_total_docks, StationTable.Columns.TOTAL_DOCKS),
		new Binding(R.id.station_latitude, StationTable.Columns.LATITUDE),
		new Binding(R.id.station_longitude, StationTable.Columns.LONGITUDE),
		new Binding(R.id.station_last_communication_time, StationTable.Columns.LAST_COMMUNICATION_TIME),
		new Binding(R.id.station_land_mark, StationTable.Columns.LAND_MARK),
	});

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_station_list, container, false);
		final AbsListView list = (AbsListView) view.findViewById(getAdapterViewId());
		list.setOnItemClickListener(this);
		return view;
	}

	@Override
	public CursorAdapter onCreateAdapter(final AdapterView<CursorAdapter> adapterView, final Bundle savedInstanceState) {
		final ModernCursorAdapter adapter = new ModernCursorAdapter(getActivity(), R.layout.list_item_station, BINDINGS);
		adapter.setViewBinder(new StationListViewBinder());
		return adapter;
	}

	@Override
	public ArcaDispatcher onCreateDispatcher(final Bundle savedInstaceState) {
		final ArcaDispatcher dispatcher = super.onCreateDispatcher(savedInstaceState);
		dispatcher.setRequestMonitor(new StationListMonitor());
		return dispatcher;
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		showLoading();
		loadStations();
	}

	private void loadStations() {
		final Uri uri = BixiContentProvider.Uris.STATIONS;
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
		// final Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
		// final int columnIndex = cursor.getColumnIndex(StationTable.Columns.ID);
		// final String itemId = cursor.getString(columnIndex);
		// StationActivity.newInstance(getActivity(), itemId);
	}
}