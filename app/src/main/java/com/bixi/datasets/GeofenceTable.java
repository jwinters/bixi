package com.bixi.datasets;

import io.pivotal.arca.provider.Column;
import io.pivotal.arca.provider.SQLiteTable;
import io.pivotal.arca.provider.Unique;
import io.pivotal.arca.provider.Unique.OnConflict;

public class GeofenceTable extends SQLiteTable {

	public static interface Columns extends SQLiteTable.Columns {

        @Unique(OnConflict.REPLACE)
        @Column(Column.Type.INTEGER)
		public static final String ID = "id";

        @Column(Column.Type.INTEGER)
        public static final String GEOFENCED = "geofenced";
	}
}