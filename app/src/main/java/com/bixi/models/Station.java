package com.bixi.models;


import com.bixi.datasets.StationTable.Columns;
import com.google.gson.annotations.SerializedName;

import io.pivotal.arca.provider.ColumnName;

public class Station {

	public static class Fields {
		public static final String ID = "id";
		public static final String STATION_NAME = "stationName";
		public static final String AVAILABLE_DOCKS = "availableDocks";
		public static final String TOTAL_DOCKS = "totalDocks";
		public static final String LATITUDE = "latitude";
		public static final String LONGITUDE = "longitude";
		public static final String STATUS_VALUE = "statusValue";
		public static final String STATUS_KEY = "statusKey";
		public static final String AVAILABLE_BIKES = "availableBikes";
		public static final String ST_ADDRESS_1 = "stAddress1";
		public static final String ST_ADDRESS_2 = "stAddress2";
		public static final String CITY = "city";
		public static final String POSTAL_CODE = "postalCode";
		public static final String LOCATION = "location";
		public static final String TEST_STATION = "testStation";
		public static final String LAST_COMMUNICATION_TIME = "lastCommunicationTime";
		public static final String LAND_MARK = "landMark";
	}

	@ColumnName(Columns.ID)
	@SerializedName(Fields.ID) 
	private int mId;
	
	@ColumnName(Columns.STATION_NAME)
	@SerializedName(Fields.STATION_NAME) 
	private String mStationName;
	
	@ColumnName(Columns.AVAILABLE_DOCKS)
	@SerializedName(Fields.AVAILABLE_DOCKS) 
	private int mAvailableDocks;
	
	@ColumnName(Columns.TOTAL_DOCKS)
	@SerializedName(Fields.TOTAL_DOCKS) 
	private int mTotalDocks;
	
	@ColumnName(Columns.LATITUDE)
	@SerializedName(Fields.LATITUDE) 
	private float mLatitude;
	
	@ColumnName(Columns.LONGITUDE)
	@SerializedName(Fields.LONGITUDE) 
	private float mLongitude;
	
	@ColumnName(Columns.STATUS_VALUE)
	@SerializedName(Fields.STATUS_VALUE) 
	private String mStatusValue;
	
	@ColumnName(Columns.STATUS_KEY)
	@SerializedName(Fields.STATUS_KEY) 
	private int mStatusKey;
	
	@ColumnName(Columns.AVAILABLE_BIKES)
	@SerializedName(Fields.AVAILABLE_BIKES) 
	private int mAvailableBikes;
	
	@ColumnName(Columns.ST_ADDRESS_1)
	@SerializedName(Fields.ST_ADDRESS_1) 
	private String mStAddress1;
	
	@ColumnName(Columns.ST_ADDRESS_2)
	@SerializedName(Fields.ST_ADDRESS_2) 
	private String mStAddress2;
	
	@ColumnName(Columns.CITY)
	@SerializedName(Fields.CITY) 
	private String mCity;
	
	@ColumnName(Columns.POSTAL_CODE)
	@SerializedName(Fields.POSTAL_CODE) 
	private String mPostalCode;
	
	@ColumnName(Columns.LOCATION)
	@SerializedName(Fields.LOCATION) 
	private String mLocation;
	
	@ColumnName(Columns.TEST_STATION)
	@SerializedName(Fields.TEST_STATION) 
	private boolean mTestStation;
	
	@ColumnName(Columns.LAST_COMMUNICATION_TIME)
	@SerializedName(Fields.LAST_COMMUNICATION_TIME) 
	private String mLastCommunicationTime;
	
	@ColumnName(Columns.LAND_MARK)
	@SerializedName(Fields.LAND_MARK) 
	private int mLandMark;
	
}