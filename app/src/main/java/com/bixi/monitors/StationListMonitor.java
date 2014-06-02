package com.bixi.monitors;

import android.content.Context;
import android.net.Uri;

import com.bixi.operations.GetStationListOperation;
import io.pivotal.arca.dispatcher.Query;
import io.pivotal.arca.dispatcher.QueryResult;
import io.pivotal.arca.monitor.RequestMonitor.AbstractRequestMonitor;
import io.pivotal.arca.service.OperationService;

public class StationListMonitor extends AbstractRequestMonitor {

	private static final int DELAY = 30 * 1000;
	
	private long mLastSync = 0;

	private boolean shouldSync() {
		return System.currentTimeMillis() > (mLastSync + DELAY);
	}

	public boolean startDataSync(final Context context, final Uri uri) {
		return OperationService.start(context, new GetStationListOperation(uri));
	}

	@Override
	public int onPostExecute(final Context context, final Query request, final QueryResult result) {
		if (shouldSync() && startDataSync(context, request.getUri())) {
			mLastSync = System.currentTimeMillis();
			return Flags.DATA_SYNCING; 
		} else {
			return 0;
		}
	}
}