package com.bixi.datasets;

import io.pivotal.arca.provider.Joins;
import io.pivotal.arca.provider.OrderBy;
import io.pivotal.arca.provider.SQLiteView;
import io.pivotal.arca.provider.Select;
import io.pivotal.arca.provider.SelectFrom;

public class StationView extends SQLiteView {

    @SelectFrom("StationTable as stations")

    @Joins({
        "LEFT JOIN GeofenceTable as fences ON stations.id = fences.id"
    })

    @OrderBy("stationName ASC")

	public static interface Columns {
        @Select("stations." + StationTable.Columns._ID)
        public static final String _ID = "_id";

        @Select("stations." + StationTable.Columns.ID)
        public static final String ID = "id";

        @Select("stations." + StationTable.Columns.STATION_NAME)
		public static final String STATION_NAME = "stationName";

        @Select("stations." + StationTable.Columns.LATITUDE)
        public static final String LATITUDE = "latitude";

        @Select("stations." + StationTable.Columns.LONGITUDE)
        public static final String LONGITUDE = "longitude";

        @Select("fences." + GeofenceTable.Columns.GEOFENCED)
        public static final String GEOFENCED = "geofenced";
	}
}