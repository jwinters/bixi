package com.bixi.providers;

import android.net.Uri;

import com.bixi.datasets.GeofenceTable;
import com.bixi.datasets.StationTable;
import com.bixi.datasets.StationView;

import io.pivotal.arca.provider.DatabaseProvider;

public class BixiContentProvider extends DatabaseProvider {

	public static final String AUTHORITY = "com.bixi.providers.BixiContentProvider";

	public static final class Uris {
		public static final Uri STATIONS = Uri.parse("content://" + AUTHORITY + "/" + Paths.STATIONS);
        public static final Uri GEOFENCES = Uri.parse("content://" + AUTHORITY + "/" + Paths.GEOFENCES);
        public static final Uri STATION_GEOFENCES = Uri.parse("content://" + AUTHORITY + "/" + Paths.STATION_GEOFENCES);
	}

	private static final class Paths {
		public static final String STATIONS = "stations";
        public static final String GEOFENCES = "geofences";
        public static final String STATION_GEOFENCES = "station_geofences";
	}

	@Override
	public boolean onCreate() {
		registerDataset(AUTHORITY, Paths.STATIONS, StationTable.class);
		registerDataset(AUTHORITY, Paths.STATIONS + "/*", StationTable.class);
        registerDataset(AUTHORITY, Paths.GEOFENCES, GeofenceTable.class);
        registerDataset(AUTHORITY, Paths.GEOFENCES + "/*", GeofenceTable.class);
        registerDataset(AUTHORITY, Paths.STATION_GEOFENCES, StationView.class);
        registerDataset(AUTHORITY, Paths.STATION_GEOFENCES + "/*", StationView.class);
		return true;
	}

}