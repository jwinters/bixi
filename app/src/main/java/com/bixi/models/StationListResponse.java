package com.bixi.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class StationListResponse {

	private static final long serialVersionUID = 1L;

    public static class Fields {
        public static final String EXECUTION_TIME = "executionTime";
        public static final String STATION_BEAN_LIST = "stationBeanList";
    }

    @SerializedName(Fields.EXECUTION_TIME)
    private String mExecutionTime;

    @SerializedName(Fields.STATION_BEAN_LIST)
    private ArrayList<Station> mStationList;

    public List<Station> getStationList() {
        return mStationList;
    }
}