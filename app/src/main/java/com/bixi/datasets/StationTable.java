package com.bixi.datasets;

import io.pivotal.arca.provider.Column;
import io.pivotal.arca.provider.SQLiteTable;
import io.pivotal.arca.provider.Unique;
import io.pivotal.arca.provider.Unique.OnConflict;

public class StationTable extends SQLiteTable {

	public static interface Columns extends SQLiteTable.Columns {

        @Unique(OnConflict.REPLACE)
        @Column(Column.Type.INTEGER)
		public static final String ID = "id";

        @Column(Column.Type.TEXT)
		public static final String STATION_NAME = "stationName";

        @Column(Column.Type.INTEGER)
        public static final String AVAILABLE_DOCKS = "availableDocks";

        @Column(Column.Type.INTEGER)
        public static final String TOTAL_DOCKS = "totalDocks";

        @Column(Column.Type.REAL)
        public static final String LATITUDE = "latitude";

        @Column(Column.Type.REAL)
        public static final String LONGITUDE = "longitude";

        @Column(Column.Type.TEXT)
        public static final String STATUS_VALUE = "statusValue";

        @Column(Column.Type.INTEGER)
        public static final String STATUS_KEY = "statusKey";

        @Column(Column.Type.INTEGER)
        public static final String AVAILABLE_BIKES = "availableBikes";

        @Column(Column.Type.TEXT)
        public static final String ST_ADDRESS_1 = "stAddress1";

        @Column(Column.Type.TEXT)
        public static final String ST_ADDRESS_2 = "stAddress2";

        @Column(Column.Type.TEXT)
        public static final String CITY = "city";

        @Column(Column.Type.TEXT)
        public static final String POSTAL_CODE = "postalCode";

        @Column(Column.Type.TEXT)
        public static final String LOCATION = "location";

        @Column(Column.Type.INTEGER)
        public static final String TEST_STATION = "testStation";

        @Column(Column.Type.TEXT)
        public static final String LAST_COMMUNICATION_TIME = "lastCommunicationTime";

        @Column(Column.Type.INTEGER)
        public static final String LAND_MARK = "landMark";
	}
}