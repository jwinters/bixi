package com.bixi.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.bixi.R;

public class LauncherActivity extends Activity {

	private static final int LAUNCH_MSG = 100;
	private static final int LAUNCH_DURATION = 2000;

	private final LaunchHandler mHandler = new LaunchHandler(this);

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launcher);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mHandler.sendEmptyMessageDelayed(LAUNCH_MSG, LAUNCH_DURATION);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeMessages(LAUNCH_MSG);
	}

	public void launch() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		StationListActivity.newInstance(this);
	}

	private static final class LaunchHandler extends Handler {

		private final LauncherActivity mActivity;

		public LaunchHandler(final LauncherActivity activity) {
			mActivity = activity;
		}

		@Override
		public void handleMessage(final Message msg) {
			super.handleMessage(msg);

			if (msg.what == LAUNCH_MSG) {
				mActivity.launch();
			}
		}
	}
}