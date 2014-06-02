package com.bixi.operations;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.bixi.application.BixiApi;
import com.bixi.models.StationListResponse;
import com.bixi.providers.BixiContentProvider;

import io.pivotal.arca.provider.DataUtils;
import io.pivotal.arca.service.Task;
import io.pivotal.arca.threading.Identifier;

public class GetStationListTask extends Task<ContentValues[]> {

	public GetStationListTask() {}

	@Override
	public Identifier<?> onCreateIdentifier() {
		return new Identifier<String>("get_station_list");
	}
	
	@Override
	public ContentValues[] onExecuteNetworking(final Context context) throws Exception {
		final StationListResponse response = BixiApi.getStationList();
		return DataUtils.getContentValues(response.getStationList());
	}

	@Override
	public void onExecuteProcessing(final Context context, final ContentValues[] values) throws Exception {
		final ContentResolver resolver = context.getContentResolver();
		resolver.delete(BixiContentProvider.Uris.STATIONS, null, null);
		resolver.bulkInsert(BixiContentProvider.Uris.STATIONS, values);
	}
}