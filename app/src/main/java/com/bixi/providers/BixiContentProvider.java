package com.bixi.providers;

import android.net.Uri;

import com.bixi.datasets.StationTable;

import io.pivotal.arca.provider.DatabaseProvider;

public class BixiContentProvider extends DatabaseProvider {

	public static final String AUTHORITY = "com.bixi.providers.BixiContentProvider";

	public static final class Uris {
		public static final Uri STATIONS = Uri.parse("content://" + AUTHORITY + "/" + Paths.STATIONS);
	}

	private static final class Paths {
		public static final String STATIONS = "stations";
	}

	@Override
	public boolean onCreate() {
		registerDataset(AUTHORITY, Paths.STATIONS, StationTable.class);
		registerDataset(AUTHORITY, Paths.STATIONS + "/*", StationTable.class);
		return true;
	}

}