package net.vpnsdk.vpn.ics;

import android.app.Activity;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager.LayoutParams;


public class L3VpnStartDialog extends Activity {

	private static final String Tag = "L3VpnStartDialog";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(Tag, "onCreate");
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		LayoutParams params = getWindow().getAttributes();
		params.width = 1;
        params.height = 1;
        getWindow().setAttributes(params);
        startVpn();
	}

	@Override
	protected void onActivityResult(int request, int result, Intent intent) {

		finish();
	}
	
	@Override
	public void onDestroy() {
		Log.d(Tag, "onDestroy");
		super.onDestroy();
	}
	
	public void startVpn() {
	}
}